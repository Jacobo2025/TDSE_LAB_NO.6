package co.edu.escuelaing.webframework;

public class Route {
    private final String method;
    private final String path;
    private final WebService webService;

    public Route(String method, String path, WebService webService) {
        this.method = method;
        this.path = path;
        this.webService = webService;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public WebService getWebService() {
        return webService;
    }

    public boolean matches(String method, String path){
        return this.method.equals(method) && this.path.equals(path);
    }
}
