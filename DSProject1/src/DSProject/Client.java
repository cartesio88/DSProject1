package DSProject;
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

		String serverIp = null;
		Integer Port = null;
		String Article = null;
		boolean done = false;
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter Server' IP you want to join: ");
		serverIp = scan.nextLine();

		System.out.println("Enter port: ");
		Port = Integer.valueOf(scan.nextLine());
		
		Registry registry = LocateRegistry.getRegistry(serverIp);
		Communicate server = (Communicate) registry.lookup(serverName);		
				
		
		System.out.println("Choose the option: \n" +
				"1) Join\n" +
				"2) Subscribe\n" +
				"3) Publish\n" +
				"4) Unsubscribe\n" +
				"5) Exit\n");
		
		
		String Choice = scan.nextLine();
		Integer Option = Integer.valueOf(Choice);
		while(!done){		
			switch(Option){ 
			case 1:

				server.JoinServer(serverIp, Port);
				break;

			case 2:

				System.out.println("Enter Article:");
				Article = scan.nextLine();			
				server.Subscribe(serverIp, Port, Article);
				break;
			
			case 3: 		
			
				System.out.println("Enter Article:");
				Article = scan.nextLine();
				server.Publish(Article, serverIp, Port);
				break;
			
			case 4:		
						
				System.out.println("Enter Article:");
				Article = scan.nextLine();
				server.Unsubscribe(serverIp, Port, Article);
				break;
			case 5:
			
				System.exit(0);
				break;
				
		}
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