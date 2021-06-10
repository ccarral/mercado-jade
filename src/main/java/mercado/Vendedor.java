package mercado;

import jade.core.Agent;
import java.util.*;

public class Vendedor extends Agent {

  private LinkedList<Existencias> existencias;

  @Override
  public void setup() {
    this.existencias = new LinkedList<Existencias>();
    Object[] args = this.getArguments();
    for (int i = 0; i < args.length; i += 3) {
      String producto = args[i].toString();
      Integer cantidad = Integer.valueOf(args[i + 1].toString());
      Double precio = Double.valueOf(args[i + 2].toString());
      this.existencias.push(new Existencias(producto, cantidad, precio));
    }
  }

  public void takeDown() {}
}
