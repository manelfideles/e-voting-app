import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;

public class RMIClient extends UnicastRemoteObject implements RMIClient_I {

    private static final long serialVersionUID = 1L;

    RMIClient() throws RemoteException {
        super();
    }

    public static void main(String args[]) {

        // System.getProperties().put("java.security.policy", "policy.all");
        // System.setSecurityManager(new RMISecurityManager());

        try {
            RMIServer_I rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
            RMIClient rmic = new RMIClient();

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }
}
