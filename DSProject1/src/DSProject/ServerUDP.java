package DSProject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class ServerUDP extends Thread implements Constants {
	boolean done = false;
	private InetAddress _ip = null;
	private DatagramSocket socket;
	private LinkedList<ServerGroup> serversRegister;

	public ServerUDP(InetAddress ip, LinkedList<ServerGroup> serversRegister) {
		_ip = ip;
		this.serversRegister = serversRegister;
	}

	@Override
	public void run() {
		System.out.println("Starting Server Ping");

		try {
			// Opening the socket
			socket = new DatagramSocket(serverUDPPort);

			/* Registering to the Registry Server */
			registerRegistryServer();

			/* Get the list of other servers */
			getOtherServers();

			// Listen to articles and pings
			byte buffer[] = new byte[1024];
			DatagramPacket pkg = new DatagramPacket(buffer, 1024, null, 0);

			while (!done) {
				pkg.setLength(1024);
				socket.receive(pkg);

				String content = pkg.getData().toString();
				System.out.println("Server! Received: " + content);

				System.out.println("ServerPing: Ping received! Sending Pong");
				pkg.setAddress(InetAddress.getByName("128.101.35.147"));
				pkg.setPort(registryServerPort);
				socket.send(pkg);
			}

			socket.close();

		} catch (UnknownHostException e) {
			System.out.println("ERROR unknown host: " + registryServerName);
		} catch (SocketException e) {
			System.out
					.println("ERROR opening the socket with the Registry Server");
			return;
		} catch (IOException e) {
			System.out.println("ERROR sending UDP package");
		}

	}

	private void getOtherServers() {
		try {
			String registryMsg = "GetList;RMI;" + _ip.toString().substring(1)
					+ ";" + serverUDPPort;

			System.out.println("Getting list of servers with the string: "
					+ registryMsg);

			InetAddress registryServerIp = InetAddress
					.getByName(registryServerName);

			DatagramPacket registryPkg = new DatagramPacket(
					registryMsg.getBytes(), registryMsg.length(),
					registryServerIp, registryServerPort);

			socket.send(registryPkg);

			socket.receive(registryPkg);

			System.out.println("List Received!!: " + registryPkg.getData());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void registerRegistryServer() {
		try {
			String registryMsg = "Register;RMI;" + _ip.toString().substring(1)
					+ ";" + serverUDPPort + ";" + serverName + ";"
					+ serverRMIPort;

			System.out.println("Registering with the string: " + registryMsg);

			InetAddress registryServerIp = InetAddress
					.getByName(registryServerName);

			System.out.println("Sending register msg to: " + registryServerIp
					+ ":" + registryServerPort);

			DatagramPacket registryPkg = new DatagramPacket(
					registryMsg.getBytes(), registryMsg.length(),
					registryServerIp, registryServerPort);

			System.out.println("ServerPing: Registering in registry server");

			socket.send(registryPkg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void deregisterRegistryServer() {
		try {
			String registryMsg = "Deregister;RMI;"
					+ _ip.toString().substring(1) + ";" + serverUDPPort + ";"
					+ serverName + ";" + serverRMIPort;

			System.out.println("Registering with the string: " + registryMsg);

			InetAddress registryServerIp = InetAddress
					.getByName(registryServerName);

			System.out.println("Sending register msg to: " + registryServerIp
					+ ":" + registryServerPort);

			DatagramPacket registryPkg = new DatagramPacket(
					registryMsg.getBytes(), registryMsg.length(),
					registryServerIp, registryServerPort);

			System.out.println("ServerPing: Registering in registry server");

			socket.send(registryPkg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		done = true;
	}

}
