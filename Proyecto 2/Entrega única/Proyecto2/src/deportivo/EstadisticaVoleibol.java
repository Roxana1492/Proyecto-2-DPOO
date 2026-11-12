package deportivo;

public class EstadisticaVoleibol extends EstadisticaPartido {
    private int aces;
    private int bloqueos;

    public EstadisticaVoleibol(int aces, int bloqueos) {
        this.aces = aces;
        this.bloqueos = bloqueos;
    }

    public int getAces() { return aces; }
    public int getBloqueos() { return bloqueos; }
}