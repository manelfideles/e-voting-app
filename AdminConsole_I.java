import java.rmi.*;

public interface AdminConsole_I extends Remote {
    public void print_on_admin_console(String s) throws RemoteException;
}
