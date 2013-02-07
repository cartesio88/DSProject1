import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class Server extends UnicastRemoteObject implements ServerInterface, Constants{
	
	private static final long serialVersionUID = 1L;
	

	

	public static void main(String[] args){
		System.out.println("Starting the Server");
		ServerPing serverPing = new ServerPing();
		
		// Init server ping
		// It logs in and answer the pings
		serverPing.run();
		
		try {
			Server server = new Server();
		} catch (RemoteException e) {
			System.out.println("ERROR creating the Server");
		}
		
		
	}
	

	protected Server() throws RemoteException {
		super();
	}
	
}
