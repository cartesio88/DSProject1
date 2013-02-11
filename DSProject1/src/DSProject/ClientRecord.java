package DSProject;

public class ClientRecord {
	private String ip;
	private int port;
	
	public ClientRecord(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public String getIP(){return ip;}
	public int getPort(){return port;}
	
	public void setIP(String ip){this.ip = ip;}
	public void setPort(int port){this.port = port;}
	
	public String toString(){
		String string="";
		
		string+=ip+":"+port;
		
		return string;
	}
}
