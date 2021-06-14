package mercado;

import jade.content.AgentAction;
import jade.core.AID;

public class SolicitarEnvio implements AgentAction {

   private AID destinatario;
   private String direc;
   private AID vendedor;

   public AID getDestinatario() {
      return destinatario;
   }

   public void setDestinatario(AID destinatario) {
      this.destinatario = destinatario;
   }

   public String getDirec() {
      return direc;
   }

   public void setDireccion(String direccion) {
      this.direc= direc;
   }

   public AID getVendedor() {
      return vendedor;
   }

   public void setVendedor(AID vendedor) {
      this.vendedor = vendedor;
   }
}
