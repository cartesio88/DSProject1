import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client implements Constants {

	private static int udpPort = 3333;
	
	public static void main(String[] args) throws InterruptedException, RemoteException, NotBoundException {
		System.out.println("Starting the Client");

		Scanner scan = new Scanner(System.in);
		String serverIp = null;
		System.out.println("Enter Server IP: ");
		serverIp = scan.nextLine();
		
		Registry registry = LocateRegistry.getRegistry(serverIp);
		Communicate server = (Communicate) registry.lookup(serverName);		
				
		
		System.out.println("Choose the option: \n" +
				"1) Join\n" +
				"2) Subscribe\n" +
				"3) Publish\n" +
				"4) Unsubscribe\n");
		
		
		String Choice = scan.nextLine();
		
		Integer Option = Integer.valueOf(Choice);
				
		Integer Port = null;
		String Article = null;
		
		switch(Option){ 
		case 1:
			System.out.println("Enter Server IP: ");
			IP = scan.nextLine();
		
			System.out.println("Enter port: ");
			Port = Integer.valueOf(scan.nextLine());
			
			server.JoinServer(IP, Port);
			
			break;
		case 2:

			System.out.println("Enter Server IP: ");
			IP = scan.nextLine();
		
			System.out.println("Enter port: ");
			Port = Integer.valueOf(scan.nextLine());
			
			System.out.println("Enter Article:");
			Article = scan.nextLine();
			
			server.Subscribe(IP, Port, Article);
			
			break;
			
		case 3: 

			System.out.println("Enter Server IP: ");
			IP = scan.nextLine();
		
			System.out.println("Enter port: ");
			Port = Integer.valueOf(scan.nextLine());
			
			System.out.println("Enter Article:");
			Article = scan.nextLine();

			server.Publish(Article, IP, Port);
			
			break;
			
		case 4:
			
			System.out.println("Enter Server IP: ");
			IP = scan.nextLine();
		
			System.out.println("Enter port: ");
			Port = Integer.valueOf(scan.nextLine());
			
			System.out.println("Enter Article:");
			Article = scan.nextLine();
			
			server.Unsubscribe(IP, Port, Article);
			
		}
				
			
		/*Start UDP server*/
		ClientUDPServer udpServer = new ClientUDPServer(udpPort);
		udpServer.start();
	
		try {

			//Registry registry = LocateRegistry.getRegistry(serverIp);
			//Communicate server = (Communicate) registry
			//		.lookup(serverName);

			System.out.println("[Client] Pinging");
			server.Ping();
			System.out.println("[Client] Joining");
			server.Join("locahost", udpPort);
	
			System.out.println("[Client] Subscribing");
			server.Subscribe("127.0.0.1", udpPort,";;;");

			System.out.println("[Client] Publishing article");
			server.Publish(";;;Article1","127.0.0.1",udpPort);



		} catch (RemoteException e) {
			System.out.println("Error locating the server");
			System.out.println(e);
		}
	}
}
