import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;
import jade.core.Runtime;

import java.awt.*;


public class Main {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        ContainerController cc = rt.createMainContainer(p);

        AgentController ac;
        try{
            ac = cc.createNewAgent("comprador-a", "mercado.Comprador",null);
            ac.getName();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}
