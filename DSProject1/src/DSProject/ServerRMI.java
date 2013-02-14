package DSProject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerRMI extends UnicastRemoteObject implements Communicate,
		Constants {

	
	private static final long serialVersionUID = 1L;
	private SubscriptionsRegister subscriptionRegister;
	private LinkedList<HostRecord> clientsRegister;
	private LinkedList<ServerGroup> serversRegister;
	private InetAddress serverIp = null;
	private DatagramSocket clientSocket = null;

	public ServerRMI(InetAddress serverIp) throws RemoteException {
		super();

		this.serverIp = serverIp;
		
		System.setProperty("java.rmi.server.hostname",serverIp.getHostAddress());
		
		try {
			System.out.println("Starting the Server");
			System.setProperty("java.net.preferIPv4Stack", "true");
			
			subscriptionRegister = new SubscriptionsRegister();
			clientsRegister = new LinkedList<HostRecord>();
			serversRegister = new LinkedList<ServerGroup>();

			clientSocket = new DatagramSocket();

			// Init server ping
			ServerUDP serverUDP = new ServerUDP(serverIp, serversRegister);
			serverUDP.start();
				
			Registry registry = LocateRegistry.createRegistry(serverRMIPort);
			registry.rebind(serverName, this);
						
			

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized boolean JoinServer(String IP, int Port)
			throws RemoteException {
		HostRecord s = new HostRecord(IP, Port);
		if (!serversRegister.contains(s)) {
			System.out.println("Joining Server: " + s);
			
			Iterator<ServerGroup> itr = serversRegister.iterator();
			while (itr.hasNext()) {
				ServerGroup entry = itr.next();
				if (entry.ip.equals(IP) && entry.port == Port)
					entry.joined = true;
			}			
			
			return true;
		}
		System.out.println("ERROR: Client already joined: " + IP + ", Port: " + Port);
		return false;
	}

	@Override
	public synchronized boolean Join(String IP, int Port)
			throws RemoteException {
		
		HostRecord c = new HostRecord(IP, Port);
		if (!clientsRegister.contains(c)) {
			System.out.println("Client join ip: " + IP + ", Port: " + Port);
			clientsRegister.add(new HostRecord(IP, Port));
			return true;
		}
		System.out.println("ERROR: Client already joined: " + IP + ", Port: " + Port);
		return false;
	}

	@Override
	public synchronized boolean Subscribe(String IP, int Port, String Article)
			throws RemoteException {

		HostRecord c = new HostRecord(IP, Port);
		if (!clientsRegister.contains(c))
			return false;

		System.out.println("Client subscribe: " + c + ", article: " + Article);

		Article a = new Article(Article);
		if(!a.isValidSubscription()) return false;
		
		return subscriptionRegister.subscribeClient(a, new HostRecord(IP, Port));
				
	}

	@Override
	public synchronized boolean Publish(String Article, String IP, int Port)
			throws RemoteException {
		System.out.println("Client publish: " + Article);

		HostRecord c = new HostRecord(IP, Port);
		if (!clientsRegister.contains(c))
			return false;

		Article a = new Article(Article);
		
		if(!a.isValidArticle()) return false;
		
		LinkedList<HostRecord> clients = subscriptionRegister
				.getClients(a);

		System.out.println("Sending to clients:");
		System.out.println(clients);

		/* Send them via UDP */
		/* ... to the clients */
		Iterator<HostRecord> itr = clients.iterator();
		while (itr.hasNext()) {
			HostRecord entry = itr.next();
			if (!c.equals(entry))
				SendArticle(Article, entry.getIP(), entry.getPort());
		}
		
		/* ... and to the joined servers */
		Iterator<ServerGroup> itr2 = serversRegister.iterator();
		while (itr2.hasNext()) {
			ServerGroup entry = itr2.next();
			if(entry.ip != IP || entry.port != Port){
				if(entry.joined){
					entry.rmi.Publish(Article, serverIp.getCanonicalHostName(), serverRMIPort);
				}
			}			
		}
		
		return true;
	}

	@Override
	public synchronized boolean Unsubscribe(String IP, int Port, String Article)
			throws RemoteException {

		HostRecord c = new HostRecord(IP, Port);
		if (!clientsRegister.contains(c))
			return false;

		System.out
				.println("Client unsubscribe: " + c + ", article: " + Article);
		
		Article a = new Article(Article);
		if(!a.isValidSubscription()) return false;
		
		return subscriptionRegister.unsubscribeClient(a,new HostRecord(IP, Port));
				

	}

	@Override
	public boolean Ping() throws RemoteException {
		System.out.println("PING!");
		return true;
	}

	@Override
	public boolean LeaveServer(String IP, int Port) throws RemoteException {
		HostRecord c = new HostRecord(IP, Port);
		if (serversRegister.contains(c)) {
			serversRegister.remove(c);
			return true;
		}
		return false;
	}

	@Override
	public boolean Leave(String IP, int Port) throws RemoteException {
		HostRecord c = new HostRecord(IP, Port);
		if (clientsRegister.contains(c)) {
			clientsRegister.remove(c);
			return true;
		}
		return false;
	}

	

	protected void SendArticle(String Article, String IP, int port) {
		System.out.println("Sending article.... to " + IP + ":" + port);
		try {
			InetAddress ClientIP = InetAddress.getByName(IP);

			byte[] outData = new byte[1024];
		
			String outMsg = Article;// inFromUser.readLine();
			outData = outMsg.getBytes();

			DatagramPacket outPacket = new DatagramPacket(outData,
					outData.length, ClientIP, port);
			clientSocket.send(outPacket);
		} catch (IOException e) {
			System.out.println("[Server] ERROR sending UDP package");
		}
	}
}