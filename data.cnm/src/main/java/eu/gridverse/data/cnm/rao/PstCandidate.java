package eu.gridverse.data.cnm.rao;

public record PstCandidate(String id, String label, String tso, int currentTap, int scheduledTap, double scheduledMovement, int operationalMinTap, int operationalMaxTap, int technicalMinTap, int technicalMaxTap, double costWeight, Double operationTimeMovementLimit, boolean controllable) {}
