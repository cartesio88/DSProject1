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
		
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(ip, port);
			rmi = (Communicate) registry.lookup(bindingName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString(){
		String s = "";
		
		s += ip+":"+port+"/"+bindingName;
		
		return s;
	}

}
