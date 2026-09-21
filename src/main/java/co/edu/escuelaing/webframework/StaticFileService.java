package co.edu.escuelaing.webframework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class StaticFileService {

    private final String root;

    private static final Map<String, String> CONTENT_TYPES = Map.ofEntries(
            Map.entry("html", "text/html"),
            Map.entry("css", "text/css"),
            Map.entry("js", "text/javascript"),
            Map.entry("png", "image/png"),
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("gif", "image/gif"),
            Map.entry("svg", "image/svg+xml"),
            Map.entry("ico", "image/x-icon"),
            Map.entry("json", "application/json"),
            Map.entry("txt", "text/plain")
    );

    public StaticFileService(String root) {
        this.root = root.endsWith("/")
                ? root.substring(0, root.length() - 1)
                : root;
    }

    public Optional<StaticFile> find(String path) {

        // Evitar directory traversal
        if (path.contains("..")) {
            return Optional.empty();
        }

        // "/" apunta a index.html
        if (path.equals("/")) {
            path = "/index.html";
        }

        // Rechazar paths sin extensión
        if (path.lastIndexOf(".") == -1) {
            return Optional.empty();
        }

        String resourcePath = root + path;

        try (InputStream in =
                     StaticFileService.class.getResourceAsStream(resourcePath)) {

            if (in == null) {
                return Optional.empty();
            }

            byte[] content = in.readAllBytes();
            String contentType = resolveContentType(path);

            return Optional.of(new StaticFile(content, contentType));

        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static String resolveContentType(String path) {
        int dotIndex = path.lastIndexOf(".");

        if (dotIndex == -1) {
            return "application/octet-stream";
        }

        String extension = path.substring(dotIndex + 1);

        return CONTENT_TYPES.getOrDefault(
                extension,
                "application/octet-stream"
        );
    }
}