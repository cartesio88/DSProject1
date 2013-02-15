package DSProject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ServerRMI extends UnicastRemoteObject implements Communicate,
		Constants {

	private static final long serialVersionUID = 1L;
	private SubscriptionsRegister subscriptionRegister;
	private LinkedList<HostRecord> clientsRegister;
	private LinkedList<ServerGroup> serversRegister;
	private LinkedList<Article> articlesRegister;
	private InetAddress serverIp = null;
	private DatagramSocket clientSocket = null;
	private int connectedClients = 0;
	ServerUDP serverUDP = null;
	public final Semaphore mutex = new Semaphore(1, true);

	public ServerRMI(InetAddress serverIp) throws RemoteException {
		super();

		this.serverIp = serverIp;

		System.setProperty("java.rmi.server.hostname",
				serverIp.getHostAddress());

		try {
			System.out.println("Starting the Server: "
					+ System.getProperty("java.rmi.server.hostname"));
			System.setProperty("java.net.preferIPv4Stack", "true");

			subscriptionRegister = new SubscriptionsRegister();
			clientsRegister = new LinkedList<HostRecord>();
			serversRegister = new LinkedList<ServerGroup>();
			articlesRegister = new LinkedList<Article>();

			clientSocket = new DatagramSocket();

			Registry registry = LocateRegistry.createRegistry(serverRMIPort);
			registry.rebind(serverName, this);

			Thread.sleep(1000); // Give it time to the register :)

			// Init server ping
			serverUDP = new ServerUDP(serverIp, serversRegister, mutex);
			serverUDP.start();

			Thread.sleep(1000);

			// serverUDP.getOtherServers();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public synchronized boolean JoinServer(String IP, int Port)
			throws RemoteException {
		ServerGroup s = new ServerGroup(IP, "", Port);
		System.out.print("Server joining us: " + s + "... ");

		serverUDP.getOtherServers();

		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ServerGroup entry = null;
		Iterator<ServerGroup> itr = serversRegister.iterator();
		while (itr.hasNext()) {
			entry = itr.next();
			if (s.equals(entry)) {

				if (entry.rmi == null) {
					if (entry.bind()) {
						entry.joined = true;
					} else {
						entry.joined = false;
						System.out
								.println("Error while server trying to join us: "
										+ s);
					}
				}
				break;
			}
		}

		mutex.release();
		if (entry.joined)
			System.out.println("DONE!");
		else
			System.out.println("ERROR!");
		return entry.joined;
	}

	@Override
	public synchronized boolean Join(String IP, int Port)
			throws RemoteException {

		HostRecord c = new HostRecord(IP, Port);
		System.out.print("Client joining us: " + c + "... ");

		if (!clientsRegister.contains(c)) {
			if (connectedClients > MAXCLIENTS) {
				System.out.println("ERROR!");
				return false;
			}
			connectedClients++;
			System.out.println("Client join ip: " + IP + ", Port: " + Port);
			clientsRegister.add(new HostRecord(IP, Port));
			System.out.println("DONE!");
			return true;
		}
		System.out.println("ERROR!");
		return false;
	}

	@Override
	public synchronized boolean Subscribe(String IP, int Port, String Article)
			throws RemoteException {

		HostRecord c = new HostRecord(IP, Port);
		System.out.print("Client subscribing us: " + c + " Subscription: "
				+ Article + "... ");
		if (!clientsRegister.contains(c)) {
			System.out.println("ERROR!");
			return false;
		}

		Article a = new Article(Article);
		if (!a.isValidSubscription()) {
			System.out.println("ERROR!");
			return false;
		}

		if (subscriptionRegister.subscribeClient(a, new HostRecord(IP, Port))) {
			System.out.println("DONE!");
			return true;
		}
		System.out.println("ERROR!");
		return false;

	}

	@Override
	public synchronized boolean Publish(String Article, String IP, int Port)
			throws RemoteException {
		HostRecord c = new HostRecord(IP, Port);
		System.out.print("Client publishing: " + c + " Article: " + Article
				+ "... ");

		ServerGroup s = new ServerGroup(IP, "", Port);
		if (!clientsRegister.contains(c) && !serversRegister.contains(s)) {
			System.out.println("Not Client nor server is joined");
			System.out.println("ERROR!");
			return false;
		}

		Article a = new Article(Article);

		if (!a.isValidArticle()) {
			System.out.println("ERROR!");
			return false;
		}

		if (articlesRegister.contains(a)) {
			System.out.println("ERROR!");
			return false;
		}

		articlesRegister.add(a);

		LinkedList<HostRecord> clients = subscriptionRegister.getClients(a);

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
			if (entry.ip != IP || entry.port != Port) {
				if (!entry.equals(s)) { // Its not me
					if (entry.joined) {// Its not the source
						System.out.println("Sending to the server: " + entry);
						//entry.rmi.Publish(Article,serverIp.getCanonicalHostName(), serverRMIPort);
						ServerPublisher thread = new ServerPublisher(entry, Article, serverIp);
						thread.run();
					}
				}
			}
		}
		System.out.println("DONE!");
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
		if (!a.isValidSubscription())
			return false;

		return subscriptionRegister.unsubscribeClient(a, new HostRecord(IP,
				Port));

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
			connectedClients--;
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