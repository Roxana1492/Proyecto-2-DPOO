package deportivo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import org.junit.jupiter.api.Test;

import autenticacion.Entrenador;
import autenticacion.Socio;
import comercio.RegistroTurnos;
import fachada.OperacionRechazadaException;

/** Pruebas de GestorTorneos: creación, turnos de empleados y cupos por ranking (HU-T01, T03, T06). */
public class GestorTorneosTest {

    private static final LocalDate HOY = LocalDate.now();

    @Test
    public void testCrearTorneoAsignaIdsConsecutivos() {
        GestorTorneos g = new GestorTorneos(new RegistroTurnos());
        Torneo t1 = g.crearTorneo("Uno", "RECREATIVO", Modalidad.INDIVIDUAL, "Tenis", HOY, new Categoria(null, null), 4, 0);
        Torneo t2 = g.crearTorneo("Dos", "COMPETITIVO", Modalidad.INDIVIDUAL, "Tenis", HOY, new Categoria(null, null), 4, 10000);
        assertEquals("T1", t1.getId());
        assertEquals("T2", t2.getId());
        assertTrue(t1 instanceof TorneoRecreativo);
        assertTrue(t2 instanceof TorneoCompetitivo);
        assertEquals(t2, g.buscar("T2"));
    }

    @Test
    public void testNoSeCreaTorneoConFechaPasada() {
        GestorTorneos g = new GestorTorneos(new RegistroTurnos());
        assertThrows(OperacionRechazadaException.class, () -> g.crearTorneo("Viejo", "RECREATIVO",
                Modalidad.INDIVIDUAL, "Tenis", HOY.minusDays(1), new Categoria(null, null), 4, 0));
    }

    @Test
    public void testBuscarTorneoInexistente() {
        GestorTorneos g = new GestorTorneos(new RegistroTurnos());
        assertThrows(OperacionRechazadaException.class, () -> g.buscar("T99"));
    }

    @Test
    public void testEmpleadoConTurnoEseDiaNoEstaLibre() {
        RegistroTurnos turnos = new RegistroTurnos();
        turnos.agregarTurno(new Turno("T1", "e1", "Laura", "Sabado", "08:00-12:00", "Gimnasio", "ENTRENADOR"));
        GestorTorneos g = new GestorTorneos(turnos);
        Entrenador e = new Entrenador("e1", "1234", "Laura");
        LocalDate sabado = HOY.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        LocalDate domingo = HOY.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        assertFalse(g.empleadoLibre(e, sabado));
        assertTrue(g.empleadoLibre(e, domingo));
    }

    @Test
    public void testSinHistorialNoEsPreferencial() {
        GestorTorneos g = new GestorTorneos(new RegistroTurnos());
        Torneo t = g.crearTorneo("Uno", "RECREATIVO", Modalidad.INDIVIDUAL, "Tenis", HOY, new Categoria(null, null), 10, 0);
        Socio nuevo = new Socio("n", "1234", "Nuevo", new Date());
        assertFalse(g.esCupoPreferencial(new Jugador(nuevo), t));
    }

    @Test
    public void testGanadorDeUnTorneoAnteriorEsPreferencial() {
        GestorTorneos g = new GestorTorneos(new RegistroTurnos());
        Socio a = new Socio("a", "1234", "A", new Date());
        Socio b = new Socio("b", "1234", "B", new Date());
        Torneo t1 = g.crearTorneo("Uno", "RECREATIVO", Modalidad.INDIVIDUAL, "Tenis", HOY, new Categoria(null, null), 10, 0);
        t1.inscribir(new InscripcionTorneo(new Jugador(a), false, 0, null, 0));
        t1.inscribir(new InscripcionTorneo(new Jugador(b), false, 0, null, 0));
        t1.iniciar(HOY);
        Encuentro e = t1.darEncuentrosPendientes().get(0);
        if (e.getLocal().contiene("a")) {
            t1.registrarResultado(e.getId(), 3, 0);
        } else {
            t1.registrarResultado(e.getId(), 0, 3);
        }
        // a ganó: su ELO sube por encima de 1000 y b baja
        assertTrue(a.darElo("Tenis") > 1000);
        assertTrue(b.darElo("Tenis") < 1000);

        // Con topK = 1 solo el mejor de la disciplina va por preferencial
        g.setTopK(1);
        Torneo t2 = g.crearTorneo("Dos", "RECREATIVO", Modalidad.INDIVIDUAL, "Tenis", HOY, new Categoria(null, null), 10, 0);
        assertTrue(g.esCupoPreferencial(new Jugador(a), t2));
        assertFalse(g.esCupoPreferencial(new Jugador(b), t2));
    }
}
