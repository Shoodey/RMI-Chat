package helpers;

import java.util.ResourceBundle;

public class Config {
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("helpers/config");
    public final static String SERVER_IP = resourceBundle.getString("server_ip");
    public final static int PORT = Integer.parseInt(resourceBundle.getString("rmi_port"));
    public final static String SERVER_NAME = resourceBundle.getString("server_name");
    public final static String PROXIES = resourceBundle.getString("proxies");
}
