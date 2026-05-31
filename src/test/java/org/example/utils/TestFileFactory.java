package org.example.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generates test files on the fly so that BVA/EP boundary data (especially the
 * oversized {@literal >}50 MB file) does not need to be committed to the repo.
 *
 * <p>Files are created under target/testdata so {@code mvn clean} removes them.
 */
public final class TestFileFactory {

    private static final Path OUT_DIR = Path.of("target", "testdata");

    /** ~50 MB threshold advertised by the upload modal. */
    private static final long MAX_BYTES = 50L * 1024 * 1024;

    private TestFileFactory() {
    }

    private static Path dir() {
        try {
            return Files.createDirectories(OUT_DIR);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create test data directory", e);
        }
    }

    private static Path write(String fileName, String content) {
        try {
            Path target = dir().resolve(fileName);
            Files.writeString(target, content, StandardCharsets.UTF_8);
            return target.toAbsolutePath();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write test file " + fileName, e);
        }
    }

    // ---- valid GeoJSON FeatureCollections -------------------------------

    /** Minimal valid FeatureCollection with a single LineString (pipe). */
    public static String validPipeFeatureCollection() {
        return """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {
                      "type": "Feature",
                      "properties": { "id": "QA_TEST_PIPE", "fungsi": "Lateral", "status": "baik" },
                      "geometry": {
                        "type": "LineString",
                        "coordinates": [[110.36, -7.78], [110.361, -7.781]]
                      }
                    }
                  ]
                }
                """;
    }

    /** Minimal valid FeatureCollection with a single Point (manhole). */
    public static String validManholeFeatureCollection() {
        return """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {
                      "type": "Feature",
                      "properties": { "id": "QA_TEST_MH", "status": "baik" },
                      "geometry": { "type": "Point", "coordinates": [110.36, -7.78] }
                    }
                  ]
                }
                """;
    }

    /** Valid JSON, but the top-level type is not FeatureCollection. */
    public static String notAFeatureCollection() {
        return """
                {
                  "type": "Feature",
                  "properties": {},
                  "geometry": { "type": "Point", "coordinates": [110.36, -7.78] }
                }
                """;
    }

    /** Syntactically broken JSON (missing closing brace / trailing comma). */
    public static String malformedJson() {
        return "{ \"type\": \"FeatureCollection\", \"features\": [ ";
    }

    // ---- file builders used by step definitions -------------------------

    /**
     * Returns the absolute path of a test file for the given case key.
     * Keys are referenced from the Gherkin Examples table.
     */
    public static String fileForCase(String caseKey) {
        return switch (caseKey) {
            case "valid_pipe_geojson" ->
                    write("valid_pipe.geojson", validPipeFeatureCollection()).toString();
            case "valid_pipe_json" ->
                    write("valid_pipe.json", validPipeFeatureCollection()).toString();
            case "valid_manhole_geojson" ->
                    write("valid_manhole.geojson", validManholeFeatureCollection()).toString();
            case "not_featurecollection" ->
                    write("not_featurecollection.geojson", notAFeatureCollection()).toString();
            case "malformed_json" ->
                    write("malformed.geojson", malformedJson()).toString();
            case "empty_file" ->
                    write("empty.geojson", "").toString();
            case "wrong_ext_txt" ->
                    write("network.txt", validPipeFeatureCollection()).toString();
            case "wrong_ext_csv" ->
                    write("network.csv", "id,fungsi,status\nQA1,Lateral,baik\n").toString();
            case "wrong_ext_png" ->
                    write("network.png", "not really a png").toString();
            case "oversized_geojson" -> createOversizedFile().toString();
            case "none" -> "";
            default -> throw new IllegalArgumentException("Unknown test file case: " + caseKey);
        };
    }

    /**
     * Creates a {@literal >}50 MB .geojson file by padding a valid FeatureCollection
     * with whitespace inside a string property, keeping it parseable JSON.
     */
    private static Path createOversizedFile() {
        try {
            Path target = dir().resolve("oversized.geojson");
            String head = "{\"type\":\"FeatureCollection\",\"_pad\":\"";
            String tail = "\",\"features\":[]}";
            long padding = MAX_BYTES + (1024 * 1024) - head.length() - tail.length();
            try (RandomAccessFile raf = new RandomAccessFile(target.toFile(), "rw")) {
                raf.setLength(0);
                raf.writeBytes(head);
                byte[] chunk = new byte[1024 * 1024];
                java.util.Arrays.fill(chunk, (byte) 'A');
                long written = 0;
                while (written < padding) {
                    int len = (int) Math.min(chunk.length, padding - written);
                    raf.write(chunk, 0, len);
                    written += len;
                }
                raf.writeBytes(tail);
            }
            return target.toAbsolutePath();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create oversized test file", e);
        }
    }
}
