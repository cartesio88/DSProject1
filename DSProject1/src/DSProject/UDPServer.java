package DSProject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
 
public class UDPServer extends Thread implements Constants {
    boolean done = false;
    int listenPort;
    public UDPServer(int listenPort){
        this.listenPort = listenPort;
    }
     
    public void run() {
        try {
        System.out.println("UDPServer is running");
        DatagramSocket serverSocket = new DatagramSocket(serverPort); 
 
        byte[] inData = new byte[1024];
        byte[] outData = new byte[1024];
 
        while(true)
            {
                DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
                System.out.println("[UDPServer] Listening...");
                serverSocket.receive(inPacket);
     
                String inMsg = new String(inPacket.getData());
                System.out.println("Article received: " + inMsg);
                 
                InetAddress IPAdress = inPacket.getAddress();
                int port = inPacket.getPort();
                 
                String outMsg = "Received";
                outData = outMsg.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(outData, outData.length, IPAdress, port); 
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
        System.out.println("ERROR sending UDP package");        
    }
    }
}