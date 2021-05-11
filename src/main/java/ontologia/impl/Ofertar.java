package ontologia.impl;


import jade.content.AgentAction;
import ontologia.*;

/**
* Protege name: Ofertar
* @author OntologyBeanGenerator v4.1
* @version 2021/05/11, 12:30:11
*/
public class Ofertar  implements AgentAction {

  private static final long serialVersionUID = 8268541762640966406L;

  private String _internalInstanceName = null;

  public Ofertar() {
    this._internalInstanceName = "";
  }

  public Ofertar(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: ofertaEnviar
   */
   private Oferta ofertaEnviar;
   public void setOfertaEnviar(Oferta value) { 
    this.ofertaEnviar=value;
   }
   public Oferta getOfertaEnviar() {
     return this.ofertaEnviar;
   }

}
