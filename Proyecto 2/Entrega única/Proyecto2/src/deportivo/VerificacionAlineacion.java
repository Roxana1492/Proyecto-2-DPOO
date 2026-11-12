package deportivo;

import autenticacion.Socio;
import java.util.List;

public class VerificacionAlineacion {
    private VerificacionSancion verificacionSancion = new VerificacionSancion();

    public String verificarPartido(Equipo equipo, List<Socio> jugadores) {
        StringBuilder motivo = new StringBuilder();
        Deporte deporte = equipo.getDeporte();

        if (jugadores.size() < deporte.getMinJugadoresHabilitados()) {
            motivo.append("No cumple con el mínimo de jugadores habilitados (Mínimo: ")
                  .append(deporte.getMinJugadoresHabilitados())
                  .append(", actual: ")
                  .append(jugadores.size())
                  .append("). ");
        }

        for (Socio socio : jugadores) {
            if (!verificacionSancion.estaHabilitado(socio)) {
                motivo.append("El jugador ").append(socio.getNombre()).append(" está suspendido. ");
            }
        }

        return motivo.toString().trim();
    }

    // Validación contra la nómina oficial registrada en la competencia (InscripcionEquipo)
    public String verificarNominaCompetencia(InscripcionEquipo inscripcionEquipo, List<Socio> jugadoresAlineados) {
        StringBuilder motivo = new StringBuilder();
        List<Socio> nominaOficial = inscripcionEquipo.getNominaHabilitada();

        for (Socio socio : jugadoresAlineados) {
            if (!nominaOficial.contains(socio)) {
                motivo.append("El jugador ").append(socio.getNombre()).append(" no está inscrito en la nómina oficial del torneo. ");
            }
        }
        return motivo.toString().trim();
    }

    public boolean verificarNomina(Equipo equipo, List<Socio> jugadores) {
        return jugadores.stream().allMatch(socio -> 
            equipo.getInscripciones().stream()
                  .anyMatch(ij -> ij.getSocio().equals(socio))
        );
    }
}