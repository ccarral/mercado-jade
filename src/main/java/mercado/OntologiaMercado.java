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
  public static final String DISPONIBLE = "Disponible";
  public static final String CANTIDAD = "cantidad";

  private OntologiaMercado() {
    super(NOMBRE_ONTOLOGIA, BasicOntology.getInstance());

    try {
      add(new ConceptSchema(PRODUCTO), Producto.class);
      add(new PredicateSchema(DISPONIBLE), Disponible.class);

      ConceptSchema producto = (ConceptSchema) getSchema(PRODUCTO);
      producto.add(NOMBRE_PRODUCTO, (PrimitiveSchema) getSchema(BasicOntology.STRING));

      PredicateSchema disponible = (PredicateSchema) getSchema(DISPONIBLE);
      disponible.add(CANTIDAD, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
      disponible.add(PRODUCTO, (ConceptSchema) getSchema(PRODUCTO));

    } catch (OntologyException oe) {
      oe.printStackTrace();
    }
  }

  public static Ontology getInstance() {
    return instance;
  }
}
