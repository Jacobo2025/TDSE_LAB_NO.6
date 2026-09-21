package co.edu.escuelaing.webframework;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {

    private static final int PORT = 8099;
    private static HttpServer server;
    private static Thread serverThread;

    @BeforeAll
    static void startServer() throws InterruptedException {
        Router router = new Router();

        router.register("GET", "/hello", (req, resp) -> {
            String name = req.getValue("name");
            return "Hello " + (name == null || name.isBlank() ? "world" : name);
        });

        router.register("GET", "/boom", (req, resp) -> {
            throw new RuntimeException("simulated failure");
        });

        StaticFileService staticFileService = new StaticFileService("/webroot");
        server = new HttpServer(router, staticFileService);

        serverThread = new Thread(() -> {
            try {
                server.start(PORT);
            } catch (IOException ignored) {
                // Se espera al cerrar el ServerSocket en stop()
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Pequeña espera para que el ServerSocket esté escuchando antes de los tests
        Thread.sleep(300);
    }

    @AfterAll
    static void stopServer() throws InterruptedException {
        server.stop();
        serverThread.join(1000);
    }

    @Test
    void shouldReturnDynamicResponseWithQueryParam() throws IOException {
        HttpURLConnection conn = get("/hello?name=Pedro");

        assertEquals(200, conn.getResponseCode());
        assertEquals("Hello Pedro", readBody(conn));
    }

    @Test
    void shouldReturnDefaultValueWhenParamMissing() throws IOException {
        HttpURLConnection conn = get("/hello");

        assertEquals(200, conn.getResponseCode());
        assertEquals("Hello world", readBody(conn));
    }

    @Test
    void shouldServeStaticFile() throws IOException {
        HttpURLConnection conn = get("/index.html");

        assertEquals(200, conn.getResponseCode());
        assertEquals("text/html", conn.getContentType());
        assertTrue(readBody(conn).contains("<html"));
    }

    @Test
    void shouldReturn404ForUnknownResource() throws IOException {
        HttpURLConnection conn = get("/no-existe");

        assertEquals(404, conn.getResponseCode());
    }

    @Test
    void shouldReturn405ForNonGetMethod() throws IOException {
        URL url = new URL("http://localhost:" + PORT + "/hello");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        assertEquals(405, conn.getResponseCode());
    }

    @Test
    void shouldReturn500WhenHandlerThrows() throws IOException {
        HttpURLConnection conn = get("/boom");

        assertEquals(500, conn.getResponseCode());
    }

    private HttpURLConnection get(String path) throws IOException {
        URL url = new URL("http://localhost:" + PORT + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return conn;
    }

    private String readBody(HttpURLConnection conn) throws IOException {
        var stream = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();

        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
}