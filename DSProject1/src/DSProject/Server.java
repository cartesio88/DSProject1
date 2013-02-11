package DSProject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

public class Server extends UnicastRemoteObject implements ServerInterface,
		Constants {

	private static final long serialVersionUID = 1L;
	private static SubscriptionsRegister subscriptionRegister;
	private static LinkedList<ClientRecord> clientsRegister;

	public static void main(String[] args) {
		System.out.println("Starting the Server");
		
		subscriptionRegister = new SubscriptionsRegister();
		clientsRegister = new LinkedList<ClientRecord>();
		
		// Init server ping
		ServerPing serverPing = new ServerPing();
		//serverPing.start();

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
	public synchronized boolean JoinServer() throws RemoteException {
		System.out.println("Joining Server");
		return false;
	}

	@Override
	public synchronized boolean Join(String IP, int Port) throws RemoteException {
		System.out.println("Client join ip: " + IP + ", Port: " + Port);
		clientsRegister.add(new ClientRecord(IP, Port));
		return true;
	}

	@Override
	public synchronized boolean Subscribe(String IP, int Port, String Article)
			throws RemoteException {
		System.out.println("Client subscribe ip: " + IP + ", Port: " + Port
				+ ", article: " + Article);
		
		subscriptionRegister.subscribeClient(new Article(Article), new ClientRecord(IP, Port));
		return true;
	}

	@Override
	public synchronized boolean Publish(String Article) throws RemoteException {
		System.out.println("Client publish: " + Article);
		
		LinkedList<ClientRecord> clients = subscriptionRegister.getClients(new Article(Article));
		
		System.out.println("Sending to clients:");
		System.out.println(clients);
		/* Send them via UDP */
		/* TODO */
		
		
		return false;
	}

	@Override
	public synchronized boolean Unsubscribe(String IP, int Port, String Article)
			throws RemoteException {
		System.out.println("Client unsubscribe ip: " + IP + ", Port: " + Port
				+ ", article: " + Article);
		
		subscriptionRegister.unsubscribeClient(new Article(Article), new ClientRecord(IP, Port));
		
		return true;
	}

	@Override
	public boolean Ping() throws RemoteException {
		System.out.println("PING!");
		return false;
	}

}
