package co.edu.escuelaing.webframework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaticFileServiceTest {

    private final StaticFileService service =
            new StaticFileService("/webroot");

    @Test
    void shouldFindIndexHtml() {
        var result = service.find("/index.html");

        assertTrue(result.isPresent());
        assertEquals("text/html", result.get().contentType());
    }

    @Test
    void shouldFindPngImage() {
        var result = service.find("/images/logo.png");

        assertTrue(result.isPresent());
        assertEquals("image/png", result.get().contentType());
        assertTrue(result.get().content().length > 0);
    }

    @Test
    void shouldReturnEmptyForNonExistentFile() {
        var result = service.find("/no-existe.html");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRejectParentDirectoryTraversal() {
        var result = service.find("/../pom.xml");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldServeIndexHtmlForRootPath() {
        var rootResult = service.find("/");
        var indexResult = service.find("/index.html");

        assertTrue(rootResult.isPresent());
        assertTrue(indexResult.isPresent());

        assertEquals("text/html", rootResult.get().contentType());

        assertArrayEquals(
                indexResult.get().content(),
                rootResult.get().content()
        );
    }

    @Test
    void shouldRejectImagesDirectory() {
        var result = service.find("/images");

        assertTrue(result.isEmpty());
    }
}