import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client implements Constants {

	public static void main(String[] args) {
		System.out.println("Starting the Client");

		try {
			Registry registry = LocateRegistry.getRegistry(serverIp);
			ServerInterface server = (ServerInterface) registry
					.lookup(serverName);

			server.Ping();
			
		} catch (RemoteException | NotBoundException e) {
			System.out.println("Error locating the server");
			System.out.println(e);
		}

	}

}
