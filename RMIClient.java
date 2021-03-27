import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Scanner;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIClient extends UnicastRemoteObject implements RMIClient_I {

    RMIClient() throws RemoteException {
        super();
    }

    public static void main(String args[]) {

        //System.getProperties().put("java.security.policy", "policy.all");
        //System.setSecurityManager(new RMISecurityManager());

        try {
            RMIServer_I rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
            //RMIServer_I rmis = (RMIServer_I) Naming.lookup("RMI_Server");
            RMIClient rmic = new RMIClient();

            while (true) {
                //System.out.println("> ");
            }
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }
}
