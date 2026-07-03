package eu.gridverse.srv.optimization.services.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import eu.gridverse.data.cnm.rao.RaoAnalysisSnapshot;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;

class PstProfilePackageParserTest {
    @Test void assemblesValidatedPstInput() throws Exception {
        Map<String,String> files = new LinkedHashMap<>();
        files.put("pst_asset.csv", "mrid,name,tso,highStep,lowStep,tap_current\np1,PST,TSO,5,-5,0\n");
        files.put("ra_availability.csv", "pst_mrid,timestamp,available,optimize\np1,T1,true,true\n");
        files.put("pst_operational_limits.csv", "pst_mrid,timestamp,source_level,direction,valueKind,limit_value\np1,T1,online,up,absolute,3\np1,T1,online,down,absolute,-2\n");
        files.put("pst_groups.csv", "group_mrid,pst_mrid\n");
        files.put("pst_costs.csv", "pst_mrid,RAPenaltyFactor,maxTapsOT\np1,2,4\n");
        files.put("tap_schedule.csv", "pst_mrid,timestamp,scheduled_tap\np1,T1,1\n");
        files.put("rao_control_params.csv", "param_name,param_value\noverload_penalty,1000\npreventive_tap_change_cost,1\nPSTlimitTS_penalty,2\nPSTlimitOT_penalty,3\npenaltyTechnical,4\n");
        files.put("rao_tso_limits.csv", "tso,timestamp,maxTapsTS_TSO\nTSO,T1,5\n");
        files.put("monitored_branches.csv", "branch_id,element_type,monitor,limit1_a,limit2_a,timestamp\nb1,line,true,100,120,T1\n");
        var snapshot = new RaoAnalysisSnapshot(Map.of("b1", 130.0), Map.of("b1", Map.of("PST", -20.0)));
        var request = new PstProfilePackageParser().parse("network", "T1", zip(files), snapshot);
        var pst = request.timeframes().getFirst().psts().getFirst();
        assertEquals(-2, pst.operationalMinTap()); assertEquals(3, pst.operationalMaxTap());
        assertEquals(100, request.timeframes().getFirst().monitoredBranches().getFirst().currentLimitA());
    }

    private static byte[] zip(Map<String,String> files) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) { for (var file : files.entrySet()) { zip.putNextEntry(new ZipEntry(file.getKey())); zip.write(file.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8)); zip.closeEntry(); } }
        return bytes.toByteArray();
    }
}
