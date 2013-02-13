import DSProject.ServerRMI;


public class Server {

	public static void main(String[] args) throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		@SuppressWarnings("unused")
		ServerRMI server = new ServerRMI();	
	}

}
