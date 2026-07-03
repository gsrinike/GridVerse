package eu.gridverse.srv.optimization.services.profile;

import eu.gridverse.data.cnm.rao.*;
import java.io.*; import java.nio.charset.StandardCharsets; import java.util.*; import java.util.function.Function; import java.util.stream.Collectors; import java.util.zip.ZipEntry; import java.util.zip.ZipInputStream;
import org.apache.commons.csv.CSVFormat; import org.apache.commons.csv.CSVRecord;

public final class PstProfilePackageParser {
    private static final Set<String> REQUIRED = Set.of("pst_asset.csv", "ra_availability.csv", "pst_operational_limits.csv", "pst_groups.csv", "pst_costs.csv", "tap_schedule.csv", "rao_control_params.csv", "rao_tso_limits.csv", "monitored_branches.csv");

    public RemedialActionRequest parse(String networkId, String timestamp, byte[] zipBytes, RaoAnalysisSnapshot snapshot) {
        Map<String, List<Map<String, String>>> tables = readTables(zipBytes);
        Map<String, Map<String, String>> assets = index(tables.get("pst_asset.csv"), "mrid");
        Map<String, Map<String, String>> costs = index(tables.get("pst_costs.csv"), "pst_mrid");
        Map<String, Map<String, String>> availability = tables.get("ra_availability.csv").stream().filter(r -> timestamp.equals(r.get("timestamp"))).collect(Collectors.toMap(r -> required(r, "pst_mrid"), Function.identity(), (a,b) -> { throw new IllegalArgumentException("duplicate availability row"); }));
        Map<String, Map<String, String>> schedule = tables.get("tap_schedule.csv").stream().filter(r -> timestamp.equals(r.get("timestamp"))).collect(Collectors.toMap(r -> required(r, "pst_mrid"), Function.identity(), (a,b) -> { throw new IllegalArgumentException("duplicate tap schedule row"); }));
        Map<String, Set<String>> groups = new LinkedHashMap<>(); tables.get("pst_groups.csv").forEach(r -> { if (!blank(r.get("group_mrid"))) groups.computeIfAbsent(r.get("group_mrid"), ignored -> new LinkedHashSet<>()).add(required(r,"pst_mrid")); });
        Set<String> excludedByGroup = groups.values().stream().filter(members -> members.stream().anyMatch(id -> !enabled(availability.get(id)))).flatMap(Set::stream).collect(Collectors.toSet());

        List<PstCandidate> psts = new ArrayList<>();
        for (var entry : assets.entrySet()) {
            String id = entry.getKey(); Map<String, String> asset = entry.getValue(); if (!enabled(availability.get(id)) || excludedByGroup.contains(id)) continue;
            int current = integer(asset, "tap_current"); int technicalLow = integer(asset, "lowStep"); int technicalHigh = integer(asset, "highStep"); int[] operational = operationalBounds(id, timestamp, current, technicalLow, technicalHigh, tables.get("pst_operational_limits.csv"));
            Map<String, String> cost = require(costs, id, "missing cost for PST "); Map<String, String> planned = require(schedule, id, "missing tap schedule for PST ");
            psts.add(new PstCandidate(id, blank(asset.get("name")) ? id : asset.get("name"), required(asset,"tso"), current, integer(planned,"scheduled_tap"), integer(planned,"scheduled_tap") - current, operational[0], operational[1], technicalLow, technicalHigh, decimal(cost,"RAPenaltyFactor"), decimal(cost,"maxTapsOT"), true));
        }
        if (psts.isEmpty()) throw new IllegalArgumentException("profile package contains no controllable PSTs at " + timestamp);
        Set<String> labels = psts.stream().map(PstCandidate::label).collect(Collectors.toSet());

        List<MonitoredBranch> branches = new ArrayList<>(); Set<String> branchIds = new HashSet<>();
        for (Map<String,String> row : tables.get("monitored_branches.csv")) if (timestamp.equals(row.get("timestamp")) && bool(row,"monitor")) {
            String id = required(row,"branch_id"); if (!branchIds.add(id)) throw new IllegalArgumentException("duplicate monitored branch at timestamp: " + id);
            String type = required(row,"element_type").toLowerCase(Locale.ROOT); if (!Set.of("line","t2w").contains(type)) throw new IllegalArgumentException("invalid monitored branch type: " + type);
            double limit = positiveMinimum(optionalDouble(row.get("limit1_a")), optionalDouble(row.get("limit2_a"))); Double current = snapshot.branchCurrentsA().get(id); if (current == null || !Double.isFinite(current)) throw new IllegalArgumentException("missing base current for monitored branch: " + id);
            Map<String, Double> sensitivity = snapshot.currentSensitivities().getOrDefault(id, Map.of()); if (!sensitivity.keySet().containsAll(labels)) throw new IllegalArgumentException("missing current sensitivities for monitored branch: " + id);
            branches.add(new MonitoredBranch(id, id, current, limit, sensitivity));
        }
        if (branches.isEmpty()) throw new IllegalArgumentException("no monitored branches at " + timestamp);

        Map<String, Double> controls = tables.get("rao_control_params.csv").stream().collect(Collectors.toMap(r -> required(r,"param_name"), r -> Double.parseDouble(required(r,"param_value"))));
        RaoParameters parameters = new RaoParameters(requiredPositive(controls,"overload_penalty"), requiredNonNegative(controls,"preventive_tap_change_cost"), requiredNonNegative(controls,"PSTlimitTS_penalty"), requiredNonNegative(controls,"PSTlimitOT_penalty"), requiredNonNegative(controls,"penaltyTechnical"));
        Map<String, Double> tsoLimits = tables.get("rao_tso_limits.csv").stream().filter(r -> timestamp.equals(r.get("timestamp"))).collect(Collectors.toMap(r -> required(r,"tso"), r -> decimal(r,"maxTapsTS_TSO")));
        List<PstGroupPair> pairs = new ArrayList<>(); for (Set<String> members : groups.values()) { List<PstCandidate> active = psts.stream().filter(p -> members.contains(p.id())).toList(); if (active.size() > 1) for (int i=1;i<active.size();i++) pairs.add(new PstGroupPair(active.getFirst().label(), active.get(i).label())); }
        return new RemedialActionRequest(networkId, timestamp, List.of(new RaoTimeframeInput(timestamp, psts, branches, tsoLimits)), pairs, parameters);
    }

