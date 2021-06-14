package mercado;

import jade.content.AgentAction;
import jade.core.AID;

public class SolicitarEnvio implements AgentAction {

   private AID destinatario;
   private Direccion direccion;
   private AID vendedor;

   public AID getDestinatario() {
      return destinatario;
   }

   public void setDestinatario(AID destinatario) {
      this.destinatario = destinatario;
   }

   public Direccion getDireccion() {
      return direccion;
   }

   public void setDireccion(Direccion direccion) {
      this.direccion = direccion;
   }

   public AID getVendedor() {
      return vendedor;
   }

   public void setVendedor(AID vendedor) {
      this.vendedor = vendedor;
   }
}
