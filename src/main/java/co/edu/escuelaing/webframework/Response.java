package co.edu.escuelaing.webframework;

public class Response {
    private int status = 200;
    private String contentType = "text/plain";



    public String getContentType() {
        return contentType;
    }

    public int getStatus() {
        return status;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
