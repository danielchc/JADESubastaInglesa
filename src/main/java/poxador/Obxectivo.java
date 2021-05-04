package poxador;

public class Obxectivo {
    public enum EstadoObxectivo {
        ESPERANDO,
        GANADA,
        EN_CURSO,
        RETIRADO
    }
    private String titulo;
    private int prezoMaximo;
    private int prezoActual;
    private String ganadorActual;
    private EstadoObxectivo estadoObxectivo;

    public Obxectivo(String titulo, int prezoMaximo) {
        this.titulo = titulo;
        this.prezoMaximo = prezoMaximo;
        this.prezoActual=0;
        this.estadoObxectivo=EstadoObxectivo.ESPERANDO;
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

    public EstadoObxectivo getEstadoObxectivo() {
        return estadoObxectivo;
    }

    public void setEstadoObxectivo(EstadoObxectivo estadoObxectivo) {
        this.estadoObxectivo = estadoObxectivo;
    }

    public int getPrezoActual() {
        return prezoActual;
    }

    public void setPrezoActual(int prezoActual) {
        this.prezoActual = prezoActual;
    }
}
