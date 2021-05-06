package vendedor;

import jade.core.AID;

import java.util.ArrayList;

public class Subasta {

    private String titulo;
    private AID ganadorActual;
    private int prezo;
    private int incremento;
    private ArrayList<AID> interesados;
    private boolean finalizada;

    public Subasta(String titulo, int prezo, int incremento) {
        this.titulo = titulo;
        this.prezo = prezo;
        this.incremento = incremento;
        this.interesados =new ArrayList<>();
        this.finalizada=false;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public AID getGanadorActual() {
        return ganadorActual;
    }

    public void setGanadorActual(AID ganadorActual) {
        this.ganadorActual = ganadorActual;
    }

    public int getPrezo() {
        return prezo;
    }

    public void setPrezo(int prezo) {
        this.prezo = prezo;
    }

    public int getIncremento() {
        return incremento;
    }

    public void setIncremento(int incremento) {
        this.incremento = incremento;
    }

    public ArrayList<AID> getInteresados() {
        return interesados;
    }

    public void setInteresados(ArrayList<AID> interesados) {
        this.interesados = interesados;
    }

    public void engadirInteresado(AID poxador) {
        this.interesados.add(poxador);
    }

    public void eliminarInteresado(AID poxador){
        this.interesados.remove(poxador);
    }

    public void eliminarInteresados(){
        this.interesados.clear();
    }

    public void engadirIncremento(){
        this.prezo+=incremento;
    }



    public int prezoAnterior(){
        return this.prezo-this.incremento;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }
}
