package mercado;

import jade.content.Predicate;

public class Disponible implements Predicate {
  private Producto producto;
  private Integer cantidad;

  public Producto getProducto() {
    return this.producto;
  }

  public void setProducto(Producto p) {
    this.producto = p;
  }

  public Integer getCantidad() {
    return this.cantidad;
  }

  public void setCantidad(Integer c) {
    this.cantidad = c;
  }
}
