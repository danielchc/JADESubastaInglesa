package ontologia.impl;


import ontologia.*;

/**
* Protege name: Oferta
* @author OntologyBeanGenerator v4.1
* @version 2021/05/10, 18:20:01
*/
public class DefaultOferta implements Oferta {

  private static final long serialVersionUID = -5916493442331476618L;

  private String _internalInstanceName = null;

  public DefaultOferta() {
    this._internalInstanceName = "";
  }

  public DefaultOferta(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: titulo
   */
   private String titulo;
   public void setTitulo(String value) { 
    this.titulo=value;
   }
   public String getTitulo() {
     return this.titulo;
   }

   /**
   * Protege name: prezo
   */
   private int prezo;
   public void setPrezo(int value) { 
    this.prezo=value;
   }
   public int getPrezo() {
     return this.prezo;
   }

}
