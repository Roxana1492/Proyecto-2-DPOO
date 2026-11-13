package deportivo;

import fachada.OperacionRechazadaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorTorneos {
    private List<Torneo> torneos;

    public GestorTorneos() {
        this.torneos = new ArrayList<>();
    }

    // Mismo orden de parámetros que el constructor de Torneo
    public Torneo crearTorneo(String idTorneo, String nombre, TipoTorneo tipo,
                              ModalidadTorneo modalidad, String disciplina,
                              LocalDate fechaInicio, int edadMaxima,
                              String nivelRequerido, int cuposTotales,
                              double tarifaInscripcion) {

        Torneo nuevoTorneo = new Torneo(idTorneo, nombre, tipo, modalidad, disciplina,
                                        fechaInicio, edadMaxima, nivelRequerido,
                                        cuposTotales, tarifaInscripcion);
        this.torneos.add(nuevoTorneo);
        return nuevoTorneo;
    }

    // Para los torneos que se cargan desde el archivo
    public void agregarTorneo(Torneo torneo) {
        if (torneo != null) {
            this.torneos.add(torneo);
        }
    }

    public List<Torneo> getTorneos() {
        return torneos;
    }

    public Torneo buscarTorneo(String idTorneo) {
        if (idTorneo == null) {
            throw new OperacionRechazadaException("El ID del torneo no puede ser nulo.");
        }
        for (Torneo t : this.torneos) {
            if (t.getIdTorneo() != null && t.getIdTorneo().equals(idTorneo)) {
                return t;
            }
        }
        throw new OperacionRechazadaException("El torneo con ID " + idTorneo + " no existe.");
    }

    public boolean inscribirParticipantePorRol(String idTorneo, Object participante, RolParticipante rol, boolean verificadoOAutorizado, boolean pagoConfirmado) {
        Torneo torneo = buscarTorneo(idTorneo);
        return torneo.inscribirPorRolEspecifico(participante, rol, verificadoOAutorizado, pagoConfirmado);
    }
}
