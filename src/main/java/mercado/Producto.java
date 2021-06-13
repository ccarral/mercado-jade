package mercado;

import jade.content.Concept;

public class Producto implements Concept {

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  private String nombre;

  public Producto(String nombre) {
    this.nombre = nombre;
  }

  public Producto(){
    nombre = null;
  }

  public String getNombre() {
    return this.nombre;
  }
}
