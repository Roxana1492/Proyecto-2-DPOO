package deportivo;

/**
 * Partido de un torneo. Si el visitante es null es un descanso (pasa cuando
 * el número de participantes es impar).
 */
public class Encuentro {

    private String id;
    private int ronda;
    private ParticipanteTorneo local;
    private ParticipanteTorneo visitante;
    private int marcadorLocal;
    private int marcadorVisitante;
    private boolean jugado;

    public Encuentro(String id, int ronda, ParticipanteTorneo local, ParticipanteTorneo visitante) {
        this.id = id;
        this.ronda = ronda;
        this.local = local;
        this.visitante = visitante;
        this.jugado = false;
    }

    public void registrarResultado(int local, int visitante) {
        this.marcadorLocal = local;
        this.marcadorVisitante = visitante;
        this.jugado = true;
    }

    public boolean esDescanso() {
        return visitante == null;
    }

    /** null si no se ha jugado o si quedó empatado. */
    public ParticipanteTorneo ganador() {
        if (!jugado || marcadorLocal == marcadorVisitante) {
            return null;
        }
        return marcadorLocal > marcadorVisitante ? local : visitante;
    }

    public String getId() { return id; }
    public int getRonda() { return ronda; }
    public ParticipanteTorneo getLocal() { return local; }
    public ParticipanteTorneo getVisitante() { return visitante; }
    public int getMarcadorLocal() { return marcadorLocal; }
    public int getMarcadorVisitante() { return marcadorVisitante; }
    public boolean isJugado() { return jugado; }
}
