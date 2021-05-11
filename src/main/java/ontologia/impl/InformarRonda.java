package ontologia.impl;


import jade.content.AgentAction;
import ontologia.*;

/**
* Protege name: InformarRonda
* @author OntologyBeanGenerator v4.1
* @version 2021/05/11, 12:30:11
*/
public class InformarRonda implements AgentAction {

  private static final long serialVersionUID = 8268541762640966406L;

  private String _internalInstanceName = null;

  public InformarRonda() {
    this._internalInstanceName = "";
  }

  public InformarRonda(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: ofertaGanadoraRonda
   */
   private Oferta ofertaGanadoraRonda;
   public void setOfertaGanadoraRonda(Oferta value) { 
    this.ofertaGanadoraRonda=value;
   }
   public Oferta getOfertaGanadoraRonda() {
     return this.ofertaGanadoraRonda;
   }

   /**
   * Protege name: ganadorRonda
   */
   private jade.core.AID ganadorRonda;
   public void setGanadorRonda(jade.core.AID value) { 
    this.ganadorRonda=value;
   }
   public jade.core.AID getGanadorRonda() {
     return this.ganadorRonda;
   }

}
