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
import java.util.regex.Pattern;

public class Client implements Constants {

	private static int udpPort = 3333;
	private static InetAddress clientIp = null;
	private static final String IPv4_REGEX = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	private static Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);

	public static void main(String[] args) throws InterruptedException,
			RemoteException, NotBoundException {
		System.out.println("Starting the Client");
		System.setProperty("java.net.preferIPv4Stack", "true");

		ClientUDPServer udpServer = new ClientUDPServer(udpPort);
		udpServer.start();
		
		
		String serverIp = null;
		Integer Port = null;
		boolean done = false;

		getClientIP();
		
		Scanner scan = new Scanner(System.in);

		if (args.length > 0) {
			
			serverIp = checkIp(args[0],scan);
			System.out.println("Server IP: "+ serverIp);
			Port = portCheck(args[1],scan);
			System.out.println("Port: "+ Port);
			udpPort = portCheck(args[2],scan);
			System.out.println("Listen port: "+ udpPort);
			
			
		} else {
			
			//Getting an IP from USER		
			System.out.println("Enter Server' IP you want to join: ");
			String s = scan.nextLine();
			serverIp = checkIp(s,scan);			
			
			//Getting a port from USER
			System.out.println("Enter port: ");
			s = scan.nextLine();
			Port = portCheck(s,scan);			
		
			System.out.println("Enter listen port: ");
			s = scan.nextLine();
			udpPort = portCheck(s,scan);			
		
			
		}	
		
		Registry registry = LocateRegistry.getRegistry(serverIp, Port);

		Communicate server = null;
		server = (Communicate) registry.lookup(serverName);

		while (!done) {
			
			try {
				
				System.out.println("Choose the option: \n" + "1) Join\n" + "2) Leave\n"
						+ "3) Subscribe\n" + "4) Publish\n"
						+ "5) Unsubscribe\n" + "6) Ping\n" + "7) Exit\n");

				String Choice = scan.nextLine();
				Integer Option = Integer.valueOf(Choice);

				switch (Option) {
				case 1:
					if (server.Join(clientIp.getHostAddress(), udpPort))
						System.out.println("Joined successfully!");
					else
						System.out.println("ERROR joining");
					break;

				case 2:{
					if (server.Leave(clientIp.getHostAddress(), udpPort))
						System.out.println("Leave successfully!");
					else
						System.out.println("ERROR joining");
					break;
				}
				case 3:{
					System.out.println("Enter Subscription:");
					String Article = scan.nextLine();
					if (server.Subscribe(clientIp.getHostAddress(), udpPort,
							Article))
						System.out.println("Subscribed successfully!");
					else
						System.out.println("ERROR subscribing");
					break;
				}
				case 4:{
					System.out.println("Enter Article:");
					String Article = scan.nextLine();
					if (server.Publish(Article, clientIp.getHostAddress(),
							udpPort)) {
						System.out.println("Published successfully!");
					} else
						System.out.println("ERROR publishing");

					break;
				}
				case 5:{

					System.out.println("Enter Article:");
					String Article = scan.nextLine();

					if (server.Unsubscribe(clientIp.getHostAddress(), udpPort,
							Article))
						System.out.println("Unsubscribed successfully!");
					else
						System.out.println("ERROR unsubscribing");
					break;
				}
				case 6:

					if (server.Ping()) {
						System.out.println("Server OK!");
					} else
						System.out.println("Server DOWN!");

					break;
				case 7:


					System.exit(0);
					break;
					
				}
			} catch (NumberFormatException e) {
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

		} catch (SocketException e) {
			System.out.println("ERROR getting the interfaces of the device");
			e.printStackTrace();
		}
	}
	private static int portCheck(String s, Scanner scan){

		int p = 0;
		boolean badRange = true;
		boolean notInt = true;
		
		
		if (s.length() == 0) p=1099;
			else {
				while (badRange || notInt) {
			
					try{
						Integer.valueOf(s);
						notInt = false;
						p = Integer.valueOf(s);
						}
					catch(NumberFormatException e){
						notInt = true;
						System.out.println("Port has wrong format try again: ");
						s = scan.nextLine();
					}
				
					badRange = p < 0 || p > 55901;
					if (badRange){
						System.out.println("Port is out of range try again: ");
						p = Integer.valueOf(scan.nextLine());
					}
			
				}
			}
		return p;
	}
	
	private static String checkIp(String IP, Scanner scan) {			
		while (!IPv4_PATTERN.matcher(IP).matches()) {
			
			System.out.println("IP has wrong format try again: ");
			IP = scan.nextLine();			
		}
		return IP;
	}	
}


	
