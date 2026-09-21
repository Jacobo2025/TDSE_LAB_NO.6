package co.edu.escuelaing.webframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Router {

    private final List<Route> routes = new ArrayList<>();

    public void register(String method, String path, WebService handler) {
        if (resolve(method, path).isPresent()) {
            throw new IllegalStateException(
                    "Route already registered: " + method + " " + path
            );
        }

        routes.add(new Route(method, path, handler));
    }

    public Optional<WebService> resolve(String method, String path) {
        for (Route route : routes) {
            if (route.matches(method, path)) {
                return Optional.of(route.getWebService());
            }
        }

        return Optional.empty();
    }
}

