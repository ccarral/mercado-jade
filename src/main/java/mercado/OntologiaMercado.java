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
  public static final String PAGAR = "Pagar";
  public static final String MONTO = "monto";
  public static final String DISPONIBLE = "Disponible";
  public static final String CANTIDAD = "cantidad";
  public static final String EXISTENCIAS = "Tengo";
  public static final String PRECIO = "precio";
  public static final String DUENO = "dueño";
  public static final String ACREEDOR = "acreedor";
  public static final String DEUDOR = "deudor";
  public static final String SOLICITARENVIO = "solicitarEnvio";
  public static final String DIRECCION = "direccion";
  public static final String DESTINATARIO = "destinatario";
  public static final String DIREC = "direc";
  public static final String VENDEDOR = "vendedor";
  public static final String CALLE = "calle";
  public static final String NUMERO = "numero";
  public static final String CIUDAD = "ciudad";
  public static final String TELEFONO = "telefono";

  private OntologiaMercado() {
    super(NOMBRE_ONTOLOGIA, BasicOntology.getInstance());

    try {
      add(new ConceptSchema(PRODUCTO), Producto.class);
      add(new PredicateSchema(DISPONIBLE), Disponible.class);
      add(new PredicateSchema(EXISTENCIAS), Existencias.class);
      add(new AgentActionSchema(PAGAR), Pagar.class);
      add(new AgentActionSchema(SOLICITARENVIO), SolicitarEnvio.class);
      add(new ConceptSchema(DIRECCION), Direccion.class);

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

      AgentActionSchema pagar = (AgentActionSchema) getSchema(PAGAR);
      pagar.add(PRODUCTO, (ConceptSchema) getSchema(PRODUCTO));
      pagar.add(MONTO, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
      pagar.add(ACREEDOR, (ConceptSchema) getSchema(BasicOntology.AID));
      pagar.add(DEUDOR, (ConceptSchema) getSchema(BasicOntology.AID));
      pagar.add(CANTIDAD, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

      AgentActionSchema solicitarEnvio = (AgentActionSchema) getSchema(SOLICITARENVIO);
      solicitarEnvio.add(DESTINATARIO, (ConceptSchema) getSchema(BasicOntology.AID));
      solicitarEnvio.add(DIRECCION, (ConceptSchema) getSchema(DIRECCION));
      solicitarEnvio.add(VENDEDOR, (ConceptSchema) getSchema(BasicOntology.AID));

      ConceptSchema direccion = (ConceptSchema) getSchema(DIRECCION);
      direccion.add(CALLE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
      direccion.add(NUMERO, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
      direccion.add(CIUDAD, (PrimitiveSchema) getSchema(BasicOntology.STRING));
      direccion.add(TELEFONO, (PrimitiveSchema) getSchema(BasicOntology.STRING));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Ontology getInstance() {
    return instance;
  }
}