    private static Map<String,List<Map<String,String>>> readTables(byte[] zipBytes) {
        Map<String,List<Map<String,String>>> result = new LinkedHashMap<>();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(zipBytes))) { ZipEntry entry; while ((entry=zip.getNextEntry())!=null) { if(entry.isDirectory()) continue; String normalized=entry.getName().replace('\\','/'); if(normalized.startsWith("/")||normalized.contains("../")) throw new IllegalArgumentException("unsafe profile archive entry"); String name=normalized.substring(normalized.lastIndexOf('/')+1); if(!REQUIRED.contains(name)) continue; if(result.containsKey(name)) throw new IllegalArgumentException("duplicate profile file: "+name); byte[] bytes=zip.readNBytes(20_000_001); if(bytes.length>20_000_000) throw new IllegalArgumentException("profile CSV too large: "+name); result.put(name, parseCsv(bytes)); } } catch(IOException e){throw new IllegalArgumentException("invalid profile ZIP",e);}
        Set<String> missing=new TreeSet<>(REQUIRED); missing.removeAll(result.keySet()); if(!missing.isEmpty()) throw new IllegalArgumentException("missing profile files: "+missing); return result;
    }
    private static List<Map<String,String>> parseCsv(byte[] bytes) throws IOException {
        try (Reader reader=new InputStreamReader(new ByteArrayInputStream(bytes),StandardCharsets.UTF_8)) { var parser=CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreEmptyLines(true).setTrim(true).get().parse(reader); List<Map<String,String>> rows=new ArrayList<>(); for(CSVRecord record:parser){Map<String,String> row=new LinkedHashMap<>();parser.getHeaderNames().forEach(h->row.put(h,record.get(h)));rows.add(row);}return rows; }
    }
    private static int[] operationalBounds(String id,String timestamp,int current,int technicalLow,int technicalHigh,List<Map<String,String>> rows){List<Map<String,String>> matches=rows.stream().filter(r->id.equals(r.get("pst_mrid"))&&(blank(r.get("timestamp"))||timestamp.equals(r.get("timestamp")))).toList();int priority=matches.stream().mapToInt(r->priority(r.get("source_level"))).max().orElse(0);int low=technicalLow,high=technicalHigh;for(var r:matches)if(priority(r.get("source_level"))==priority){int value=integer(r,"limit_value");boolean incremental="incremental".equalsIgnoreCase(r.get("valueKind"));String direction=required(r,"direction").toLowerCase(Locale.ROOT);if(direction.equals("up")||direction.equals("upanddown"))high=Math.min(high,incremental?current+Math.abs(value):value);if(direction.equals("down")||direction.equals("upanddown"))low=Math.max(low,incremental?current-Math.abs(value):direction.equals("upanddown")?-Math.abs(value):value);}if(low>high)throw new IllegalArgumentException("operational bounds are empty for PST "+id);return new int[]{low,high};}
    private static int priority(String v){return switch(Objects.requireNonNullElse(v,"").toLowerCase(Locale.ROOT)){case "online"->3;case "offline"->2;case "igm"->1;default->0;};}
    private static boolean enabled(Map<String,String> r){return r!=null&&bool(r,"available")&&bool(r,"optimize");}
    private static boolean bool(Map<String,String> r,String k){String v=required(r,k).toLowerCase(Locale.ROOT);if(!v.equals("true")&&!v.equals("false"))throw new IllegalArgumentException("invalid boolean "+k+": "+v);return Boolean.parseBoolean(v);}
    private static Map<String,Map<String,String>> index(List<Map<String,String>> rows,String key){return rows.stream().collect(Collectors.toMap(r->required(r,key),Function.identity(),(a,b)->{throw new IllegalArgumentException("duplicate "+key);},LinkedHashMap::new));}
    private static <T> T require(Map<String,T> map,String key,String prefix){T v=map.get(key);if(v==null)throw new IllegalArgumentException(prefix+key);return v;}
    private static String required(Map<String,String> row,String key){String v=row==null?null:row.get(key);if(blank(v))throw new IllegalArgumentException("missing value: "+key);return v.trim();}
    private static int integer(Map<String,String> row,String key){try{return Integer.parseInt(required(row,key));}catch(NumberFormatException e){throw new IllegalArgumentException("invalid integer: "+key,e);}}
    private static double decimal(Map<String,String> row,String key){try{double v=Double.parseDouble(required(row,key));if(!Double.isFinite(v))throw new NumberFormatException();return v;}catch(NumberFormatException e){throw new IllegalArgumentException("invalid number: "+key,e);}}
    private static Double optionalDouble(String value){if(blank(value))return null;double v=Double.parseDouble(value);return Double.isFinite(v)&&v>0?v:null;}
    private static double positiveMinimum(Double a,Double b){if(a==null&&b==null)throw new IllegalArgumentException("monitored branch requires a positive side limit");return a==null?b:b==null?a:Math.min(a,b);}
    private static double requiredPositive(Map<String,Double> values,String key){double v=require(values,key,"missing control parameter: ");if(v<=0)throw new IllegalArgumentException(key+" must be positive");return v;}
    private static double requiredNonNegative(Map<String,Double> values,String key){double v=require(values,key,"missing control parameter: ");if(v<0)throw new IllegalArgumentException(key+" must be non-negative");return v;}
    private static boolean blank(String v){return v==null||v.isBlank();}
}
