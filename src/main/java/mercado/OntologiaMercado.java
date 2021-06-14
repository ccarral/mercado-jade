package mercado;

import jade.content.onto.*;
import jade.content.schema.*;

public class OntologiaMercado extends Ontology {

  // Instancia privada de la ontologia
  private static Ontology instance = new OntologiaMercado();

  // Nombre de la ontologia
  public static final String NOMBRE_ONTOLOGIA = "Ontologia-mercado";

  // Vocabulario

  public static final String PRODUCTO = "Producto";
  public static final String NOMBRE_PRODUCTO = "nombre";
  public static final String SOLICITAR = "Solicitar";
  public static final String COMPRAR = "Comprar";
  public static final String DISPONIBLE = "Disponible";
  public static final String CANTIDAD = "cantidad";
  public static final String EXISTENCIAS = "Tengo";
  public static final String PRECIO = "precio";
  public static final String DUENO = "dueño";

  private OntologiaMercado() {
    super(NOMBRE_ONTOLOGIA, BasicOntology.getInstance());

    try {
      add(new ConceptSchema(PRODUCTO), Producto.class);
      add(new PredicateSchema(DISPONIBLE), Disponible.class);
      add(new PredicateSchema(EXISTENCIAS), Existencias.class);
      add(new AgentActionSchema(COMPRAR), Comprar.class);

      ConceptSchema producto = (ConceptSchema) getSchema(PRODUCTO);
      producto.add(NOMBRE_PRODUCTO, (PrimitiveSchema) getSchema(BasicOntology.STRING));

      PredicateSchema disponible = (PredicateSchema) getSchema(DISPONIBLE);

      disponible.add(CANTIDAD, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
      disponible.add(PRODUCTO, (ConceptSchema) getSchema(PRODUCTO));

      PredicateSchema existencias = (PredicateSchema) getSchema(EXISTENCIAS);
      existencias.add(PRODUCTO, (ConceptSchema) getSchema(PRODUCTO));
      existencias.add(PRECIO, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
      existencias.add(CANTIDAD, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
      existencias.add(DUENO, (ConceptSchema) getSchema(BasicOntology.AID));

      AgentActionSchema comprar = (AgentActionSchema) getSchema(COMPRAR);
      comprar.add(PRODUCTO, (ConceptSchema) getSchema(PRODUCTO));
      comprar.add(CANTIDAD, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Ontology getInstance() {
    return instance;
  }
}
