package deportivo;

/** Une a un participante con el torneo: guarda el tipo de cupo y lo que pagó. */
public class InscripcionTorneo {

    private ParticipanteTorneo participante;
    private boolean preferencial;
    private double montoPagado;
    private String confirmacionPago;
    private int orden;

    public InscripcionTorneo(ParticipanteTorneo participante, boolean preferencial, double montoPagado,
            String confirmacionPago, int orden) {
        this.participante = participante;
        this.preferencial = preferencial;
        this.montoPagado = montoPagado;
        this.confirmacionPago = confirmacionPago;
        this.orden = orden;
    }

    // En dobles cada pareja ocupa 2 cupos
    public int cuposQueOcupa() {
        if (participante instanceof Pareja) {
            return 2;
        }
        return 1;
    }

    public ParticipanteTorneo getParticipante() { return participante; }
    public boolean isPreferencial() { return preferencial; }
    public void setPreferencial(boolean preferencial) { this.preferencial = preferencial; }
    public double getMontoPagado() { return montoPagado; }
    public String getConfirmacionPago() { return confirmacionPago; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}
