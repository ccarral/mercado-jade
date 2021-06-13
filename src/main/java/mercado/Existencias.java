package mercado;

import jade.content.Predicate;

public class Existencias implements Predicate {
  private Producto producto;
  private int cantidad;
  private double precio;


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

  public Producto getProducto() {
    return this.producto;
  }
}
