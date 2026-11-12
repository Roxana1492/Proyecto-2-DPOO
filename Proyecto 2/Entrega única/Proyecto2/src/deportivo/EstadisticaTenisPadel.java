package deportivo;

public class EstadisticaTenisPadel extends EstadisticaPartidoIndividual {
    private int setsGanados;
    private int gamesGanados;
    private int aces;
    private int erroresNoForzados;

    public EstadisticaTenisPadel(int setsGanados, int gamesGanados, int aces, int erroresNoForzados) {
        super();
        this.setsGanados = setsGanados;
        this.gamesGanados = gamesGanados;
        this.aces = aces;
        this.erroresNoForzados = erroresNoForzados;
    }

    public int getSetsGanados() { return setsGanados; }
    public void setSetsGanados(int setsGanados) { this.setsGanados = setsGanados; }
    public int getGamesGanados() { return gamesGanados; }
    public void setGamesGanados(int gamesGanados) { this.gamesGanados = gamesGanados; }
    public int getAces() { return aces; }
    public void setAces(int aces) { this.aces = aces; }
    public int getErroresNoForzados() { return erroresNoForzados; }
    public void setErroresNoForzados(int erroresNoForzados) { this.erroresNoForzados = erroresNoForzados; }
}
