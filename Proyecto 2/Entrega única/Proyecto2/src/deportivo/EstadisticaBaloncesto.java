package deportivo;

public class EstadisticaBaloncesto extends EstadisticaPartido {
    private int puntos;
    private int rebotes;

    public EstadisticaBaloncesto(int puntos, int rebotes) {
        this.puntos = puntos;
        this.rebotes = rebotes;
    }

    public int getPuntos() { return puntos; }
    public int getRebotes() { return rebotes; }
}