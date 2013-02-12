package DSProject;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.LinkedList;
import java.net.*;
import java.io.*;

public class Server extends UnicastRemoteObject implements ServerInterface,
		Constants {

	private static final long serialVersionUID = 1L;
	private static SubscriptionsRegister subscriptionRegister;
	private static LinkedList<HostRecord> clientsRegister;
	private static LinkedList<HostRecord> serversRegister;
	private static InetAddress _ip = null;

	public static void main(String[] args) {
		System.out.println("Starting the Server");
		
		subscriptionRegister = new SubscriptionsRegister();
		clientsRegister = new LinkedList<HostRecord>();
		serversRegister = new LinkedList<HostRecord>();
		
		/* Get the IP from the interface */
		getServerIP();
		
		// Init server ping
		ServerPing serverPing = new ServerPing(_ip);
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

	protected static void getServerIP(){
		try {
			
			System.setProperty("java.net.preferIPv4Stack" , "true");
			
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			
			while(nets.hasMoreElements()){
				NetworkInterface ni = nets.nextElement();
				if(!ni.isLoopback() && ni.isUp()){
					_ip = ni.getInetAddresses().nextElement();
					System.out.println(_ip);
					break;
				}	
			}
		
			System.setProperty("java.rmi.server.hostname", _ip.getCanonicalHostName());
			System.out.println("El valor de la propi es:"+System.getProperty("java.rmi.server.hostname"));
			
			
		} catch (SocketException e) {
			System.out.println("ERROR getting the interfaces of the device");
			e.printStackTrace();
		}
		
		
		
		
		
	}
	@Override
	public synchronized boolean JoinServer() throws RemoteException {
		System.out.println("Joining Server");
		return false;
	}

	@Override
	public synchronized boolean Join(String IP, int Port) throws RemoteException {
		System.out.println("Client join ip: " + IP + ", Port: " + Port);
		clientsRegister.add(new HostRecord(IP, Port));
		return true;
	}

	@Override
	public synchronized boolean Subscribe(String IP, int Port, String Article)
			throws RemoteException {
		System.out.println("Client subscribe ip: " + IP + ", Port: " + Port
				+ ", article: " + Article);
		
		subscriptionRegister.subscribeClient(new Article(Article), new HostRecord(IP, Port));
		return true;
	}

	@Override
	public synchronized boolean Publish(String Article) throws RemoteException {
		System.out.println("Client publish: " + Article);
		
		LinkedList<HostRecord> clients = subscriptionRegister.getClients(new Article(Article));
		
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
		
		subscriptionRegister.unsubscribeClient(new Article(Article), new HostRecord(IP, Port));
		
		return true;
	}

	@Override
	public boolean Ping() throws RemoteException {
		System.out.println("PING!");
		return false;
	}

}

class UDPServer
{
	public static void SendAtricleToClien() throws Exception 
	{
		DatagramSocket serverSocket = new DatagramSocket(9876); 
		byte[] replyData = new byte[1024];
		byte[] sendData = new byte[1024];
		while(true)
			{
				//recieve REPLY
				DatagramPacket replyPacket = new DatagramPacket(replyData, replyData.length);
				serverSocket.receive(replyPacket);
				String replyMsg = new String(replyPacket.getData());
				InetAddress IPAdress = replyPacket.getAddress();
				int port = replyPacket.getPort();
				System.out.println("Mesage from a client + its IP : " + replyMsg);
				//send Message
				String articleToSend = "The article!";
				sendData = articleToSend.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAdress, port); //Connect IP and POrt here
				serverSocket.send(sendPacket);
			}
		
	}
}