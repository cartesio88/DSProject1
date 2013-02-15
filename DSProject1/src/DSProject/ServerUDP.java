package DSProject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;


public class ServerUDP extends Thread implements Constants {
	boolean done = false;
	private InetAddress _ip = null;
	private DatagramSocket socket;
	private LinkedList<ServerGroup> serversRegister;
	private Semaphore mutex;
	private boolean firstExecution = true;
	
	
	public ServerUDP(InetAddress ip, LinkedList<ServerGroup> serversRegister, Semaphore mutex) {
		_ip = ip;
		this.mutex = mutex;
		this.serversRegister = serversRegister;
	}

	@Override
	public void run() {
		try {
			// Opening the socket
			socket = new DatagramSocket(serverUDPPort);

			/* Registering to the Registry Server */
			registerRegistryServer();

			getOtherServers();

			// Listen to articles and pings
			byte buffer[] = new byte[1024];
			DatagramPacket pkg = new DatagramPacket(buffer, 1024, null, 0);

			while (!done) {

				pkg.setLength(1024);
				socket.receive(pkg);
				if (pkg.getData()[0] != 'h') { // List of server :)

					String list = new String(pkg.getData(), pkg.getOffset(),
							pkg.getLength(), "UTF-8").trim();
					System.out.println("Servers list: " + list);

					parseOtherServersList(list);
				} else { // Respond with a pong!
					try {
						InetAddress registryServerIp;

						registryServerIp = InetAddress
								.getByName(registryServerName);

						DatagramPacket outPkg = new DatagramPacket(
								pkg.getData(), pkg.getData().length,
								registryServerIp, pkg.getPort());

						socket.send(outPkg);

					} catch (UnknownHostException e) {
						System.out.println("Could not send pong");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

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

	public void getOtherServers() {
		try {
			mutex.acquire();

			/*
			 * GET THE SERVERS LIST
			 */

			String registryMsg = "GetList;RMI;" + _ip.toString().substring(1)
					+ ";" + serverUDPPort;

			// System.out.println("Getting list of servers with the string: "
			// + registryMsg);

			InetAddress registryServerIp = InetAddress
					.getByName(registryServerName);

			DatagramPacket registryPkg = new DatagramPacket(
					registryMsg.getBytes(), registryMsg.length(),
					registryServerIp, registryServerPort);

			socket.send(registryPkg);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void parseOtherServersList(String list) {
		// Splitlist
		String[] temp = list.split(";");
		String IP = null;
		String BindingName = null;
		int Port = 0;

		for (int i = 0; i < temp.length; i++) {
			switch (i % 3) {
			case 0:
				IP = temp[i];
				// System.out.println("IP: "+IP);
				break;
			case 1:
				BindingName = temp[i];
				// System.out.println("Name: "+BindingName);
				break;
			case 2:
				Port = Integer.valueOf(temp[i].trim());
				// Creating the server
				ServerGroup s = new ServerGroup(IP, BindingName, Port);

				if(!serversRegister.contains(s)) serversRegister.add(s);
				
				if (firstExecution) {
					if (s.bind()) {
						try {
							System.out.println("Joining Server Group: "+s);
							s.rmi.JoinServer(_ip.getHostAddress(), serverRMIPort);
							s.joined = true;
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}

				break;
			}
		}

		mutex.release();
		firstExecution = false;

	}

	private void registerRegistryServer() {
		try {
			String registryMsg = "Register;RMI;" + _ip.toString().substring(1)
					+ ";" + serverUDPPort + ";" + serverName + ";"
					+ serverRMIPort;

			// System.out.println("Registering with the string: " +
			// registryMsg);

			InetAddress registryServerIp = InetAddress
					.getByName(registryServerName);

			// System.out.println("Sending register msg to: " + registryServerIp
			// + ":" + registryServerPort);

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

			System.out.println("Deegistering with the string: " + registryMsg);

			InetAddress registryServerIp = InetAddress
					.getByName(registryServerName);

			// System.out.println("Sending register msg to: " + registryServerIp
			// + ":" + registryServerPort);

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
