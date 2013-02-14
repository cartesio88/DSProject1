import DSProject.ServerRMI;


public class Server {

	public static void main(String[] args) throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.rmi.server.codebase", "file:./bin");
		System.setProperty("java.security.policy", "file:./policyfile");
		
		@SuppressWarnings("unused")
		ServerRMI server = new ServerRMI();	
	}

}
