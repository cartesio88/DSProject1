import java.net.InetAddress;


public interface Constants {
	// Registry-server constants
	final String registryServerName = "dio.cs.umn.edu";
	final int registryServerPort = 5105;
	
	// Server constants
	final int MAXCLIENTS = 5;
	final int serverPort = 5105;
	final String serverIp = "134.84.74.49";
	final String serverName = "rmi://"+serverIp+"/RomanCesarServer";
}
