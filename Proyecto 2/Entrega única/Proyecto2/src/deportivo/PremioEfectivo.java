package deportivo;

/** Premio en efectivo de un torneo competitivo. El pago lo hace el club por fuera del sistema. */
public class PremioEfectivo extends Premio {

    private double monto;

    public PremioEfectivo(int puesto, ParticipanteTorneo beneficiario, double monto) {
        super(puesto, beneficiario);
        this.monto = monto;
    }

    public double getMonto() { return monto; }

    @Override
    public String describir() {
        String texto = puesto + ". puesto: $" + String.format("%.0f", monto) + " para " + beneficiario.darNombre();
        if (beneficiario instanceof EquipoTorneo) {
            texto += " (se entrega al capitán " + ((EquipoTorneo) beneficiario).getCapitan().darNombre() + ")";
        }
        return texto;
    }
}
