package co.edu.escuelaing.webframework;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RouterTest {

    @Test
    void shouldRegisterAndResolveRoute() {
        Router router = new Router();

        WebService handler = (request, response) -> "Hello";

        router.register("GET", "/hello", handler);

        Optional<WebService> result = router.resolve("GET", "/hello");

        assertTrue(result.isPresent());
        assertEquals(handler, result.get());
    }

    @Test
    void shouldExecuteResolvedHandler() {
        Router router = new Router();

        WebService handler = (request, response) -> "Hello";

        router.register("GET", "/hello", handler);

        WebService resolved = router.resolve("GET", "/hello").get();

        String result = resolved.handle(
                new Request("GET", "/hello"),
                new Response()
        );

        assertEquals("Hello", result);
    }

    @Test
    void shouldReturnEmptyWhenRouteDoesNotExist() {
        Router router = new Router();

        Optional<WebService> result = router.resolve("GET", "/hello");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenRouteAlreadyExists() {
        Router router = new Router();

        WebService handler = (request, response) -> "Hello";

        router.register("GET", "/hello", handler);

        assertThrows(
                IllegalStateException.class,
                () -> router.register("GET", "/hello", handler)
        );
    }

    @Test
    void shouldAllowSamePathWithDifferentMethods() {
        Router router = new Router();

        WebService getHandler = (request, response) -> "GET";
        WebService postHandler = (request, response) -> "POST";

        router.register("GET", "/hello", getHandler);
        router.register("POST", "/hello", postHandler);

        assertEquals(
                getHandler,
                router.resolve("GET", "/hello").get()
        );

        assertEquals(
                postHandler,
                router.resolve("POST", "/hello").get()
        );
    }

    @Test
    void shouldReturnEmptyForTrailingSlash() {
        Router router = new Router();

        WebService handler = (request, response) -> "Hello";

        router.register("GET", "/hello", handler);

        assertTrue(
                router.resolve("GET", "/hello/").isEmpty()
        );
    }

    @Test
    void shouldReturnEmptyForDifferentMethod() {
        Router router = new Router();

        WebService handler = (request, response) -> "Hello";

        router.register("GET", "/hello", handler);

        assertTrue(
                router.resolve("POST", "/hello").isEmpty()
        );
    }
}

