package DSProject;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client implements Constants {
	
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Starting the Client");

		/*Start UDP server*/
		
		UDPServer udpServer = new UDPServer(3333);
		udpServer.start();
		
		Thread.sleep(2000);
		
		try {
			
			Registry registry = LocateRegistry.getRegistry(serverIp);
			ServerInterface server = (ServerInterface) registry
					.lookup(serverName);
			
			System.out.println("[Client] Pinging");
			server.Ping();
			System.out.println("[Client] Joining");
			server.Join("locahost", 3333);
			Thread.sleep(2000);
			
			System.out.println("[Client] Subscribing");
			server.Subscribe("localhost", 3333,";;;");
			Thread.sleep(2000);
			
			System.out.println("[Client] Publishing article");
			server.Publish(";;;Article1");
			Thread.sleep(2000);
			
			
			
		} catch (RemoteException | NotBoundException e) {
			System.out.println("Error locating the server");
			System.out.println(e);
		}
	}
}

