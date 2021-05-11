package ontologia.impl;


import ontologia.*;

/**
* Protege name: Oferta
* @author OntologyBeanGenerator v4.1
* @version 2021/05/11, 12:30:11
*/
public class Oferta implements jade.content.Concept {

  private static final long serialVersionUID = 8268541762640966406L;

  private String _internalInstanceName = null;

  public Oferta() {
    this._internalInstanceName = "";
  }

  public Oferta(String instance_name) {
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
