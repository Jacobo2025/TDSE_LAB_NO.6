package co.edu.escuelaing.webframework;

@FunctionalInterface
public interface WebService {
    String handle(Request request, Response response);
}
