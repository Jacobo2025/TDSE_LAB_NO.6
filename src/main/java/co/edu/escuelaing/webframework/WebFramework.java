package co.edu.escuelaing.webframework;

import java.io.IOException;

public class WebFramework {

    private static final Router router = new Router();
    private static StaticFileService staticFileService;
    private static HttpServer server;

    private WebFramework() {
    }

    public static void get(String path, WebService handler) {
        router.register("GET", path, handler);
    }

    public static void staticfiles(String root) {
        staticFileService = new StaticFileService(root);
    }

    public static void start() throws IOException {
        start(defaultPort());
    }

    public static void start(int port) throws IOException {

        if (staticFileService == null) {
            staticFileService = new StaticFileService("/webroot");
        }

        server = new HttpServer(router, staticFileService);
        server.start(port);
    }

    public static void stop() {

        if (server != null) {
            server.stop();
        }
    }

    private static int defaultPort() {
        String portValue = System.getenv("PORT");
        return (portValue == null || portValue.isBlank())
                ? 8080
                : Integer.parseInt(portValue);
    }
}