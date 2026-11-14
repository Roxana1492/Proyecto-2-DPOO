package deportivo;

public class PosicionTorneo {

    private ParticipanteTorneo participante;
    private int puntos;
    private int marcadorFavor;
    private int marcadorContra;
    private int ordenInscripcion;

    public PosicionTorneo(ParticipanteTorneo participante, int ordenInscripcion) {
        this.participante = participante;
        this.ordenInscripcion = ordenInscripcion;
    }

    public int diferencia() {
        return marcadorFavor - marcadorContra;
    }

    public void sumar(int puntos, int favor, int contra) {
        this.puntos += puntos;
        this.marcadorFavor += favor;
        this.marcadorContra += contra;
    }

    public ParticipanteTorneo getParticipante() { return participante; }
    public int getPuntos() { return puntos; }
    public int getMarcadorFavor() { return marcadorFavor; }
    public int getMarcadorContra() { return marcadorContra; }
    public int getOrdenInscripcion() { return ordenInscripcion; }
}
