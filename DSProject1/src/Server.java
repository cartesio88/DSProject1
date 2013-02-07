import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements ServerInterface,
		Constants {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		System.out.println("Starting the Server");
		
		// Init server ping
		ServerPing serverPing = new ServerPing();
		serverPing.start();

		try {
			
			Server server = new Server();
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(serverName, server);

		} catch (RemoteException e) {
			System.out.println("ERROR creating the Server");
			System.out.println(e);
		} 
	}

	protected Server() throws RemoteException {
		super();
	}

	@Override
	public boolean JoinServer() throws RemoteException {
		System.out.println("Joining Server");
		return false;
	}

	@Override
	public boolean Join(String IP, int Port) throws RemoteException {
		System.out.println("Client join ip: " + IP + ", Port: " + Port);
		return false;
	}

	@Override
	public boolean Subscribe(String IP, int Port, String Article)
			throws RemoteException {
		System.out.println("Client subscribe ip: " + IP + ", Port: " + Port
				+ ", article: " + Article);
		return false;
	}

	@Override
	public boolean Publish(String Article) throws RemoteException {
		System.out.println("Client publish: " + Article);
		return false;
	}

	@Override
	public boolean Unsubscribe(String IP, int Port, String Article)
			throws RemoteException {
		System.out.println("Client unsubscribe ip: " + IP + ", Port: " + Port
				+ ", article: " + Article);
		return false;
	}

	@Override
	public boolean Ping() throws RemoteException {
		System.out.println("PING!");
		return false;
	}

}
