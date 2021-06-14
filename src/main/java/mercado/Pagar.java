package mercado;

import jade.content.AgentAction;
import jade.core.AID;

// Pagar(Luis,Alejandra, $100,"Manzana",5);

public class Pagar implements AgentAction {
  private AID deudor = null;
  private AID acreedor = null;
  private double monto = 0;
  private Producto producto = null;
  private int cantidad = 0;

  public AID getDeudor() {
    return deudor;
  }

  public void setDeudor(AID deudor) {
    this.deudor = deudor;
  }

  public AID getAcreedor() {
    return acreedor;
  }

  public void setAcreedor(AID acreedor) {
    this.acreedor = acreedor;
  }

  public double getMonto() {
    return monto;
  }

  public void setMonto(double monto) {
    this.monto = monto;
  }

  public Producto getProducto() {
    return producto;
  }

  public void setProducto(Producto p) {
    this.producto = p;
  }

  public String toString() {
    return String.format(
        "%s  %s  %.2f", getDeudor().getName(), getProducto().getNombre(), getMonto());
  }

  public int getCantidad() {
    return cantidad;
  }

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }
}
