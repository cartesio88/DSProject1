package DSProject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerUDP extends Thread implements Constants {
	boolean done = false;
	private static InetAddress _ip = null;
	private static DatagramSocket socket;
	private static ServerRMI server;
	
	public ServerUDP(InetAddress ip, ServerRMI server) {
		_ip = ip;
		this.server = server;
	}

	@Override
	public void run() {
		System.out.println("Starting Server Ping");

		try {
			// Opening the socket
			socket = new DatagramSocket(serverPort);

			registerRegistryServer();

			// Listen to articles and pings

			byte buffer[] = new byte[1024];
			DatagramPacket pkg = new DatagramPacket(buffer, 1024, null, 0);

			while (!done) {
				socket.receive(pkg);

				String content = pkg.getData().toString();
				System.out.println("Server! Received: "+content);
				
				if(content.equals("Ping")){ // Its a Ping! Answer
					pkg.setAddress(InetAddress.getByName(registryServerName));
					pkg.setPort(registryServerPort);
					socket.send(pkg);
				}else{ // Its an Article, propagate it!
					server.Publish(content, pkg.getAddress().getHostName(),  pkg.getPort());
				}
				
				System.out.println("ServerPing: Ping received! Sending Pong");
				// registryServerSocket.send(pingPkg);

				// pingPkg.setLength(1024);

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

	

	private void registerRegistryServer() {
		try {
			String registryMsg = "Register;RMI;" + _ip.toString().substring(1)
					+ ";" + serverPort + ";" + serverName + ";1099";

			System.out.println("Registering with the string: " + registryMsg);

			InetAddress registryServerIp = InetAddress.getByName(registryServerName);

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
