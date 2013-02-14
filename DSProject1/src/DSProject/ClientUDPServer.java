package DSProject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
 
public class ClientUDPServer extends Thread implements Constants {
    boolean done = false;
    int listenPort;
    DatagramSocket serverSocket = null;
    
    public ClientUDPServer(int listenPort){
        this.listenPort = listenPort;
    }
     
    public void run() {
        try {
        System.out.println("Client UDPServer is running");
        
        byte[] inData = new byte[1024];
        DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
        
        serverSocket = new DatagramSocket(listenPort); 
        
        while(true)
            {                
                serverSocket.receive(inPacket);
                String inMsg = new String(inPacket.getData());
                System.out.println("Article received: " + inMsg);
             }
        } catch (IOException e) {
        System.out.println("ERROR sending UDP package");        
    }
    }
}