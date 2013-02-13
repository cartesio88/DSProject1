package DSProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class Server extends UnicastRemoteObject implements Communicate,
		Constants {

	private static final long serialVersionUID = 1L;
	private static SubscriptionsRegister subscriptionRegister;
	private static LinkedList<HostRecord> clientsRegister;
	private static LinkedList<HostRecord> serversRegister;
	private static InetAddress _ip = null;
	private static DatagramSocket clientSocket = null;

	public static void main(String[] args) throws Exception {
		System.out.println("Starting the Server");
		System.setProperty("java.net.preferIPv4Stack", "true");

		subscriptionRegister = new SubscriptionsRegister();
		clientsRegister = new LinkedList<HostRecord>();
		serversRegister = new LinkedList<HostRecord>();

		clientSocket = new DatagramSocket();

		/* Get the IP from the interface */
		getServerIP();

		// Init server ping
		ServerPing serverPing = new ServerPing(_ip);
		serverPing.start();

		/*try {

			Server server = new Server();
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(serverName, server);

		} catch (RemoteException e) {
			System.out.println("ERROR creating the Server");
			System.out.println(e);
		}*/
	}

	protected Server() throws RemoteException {
		super();
	}

	protected static void getServerIP() {
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface
					.getNetworkInterfaces();

			while (nets.hasMoreElements()) {
				NetworkInterface ni = nets.nextElement();
				if (!ni.isLoopback() && ni.isUp()) {
					_ip = ni.getInetAddresses().nextElement();
					break;
				}
			}

			System.setProperty("java.rmi.server.hostname",
					_ip.getCanonicalHostName());
			System.out.println("El valor de la ip es:"
					+ System.getProperty("java.rmi.server.hostname"));

		} catch (SocketException e) {
			System.out.println("ERROR getting the interfaces of the device");
			e.printStackTrace();
		}
	}

	@Override
	public synchronized boolean JoinServer(String IP, int Port) throws RemoteException {
		System.out.println("Joining Server");
		return false;
	}

	@Override
	public synchronized boolean Join(String IP, int Port)
			throws RemoteException {
		System.out.println("Client join ip: " + IP + ", Port: " + Port);
		clientsRegister.add(new HostRecord(IP, Port));
		return true;
	}

	@Override
	public synchronized boolean Subscribe(String IP, int Port, String Article)
			throws RemoteException {
		System.out.println("Client subscribe ip: " + IP + ", Port: " + Port
				+ ", article: " + Article);

		subscriptionRegister.subscribeClient(new Article(Article),
				new HostRecord(IP, Port));
		return true;
	}

	@Override
	public synchronized boolean Publish(String Article, String IP, int Port) throws RemoteException {
		System.out.println("Client publish: " + Article);

		LinkedList<HostRecord> clients = subscriptionRegister
				.getClients(new Article(Article));

		System.out.println("Sending to clients:");
		System.out.println(clients);

		/* Send them via UDP */
		Iterator<HostRecord> itr = clients.iterator();
		while (itr.hasNext()) {
			HostRecord entry = itr.next();
			SendArticle(Article, entry.getIP(), entry.getPort());
		}
		return false;
	}

	@Override
	public synchronized boolean Unsubscribe(String IP, int Port, String Article)
			throws RemoteException {
		System.out.println("Client unsubscribe ip: " + IP + ", Port: " + Port
				+ ", article: " + Article);

		subscriptionRegister.unsubscribeClient(new Article(Article),
				new HostRecord(IP, Port));

		return true;
	}

	@Override
	public boolean Ping() throws RemoteException {
		System.out.println("PING!");
		return false;
	}

	protected static void SendArticle(String Article, String IP, int port) {
		System.out.println("Sending article.... to " + IP + ":" + port);
		try {

			InetAddress ClientIP = InetAddress.getByName(IP);

			byte[] outData = new byte[1024];
			byte[] inData = new byte[1024];

			String outMsg = Article;// inFromUser.readLine();
			outData = outMsg.getBytes();

			DatagramPacket outPacket = new DatagramPacket(outData,
					outData.length, ClientIP, port);
			clientSocket.send(outPacket);
		} catch (IOException e) {
			System.out.println("[Server] ERROR sending UDP package");
		}
	}

	@Override
	public boolean LeaveServer(String IP, int Port) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean Leave(String IP, int Port) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}