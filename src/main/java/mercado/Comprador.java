package mercado;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.*;

public class Comprador extends Agent {

  private Ontology ontologia = OntologiaMercado.getInstance();
  private Codec codec = new SLCodec();
  private ArrayList<Existencias> respuestasRecibidas;
  private int recibidasTotal = 0;
  private int vendedoresTotales = 0;
  private float saldo = 100;
  private ArrayList<Existencias> carrito;
  private Scanner scanner;
  private boolean pagado = false;

  private Direccion direccion;

  @Override
  public void setup() {

    // Obtener direccion de args
    // (calle, num,ciudad,tel)

    direccion = new Direccion();

    Object[] args = getArguments();

    direccion.setCalle(String.valueOf(args[0]));
    direccion.setNumero(Integer.valueOf(String.valueOf(args[1])));
    direccion.setCiudad(String.valueOf(args[2]));
    direccion.setTelefono(String.valueOf(args[3]));

    scanner = new Scanner(System.in);

    this.respuestasRecibidas = new ArrayList<Existencias>();
    this.carrito = new ArrayList<Existencias>();
    this.getContentManager().registerLanguage(codec);
    this.getContentManager().registerOntology(ontologia);
    this.addBehaviour(new Controlador());
  }

  private void listarCarrito() {
    int i = 0;
    if (carrito.size() == 0) {
      System.out.println("---Carrito vacío---");
    } else {

      System.out.println(
          "    Vendedor                             Producto      Cantidad         Subtotal");
      double total = 0.0;
      for (Existencias e : carrito) {
        // Conteo- vendedor - producto - cantidad - precio
        total += e.getCantidad() * e.getPrecio();
        System.out.printf(
            "  %d.- %s   %s  %d   %.2f\n",
            i + 1,
            e.getDueño().getName(),
            e.getProducto().getNombre(),
            e.getCantidad(),
            e.getPrecio());
        i++;
      }
      System.out.println("---");
      System.out.printf("\nTotal: %.2f\n", total);
      System.out.printf("Saldo: %.2f\n", saldo);
    }
  }

  private class Controlador extends SimpleBehaviour {
    @Override
    public void action() {
      System.out.println("Ingrese lo que desea hacer:");
      System.out.println(" 1.  Buscar un producto:");
      System.out.println(" 2.  Ver carrito");
      System.out.println(" 3.  Pagar");
      System.out.println(" 4.  Solicitar envío");
      System.out.println(" 5.  Salir");

      System.out.print("  >> ");
      int opcion = Integer.valueOf(scanner.nextLine());

      switch (opcion) {
        case 1:
          System.out.println("-------------------------------");
          System.out.println("Ingrese un producto a buscar:");
          String nombreProducto = scanner.nextLine();
          System.out.println("Ingrese la cantidad que busca.");
          String cantidad = scanner.nextLine();
          buscarProducto(nombreProducto, Integer.valueOf(cantidad));
          break;

        case 2:
          listarCarrito();
          myAgent.addBehaviour(new Controlador());
          break;

        case 3:
          for (Existencias e : carrito) {
            myAgent.addBehaviour(
                new PagarProducto(
                    e.getDueño(),
                    e.getProducto(),
                    e.getCantidad() * e.getPrecio(),
                    e.getCantidad()));
          }

          break;

        case 4:
          // Obtener los AIDs de los vendedores
          Set<AID> aids = new HashSet<>();

          for (Existencias e : carrito) {
            aids.add(e.getDueño());
          }

          // Solicitar envíos a cada uno
          for (AID aid : aids) {
            solicitarEnvio(aid, direccion);
          }

          break;

        case 5:
        default:
          System.exit(0);
          break;
      }
    }

    @Override
    public boolean done() {
      return true;
    }
  }

  protected void takeDown() {}

  public void solicitarEnvio(AID vendedor, Direccion dir) {
    addBehaviour(new SolicitarEnvioBehaviour(dir, vendedor));
  }

  private class SolicitarEnvioBehaviour extends SimpleBehaviour {
    private Direccion direccion;
    private AID vendedor;

    public SolicitarEnvioBehaviour(Direccion dir, AID vendedor) {
      this.direccion = dir;
      this.vendedor = vendedor;
    }

    @Override
    public void action() {
      ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST);
      SolicitarEnvio solicitud = new SolicitarEnvio();
      solicitud.setVendedor(vendedor);
      solicitud.setDestinatario(getAID());
      solicitud.setDireccion(direccion);

      Action actOp = new Action(getAID(), solicitud);

      mensaje.addReceiver(vendedor);
      mensaje.setOntology(ontologia.getName());
      mensaje.setLanguage(codec.getName());

      try {
        getContentManager().fillContent(mensaje, actOp);
      } catch (Exception e) {
        e.printStackTrace();
      }

      send(mensaje);
    }

