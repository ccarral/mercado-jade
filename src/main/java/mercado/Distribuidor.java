package mercado;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class Distribuidor extends Agent {
  private Ontology ontologia = OntologiaMercado.getInstance();
  private Codec codec = new SLCodec();

  @Override
  public void setup() {
    getContentManager().registerLanguage(codec);
    getContentManager().registerOntology(ontologia);

    DFAgentDescription descripcion = new DFAgentDescription();
    descripcion.setName(getAID());
    ServiceDescription descripcionServicio = new ServiceDescription();
    descripcionServicio.setType("Distribuidor");
    descripcionServicio.setName(getLocalName() + "-distribuidor");
    descripcion.addServices(descripcionServicio);

    addBehaviour(new RecibirSolicitudEnvio());

    try {
      DFService.register(this, descripcion);
    } catch (FIPAException e) {
      e.printStackTrace();
    }
  }

  private class RecibirSolicitudEnvio extends CyclicBehaviour {

    @Override
    public void action() {
      ACLMessage mensaje = myAgent.receive();
      if (mensaje != null) {

        try {

          if (mensaje.getPerformative() == ACLMessage.REQUEST) {
            Action a = (Action) getContentManager().extractContent(mensaje);
            SolicitarEnvio solicitud = (SolicitarEnvio) a.getAction();
            System.out.printf("Enviando pedido a: %s\n", solicitud.getVendedor().getName());
            System.out.printf("Calle: %s\n", solicitud.getDireccion().getCalle());
            System.out.printf("Número: %d\n", solicitud.getDireccion().getNumero());
            System.out.printf("Telefono: %s\n", solicitud.getDireccion().getTelefono());
          }

        } catch (Exception e) {
          e.printStackTrace();
        }

      } else {
        block();
      }
    }
  }
}
