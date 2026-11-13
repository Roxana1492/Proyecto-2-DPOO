package deportivo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FachadaTorneos {
    private List<Torneo> torneos;

    public FachadaTorneos() {
        this.torneos = new ArrayList<>();
    }

    public Torneo crearTorneo(String idTorneo, String nombre, TipoTorneo tipo, 
                              ModalidadTorneo modalidad, String deporte, 
                              LocalDate fechaInicio, int cuposMaximos, 
                              String nivelMinimo, int minimoParticipantes, 
                              double tarifaInscripcion) {
        
        Torneo nuevoTorneo = new Torneo(idTorneo, nombre, tipo, modalidad, deporte, 
                                        fechaInicio, cuposMaximos, nivelMinimo, 
                                        minimoParticipantes, tarifaInscripcion);
        this.torneos.add(nuevoTorneo);
        return nuevoTorneo;
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
        if (torneo == null) {
            return false;
        }
        return torneo.inscribirPorRolEspecifico(participante, rol, verificadoOAutorizado, pagoConfirmado);
    }
}