package co.edu.escuelaing.app;

import co.edu.escuelaing.webframework.HttpServer;
import co.edu.escuelaing.webframework.Router;
import co.edu.escuelaing.webframework.StaticFileService;

public class Application {

    public static void main(String[] args) throws Exception {

        Router router = new Router();

        router.register("GET", "/hello", (req, resp) -> {
            String name = req.getValue("name");
            return "Hello " + (name == null || name.isBlank() ? "world" : name);
        });

        router.register("GET", "/square", (req, resp) -> {
            String valueStr = req.getValue("value");
            double value;

            try {
                value = Double.parseDouble(valueStr);
            } catch (Exception e) {
                resp.setStatus(400);
                resp.setContentType("application/json");
                return "{\"error\":\"invalid or missing 'value' parameter\"}";
            }

            resp.setContentType("application/json");
            double square = value * value;
            return "{\"input\":" + value + ",\"square\":" + square + "}";
        });

        StaticFileService staticFileService = new StaticFileService("/webroot");
        HttpServer server = new HttpServer(router, staticFileService);

        server.start(8080);
    }
}