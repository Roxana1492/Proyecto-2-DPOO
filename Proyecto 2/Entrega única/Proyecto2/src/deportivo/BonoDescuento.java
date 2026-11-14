package deportivo;

import autenticacion.Usuario;
import fachada.OperacionRechazadaException;

/**
 * Bono del 20% que se gana en un torneo recreativo. Es de un solo uso y no
 * se puede combinar con puntos ni con códigos de empleado.
 */
public class BonoDescuento extends Premio {

    private String id;
    private double porcentaje;
    private Usuario titular;
    private boolean usado;

    public BonoDescuento(String id, double porcentaje, Usuario titular, int puesto, ParticipanteTorneo beneficiario) {
        super(puesto, beneficiario);
        this.id = id;
        this.porcentaje = porcentaje;
        this.titular = titular;
        this.usado = false;
    }

    /** @return el valor que se descuenta del subtotal. */
    public double canjear(String login, double subtotal, boolean otroDescuento) {
        if (!titular.getLogin().equals(login)) {
            throw new OperacionRechazadaException("Ese bono no es suyo.");
        }
        if (usado) {
            throw new OperacionRechazadaException("Ese bono ya fue usado.");
        }
        if (otroDescuento) {
            throw new OperacionRechazadaException("El bono no se puede combinar con puntos ni códigos de empleado.");
        }
        usado = true;
        return subtotal * porcentaje / 100;
    }

    public void marcarUsado() {
        this.usado = true;
    }

    public String getId() { return id; }
    public double getPorcentaje() { return porcentaje; }
    public Usuario getTitular() { return titular; }
    public boolean isUsado() { return usado; }

    @Override
    public String describir() {
        return "Bono " + id + " del " + String.format("%.0f", porcentaje) + "% para " + titular.darNombre();
    }
}
