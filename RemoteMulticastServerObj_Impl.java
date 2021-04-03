import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteMulticastServerObj_Impl extends Remote {
    public void ping() throws RemoteException;
}