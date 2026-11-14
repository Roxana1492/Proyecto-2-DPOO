package deportivo;

public abstract class Premio {

    protected int puesto;
    protected ParticipanteTorneo beneficiario;

    public Premio(int puesto, ParticipanteTorneo beneficiario) {
        this.puesto = puesto;
        this.beneficiario = beneficiario;
    }

    public int getPuesto() { return puesto; }
    public ParticipanteTorneo getBeneficiario() { return beneficiario; }

    public abstract String describir();
}
