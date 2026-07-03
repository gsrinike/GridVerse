package eu.gridverse.srv.cnm.services.rdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class SafeArchiveExpander {
    public static final long MAX_FILE_BYTES = 1_073_741_824L;
    private static final int MAX_DEPTH = 4;
    private static final long MAX_EXPANDED_BYTES = 2L * MAX_FILE_BYTES;

    public List<Payload> expand(String fileName, byte[] content) {
        if (content.length > MAX_FILE_BYTES) throw new IllegalArgumentException("file exceeds 1 GB");
        List<Payload> payloads = new ArrayList<>();
        expand(fileName, content, 0, payloads, new long[] {0});
        return List.copyOf(payloads);
    }

    private void expand(String name, byte[] content, int depth, List<Payload> result, long[] expanded) {
        validateName(name);
        if (!name.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            if (isPayload(name)) result.add(new Payload(name, content));
            return;
        }
        if (depth >= MAX_DEPTH) throw new IllegalArgumentException("nested archive depth exceeded");
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(content))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory() || isMetadata(entry.getName())) continue;
                validateName(entry.getName());
                byte[] bytes = readBounded(zip, MAX_FILE_BYTES);
                expanded[0] += bytes.length;
                if (expanded[0] > MAX_EXPANDED_BYTES) throw new IllegalArgumentException("expanded archive size exceeded");
                expand(entry.getName(), bytes, depth + 1, result, expanded);
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("invalid archive: " + name, exception);
        }
    }

    private static byte[] readBounded(ZipInputStream input, long limit) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192]; int read;
        while ((read = input.read(buffer)) >= 0) {
            if ((long) output.size() + read > limit) throw new IllegalArgumentException("archive entry exceeds 1 GB");
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private static void validateName(String name) {
        String normalized = name.replace('\\', '/');
        if (normalized.startsWith("/") || normalized.contains("../") || normalized.equals("..")) throw new IllegalArgumentException("unsafe archive entry");
    }
    private static boolean isMetadata(String name) { return name.startsWith("__MACOSX/") || name.endsWith(".DS_Store"); }
    private static boolean isPayload(String name) { String lower = name.toLowerCase(Locale.ROOT); return lower.endsWith(".xml") || lower.endsWith(".rdf"); }
    public record Payload(String name, byte[] content) { public Payload { content = content.clone(); } }
}
