package eu.gridverse.data.cnm.rao;

public record RaoParameters(double overloadPenalty, double preventiveTapChangeCost, double tsoMovementPenalty, double operationTimeMovementPenalty, double technicalLimitPenalty) {
    public RaoParameters { if (overloadPenalty <= 0 || preventiveTapChangeCost < 0 || tsoMovementPenalty < 0 || operationTimeMovementPenalty < 0 || technicalLimitPenalty < 0) throw new IllegalArgumentException("invalid RAO penalty"); }
}
