package co.edu.escuelaing.webframework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpServer {

    private final Router router;
    private final StaticFileService staticFileService;

    private boolean running;
    private ServerSocket serverSocket;

    public HttpServer(
            Router router,
            StaticFileService staticFileService
    ) {
        this.router = router;
        this.staticFileService = staticFileService;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;

        try {
            while (running) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleConnection(clientSocket);
                }
            }
        } finally {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }

    public void stop() {
        running = false;
    }

    private void handleConnection(Socket clientSocket) {

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );
                OutputStream out = clientSocket.getOutputStream()
        ) {

            // 1. Leer la primera línea
            String requestLine = in.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                sendError(out, 400, "Bad Request");
                return;
            }

            // 2. Parsear method y rawTarget
            String[] parts = requestLine.split(" ");

            if (parts.length < 2) {
                sendError(out, 400, "Bad Request");
                return;
            }

            String method = parts[0];
            String rawTarget = parts[1];

            if (!method.equals("GET")) {
                sendError(out, 405, "Method Not Allowed");
                return;
            }

            // 3. Crear Request
            Request request;

            try {
                request = new Request(method, rawTarget);
            } catch (IllegalArgumentException e) {
                sendError(out, 400, "Bad Request");
                return;
            }

            // 4. Buscar ruta dinámica
            Optional<WebService> handler =
                    router.resolve(
                            request.getMethod(),
                            request.getPath()
                    );

            if (handler.isPresent()) {

                Response response = new Response();

                try {
                    String body = handler.get().handle(
                            request,
                            response
                    );

                    sendResponse(
                            out,
                            response.getStatus(),
                            response.getContentType(),
                            body
                    );

                } catch (Exception e) {
                    sendError(
                            out,
                            500,
                            "Internal Server Error"
                    );
                }

                return;
            }

            // 4b. Buscar archivo estático
            Optional<StaticFile> staticFile =
                    staticFileService.find(request.getPath());

            if (staticFile.isPresent()) {

                StaticFile file = staticFile.get();

                sendResponse(
                        out,
                        200,
                        file.contentType(),
                        file.content()
                );

                return;
            }

            // 4c. No existe ruta ni archivo
            sendError(out, 404, "Not Found");

        } catch (IOException e) {
            // La conexión actual falló.
            // No dejamos que una conexión defectuosa
            // detenga todo el servidor.
        }
    }

    private void sendResponse(
            OutputStream out,
            int status,
            String contentType,
            String body
    ) throws IOException {

        byte[] content =
                body.getBytes(StandardCharsets.UTF_8);

        String response =
                "HTTP/1.1 " +
                        status +
                        " " +
                        statusText(status) +
                        "\r\n" +
                        "Content-Type: " +
                        contentType +
                        "\r\n" +
                        "Content-Length: " +
                        content.length +
                        "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

        out.write(
                response.getBytes(StandardCharsets.UTF_8)
        );

        out.write(content);
        out.flush();
    }

    private void sendResponse(
            OutputStream out,
            int status,
            String contentType,
            byte[] content
    ) throws IOException {

        String response =
                "HTTP/1.1 " +
                        status +
                        " " +
                        statusText(status) +
                        "\r\n" +
                        "Content-Type: " +
                        contentType +
                        "\r\n" +
                        "Content-Length: " +
                        content.length +
                        "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

        out.write(
                response.getBytes(StandardCharsets.UTF_8)
        );

        out.write(content);
        out.flush();
    }

    private void sendError(
            OutputStream out,
            int status,
            String message
    ) throws IOException {

        String body =
                "<html><body><h1>" +
                        status +
                        " " +
                        message +
                        "</h1></body></html>";

        sendResponse(
                out,
                status,
                "text/html",
                body
        );
    }

    private String statusText(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            default -> "";
        };
    }
}