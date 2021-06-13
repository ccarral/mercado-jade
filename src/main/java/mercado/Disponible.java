package mercado;

import jade.content.*;

public class Disponible implements Predicate{
  private Producto producto;
  private Integer cantidad;

 public Disponible(Producto p, Integer c){
    producto = p;
    cantidad = c;
  }

  public Disponible(){
     producto = null;
     cantidad = 0;
  }


  public Producto getProducto() {
    return this.producto;
  }

  public void setProducto(Producto p) {
    this.producto = p;
  }

  public Integer getCantidad() {
    return this.cantidad;
  }

  public void setCantidad(Integer p) {
    this.cantidad = p;
  }
}