    @Override
    public boolean done() {
      addBehaviour(new Controlador());
      return true;
    }
  }

  private class SolicitarProducto extends SimpleBehaviour {
    Disponible pregunta;
    DFAgentDescription destinatario;

    public SolicitarProducto(String nombreProducto, Integer cantidad, DFAgentDescription desc) {
      pregunta = new Disponible(new Producto(nombreProducto), cantidad);

      destinatario = desc;
    }

    public void action() {

      // Eliminar lista de respuestas
      respuestasRecibidas = null;
      respuestasRecibidas = new ArrayList<Existencias>();

      // Preparar solicitud
      ACLMessage mensaje = new ACLMessage(ACLMessage.QUERY_IF);
      mensaje.addReceiver(destinatario.getName());
      mensaje.setOntology(ontologia.getName());
      mensaje.setLanguage(codec.getName());
      try {
        getContentManager().fillContent(mensaje, pregunta);
      } catch (Codec.CodecException e) {
        e.printStackTrace();
      } catch (OntologyException e) {
        e.printStackTrace();
      }
      send(mensaje);
    }

    public boolean done() {
      return true;
    }
  }

  private class PagarProducto extends SimpleBehaviour {

    private AID acreedor;
    private int cantidad;
    private double monto;
    private Producto producto;

    public PagarProducto(AID acreedor, Producto p, double monto, int cantidad) {
      this.acreedor = acreedor;
      this.monto = monto;
      this.producto = p;
      this.cantidad = cantidad;
    }

    @Override
    public void action() {
      ACLMessage mensaje = new ACLMessage(ACLMessage.PROPOSE);
      mensaje.addReceiver(acreedor);
      mensaje.setOntology(ontologia.getName());
      mensaje.setLanguage(codec.getName());

      Pagar pago = new Pagar();

      pago.setAcreedor(acreedor);
      pago.setDeudor(getAID());
      pago.setCantidad(cantidad);
      pago.setMonto(monto);
      pago.setProducto(producto);

      Action actOp = new Action(getAID(), pago);

      try {
        getContentManager().fillContent(mensaje, actOp);
      } catch (Exception e) {
        e.printStackTrace();
      }

      send(mensaje);
    }

    @Override
    public boolean done() {
      // TODO: bloquear hasta recibir respuesta afirmativa
      addBehaviour(new Controlador());
      return true;
    }
  }

  private class RecibirRespuesta extends SimpleBehaviour {
    @Override
    public void action() {
      while (recibidasTotal != vendedoresTotales) {
        ACLMessage mensaje = myAgent.blockingReceive();
        recibidasTotal += 1;
        if (mensaje != null) {
          ContentElement ce = null;
          try {

            // Si es REFUSE, simplemente no tiene el producto.
            if (mensaje.getPerformative() != ACLMessage.REFUSE) {
              ce = myAgent.getContentManager().extractContent(mensaje);

              if (ce instanceof Existencias) {
                Existencias existencias = (Existencias) ce;
                respuestasRecibidas.add(existencias);
              }
            }

            if (recibidasTotal >= vendedoresTotales) {
              // Ya se recibieron todas las respuestas
              if (respuestasRecibidas.size() > 0) {
                // Imprimir respuestas
                System.out.println("Respuestas recibidas:");
                int i = 0;
                System.out.println("    Vendedor  Producto  Precio unitario");
                // Conteo- vendedor - producto - cantidad - precio
                for (Existencias e : respuestasRecibidas) {
                  System.out.printf(
                      "  %d.- %s   %s   %d    %.2f\n",
                      i + 1,
                      e.getDueño().getName(),
                      e.getProducto().getNombre(),
                      e.getCantidad(),
                      e.getPrecio());
                  i++;
                }
                System.out.print("Ingrese el producto a agregar al carrito (0 para cancelar): >> ");
                int recibido = Integer.valueOf(scanner.nextLine());
                Existencias e = respuestasRecibidas.get(i - 1);
                if (recibido != 0 && saldo - e.getCantidad() * e.getPrecio() > 0) {
                  saldo -= e.getCantidad() * e.getPrecio();
                  carrito.add(e);
                } else if (recibido == 0) {
                  System.out.println("--- Operación cancelada ---");
                } else {
                  System.out.println("--- Fondos insuficientes! ---");
                }

              } else {
                System.out.println("-- No se encontraron resultados. ---");
              }
            }

          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }

      myAgent.addBehaviour(new Controlador());
      // else {
      // block();
      // }
    }

    @Override
    public boolean done() {
      return recibidasTotal >= vendedoresTotales;
    }
  }

  public DFAgentDescription[] buscarVendedores() {
    // Buscar vendedores en la sección amarilla.
    DFAgentDescription busqueda = new DFAgentDescription();
    ServiceDescription descriptionServicio = new ServiceDescription();
    descriptionServicio.setType("Vendedor");
    busqueda.addServices(descriptionServicio);
    DFAgentDescription[] resultados = null;
    try {
      resultados = DFService.search(this, busqueda);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return resultados;
  }

  public void buscarProducto(String producto, int cantidad) {
    // Enviar msj a todos los vendedores
    System.out.println("Buscando vendedores.");
    DFAgentDescription[] vendedores = this.buscarVendedores();

    vendedoresTotales = vendedores.length;
    recibidasTotal = 0;

    for (int i = 0; i < vendedores.length; i++) {
      this.addBehaviour(new SolicitarProducto(producto, cantidad, vendedores[i]));
    }

    this.addBehaviour(new RecibirRespuesta());
  }
}
