package mercado;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
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
  private LinkedList<Pagar> pagos;

  private double saldo = 0.0;

  @Override
  public void setup() {

    this.getContentManager().registerLanguage(codec);
    this.getContentManager().registerOntology(ontologia);

    this.existencias = new LinkedList<Existencias>();

    this.pagos = new LinkedList<>();

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

    this.addBehaviour(new RecibirSolicitud());
  }

  private class RecibirSolicitud extends CyclicBehaviour {
    public void action() {
      ACLMessage mensaje = myAgent.receive();
      // mensaje.setOntology(OntologiaMercado.NOMBRE_ONTOLOGIA);

      if (mensaje != null) {
        // Se recibió un mensaje, hay que procesarlo.
        ACLMessage respuesta = mensaje.createReply();

        try {
          ContentManager cm = myAgent.getContentManager();
          ContentElement ce = null;
          ce = cm.extractContent(mensaje);

          if (mensaje.getPerformative() == ACLMessage.QUERY_IF) {
            // No lo tiene por default
            respuesta.setPerformative(ACLMessage.REFUSE);
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
          } else if (mensaje.getPerformative() == ACLMessage.PROPOSE) {
            if (ce instanceof Pagar) {
              Pagar pago = (Pagar) ce;
              // Procesar pago

              respuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

              saldo += pago.getMonto();
              pagos.add(pago);

              // Buscar producto y restarle la cantidad que fue comprada

              for (Existencias e : existencias) {
                if (e.getProducto().getNombre().equals(pago.getProducto().getNombre())) {
                  e.setCantidad(e.getCantidad() - pago.getCantidad());
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

  public void takeDown() {
    System.out.println("Pagos al vendedor:" + getName());
    for (Pagar p : pagos) {
      System.out.println(p.toString());
    }

    System.out.printf("Saldo final del vendedor: %.2f\n", saldo);
  }
}
