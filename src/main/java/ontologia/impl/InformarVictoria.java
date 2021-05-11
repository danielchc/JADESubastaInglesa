package ontologia.impl;


import jade.content.AgentAction;
import ontologia.*;

/**
* Protege name: InformarVictoria
* @author OntologyBeanGenerator v4.1
* @version 2021/05/11, 12:30:11
*/
public class InformarVictoria implements AgentAction {

  private static final long serialVersionUID = 8268541762640966406L;

  private String _internalInstanceName = null;

  public InformarVictoria() {
    this._internalInstanceName = "";
  }

  public InformarVictoria(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: agenteGanador
   */
   private jade.core.AID agenteGanador;
   public void setAgenteGanador(jade.core.AID value) { 
    this.agenteGanador=value;
   }
   public jade.core.AID getAgenteGanador() {
     return this.agenteGanador;
   }

   /**
   * Protege name: ofertaGanadora
   */
   private Oferta ofertaGanadora;
   public void setOfertaGanadora(Oferta value) { 
    this.ofertaGanadora=value;
   }
   public Oferta getOfertaGanadora() {
     return this.ofertaGanadora;
   }

}
