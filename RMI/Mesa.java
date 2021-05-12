package RMI;

import MULTICAST.RemoteMulticastServerObj_Impl;

import java.io.Serializable;
import java.util.HashMap;

public class Mesa implements Serializable {
    private static final long serialVersionUID = 1L;
    public String dep;
    RemoteMulticastServerObj_Impl remoteServerObj;
    private boolean isOn = false;

    public HashMap<String, Integer> num_eleitores = new HashMap<>(); // {Nome da eleicao : Num de pessoas que ja votaram
                                                                     // nessa eleicao }

    public Mesa(String dep, RemoteMulticastServerObj_Impl remoteServerObj) {
        this.dep = dep;
        this.remoteServerObj = remoteServerObj;
    }

    public String toString() {
        return "RMI.Mesa:" + dep + "; State: " + isOn;
    }

    public String getDep() {
        return dep;
    }

    public void setState(boolean isOn) {
        this.isOn = isOn;
    }

    public HashMap<String, Integer> getNum_eleitores() {
        return num_eleitores;
    }
}
