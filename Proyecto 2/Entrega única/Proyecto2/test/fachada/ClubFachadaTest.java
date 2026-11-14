package fachada;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import autenticacion.Administrador;
import autenticacion.Entrenador;
import autenticacion.Socio;
import comercio.RegistroTurnos;
import deportivo.Nivel;
import persistencia.PersistenciaClub;

/**
 * Pruebas de integración por la fachada, como las usa la consola
 * (HU-T02, T05, T07, T11 y T12). No se cargan archivos.
 */
public class ClubFachadaTest {

    private static final LocalDate HOY = LocalDate.now();

    private ClubFachada crear() {
        ClubFachada f = new ClubFachada(null, new RegistroTurnos(), new PersistenciaClub("datos-prueba"));
        f.getAutenticador().registrarUsuario(new Administrador("admin", "1234"));
        Socio s1 = new Socio("socio1", "1234", "Carlos", new Date());
        s1.setNivel(Nivel.INTERMEDIO);
        Socio s2 = new Socio("socio2", "1234", "Ana", new Date());
        s2.setNivel(Nivel.INTERMEDIO);
        f.getAutenticador().registrarUsuario(s1);
        f.getAutenticador().registrarUsuario(s2);
        f.getAutenticador().registrarUsuario(new Entrenador("e1", "1234", "Laura"));
        f.getMatriculas().agregarCodigo("202312345");
        return f;
    }

    @Test
    public void testAutenticarDevuelveElRol() {
        ClubFachada f = crear();
        assertEquals("SOCIO", f.autenticar("socio1", "1234"));
        assertEquals("ENTRENADOR", f.autenticar("e1", "1234"));
        assertThrows(OperacionRechazadaException.class, () -> f.autenticar("socio1", "mala"));
    }

    @Test
    public void testRegistroDeEstudianteMatriculado() {
        ClubFachada f = crear();
        f.registrarCuenta("Luisa", "1010", LocalDate.of(2005, 1, 1), "INTERMEDIO", "est1", "abc12345",
                "ESTUDIANTE", "202312345");
        assertEquals("ESTUDIANTE", f.autenticar("est1", "abc12345"));
    }

    @Test
    public void testRegistroDeEstudianteNoMatriculadoSeRechaza() {
        ClubFachada f = crear();
        assertThrows(OperacionRechazadaException.class, () -> f.registrarCuenta("Luisa", "1010",
                LocalDate.of(2005, 1, 1), "INTERMEDIO", "est1", "abc12345", "ESTUDIANTE", "111111111"));
    }

    @Test
    public void testNoSeRepiteLoginNiDocumento() {
        ClubFachada f = crear();
        f.registrarCuenta("Pedro", "2020", LocalDate.of(1995, 5, 5), "AVANZADO", "ext1", "abc12345", "EXTERNO", null);
        assertThrows(OperacionRechazadaException.class, () -> f.registrarCuenta("Otro", "3030",
                LocalDate.of(1995, 5, 5), "AVANZADO", "ext1", "abc12345", "EXTERNO", null));
        assertThrows(OperacionRechazadaException.class, () -> f.registrarCuenta("Otro", "2020",
                LocalDate.of(1995, 5, 5), "AVANZADO", "ext2", "abc12345", "EXTERNO", null));
    }

    @Test
    public void testSoloElAdministradorCreaTorneos() {
        ClubFachada f = crear();
        assertThrows(OperacionRechazadaException.class, () -> f.crearTorneo("socio1", "Copa", "RECREATIVO",
                "INDIVIDUAL", "Tenis", HOY, null, null, 8, 0));
    }

    @Test
    public void testCompetitivoSinConfirmacionDePagoSeRechaza() {
        ClubFachada f = crear();
        String id = f.crearTorneo("admin", "Copa", "COMPETITIVO", "INDIVIDUAL", "Tenis", HOY, null, null, 8, 30000);
        assertThrows(OperacionRechazadaException.class, () -> f.inscribirIndividual("socio1", id, null));
        f.inscribirIndividual("socio1", id, "PAGO-1");
        // El empleado no paga
        f.inscribirIndividual("e1", id, null);
        assertEquals(2, f.listarMisTorneos("socio1").size() + f.listarMisTorneos("e1").size());
    }

    @Test
    public void testModalidadEquivocadaSeRechaza() {
        ClubFachada f = crear();
        String id = f.crearTorneo("admin", "Dobles", "RECREATIVO", "DOBLES", "Tenis", HOY, null, null, 8, 0);
        assertThrows(OperacionRechazadaException.class, () -> f.inscribirIndividual("socio1", id, null));
        f.inscribirPareja("socio1", "socio2", id, null);
        assertTrue(f.listarMisTorneos("socio2").get(0).contains("Carlos y Ana"));
    }

    @Test
    public void testEquipoSoloDeSocios() {
        ClubFachada f = crear();
        String id = f.crearTorneo("admin", "Fútbol", "RECREATIVO", "CONJUNTO", "Futbol", HOY, null, null, 4, 0);
        List<String> conEmpleado = new ArrayList<>();
        conEmpleado.add("e1");
        assertThrows(OperacionRechazadaException.class, () -> f.inscribirEquipo("socio1", id, "Tigres", conEmpleado, null));
        List<String> socios = new ArrayList<>();
        socios.add("socio2");
        f.inscribirEquipo("socio1", id, "Tigres", socios, null);
        assertEquals(1, f.listarMisTorneos("socio2").size());
    }

    @Test
    public void testFlujoCompletoConBono() {
        ClubFachada f = crear();
        String id = f.crearTorneo("admin", "Recreativo", "RECREATIVO", "INDIVIDUAL", "Padel", HOY, null, null, 4, 0);
        f.inscribirIndividual("socio1", id, null);
        f.inscribirIndividual("socio2", id, null);
        f.desinscribir("socio2", id);
        f.inscribirIndividual("socio2", id, null);
        f.iniciarTorneo("admin", id);
        String linea = f.listarEncuentrosPendientes(id).get(0);
        String idEncuentro = linea.substring(0, linea.indexOf(" "));
        // Gana el local
        f.registrarResultado("admin", id, idEncuentro, 3, 1);
        f.finalizarTorneo("admin", id);
        assertTrue(f.consultarDetalleTorneo(id).contains("Finalizado"));

        String ganador = linea.contains("Carlos vs") ? "socio1" : "socio2";
        List<String> bonos = f.consultarBonos(ganador);
        assertEquals(1, bonos.size());
        String idBono = bonos.get(0).substring(0, bonos.get(0).indexOf(" "));
        assertEquals(20000.0, f.canjearBono(ganador, idBono, 100000, false), 0.01);
        assertTrue(f.consultarBonos(ganador).isEmpty());
    }
}
