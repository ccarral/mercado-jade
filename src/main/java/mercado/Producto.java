package mercado;

import jade.content.Concept;

public class Producto implements Concept {

  private String nombre;

  public Producto(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return this.nombre;
  }
}
