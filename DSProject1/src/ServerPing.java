import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerPing extends Thread implements Constants {
	boolean done = false;

	@Override
	public void run() {
		System.out.println("Starting Server Ping");

		try {
			// Opening the socket
			DatagramSocket registryServerSocket = new DatagramSocket(serverPort);

			String registryMsg = "Register;RMI;" + serverIp + ";" + serverPort;
			
			System.out.println("Registering with the string: "+registryMsg);
			
			InetAddress registryServerIp;

			registryServerIp = InetAddress.getByName(registryServerName);

			DatagramPacket registryPkg = new DatagramPacket(
					registryMsg.getBytes(), registryMsg.length(),
					registryServerIp, registryServerPort);

			System.out.println("ServerPing: Registering in registry server");
			registryServerSocket.send(registryPkg);

			// Answer pings
			byte buffer[] = new byte[1024];
			DatagramPacket pingPkg = new DatagramPacket(buffer, 1024,
					registryServerIp, registryServerPort);

			System.out.println("ServerPing: Listening to the pings");
			while (!done) {
				System.out.println("ServerPing: Waiting for a ping");
				registryServerSocket.receive(pingPkg);

				pingPkg.setAddress(registryServerIp);
				pingPkg.setPort(registryServerPort);

				System.out.println("ServerPing: Ping received! Sending Pong");
				registryServerSocket.send(pingPkg);

				pingPkg.setLength(1024);

			}

			registryServerSocket.close();

		} catch (UnknownHostException e) {
			System.out.println("ERROR unknown host: " + registryServerName);
		} catch (SocketException e) {
			System.out.println("ERROR opening the socket with the Registry Server");
			return;
		} catch (IOException e) {
			System.out.println("ERROR sending UDP package");
		}

	}

	public void stopServer() {
		done = true;
	}

}
