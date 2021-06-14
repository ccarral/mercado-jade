package mercado;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.*;
import java.util.*;

public class Vendedor extends Agent {

  private Ontology ontologia = OntologiaMercado.getInstance();
  private Codec codec = new SLCodec();

  private LinkedList<Existencias> existencias;

  @Override
  public void setup() {

    this.getContentManager().registerLanguage(codec);
    this.getContentManager().registerOntology(ontologia);

    this.existencias = new LinkedList<Existencias>();

    Object[] args = this.getArguments();

    for (int i = 0; i < args.length; i += 3) {

      String producto = args[i].toString();
      Integer cantidad = Integer.valueOf(args[i + 1].toString());
      Double precio = Double.valueOf(args[i + 2].toString());
      Existencias e = new Existencias(producto, cantidad, precio);
      e.setDueño(getAID());
      this.existencias.push(e);
    }

    // Registrar en la sección amarilla.
    DFAgentDescription descripcion = new DFAgentDescription();
    descripcion.setName(getAID());
    ServiceDescription descripcionServicio = new ServiceDescription();
    descripcionServicio.setType("Vendedor");
    descripcionServicio.setName(getLocalName() + "-vendedor");
    descripcion.addServices(descripcionServicio);

    try {
      DFService.register(this, descripcion);
    } catch (FIPAException e) {
      e.printStackTrace();
    }

    this.addBehaviour(new RecibirSolicitudDisponibilidad());
  }

  private class RecibirSolicitudDisponibilidad extends CyclicBehaviour {
    public void action() {
      ACLMessage mensaje = myAgent.receive();
      // mensaje.setOntology(OntologiaMercado.NOMBRE_ONTOLOGIA);

      if (mensaje != null) {
        // Se recibió un mensaje, hay que procesarlo.
        ACLMessage respuesta = mensaje.createReply();

        // No lo tiene por default
        respuesta.setPerformative(ACLMessage.REFUSE);

        try {
          ContentManager cm = myAgent.getContentManager();
          ContentElement ce = null;
          ce = cm.extractContent(mensaje);

          if (mensaje.getPerformative() == ACLMessage.QUERY_IF) {
            if (ce instanceof Disponible) {
              Disponible pregunta = (Disponible) ce;
              for (Existencias e : existencias) {
                if (e.getProducto().getNombre().equals(pregunta.getProducto().getNombre())) {
                  if (e.getCantidad() >= pregunta.getCantidad()) {
                    // El producto sí existe. Informar cuanto es el precio unitario
                    Existencias respuestaExistencias = new Existencias();
                    respuestaExistencias.setDueño(getAID());

                    // Regresar una respuesta con el número de artículos que solicitó y el precio
                    // total de arts*precio.
                    respuestaExistencias.setCantidad(pregunta.getCantidad());
                    respuestaExistencias.setPrecio(e.getPrecio());
                    respuestaExistencias.setProducto(e.getProducto());

                    respuesta.setPerformative(ACLMessage.INFORM);
                    getContentManager().fillContent(respuesta, respuestaExistencias);
                  }
                }
              }
            }
          }

          send(respuesta);

        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        block();
      }
    }
  }

  public void takeDown() {}
}
