package eu.gridverse.solver.rao;

import eu.gridverse.data.cnm.rao.MonitoredBranch;
import eu.gridverse.data.cnm.rao.PstCandidate;
import eu.gridverse.data.cnm.rao.RemedialActionRequest;
import eu.gridverse.data.cnm.rao.RaoTimeframeInput;
import eu.gridverse.solver.model.NeutralOptimizationProblem;
import eu.gridverse.solver.model.NeutralOptimizationProblem.Constraint;
import eu.gridverse.solver.model.NeutralOptimizationProblem.Relation;
import eu.gridverse.solver.model.NeutralOptimizationProblem.Variable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PreventiveRaoProblemMapper {
    public NeutralOptimizationProblem map(RemedialActionRequest request) {
        validate(request);
        List<Variable> variables = new ArrayList<>();
        List<Constraint> constraints = new ArrayList<>();
        Map<String, Double> objective = new LinkedHashMap<>();
        var parameters = request.parameters();

        for (RaoTimeframeInput timeframe : request.timeframes()) {
            for (PstCandidate pst : timeframe.psts()) {
                String tap = tap(timeframe.timeframe(), pst.label());
                double lower = parameters.technicalLimitPenalty() > 0 ? pst.technicalMinTap() : pst.operationalMinTap();
                double upper = parameters.technicalLimitPenalty() > 0 ? pst.technicalMaxTap() : pst.operationalMaxTap();
                variables.add(new Variable(tap, lower, upper, true));
                variables.add(nonNegative(change(timeframe.timeframe(), pst.label())));
                variables.add(nonNegative(movement(timeframe.timeframe(), pst.label())));
                objective.put(change(timeframe.timeframe(), pst.label()), parameters.preventiveTapChangeCost() * pst.costWeight());

                constraints.add(constraint("tap_change_pos", timeframe.timeframe(), pst.label(), map(tap, -1, change(timeframe.timeframe(), pst.label()), 1), Relation.GREATER_OR_EQUAL, -pst.currentTap()));
                constraints.add(constraint("tap_change_neg", timeframe.timeframe(), pst.label(), map(tap, 1, change(timeframe.timeframe(), pst.label()), 1), Relation.GREATER_OR_EQUAL, pst.currentTap()));
                double scheduledOffset = pst.scheduledMovement() - pst.scheduledTap();
                constraints.add(constraint("movement_pos", timeframe.timeframe(), pst.label(), map(tap, -1, movement(timeframe.timeframe(), pst.label()), 1), Relation.GREATER_OR_EQUAL, scheduledOffset));
                constraints.add(constraint("movement_neg", timeframe.timeframe(), pst.label(), map(tap, 1, movement(timeframe.timeframe(), pst.label()), 1), Relation.GREATER_OR_EQUAL, -scheduledOffset));

                if (parameters.technicalLimitPenalty() > 0) {
                    String below = "technical_lower_slack__" + safe(timeframe.timeframe()) + "__" + safe(pst.label());
                    String above = "technical_upper_slack__" + safe(timeframe.timeframe()) + "__" + safe(pst.label());
                    variables.add(nonNegative(below)); variables.add(nonNegative(above));
                    objective.put(below, parameters.technicalLimitPenalty()); objective.put(above, parameters.technicalLimitPenalty());
                    constraints.add(constraint("operational_lower", timeframe.timeframe(), pst.label(), map(tap, 1, below, 1), Relation.GREATER_OR_EQUAL, pst.operationalMinTap()));
                    constraints.add(constraint("operational_upper", timeframe.timeframe(), pst.label(), map(tap, 1, above, -1), Relation.LESS_OR_EQUAL, pst.operationalMaxTap()));
                }
            }

            for (MonitoredBranch branch : timeframe.monitoredBranches()) {
                String flow = flow(timeframe.timeframe(), branch.label()); String overload = overload(timeframe.timeframe(), branch.label());
                variables.add(new Variable(flow, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false)); variables.add(nonNegative(overload));
                objective.put(overload, parameters.overloadPenalty());
                Map<String, Double> equation = new LinkedHashMap<>(); equation.put(flow, 1.0);
                double rhs = branch.baseCurrentA();
                for (PstCandidate pst : timeframe.psts()) {
                    double sensitivity = branch.currentSensitivityByPst().getOrDefault(pst.label(), 0.0);
                    equation.put(tap(timeframe.timeframe(), pst.label()), -sensitivity); rhs -= sensitivity * pst.currentTap();
                }
                constraints.add(constraint("flow", timeframe.timeframe(), branch.label(), equation, Relation.EQUAL, rhs));
                constraints.add(constraint("overload_upper", timeframe.timeframe(), branch.label(), map(overload, 1, flow, -1), Relation.GREATER_OR_EQUAL, -branch.currentLimitA()));
                constraints.add(constraint("overload_lower", timeframe.timeframe(), branch.label(), map(overload, 1, flow, 1), Relation.GREATER_OR_EQUAL, -branch.currentLimitA()));
            }

            for (var pair : request.equalityGroups()) constraints.add(constraint("symmetry", timeframe.timeframe(), pair.memberPstLabel(), map(tap(timeframe.timeframe(), pair.memberPstLabel()), 1, tap(timeframe.timeframe(), pair.anchorPstLabel()), -1), Relation.EQUAL, 0));
            for (var limit : timeframe.tsoMovementLimits().entrySet()) {
                String slack = "tso_slack__" + safe(timeframe.timeframe()) + "__" + safe(limit.getKey()); variables.add(nonNegative(slack)); objective.put(slack, parameters.tsoMovementPenalty());
                Map<String, Double> coefficients = new LinkedHashMap<>();
                timeframe.psts().stream().filter(p -> limit.getKey().equals(p.tso())).forEach(p -> coefficients.put(movement(timeframe.timeframe(), p.label()), 1.0));
                Map<String, Double> soft = new LinkedHashMap<>(coefficients); soft.put(slack, -1.0);
                constraints.add(constraint("tso_soft", timeframe.timeframe(), limit.getKey(), soft, Relation.LESS_OR_EQUAL, limit.getValue()));
                constraints.add(constraint("tso_hard", timeframe.timeframe(), limit.getKey(), coefficients, Relation.LESS_OR_EQUAL, limit.getValue()));
            }
        }

        Map<String, PstCandidate> firstPsts = request.timeframes().getFirst().psts().stream().collect(java.util.stream.Collectors.toMap(PstCandidate::label, p -> p));
        for (PstCandidate pst : firstPsts.values()) if (pst.operationTimeMovementLimit() != null) {
            String slack = "ot_slack__" + safe(pst.label()); variables.add(nonNegative(slack)); objective.put(slack, parameters.operationTimeMovementPenalty());
            Map<String, Double> coefficients = new LinkedHashMap<>(); request.timeframes().forEach(tf -> coefficients.put(movement(tf.timeframe(), pst.label()), 1.0)); coefficients.put(slack, -1.0);
            constraints.add(new Constraint("ot_soft__" + safe(pst.label()), coefficients, Relation.LESS_OR_EQUAL, pst.operationTimeMovementLimit()));
        }
        return new NeutralOptimizationProblem(List.copyOf(variables), List.copyOf(constraints), Map.copyOf(objective));
    }

    private static void validate(RemedialActionRequest request) {
        List<String> labels = request.timeframes().getFirst().psts().stream().filter(PstCandidate::controllable).map(PstCandidate::label).toList();
        if (labels.isEmpty()) throw new IllegalArgumentException("no controllable PSTs");
        for (RaoTimeframeInput tf : request.timeframes()) {
            if (tf.monitoredBranches().isEmpty()) throw new IllegalArgumentException("no monitored branches for " + tf.timeframe());
            if (!tf.psts().stream().filter(PstCandidate::controllable).map(PstCandidate::label).toList().equals(labels)) throw new IllegalArgumentException("timeframes must expose the same controllable PST order");
            for (PstCandidate pst : tf.psts()) if (pst.operationalMinTap() > pst.operationalMaxTap() || pst.technicalMinTap() > pst.technicalMaxTap()) throw new IllegalArgumentException("invalid tap bounds for " + pst.label());
            for (MonitoredBranch branch : tf.monitoredBranches()) if (!Double.isFinite(branch.baseCurrentA()) || !Double.isFinite(branch.currentLimitA()) || branch.currentLimitA() <= 0) throw new IllegalArgumentException("invalid monitored branch " + branch.label());
        }
    }
    private static Variable nonNegative(String name) { return new Variable(name, 0, Double.POSITIVE_INFINITY, false); }
    private static Map<String, Double> map(String a, double av, String b, double bv) { Map<String, Double> result = new LinkedHashMap<>(); result.put(a, av); result.put(b, bv); return result; }
    private static Constraint constraint(String kind, String scope, String id, Map<String, Double> c, Relation r, double rhs) { return new Constraint(kind + "__" + safe(scope) + "__" + safe(id), c, r, rhs); }
    private static String tap(String tf, String id) { return "tap__" + safe(tf) + "__" + safe(id); }
    private static String change(String tf, String id) { return "tap_change__" + safe(tf) + "__" + safe(id); }
    private static String movement(String tf, String id) { return "movement__" + safe(tf) + "__" + safe(id); }
    private static String flow(String tf, String id) { return "flow__" + safe(tf) + "__" + safe(id); }
    private static String overload(String tf, String id) { return "overload__" + safe(tf) + "__" + safe(id); }
    private static String safe(String value) { return value.replaceAll("[^A-Za-z0-9_]", "_"); }
}
