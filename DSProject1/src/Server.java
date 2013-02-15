import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import DSProject.ServerRMI;


public class Server {

	public static void main(String[] args) throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.rmi.server.codebase", "file:./bin");
		System.setProperty("java.security.policy", "file:./policyfile");
		
		int serverRMIPort = Integer.valueOf(args[0]);
		
		InetAddress serverIp = getServerIP();
		@SuppressWarnings("unused")
		ServerRMI server = new ServerRMI(serverIp, serverRMIPort);	
	}
	
	protected static InetAddress getServerIP() {
		InetAddress serverIp = null;
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface
					.getNetworkInterfaces();

			while (nets.hasMoreElements()) {
				NetworkInterface ni = nets.nextElement();
				if (!ni.isLoopback() && ni.isUp()) {
					serverIp = ni.getInetAddresses().nextElement();
					break;
				}
			}
			System.setProperty("java.rmi.server.hostname",serverIp.getHostAddress());
			

		} catch (SocketException e) {
			System.out.println("ERROR getting the interfaces of the device");
			e.printStackTrace();
		}
		return serverIp;
	}

}
