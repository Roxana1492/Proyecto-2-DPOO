package deportivo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VerificacionReserva {

    private List<ReservaInstalacion> reservasActivas;

    public VerificacionReserva() {
        this.reservasActivas = new ArrayList<>();
    }

    public VerificacionReserva(GestorInstalacion gestor) {
        if (gestor == null) {
            this.reservasActivas = new ArrayList<>();
        } else {
            this.reservasActivas = gestor.getReservasActivas();
        }
    }

    public VerificacionReserva(List<ReservaInstalacion> reservasActivas) {
        if (reservasActivas == null) {
            this.reservasActivas = new ArrayList<>();
        } else {
            this.reservasActivas = reservasActivas;
        }
    }

    public boolean verificarDisponibilidad(
            Instalacion instalacion,
            Disciplina disciplina,
            Date fecha,
            String bloqueHorario) {

        if (instalacion == null || disciplina == null
                || fecha == null || bloqueHorario == null
                || bloqueHorario.trim().isEmpty()
                || instalacion.getDisciplina() == null
                || disciplina.getNombre() == null) {
            return false;
        }

        if (!instalacion.getDisciplina().equalsIgnoreCase(disciplina.getNombre())) {
            return false;
        }

        int instalacionesOcupadas = 0;

        for (ReservaInstalacion reserva : reservasActivas) {
            if (reserva == null || reserva.getInstalacion() == null
                    || reserva.getBloqueHorario() == null) {
                continue;
            }

            boolean mismoBloque = reserva.getBloqueHorario()
                    .equalsIgnoreCase(bloqueHorario);

            if (!mismoBloque) {
                continue;
            }

            // La instalación pedida ya está reservada en ese bloque
            if (esLaMismaInstalacion(reserva.getInstalacion(), instalacion)) {
                return false;
            }

            boolean mismaDisciplina = reserva.getInstalacion().getDisciplina() != null
                    && reserva.getInstalacion().getDisciplina()
                            .equalsIgnoreCase(disciplina.getNombre());

            if (mismaDisciplina) {
                instalacionesOcupadas++;
            }
        }

        return instalacionesOcupadas
                < disciplina.getMaxInstalacionesSimultaneas();
    }

    private boolean esLaMismaInstalacion(Instalacion a, Instalacion b) {
        if (a == b) {
            return true;
        }
        return a.getNombre() != null && a.getNombre().equalsIgnoreCase(b.getNombre());
    }
}