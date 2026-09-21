package co.edu.escuelaing.app;

import static co.edu.escuelaing.webframework.WebFramework.*;

public class Application {

    public static void main(String[] args) throws Exception {

        staticfiles("/webroot");

        get("/hello", (req, resp) -> {
            String name = req.getValue("name");
            if (name == null || name.isBlank()) {
                name = "world";
            }

            String greetingPrefix = System.getenv()
                    .getOrDefault("GREETING_PREFIX", "Hello");

            return greetingPrefix + " " + name;
        });

        get("/square", (req, resp) -> {
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

        // /shutdown solo disponible en desarrollo, nunca en producción.
        String environment = System.getenv()
                .getOrDefault("APP_ENV", "development");

        if (!environment.equals("production")) {
            get("/shutdown", (req, resp) -> {
                stop();
                return "Server will stop after this response.";
            });
        }

        start();
    }
}