
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    boolean JoinServer() throws RemoteException;
    boolean Join(String IP, int Port) throws RemoteException;
    boolean Subscribe(String IP, int Port, String Article) throws RemoteException;
    boolean Publish(String Article) throws RemoteException;
    boolean Unsubscribe(String IP, int Port, String Article) throws RemoteException;
    boolean Ping() throws RemoteException;
}
