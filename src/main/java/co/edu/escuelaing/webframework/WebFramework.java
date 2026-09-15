package src.main.java.co.edu.escuelaing.webframework;

import java.util.HashMap;
import java.util.Map;

public class WebFramework {

    private static Map<String, WebService> webServices = new HashMap<>();

    public static void get(String route, WebService ws){
        webServices.put(route, ws);
    }

    public static String invoke(String route){
        WebService ws = webServices.get(route);
        return ws.call();
    }

    public static boolean isImplemented(String route){
        WebService ws = webServices.get(route);
        return ws != null;
    }

    public static void start(){
        String[] args = {};
        
        
    }
}
