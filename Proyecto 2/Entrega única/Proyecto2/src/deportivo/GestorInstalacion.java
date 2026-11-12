package deportivo;

import autenticacion.Socio;
import java.util.ArrayList;
import java.util.List;

public class GestorInstalacion {
    private int maxSimultaneas;
    private List<ReservaInstalacion> reservasActivas;

    public GestorInstalacion(int maxSimultaneas) {
        this.maxSimultaneas = maxSimultaneas;
        this.reservasActivas = new ArrayList<>();
    }

    public boolean reservar(Instalacion instalacion, Socio socioResponsable, String bloqueHorario, List<Socio> jugadores, boolean tieneBebidaCaliente) {
        // Validar restricción de bebidas calientes
        if (instalacion.restringeBebidasCalientes() && tieneBebidaCaliente) {
            return false; 
        }

        // Validar capacidad o modalidad (ej. dobles requiere 4 jugadores)
        if (jugadores.size() < instalacion.getCapacidadRequeridaModalidad()) {
            return false;
        }

        // Contar cuántas instalaciones de la misma disciplina están ocupadas en el mismo bloque horario
        long ocupadasEnBloque = reservasActivas.stream()
                .filter(r -> r.getBloqueHorario().equals(bloqueHorario) && 
                             r.getInstalacion().getDisciplina().equals(instalacion.getDisciplina()))
                .count();

        if (ocupadasEnBloque >= maxSimultaneas) {
            return false; 
        }

        // Registrar la reserva
        ReservaInstalacion nuevaReserva = new ReservaInstalacion(instalacion, socioResponsable, bloqueHorario, jugadores, tieneBebidaCaliente);
        reservasActivas.add(nuevaReserva);
        return true;
    }

    public List<ReservaInstalacion> getReservasActivas() {
        return reservasActivas;
    }
}