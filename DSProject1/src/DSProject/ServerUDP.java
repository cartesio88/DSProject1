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
			
			InetAddress registryServerIp = InetAddress
					.getByName(registryServerName);
			
			while (!done) {
												
				pkg.setLength(1024);
				socket.receive(pkg);

				String content = new String(pkg.getData(), "UTF-8");
				
				//System.out.println("ServerPing: Ping received! Sending Pong: "+content);
				
				pkg.setAddress(InetAddress.getByName("128.101.35.147"));
			
				DatagramPacket outPkg = new DatagramPacket(content.getBytes(), content.getBytes().length, registryServerIp, pkg.getPort() );
			
				socket.send(outPkg);
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
	
			/*
			 * GET THE SERVERS LIST
			 */
			
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

			
			byte buffer[] = new byte[1024];
			DatagramPacket inPkg = new DatagramPacket(buffer, 1024, null, 0);
			socket.receive(inPkg);

			String list = new String(inPkg.getData(), "UTF-8");
			System.out.println("List Received!!: " + list);
			
			/*
			 * PROCESS THE LIST, AND JOIN THE SERVERS
			 * IP - BINDING-NAME - PORT
			 */
			String ip;
			String name;
			int port;
			int index;
			while(list.length() > 0){
				// Getting IP
				index = list.indexOf(';');
				ip = list.substring(0, index);
				list = list.substring(index+1);
				
				// Getting binding name
				index = list.indexOf(';');
				name = list.substring(0, index);
				list = list.substring(index+1);
				
				// Getting port
				index = list.indexOf(';');
				if(index == -1) //Last value
					index = list.length();
				port = Integer.parseInt(list.substring(0, index));
				list = list.substring(0, index+1);
				
				// Creating the server
				ServerGroup s = new ServerGroup(ip, name, port);
				System.out.println("Joining Server Group: "+s);
				serversRegister.add(s);
				s.rmi.JoinServer(_ip.getHostAddress(), serverRMIPort);
			}
			
			
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
		deregisterRegistryServer();
		done = true;
		
	}

}
