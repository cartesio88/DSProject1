
package Communicate;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Communicate extends Remote {
    boolean JoinServer() throws RemoteException;
    boolean Join(string IP, int Port) throws RemoteException;
    boolean Subscribe(string IP, int Port, string Article) throws RemoteException;
    boolean Publish(string Article) throws RemoteException;
    boolean Unsubscribe(string IP, int Port, string Article) throws RemoteException;
    boolean Ping() throws RemoteException;
}
