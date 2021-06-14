package mercado;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.Action;
import jade.core.AID;
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
  private double consumoMinimo = 70;

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

      if (mensaje != null) {
        // Se recibió un mensaje, hay que procesarlo.
        mensaje.setOntology(ontologia.getName());
        ACLMessage respuesta = mensaje.createReply();

        try {
          ContentManager cm = myAgent.getContentManager();
          ContentElement ce = null;

          if (mensaje.getPerformative() == ACLMessage.QUERY_IF) {
            // No lo tiene por default
            ce = cm.extractContent(mensaje);

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

            Action a = (Action) getContentManager().extractContent(mensaje);

            Pagar pago = (Pagar) a.getAction();
            // Procesar pago

            respuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

            saldo += pago.getMonto();
            pagos.add(pago);

            System.out.printf(
                "\n%s: Recibido el pago de %s por %.2f\n",
                getAID().getName(), pago.getDeudor().getName(), pago.getMonto());

            // Buscar producto y restarle la cantidad que fue comprada

            for (Existencias e : existencias) {
              if (e.getProducto().getNombre().equals(pago.getProducto().getNombre())) {
                e.setCantidad(e.getCantidad() - pago.getCantidad());
              }
            }

          } else if (mensaje.getPerformative() == ACLMessage.REQUEST) {

            Action a = (Action) getContentManager().extractContent(mensaje);
            SolicitarEnvio solicitud = (SolicitarEnvio) a.getAction();

            // Procesar solicitud de envio
            // Obtener AID de quien lo solicita
            AID destinatario = solicitud.getDestinatario();
            // Verificar que consumió lo suficiente para enviar

            double consumo = 0.0;

            for (Pagar p : pagos) {
              if (p.getDeudor().getName().equals(destinatario.getName())) {
                consumo += p.getMonto();
              }
            }

            if (consumo >= consumoMinimo) {
              // Generar mensaje para el distribuidor
              ACLMessage mensajeDistribuidor = new ACLMessage(ACLMessage.REQUEST);

              System.out.printf(
                  "\n%s: Se cumple el consumo mínimo. Reenviando solicitud a distribuidor\n",
                  getAID().getName());

              // Aceptar solicitud
              respuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

              // Buscar un distribuidor
              DFAgentDescription busqueda = new DFAgentDescription();
              ServiceDescription desc = new ServiceDescription();
              desc.setType("Distribuidor");
              busqueda.addServices(desc);

              DFAgentDescription[] resultados = DFService.search(myAgent, busqueda);

              mensajeDistribuidor.setOntology(ontologia.getName());
              mensajeDistribuidor.setLanguage(codec.getName());
              mensajeDistribuidor.addReceiver(resultados[0].getName());

              Action actOp = new Action(getAID(), solicitud);

              getContentManager().fillContent(mensajeDistribuidor, actOp);

              send(mensajeDistribuidor);

            } else {
              // No se consumió lo suficiente, negar solicitud
              System.out.printf(
                  "\n%s: No se cumple el conumo mínimo. Negando solicitud de envio\n", getName());
              respuesta.setPerformative(ACLMessage.REFUSE);
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
    System.out.println("\nPagos al vendedor:" + getName());
    for (Pagar p : pagos) {
      System.out.println(p.toString());
    }

    System.out.printf("Saldo final del vendedor: %.2f\n", saldo);
  }
}
