package DSProject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerRMI extends UnicastRemoteObject implements Communicate,
		Constants {

	private static final long serialVersionUID = 1L;
	private static SubscriptionsRegister subscriptionRegister;
	private static LinkedList<HostRecord> clientsRegister;
	private static LinkedList<ServerGroup> serversRegister;
	private static InetAddress serverIp = null;
	private static DatagramSocket clientSocket = null;

	public ServerRMI() throws RemoteException {
		super();

		try {
			System.out.println("Starting the Server");
	
			subscriptionRegister = new SubscriptionsRegister();
			clientsRegister = new LinkedList<HostRecord>();
			serversRegister = new LinkedList<ServerGroup>();

			clientSocket = new DatagramSocket();

			/* Get the IP from the interface */
			getServerIP();
			
			// Init server ping
			ServerUDP serverUDP = new ServerUDP(serverIp, serversRegister);
			serverUDP.start();

			/*Registry registry = LocateRegistry.getRegistry();
			registry.rebind(serverName, this);*/

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
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean Join(String IP, int Port)
			throws RemoteException {
		System.out.println("Client join ip: " + IP + ", Port: " + Port);

		HostRecord c = new HostRecord(IP, Port);
		if (!clientsRegister.contains(c)) {
			clientsRegister.add(new HostRecord(IP, Port));
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean Subscribe(String IP, int Port, String Article)
			throws RemoteException {

		HostRecord c = new HostRecord(IP, Port);
		if (!clientsRegister.contains(c))
			return false;

		System.out.println("Client subscribe: " + c + ", article: " + Article);

		subscriptionRegister.subscribeClient(new Article(Article),
				new HostRecord(IP, Port));

		return true;
	}

	@Override
	public synchronized boolean Publish(String Article, String IP, int Port)
			throws RemoteException {
		System.out.println("Client publish: " + Article);

		HostRecord c = new HostRecord(IP, Port);
		if (!clientsRegister.contains(c))
			return false;

		LinkedList<HostRecord> clients = subscriptionRegister
				.getClients(new Article(Article));

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
		
		return false;
	}

	@Override
	public synchronized boolean Unsubscribe(String IP, int Port, String Article)
			throws RemoteException {

		HostRecord c = new HostRecord(IP, Port);
		if (!clientsRegister.contains(c))
			return false;

		System.out
				.println("Client unsubscribe: " + c + ", article: " + Article);

		subscriptionRegister.unsubscribeClient(new Article(Article),
				new HostRecord(IP, Port));

		return true;
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

	protected static void getServerIP() {
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface
					.getNetworkInterfaces();

			while (nets.hasMoreElements()) {
				NetworkInterface ni = nets.nextElement();
				if (!ni.isLoopback() && ni.isUp()) {
					serverIp = ni.getInetAddresses().nextElement();
					break;
				}
			}
			System.setProperty("java.rmi.server.hostname",serverIp.getCanonicalHostName());
			
			System.out.println("El valor de la ip es:"
					+ System.getProperty("java.rmi.server.hostname"));

		} catch (SocketException e) {
			System.out.println("ERROR getting the interfaces of the device");
			e.printStackTrace();
		}
	}

	protected static void SendArticle(String Article, String IP, int port) {
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