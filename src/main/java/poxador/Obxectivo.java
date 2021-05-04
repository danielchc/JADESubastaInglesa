package poxador;

public class Obxectivo {
    private String titulo;
    private int prezoMaximo;
    private String ganadorActual;
    private boolean poxaFinalizada;

    public Obxectivo(String titulo, int prezoMaximo) {
        this.titulo = titulo;
        this.prezoMaximo = prezoMaximo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getPrezoMaximo() {
        return prezoMaximo;
    }

    public void setPrezoMaximo(int prezoMaximo) {
        this.prezoMaximo = prezoMaximo;
    }

    public String getGanadorActual() {
        return ganadorActual;
    }

    public void setGanadorActual(String ganadorActual) {
        this.ganadorActual = ganadorActual;
    }

    public boolean isPoxaFinalizada() {
        return poxaFinalizada;
    }

    public void setPoxaFinalizada(boolean poxaFinalizada) {
        this.poxaFinalizada = poxaFinalizada;
    }
}
