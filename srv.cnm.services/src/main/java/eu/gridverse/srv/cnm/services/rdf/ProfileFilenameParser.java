package eu.gridverse.srv.cnm.services.rdf;

import eu.gridverse.data.cnm.common.CnmProfileMetadata;
import eu.gridverse.data.cnm.common.ProfileFamily;
import eu.gridverse.data.cnm.common.TimeFrame;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ProfileFilenameParser {
    private static final Pattern PATTERN = Pattern.compile("^(?<time>\\d{8}T\\d{4}Z)_(?<frame>[^_]+)_(?<tso>[^_]+)_(?<profile>[^_]+)_(?<version>[^.]+).*$");
    public Optional<CnmProfileMetadata> parse(String fileName, String objectKey) {
        var matcher = PATTERN.matcher(fileName);
        if (!matcher.matches()) return Optional.empty();
        try {
            Instant time = Instant.parse(matcher.group("time").replaceFirst("(\\d{4})(\\d{2})(\\d{2})T(\\d{2})(\\d{2})Z", "$1-$2-$3T$4:$5:00Z"));
            TimeFrame frame = parseFrame(matcher.group("frame"));
            ProfileFamily family = parseFamily(matcher.group("profile"));
            return Optional.of(new CnmProfileMetadata(fileName, time, frame, matcher.group("tso"), family, matcher.group("version"), objectKey));
        } catch (DateTimeParseException ignored) { return Optional.empty(); }
    }
    private static TimeFrame parseFrame(String value) {
        return switch (value.toUpperCase(Locale.ROOT)) { case "ID" -> TimeFrame.INTRADAY; case "1D", "DA" -> TimeFrame.DAY_AHEAD; case "2D" -> TimeFrame.TWO_DAYS_AHEAD; default -> TimeFrame.DAY_AHEAD; };
    }
    private static ProfileFamily parseFamily(String value) {
        try { return ProfileFamily.valueOf(value.toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException ignored) { return ProfileFamily.UNKNOWN; }
    }
}
