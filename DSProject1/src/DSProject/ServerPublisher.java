package DSProject;

import java.net.InetAddress;
import java.rmi.RemoteException;

public class ServerPublisher extends Thread implements Constants{
	private ServerGroup dst;
	String article;
	InetAddress serverIp;
	
	public ServerPublisher(ServerGroup dst, String article, InetAddress serverIp){
		this.dst = dst;
		this.serverIp = serverIp;
		this.article = article;
	}
	
	@Override
	public void run(){
		try {
			dst.rmi.Publish(article,serverIp.getHostAddress(), serverRMIPort);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
