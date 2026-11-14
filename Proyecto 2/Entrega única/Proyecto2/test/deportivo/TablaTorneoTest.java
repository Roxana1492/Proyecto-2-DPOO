package deportivo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import autenticacion.Socio;

/** Pruebas de la tabla de posiciones y el desempate (HU-T09). */
public class TablaTorneoTest {

    private Jugador jugador(String login) {
        return new Jugador(new Socio(login, "1234", login, new Date()));
    }

    private Encuentro jugado(ParticipanteTorneo local, ParticipanteTorneo visitante, int ml, int mv) {
        Encuentro e = new Encuentro("E", 1, local, visitante);
        e.registrarResultado(ml, mv);
        return e;
    }

    @Test
    public void testGanarDaTresPuntos() {
        Jugador a = jugador("a");
        Jugador b = jugador("b");
        List<ParticipanteTorneo> lista = new ArrayList<>();
        lista.add(a);
        lista.add(b);
        TablaTorneo tabla = new TablaTorneo(lista);
        tabla.actualizar(jugado(a, b, 0, 2), false);
        assertEquals(b, tabla.ordenarConDesempate().get(0).getParticipante());
        assertEquals(3, tabla.ordenarConDesempate().get(0).getPuntos());
    }

    @Test
    public void testDesempatePorDiferenciaYMarcadorAFavor() {
        Jugador a = jugador("a");
        Jugador b = jugador("b");
        Jugador c = jugador("c");
        List<ParticipanteTorneo> lista = new ArrayList<>();
        lista.add(a);
        lista.add(b);
        lista.add(c);
        TablaTorneo tabla = new TablaTorneo(lista);
        // Los tres quedan con 3 puntos
        tabla.actualizar(jugado(a, b, 1, 0), false);
        tabla.actualizar(jugado(b, c, 5, 0), false);
        tabla.actualizar(jugado(c, a, 2, 1), false);
        List<PosicionTorneo> orden = tabla.ordenarConDesempate();
        assertEquals(b, orden.get(0).getParticipante()); // diferencia +4
        assertEquals(a, orden.get(1).getParticipante()); // diferencia 0, a favor 2
        assertEquals(c, orden.get(2).getParticipante()); // diferencia -4
    }

    @Test
    public void testSiTodoEsIgualGanaElQueSeInscribioPrimero() {
        Jugador a = jugador("a");
        Jugador b = jugador("b");
        List<ParticipanteTorneo> lista = new ArrayList<>();
        lista.add(a);
        lista.add(b);
        TablaTorneo tabla = new TablaTorneo(lista);
        tabla.actualizar(jugado(a, b, 1, 1), true);
        assertEquals(a, tabla.ordenarConDesempate().get(0).getParticipante());
    }

    @Test
    public void testCalendarioConNumeroImparTieneDescansos() {
        List<ParticipanteTorneo> lista = new ArrayList<>();
        lista.add(jugador("a"));
        lista.add(jugador("b"));
        lista.add(jugador("c"));
        List<Encuentro> encuentros = new GeneradorCalendario().generarLiga(lista);
        int partidos = 0;
        int descansos = 0;
        for (Encuentro e : encuentros) {
            if (e.esDescanso()) {
                descansos++;
            } else {
                partidos++;
            }
        }
        assertEquals(3, partidos);
        assertEquals(3, descansos);
    }
}
