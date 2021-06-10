package mercado;

public class Existencias {
  private Producto producto;
  private int cantidad;
  private double precioUnitario;

  public Existencias(String nombreProducto, Integer cantidad, Double precioUnitario) {
    producto = new Producto(nombreProducto);
    this.cantidad = cantidad;
    this.precioUnitario = precioUnitario;
  }
}
