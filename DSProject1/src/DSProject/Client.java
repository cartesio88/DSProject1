package DSProject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.Scanner;

public class Client implements Constants {

	private static int udpPort = 3333;
	private static InetAddress clientIp = null;

	public static void main(String[] args) throws InterruptedException,
			RemoteException, NotBoundException {
		System.out.println("Starting the Client");
		System.setProperty("java.net.preferIPv4Stack", "true");

		String serverIp;
		Integer Port = null;
		String Article = null;
		boolean done = false;
		
		getClientIP();

		Scanner scan = new Scanner(System.in);
		System.out.println("Enter Server' IP you want to join: ");
		serverIp = scan.nextLine();

		System.out.println("Enter port: ");
		Port = Integer.valueOf(scan.nextLine());

		Registry registry = LocateRegistry.getRegistry(serverIp);
		Communicate server = (Communicate) registry.lookup(serverName);

		while (!done) {

			System.out.println("Choose the option: \n" + "1) Join\n"
					+ "2) Subscribe\n" + "3) Publish\n" + "4) Unsubscribe\n"
					+ "5) Ping\n" + "6) Exit\n");

			String Choice = scan.nextLine();
			Integer Option = Integer.valueOf(Choice);

			switch (Option) {
			case 1:
				if (server.Join(clientIp.getHostAddress(), udpPort))
					System.out.println("Joined successfully!");
				else
					System.out.println("ERROR joining");
				break;

			case 2:

				System.out.println("Enter Article:");
				Article = scan.nextLine();
				if (server.Subscribe(clientIp.getHostAddress(), udpPort, Article))
					System.out.println("Subscribed successfully!");
				else
					System.out.println("ERROR subscribing");
				break;

			case 3:

				System.out.println("Enter Article:");
				Article = scan.nextLine();
				if (server.Publish(Article, clientIp.getHostAddress(), udpPort)) {
					System.out.println("Published successfully!");
				} else {
					System.out.println("ERROR publishing");
				}
				break;

			case 4:

				System.out.println("Enter Article:");
				Article = scan.nextLine();
				if (server.Unsubscribe(clientIp.getHostAddress(), udpPort, Article))
					System.out.println("Unsubscribed successfully!");
				else
					System.out.println("ERROR unsubscribing");
				break;
			case 5:
				if (server.Ping()) {
					System.out.println("Server OK!");
				} else {
					System.out.println("Server DOWN!");
				}
				break;
			case 6:

				System.exit(0);
				break;

			}
		}

		
	}
	
	
	private static void getClientIP() {
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface
					.getNetworkInterfaces();

			while (nets.hasMoreElements()) {
				NetworkInterface ni = nets.nextElement();
				if (!ni.isLoopback() && ni.isUp()) {
					clientIp = ni.getInetAddresses().nextElement();
					break;
				}
			}
			System.setProperty("java.rmi.server.hostname",clientIp.getHostAddress());
			
			System.out.println("El valor de la ip es:"
					+ System.getProperty("java.rmi.server.hostname"));

		} catch (SocketException e) {
			System.out.println("ERROR getting the interfaces of the device");
			e.printStackTrace();
		}
	}
}