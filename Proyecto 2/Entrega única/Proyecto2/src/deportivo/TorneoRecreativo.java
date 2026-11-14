package deportivo;

import java.time.LocalDate;
import java.util.List;

import autenticacion.Usuario;

/** Torneo sin tarifa. El ganador recibe un bono del 20% (uno por integrante). */
public class TorneoRecreativo extends Torneo {

    private double porcentajeBono = 20;

    public TorneoRecreativo(String id, String nombre, String disciplina, Modalidad modalidad, LocalDate fecha,
            Categoria categoria, int cuposTotales) {
        super(id, nombre, disciplina, modalidad, fecha, categoria, cuposTotales);
    }

    @Override
    public double calcularCobro(ParticipanteTorneo p) {
        return 0;
    }

    @Override
    public void repartirPremios(List<PosicionTorneo> tabla) {
        ParticipanteTorneo ganador = tabla.get(0).getParticipante();
        int numero = 1;
        for (Usuario u : ganador.darIntegrantes()) {
            BonoDescuento bono = new BonoDescuento("B-" + getId() + "-" + numero, porcentajeBono, u, 1, ganador);
            u.agregarBono(bono);
            premios.add(bono);
            numero++;
        }
    }

    public double getPorcentajeBono() { return porcentajeBono; }
}
