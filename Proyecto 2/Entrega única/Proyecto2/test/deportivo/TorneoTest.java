package deportivo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import autenticacion.Entrenador;
import autenticacion.Socio;
import autenticacion.Usuario;
import fachada.OperacionRechazadaException;

/**
 * Pruebas de las reglas de Torneo, TorneoRecreativo y TorneoCompetitivo
 * (HU-T01, T02, T04, T05, T08, T09, T10 y T13).
 */
public class TorneoTest {

    private static final LocalDate HOY = LocalDate.now();

    private Socio socio(String login, Nivel nivel) {
        Socio s = new Socio(login, "1234", "Socio " + login, new Date());
        s.setNivel(nivel);
        return s;
    }

    private TorneoRecreativo recreativo(Modalidad modalidad, int cupos) {
        return new TorneoRecreativo("T1", "Copa", "Tenis", modalidad, HOY, new Categoria(null, null), cupos);
    }

    private TorneoCompetitivo competitivo(int cupos, double tarifa) {
        return new TorneoCompetitivo("T2", "Copa", "Tenis", Modalidad.INDIVIDUAL, HOY, new Categoria(null, null),
                cupos, tarifa);
    }

    private void inscribir(Torneo t, Usuario u, boolean preferencial) {
        Jugador j = new Jugador(u);
        t.inscribir(new InscripcionTorneo(j, preferencial, t.calcularCobro(j), "PAGO", 0));
    }

    // ---------------- HU-T01: creación y cupos ----------------

    @Test
    public void testCuposPreferencialesIndividual() {
        assertEquals(2, recreativo(Modalidad.INDIVIDUAL, 10).getCuposPreferenciales());
        assertEquals(3, recreativo(Modalidad.INDIVIDUAL, 12).getCuposPreferenciales());
        assertEquals(1, recreativo(Modalidad.INDIVIDUAL, 5).getCuposPreferenciales());
        assertEquals(1, recreativo(Modalidad.INDIVIDUAL, 1).getCuposPreferenciales());
    }

    @Test
    public void testCuposPreferencialesDobles() {
        // 6 cupos = 3 parejas; 20% de 3 = 0.6 -> 1 pareja = 2 cupos
        assertEquals(2, recreativo(Modalidad.DOBLES, 6).getCuposPreferenciales());
    }

    @Test
    public void testDoblesConCuposImparesSeRechaza() {
        assertThrows(OperacionRechazadaException.class, () -> recreativo(Modalidad.DOBLES, 5));
    }

    @Test
    public void testCompetitivoSinTarifaSeRechaza() {
        assertThrows(OperacionRechazadaException.class, () -> competitivo(8, 0));
    }

