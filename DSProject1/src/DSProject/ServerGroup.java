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

	public ServerGroup() {
		joined = false;

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

}
