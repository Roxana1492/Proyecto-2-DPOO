package deportivo;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabla de posiciones de la liga. Victoria 3 puntos, empate 1, derrota 0.
 * Desempate: puntos, diferencia de marcador, marcador a favor y orden de inscripción.
 */
public class TablaTorneo {

    private List<PosicionTorneo> posiciones;

    public TablaTorneo(List<ParticipanteTorneo> participantes) {
        this.posiciones = new ArrayList<>();
        for (int i = 0; i < participantes.size(); i++) {
            posiciones.add(new PosicionTorneo(participantes.get(i), i + 1));
        }
    }

    public void actualizar(Encuentro encuentro, boolean permiteEmpate) {
        if (encuentro.esDescanso() || !encuentro.isJugado()) {
            return;
        }
        PosicionTorneo local = buscar(encuentro.getLocal());
        PosicionTorneo visitante = buscar(encuentro.getVisitante());
        int ml = encuentro.getMarcadorLocal();
        int mv = encuentro.getMarcadorVisitante();

        int puntosLocal = 0;
        int puntosVisitante = 0;
        if (ml > mv) {
            puntosLocal = 3;
        } else if (mv > ml) {
            puntosVisitante = 3;
        } else if (permiteEmpate) {
            puntosLocal = 1;
            puntosVisitante = 1;
        }
        local.sumar(puntosLocal, ml, mv);
        visitante.sumar(puntosVisitante, mv, ml);
    }

    public List<PosicionTorneo> ordenarConDesempate() {
        List<PosicionTorneo> ordenada = new ArrayList<>(posiciones);
        ordenada.sort((a, b) -> {
            if (a.getPuntos() != b.getPuntos()) {
                return b.getPuntos() - a.getPuntos();
            }
            if (a.diferencia() != b.diferencia()) {
                return b.diferencia() - a.diferencia();
            }
            if (a.getMarcadorFavor() != b.getMarcadorFavor()) {
                return b.getMarcadorFavor() - a.getMarcadorFavor();
            }
            return a.getOrdenInscripcion() - b.getOrdenInscripcion();
        });
        return ordenada;
    }

    private PosicionTorneo buscar(ParticipanteTorneo participante) {
        for (PosicionTorneo p : posiciones) {
            if (p.getParticipante() == participante) {
                return p;
            }
        }
        return null;
    }
}
