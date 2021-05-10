package ontologia.impl;


import ontologia.*;

/**
* Protege name: Ofertar
* @author OntologyBeanGenerator v4.1
* @version 2021/05/10, 18:20:01
*/
public class DefaultOfertar implements Ofertar {

  private static final long serialVersionUID = -5916493442331476618L;

  private String _internalInstanceName = null;

  public DefaultOfertar() {
    this._internalInstanceName = "";
  }

  public DefaultOfertar(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: oferta
   */
   private Oferta oferta;
   public void setOferta(Oferta value) { 
    this.oferta=value;
   }
   public Oferta getOferta() {
     return this.oferta;
   }

}
