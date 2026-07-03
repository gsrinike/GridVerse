package eu.gridverse.srv.cnm.services.rdf;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
class SafeArchiveExpanderTest {
    @Test void expandsRdfAndIgnoresMetadata() throws Exception {
        var output = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(output)) {
            zip.putNextEntry(new ZipEntry("20250101T0000Z_1D_TSO_EQUIPMENT_v1.xml")); zip.write("<rdf/>".getBytes()); zip.closeEntry();
            zip.putNextEntry(new ZipEntry("__MACOSX/.DS_Store")); zip.write("x".getBytes()); zip.closeEntry();
        }
        var payloads = new SafeArchiveExpander().expand("profiles.zip", output.toByteArray());
        assertEquals(1, payloads.size()); assertEquals("<rdf/>", new String(payloads.getFirst().content()));
    }
    @Test void rejectsTraversal() throws Exception {
        var output = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(output)) { zip.putNextEntry(new ZipEntry("../escape.xml")); zip.write("x".getBytes()); }
        assertThrows(IllegalArgumentException.class, () -> new SafeArchiveExpander().expand("bad.zip", output.toByteArray()));
    }
}
