package DSProject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerGroup {
	public String ip;
	public int port;
	public String bindingName;
	public boolean joined;
	public Communicate rmi;

	public ServerGroup(String ip, String bindingName, int port) {
		joined = false;
		this.ip = ip;
		this.bindingName = bindingName;
		this.port = port;
		this.rmi = null;		
	}

	public boolean bind(){
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(ip, port);
			rmi = (Communicate) registry.lookup(bindingName);
		} catch (RemoteException e) {
			rmi = null;
			e.printStackTrace();
			return false;
		} catch (NotBoundException e) {
			System.out.println("Server not available "+toString());
			rmi = null;
			return false;
		}
		return true;
	}
	public boolean equals(Object o) {
		ServerGroup s = (ServerGroup) o;

		return s.ip.equals(ip) && s.port == port;
	}

	public String toString() {
		String s = "";
		s += bindingName + "@" + ip + ":" + port;
		return s;
	}

}
