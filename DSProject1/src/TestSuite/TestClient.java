package TestSuite;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import DSProject.Constants;

import DSProject.ServerInterface;

public class TestClient implements Constants{
	public static void main(String[] args) {
		System.out.println("Starting the Client");

		try {
			Registry registry = LocateRegistry.getRegistry(serverIp);
			ServerInterface server = (ServerInterface) registry
					.lookup(serverName);

			
			/* Diferent tests */
			
			
		} catch (RemoteException | NotBoundException e) {
			System.out.println("Error locating the server");
			System.out.println(e);
		}
	}
}
