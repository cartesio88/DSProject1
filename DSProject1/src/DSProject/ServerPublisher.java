package DSProject;

import java.net.InetAddress;
import java.rmi.RemoteException;

public class ServerPublisher extends Thread implements Constants {
	private ServerGroup dst;
	String article;
	InetAddress serverIp;
	int serverRMIPort;

	public ServerPublisher(ServerGroup dst, String article,
			InetAddress serverIp, int serverRMIPort) {
		this.dst = dst;
		this.serverIp = serverIp;
		this.serverRMIPort = serverRMIPort;
		this.article = article;
	}

	@Override
	public void run() {
		System.out.println("ServerPublisher working :)..." + dst);
		try {
			if (dst != null) {
				if (dst.rmi != null)
					dst.rmi.Publish(article, serverIp.getHostAddress(),
							serverRMIPort);
				else
					dst.joined = false;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ServerPublisher DONE :)");
	}
}
