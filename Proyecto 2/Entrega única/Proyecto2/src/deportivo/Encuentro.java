package deportivo;

public class Encuentro {
    private String idEncuentro;
    private int ronda;
    private Object participante1;
    private Object participante2;
    private Integer marcador1;
    private Integer marcador2;
    private Integer desempate1;
    private Integer desempate2;
    private boolean jugado;

    public Encuentro(String idEncuentro, int ronda, Object participante1, Object participante2) {
        this.idEncuentro = idEncuentro;
        this.ronda = ronda;
        this.participante1 = participante1;
        this.participante2 = participante2;
        this.jugado = false;
    }

    public boolean registrarMarcador(int m1, int m2, boolean permiteEmpate) {
        if (this.jugado || m1 < 0 || m2 < 0) {
            return false;
        }
        if (!permiteEmpate && m1 == m2) {
            return false; // La modalidad exige un ganador directo o desempate
        }

        this.marcador1 = m1;
        this.marcador2 = m2;
        this.jugado = true;
        return true;
    }

    // HU-T09: Registrar marcador con desempate (ej. tie-break o penales cuando el tiempo regular quedó empatado)
    public boolean registrarMarcadorConDesempate(int m1, int m2, int d1, int d2) {
        if (this.jugado || m1 < 0 || m2 < 0 || d1 < 0 || d2 < 0) {
            return false;
        }
        if (m1 != m2 || d1 == d2) {
            return false; // El desempate solo aplica si hubo empate regular y el desempate define un ganador
        }

        this.marcador1 = m1;
        this.marcador2 = m2;
        this.desempate1 = d1;
        this.desempate2 = d2;
        this.jugado = true;
        return true;
    }

    public Object getGanador() {
        if (!this.jugado) {
            return null;
        }
        if (this.marcador1 > this.marcador2) {
            return this.participante1;
        } else if (this.marcador2 > this.marcador1) {
            return this.participante2;
        } else if (this.desempate1 != null && this.desempate2 != null) {
            return (this.desempate1 > this.desempate2) ? this.participante1 : this.participante2;
        }
        return null; // Empate oficial
    }

    public boolean esEmpate() {
        return this.jugado && getGanador() == null;
    }

    // Getters
    public String getIdEncuentro() { return idEncuentro; }
    public int getRonda() { return ronda; }
    public Object getParticipante1() { return participante1; }
    public Object getParticipante2() { return participante2; }
    public Integer getMarcador1() { return marcador1; }
    public Integer getMarcador2() { return marcador2; }
    public Integer getDesempate1() { return desempate1; }
    public Integer getDesempate2() { return desempate2; }
    public boolean isJugado() { return jugado; }
}