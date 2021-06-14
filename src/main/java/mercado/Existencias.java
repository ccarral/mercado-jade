package mercado;

import jade.content.*;
import jade.core.*;

public class Existencias implements Predicate {
  private Producto producto;
  private int cantidad;
  private double precio;
  private AID dueño;

  public void setDueño(AID id) {
    this.dueño = id;
  }

  public AID getDueño() {
    return this.dueño;
  }

  public int getCantidad() {
    return cantidad;
  }

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }

  public double getPrecio() {
    return precio;
  }

  public void setPrecio(double precio) {
    this.precio = precio;
  }

  public Existencias(String nombreProducto, Integer cantidad, Double precioUnitario) {
    producto = new Producto(nombreProducto);
    this.cantidad = cantidad;
    this.precio = precioUnitario;
  }

  public Existencias() {
    producto = null;
    cantidad = 0;
    precio = 0.0;
  }

  public Producto getProducto() {
    return this.producto;
  }

  public void setProducto(Producto p) {
    this.producto = p;
  }
}
