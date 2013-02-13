package DSProject;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client implements Constants {

	private static int udpPort = 3333;
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Starting the Client");

		/*Start UDP server*/

		ClientUDPServer udpServer = new ClientUDPServer(udpPort);
		udpServer.start();

	
		try {

			Registry registry = LocateRegistry.getRegistry(serverIp);
			Communicate server = (Communicate) registry
					.lookup(serverName);

			System.out.println("[Client] Pinging");
			server.Ping();
			System.out.println("[Client] Joining");
			server.Join("locahost", udpPort);
	
			System.out.println("[Client] Subscribing");
			server.Subscribe("127.0.0.1", udpPort,";;;");

			System.out.println("[Client] Publishing article");
			server.Publish(";;;Article1","127.0.0.1",udpPort);



		} catch (RemoteException | NotBoundException e) {
			System.out.println("Error locating the server");
			System.out.println(e);
		}
	}
}
