package deportivo;

import java.util.ArrayList;
import java.util.List;

/**
 * Arma la liga todos contra todos con el método de rotación (método del círculo):
 * el primero queda fijo y los demás rotan una posición en cada ronda.
 */
public class GeneradorCalendario {

    public List<Encuentro> generarLiga(List<ParticipanteTorneo> participantes) {
        List<ParticipanteTorneo> lista = new ArrayList<>(participantes);
        if (lista.size() % 2 != 0) {
            lista.add(null); // descanso
        }
        int n = lista.size();
        List<Encuentro> encuentros = new ArrayList<>();
        int consecutivo = 1;

        for (int ronda = 1; ronda < n; ronda++) {
            for (int i = 0; i < n / 2; i++) {
                ParticipanteTorneo local = lista.get(i);
                ParticipanteTorneo visitante = lista.get(n - 1 - i);
                // El descanso siempre queda como visitante
                if (local == null) {
                    local = visitante;
                    visitante = null;
                }
                encuentros.add(new Encuentro("E" + consecutivo, ronda, local, visitante));
                consecutivo++;
            }
            ParticipanteTorneo ultimo = lista.remove(n - 1);
            lista.add(1, ultimo);
        }
        return encuentros;
    }
}
