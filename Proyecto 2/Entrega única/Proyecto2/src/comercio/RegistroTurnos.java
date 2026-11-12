package comercio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import deportivo.SolicitudCambioTurno;
import deportivo.Turno;

public class RegistroTurnos {

    private static final int MIN_ENTRENADORES = 2;
    private static final int MIN_FISIOTERAPEUTAS = 1;

    private List<Turno> turnos;

    public RegistroTurnos() {
        this.turnos = new ArrayList<>();
    }

    public RegistroTurnos(List<Turno> turnos) {
        this.turnos = (turnos == null) ? new ArrayList<>() : new ArrayList<>(turnos);
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    public void agregarTurno(Turno turno) {
        if (turno != null) {
            turnos.add(turno);
        }
    }

    public boolean verificarCobertura(SolicitudCambioTurno solicitud) {
        if (solicitud == null) {
            return false;
        }

        Turno turno = solicitud.getTurnoOrigen();

        if (turno == null) {
            return false;
        }

        String solicitante = solicitud.getEmpleadoSolicitante();
        String reemplazo = solicitud.getEmpleadoReemplazo();

        if (solicitante == null || reemplazo == null) {
            return false;
        }

        if (solicitante.trim().isEmpty() || reemplazo.trim().isEmpty()) {
            return false;
        }

        if (solicitante.equals(reemplazo)) {
            return false;
        }

        return cumpleCoberturaMinima(solicitud);
    }

    private boolean cumpleCoberturaMinima(SolicitudCambioTurno solicitud) {
        Turno turnoOrigen = solicitud.getTurnoOrigen();
        String solicitante = solicitud.getEmpleadoSolicitante();
        String reemplazo = solicitud.getEmpleadoReemplazo();

        int entrenadores = 0;
        int fisioterapeutas = 0;

        for (Turno turno : turnos) {
            boolean mismoMomento = Objects.equals(turno.getDiaSemana(), turnoOrigen.getDiaSemana())
                    && Objects.equals(turno.getFranjaHoraria(), turnoOrigen.getFranjaHoraria());

            if (!mismoMomento) {
                continue;
            }

            if (solicitante.equals(turno.getEmpleadoId()) || reemplazo.equals(turno.getEmpleadoId())) {
                continue;
            }

            if ("ENTRENADOR".equalsIgnoreCase(turno.getRol())) {
                entrenadores++;
            } else if ("FISIOTERAPEUTA".equalsIgnoreCase(turno.getRol())) {
                fisioterapeutas++;
            }
        }

        if ("ENTRENADOR".equalsIgnoreCase(turnoOrigen.getRol())) {
            entrenadores++;
        } else if ("FISIOTERAPEUTA".equalsIgnoreCase(turnoOrigen.getRol())) {
            fisioterapeutas++;
        }

        return entrenadores >= MIN_ENTRENADORES
                && fisioterapeutas >= MIN_FISIOTERAPEUTAS;
    }

    public void resolverSolicitud(SolicitudCambioTurno solicitud) {
        if (solicitud == null) {
            return;
        }

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            return;
        }

        if (verificarCobertura(solicitud)) {
            solicitud.setEstado("APROBADA");

            Turno turno = solicitud.getTurnoOrigen();
            turno.setEmpleadoId(solicitud.getEmpleadoReemplazo());
            turno.setNombreEmpleado(solicitud.getEmpleadoReemplazo());
        } else {
            solicitud.setEstado("RECHAZADA");
        }
    }
}