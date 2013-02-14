package TestSuite;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import DSProject.Article;
import DSProject.HostRecord;
import DSProject.Constants;

import DSProject.Communicate;

public class TestClient implements Constants{
	public static void main(String[] args) {
		System.out.println("Starting the Client");

		try {
			Registry registry = LocateRegistry.getRegistry("localhost");
			Communicate server = (Communicate) registry
					.lookup(serverName);
			
			
			/* Diferent tests */
			/* Instantiate clients */
			HostRecord c1 = new HostRecord("192.168.1.1",1234);
			HostRecord c2 = new HostRecord("192.168.1.2",1235);
			HostRecord c3 = new HostRecord("192.168.1.3",1236);
			HostRecord c4 = new HostRecord("192.168.1.4",1237);
			
			
			/* Instantiate articles */
			Article a1 = new Article("","","","Article 1");
			Article a2 = new Article("sports","","","Article 2");
			Article a3 = new Article("","John","","Article 3");
			Article a4 = new Article("","","UMN","Article 4");
			Article a5 = new Article("science","Joshua","","Article 5");
			Article a6 = new Article("lifestyle","","UC","Article 6");
			Article a7 = new Article("","Joshua","UMN","Article 7");
			Article a8 = new Article("science","","","Article 8");
			
			
			/* Instantiate subscriptions */
			Article s1 = new Article("","","","");
			Article s2 = new Article("science","John","","");
			Article s3 = new Article("","Joshua","","");
			Article s4 = new Article("","","","UMN");
			Article s5 = new Article("science","Joshua","","");
			Article s6 = new Article("","Joshua","UMN","");
	
			
			/* Perform tests */
			server.Join(c1.getIP(), c1.getPort());
			server.Join(c2.getIP(), c2.getPort());
			server.Join(c3.getIP(), c3.getPort());
			
			server.Subscribe(c1.getIP(), c1.getPort(), s1.encode());
			server.Subscribe(c2.getIP(), c1.getPort(), s2.encode());
			server.Subscribe(c3.getIP(), c1.getPort(), s3.encode());
			
			/*System.out.println("Publishing A1");
			server.Publish(a1.encode());
			System.out.println("Publishing A2");
			server.Publish(a2.encode());
			System.out.println("Publishing A3");
			server.Publish(a3.encode());
			System.out.println("Publishing A4");
			server.Publish(a4.encode());
			System.out.println("Publishing A5");
			server.Publish(a5.encode());
			System.out.println("Publishing A6");
			server.Publish(a6.encode());
			System.out.println("Publishing A7");
			server.Publish(a7.encode());
			System.out.println("Publishing A8");
			server.Publish(a8.encode());*/
			
			
		} catch (RemoteException e) {
			System.out.println("Error locating the server");
			System.out.println(e);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