    @Test
    public void testTorneoNuevoQuedaConInscripcionesAbiertasYFondoEnCero() {
        TorneoCompetitivo t = competitivo(8, 40000);
        assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, t.getEstado());
        assertEquals(0.0, t.getFondoPremios(), 0.01);
    }

    // ---------------- HU-T02 y HU-T03: inscripción ----------------

    @Test
    public void testInscripcionOcupaCupoRegularYSumaAlFondo() {
        TorneoCompetitivo t = competitivo(5, 40000);
        inscribir(t, socio("a", null), false);
        assertEquals(3, t.cuposRegularesLibres());
        assertEquals(40000.0, t.getFondoPremios(), 0.01);
    }

    @Test
    public void testNoSePuedeInscribirDosVeces() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 8);
        Socio a = socio("a", null);
        inscribir(t, a, false);
        assertThrows(OperacionRechazadaException.class, () -> inscribir(t, a, false));
    }

    @Test
    public void testCategoriaPorNivel() {
        Torneo t = new TorneoRecreativo("T1", "Copa", "Tenis", Modalidad.INDIVIDUAL, HOY,
                new Categoria(null, Nivel.AVANZADO), 8);
        assertThrows(OperacionRechazadaException.class, () -> inscribir(t, socio("a", Nivel.PRINCIPIANTE), false));
        inscribir(t, socio("b", Nivel.AVANZADO), false);
        assertEquals(1, t.getInscripciones().size());
    }

    @Test
    public void testCategoriaPorEdad() {
        Torneo t = new TorneoRecreativo("T1", "Sub 18", "Tenis", Modalidad.INDIVIDUAL, HOY,
                new Categoria(18, null), 8);
        Socio mayor = new Socio("m", "1234", "Mayor", java.sql.Date.valueOf(LocalDate.of(1990, 1, 1)));
        assertThrows(OperacionRechazadaException.class, () -> inscribir(t, mayor, false));
    }

    @Test
    public void testRegularesAgotadosAunqueQuedenPreferenciales() {
        // 5 cupos: 1 preferencial y 4 regulares
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 5);
        for (int i = 0; i < 4; i++) {
            inscribir(t, socio("s" + i, null), false);
        }
        assertEquals(1, t.cuposPreferencialesLibres());
        assertThrows(OperacionRechazadaException.class, () -> inscribir(t, socio("x", null), false));
    }

    @Test
    public void testPreferencialSinCupoPasaARegular() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 5);
        inscribir(t, socio("a", null), true);
        inscribir(t, socio("b", null), true);
        assertTrue(t.getInscripciones().get(0).isPreferencial());
        assertFalse(t.getInscripciones().get(1).isPreferencial());
        assertEquals(0, t.cuposPreferencialesLibres());
        assertEquals(3, t.cuposRegularesLibres());
    }

    @Test
    public void testParejaOcupaDosCupos() {
        TorneoRecreativo t = recreativo(Modalidad.DOBLES, 10);
        Pareja p = new Pareja(socio("a", null), socio("b", null));
        t.inscribir(new InscripcionTorneo(p, false, 0, null, 0));
        assertEquals(6, t.cuposRegularesLibres());
    }

    @Test
    public void testEmpleadoNoPagaEnCompetitivo() {
        TorneoCompetitivo t = competitivo(8, 40000);
        Entrenador e = new Entrenador("e1", "1234", "Laura");
        assertEquals(0.0, t.calcularCobro(new Jugador(e)), 0.01);
        assertEquals(40000.0, t.calcularCobro(new Jugador(socio("a", null))), 0.01);
    }

    // ---------------- HU-T14: liberación de preferenciales ----------------

    @Test
    public void testLiberarPreferencialesLosVuelveRegulares() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 10);
        t.liberarCuposPreferenciales();
        assertEquals(0, t.cuposPreferencialesLibres());
        assertEquals(10, t.cuposRegularesLibres());
        assertThrows(OperacionRechazadaException.class, () -> t.liberarCuposPreferenciales());
    }

    @Test
    public void testNoSeLiberaConElTorneoEnCurso() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 10);
        inscribir(t, socio("a", null), false);
        inscribir(t, socio("b", null), false);
        t.iniciar(HOY);
        assertThrows(OperacionRechazadaException.class, () -> t.liberarCuposPreferenciales());
    }

    // ---------------- HU-T05: desinscripción ----------------

    @Test
    public void testDesinscribirLiberaCupoYDevuelveTarifa() {
        TorneoCompetitivo t = competitivo(5, 40000);
        inscribir(t, socio("a", null), false);
        t.desinscribir("a");
        assertEquals(4, t.cuposRegularesLibres());
        assertEquals(0.0, t.getFondoPremios(), 0.01);
    }

    @Test
    public void testDesinscribirPreferencialLiberaPreferencial() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 5);
        inscribir(t, socio("a", null), true);
        t.desinscribir("a");
        assertEquals(1, t.cuposPreferencialesLibres());
    }

    @Test
    public void testNoSePuedeDesinscribirDespuesDeIniciar() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 8);
        inscribir(t, socio("a", null), false);
        inscribir(t, socio("b", null), false);
        t.iniciar(HOY);
        assertThrows(OperacionRechazadaException.class, () -> t.desinscribir("a"));
    }

    @Test
    public void testSoloElCapitanRetiraAlEquipo() {
        TorneoRecreativo t = recreativo(Modalidad.CONJUNTO, 4);
        Socio capitan = socio("cap", null);
        List<Socio> otros = new ArrayList<>();
        otros.add(socio("j1", null));
        t.inscribir(new InscripcionTorneo(new EquipoTorneo("Tigres", capitan, otros), false, 0, null, 0));
        assertThrows(OperacionRechazadaException.class, () -> t.desinscribir("j1"));
        t.desinscribir("cap");
        assertEquals(0, t.getInscripciones().size());
    }

    // ---------------- HU-T04 y HU-T13: inicio y calendario ----------------

    @Test
    public void testNoIniciaConMenosDeDos() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 8);
        inscribir(t, socio("a", null), false);
        assertThrows(OperacionRechazadaException.class, () -> t.iniciar(HOY));
    }

    @Test
    public void testNoIniciaAntesDeLaFecha() {
        TorneoRecreativo t = new TorneoRecreativo("T1", "Copa", "Tenis", Modalidad.INDIVIDUAL, HOY.plusDays(3),
                new Categoria(null, null), 8);
        inscribir(t, socio("a", null), false);
        inscribir(t, socio("b", null), false);
        assertThrows(OperacionRechazadaException.class, () -> t.iniciar(HOY));
    }

    @Test
    public void testIniciarGeneraTodosContraTodos() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 8);
        for (int i = 0; i < 4; i++) {
            inscribir(t, socio("s" + i, null), false);
        }
        t.iniciar(HOY);
        assertEquals(EstadoTorneo.EN_CURSO, t.getEstado());
        // 4 participantes: 3 rondas y 6 encuentros
        assertEquals(6, t.darEncuentrosPendientes().size());
        assertEquals(3, t.getEncuentros().get(5).getRonda());
    }

    // ---------------- HU-T08: resultados ----------------

    @Test
    public void testEmpateNoPermitidoEnIndividual() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 8);
        inscribir(t, socio("a", null), false);
        inscribir(t, socio("b", null), false);
        t.iniciar(HOY);
        String id = t.darEncuentrosPendientes().get(0).getId();
        assertThrows(OperacionRechazadaException.class, () -> t.registrarResultado(id, 2, 2));
    }

    @Test
    public void testEmpatePermitidoEnConjunto() {
        TorneoRecreativo t = recreativo(Modalidad.CONJUNTO, 4);
        t.inscribir(new InscripcionTorneo(new EquipoTorneo("A", socio("a", null), new ArrayList<>()), false, 0, null, 0));
        t.inscribir(new InscripcionTorneo(new EquipoTorneo("B", socio("b", null), new ArrayList<>()), false, 0, null, 0));
        t.iniciar(HOY);
        t.registrarResultado(t.darEncuentrosPendientes().get(0).getId(), 1, 1);
        assertEquals(1, t.darPosiciones().get(0).getPuntos());
        assertEquals(1, t.darPosiciones().get(1).getPuntos());
    }

    @Test
    public void testNoSeRegistraDosVecesElMismoEncuentro() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 8);
        inscribir(t, socio("a", null), false);
        inscribir(t, socio("b", null), false);
        t.iniciar(HOY);
        String id = t.darEncuentrosPendientes().get(0).getId();
        t.registrarResultado(id, 2, 0);
        assertThrows(OperacionRechazadaException.class, () -> t.registrarResultado(id, 3, 0));
    }

    // ---------------- HU-T09 y HU-T10: finalización y premios ----------------

    /** Juega todos los encuentros: gana siempre el que se inscribió primero. */
    private void jugarTodo(Torneo t) {
        for (Encuentro e : t.darEncuentrosPendientes()) {
            int ordenLocal = t.buscarInscripcion(e.getLocal().darIntegrantes().get(0).getLogin()).getOrden();
            int ordenVisitante = t.buscarInscripcion(e.getVisitante().darIntegrantes().get(0).getLogin()).getOrden();
            if (ordenLocal < ordenVisitante) {
                t.registrarResultado(e.getId(), 2, 0);
            } else {
                t.registrarResultado(e.getId(), 0, 2);
            }
        }
    }

    @Test
    public void testNoSeFinalizaConEncuentrosPendientes() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 8);
        inscribir(t, socio("a", null), false);
        inscribir(t, socio("b", null), false);
        t.iniciar(HOY);
        assertThrows(OperacionRechazadaException.class, () -> t.finalizar());
    }

    @Test
    public void testRecreativoEntregaBonoAlGanador() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 8);
        Socio a = socio("a", null);
        inscribir(t, a, false);
        inscribir(t, socio("b", null), false);
        t.iniciar(HOY);
        jugarTodo(t);
        t.finalizar();
        assertEquals(EstadoTorneo.FINALIZADO, t.getEstado());
        assertEquals(1, a.darBonosDisponibles().size());
        assertEquals(20.0, a.darBonosDisponibles().get(0).getPorcentaje(), 0.01);
    }

    @Test
    public void testParejaGanadoraRecibeUnBonoCadaUno() {
        TorneoRecreativo t = recreativo(Modalidad.DOBLES, 8);
        Socio a = socio("a", null);
        Socio b = socio("b", null);
        t.inscribir(new InscripcionTorneo(new Pareja(a, b), false, 0, null, 0));
        t.inscribir(new InscripcionTorneo(new Pareja(socio("c", null), socio("d", null)), false, 0, null, 0));
        t.iniciar(HOY);
        jugarTodo(t);
        t.finalizar();
        assertEquals(1, a.darBonos().size());
        assertEquals(1, b.darBonos().size());
    }

    @Test
    public void testNoSeEntreganPremiosDosVeces() {
        TorneoRecreativo t = recreativo(Modalidad.INDIVIDUAL, 8);
        inscribir(t, socio("a", null), false);
        inscribir(t, socio("b", null), false);
        t.iniciar(HOY);
        jugarTodo(t);
        t.finalizar();
        assertThrows(OperacionRechazadaException.class, () -> t.finalizar());
        assertEquals(1, t.getPremios().size());
    }

    @Test
    public void testCompetitivoReparte60_30_10SinEmpleados() {
        // El empleado queda de primero pero no recibe efectivo: los demás suben
        TorneoCompetitivo t = competitivo(8, 25000);
        inscribir(t, new Entrenador("e1", "1234", "Laura"), false);
        inscribir(t, socio("a", null), false);
        inscribir(t, socio("b", null), false);
        inscribir(t, socio("c", null), false);
        assertEquals(75000.0, t.getFondoPremios(), 0.01);
        t.iniciar(HOY);
        jugarTodo(t);
        t.finalizar();

        List<Premio> premios = t.getPremios();
        assertEquals(3, premios.size());
        assertEquals("Socio a", premios.get(0).getBeneficiario().darNombre());
        assertEquals(45000.0, ((PremioEfectivo) premios.get(0)).getMonto(), 0.01);
        assertEquals(22500.0, ((PremioEfectivo) premios.get(1)).getMonto(), 0.01);
        assertEquals(7500.0, ((PremioEfectivo) premios.get(2)).getMonto(), 0.01);
    }

    @Test
    public void testCompetitivoConDosParticipantesNoAsignaTercerPuesto() {
        TorneoCompetitivo t = competitivo(8, 50000);
        inscribir(t, socio("a", null), false);
        inscribir(t, socio("b", null), false);
        t.iniciar(HOY);
        jugarTodo(t);
        t.finalizar();
        assertEquals(2, t.getPremios().size());
        assertEquals(60000.0, ((PremioEfectivo) t.getPremios().get(0)).getMonto(), 0.01);
        assertEquals(30000.0, ((PremioEfectivo) t.getPremios().get(1)).getMonto(), 0.01);
    }

    @Test
    public void testCompetitivoSoloConEmpleadosNoTieneFondo() {
        TorneoCompetitivo t = competitivo(8, 50000);
        inscribir(t, new Entrenador("e1", "1234", "Laura"), false);
        inscribir(t, new Entrenador("e2", "1234", "Andrés"), false);
        t.iniciar(HOY);
        jugarTodo(t);
        t.finalizar();
        assertEquals(0.0, t.getFondoPremios(), 0.01);
        assertTrue(t.getPremios().isEmpty());
    }
}
