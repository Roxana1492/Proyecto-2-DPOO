package deportivo;

import java.time.LocalDate;
import java.util.List;

import autenticacion.Empleado;
import autenticacion.Usuario;
import fachada.OperacionRechazadaException;

/**
 * Torneo con tarifa. Lo recaudado forma el fondo de premios, que se reparte
 * 60%, 30% y 10% entre los tres primeros. Los empleados no pagan y no reciben
 * efectivo (tampoco una pareja donde haya un empleado).
 */
public class TorneoCompetitivo extends Torneo {

    private static final double[] REPARTO = { 0.60, 0.30, 0.10 };

    private double tarifa;
    private double fondoPremios;

    public TorneoCompetitivo(String id, String nombre, String disciplina, Modalidad modalidad, LocalDate fecha,
            Categoria categoria, int cuposTotales, double tarifa) {
        super(id, nombre, disciplina, modalidad, fecha, categoria, cuposTotales);
        if (tarifa <= 0) {
            throw new OperacionRechazadaException("Un torneo competitivo debe tener tarifa mayor a 0.");
        }
        this.tarifa = tarifa;
        this.fondoPremios = 0;
    }

    // Paga la tarifa salvo que todos los integrantes sean empleados
    @Override
    public double calcularCobro(ParticipanteTorneo p) {
        for (Usuario u : p.darIntegrantes()) {
            if (!(u instanceof Empleado)) {
                return tarifa;
            }
        }
        return 0;
    }

    @Override
    public void inscribir(InscripcionTorneo inscripcion) {
        super.inscribir(inscripcion);
        fondoPremios += inscripcion.getMontoPagado();
    }

    @Override
    public InscripcionTorneo desinscribir(String login) {
        InscripcionTorneo inscripcion = super.desinscribir(login);
        devolverTarifa(inscripcion);
        return inscripcion;
    }

    public void devolverTarifa(InscripcionTorneo inscripcion) {
        fondoPremios -= inscripcion.getMontoPagado();
    }

    @Override
    public void repartirPremios(List<PosicionTorneo> tabla) {
        if (fondoPremios == 0) {
            return; // todos eran empleados: no hay nada que repartir
        }
        int puesto = 0;
        for (PosicionTorneo posicion : tabla) {
            ParticipanteTorneo p = posicion.getParticipante();
            if (puesto < REPARTO.length && !p.incluyeEmpleado()) {
                premios.add(new PremioEfectivo(puesto + 1, p, fondoPremios * REPARTO[puesto]));
                puesto++;
            }
        }
        // Si hubo menos de 3 premiados, lo que sobra queda en el fondo general del club
    }

    public double getTarifa() { return tarifa; }
    public double getFondoPremios() { return fondoPremios; }
}
