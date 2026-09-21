
package co.edu.escuelaing.webframework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTest {

    @Test
    void shouldParseMethod() {
        Request request = new Request("GET", "/users");

        assertEquals("GET", request.getMethod());
    }

    @Test
    void shouldParsePath() {
        Request request = new Request("GET", "/users?id=25");

        assertEquals("/users", request.getPath());
    }

    @Test
    void shouldParseQueryParameter() {
        Request request = new Request("GET", "/users?id=25");

        assertEquals("25", request.getValue("id"));
    }

    @Test
    void shouldReturnNullForMissingParameter() {
        Request request = new Request("GET", "/users?id=25");

        assertNull(request.getValue("name"));
    }

    @Test
    void shouldHandleRequestWithoutQueryParameters() {
        Request request = new Request("GET", "/users");

        assertEquals("/users", request.getPath());
        assertNull(request.getValue("id"));
    }

    @Test
    void shouldParseMultipleQueryParameters() {
        Request request = new Request(
                "GET",
                "/users?id=25&name=Bob"
        );

        assertEquals("25", request.getValue("id"));
        assertEquals("Bob", request.getValue("name"));
    }

    @Test
    void shouldDecodeQueryParameters() {
        Request request = new Request(
                "GET",
                "/users?name=Bob%20Dylan"
        );

        assertEquals("Bob Dylan", request.getValue("name"));
    }

    @Test
    void shouldThrowExceptionForInvalidEncoding() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Request("GET", "/users?name=%")
        );
    }

    @Test
    void shouldReturnEmptyValueForFlagParameter() {
        Request request = new Request("GET", "/users?flag");

        assertEquals("", request.getValue("flag"));
    }

    @Test
    void shouldIgnoreEmptyQueryParameters() {
        Request request = new Request("GET", "/users?a=1&&b=2");

        assertEquals("1", request.getValue("a"));
        assertEquals("2", request.getValue("b"));
    }
}

