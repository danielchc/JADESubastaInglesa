package ontologia.impl;


import jade.content.AgentAction;
import ontologia.*;

/**
* Protege name: Proponer
* @author OntologyBeanGenerator v4.1
* @version 2021/05/11, 12:30:11
*/
public class Proponer implements AgentAction {

  private static final long serialVersionUID = 8268541762640966406L;

  private String _internalInstanceName = null;

  public Proponer() {
    this._internalInstanceName = "";
  }

  public Proponer(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: propostaOferta
   */
   private Oferta propostaOferta;
   public void setPropostaOferta(Oferta value) { 
    this.propostaOferta=value;
   }
   public Oferta getPropostaOferta() {
     return this.propostaOferta;
   }

}
