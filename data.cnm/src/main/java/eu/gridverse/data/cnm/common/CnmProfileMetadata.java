package eu.gridverse.data.cnm.common;

import java.time.Instant;

public record CnmProfileMetadata(String fileName, Instant scenarioTime, TimeFrame timeFrame, String tso, ProfileFamily profileFamily, String version, String objectKey) {}
