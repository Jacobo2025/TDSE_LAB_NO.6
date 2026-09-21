package co.edu.escuelaing.app;

import static co.edu.escuelaing.webframework.WebFramework.*;

public class Application {

    public static void main(String[] args) throws Exception {

        staticfiles("/webroot");

        get("/hello", (req, resp) -> {
            String name = req.getValue("name");
            return "Hello " + (name == null || name.isBlank() ? "world" : name);
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

        start();
    }
}