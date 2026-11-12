package pruebas;

import autenticacion.Socio;
import deportivo.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Prueba2 {

    private static Date fechaHaceDias(int dias) {
        LocalDate fecha = LocalDate.now().minusDays(dias);
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 2: FLUJO CONTINUO DE PARTIDOS, ESTADÍSTICAS Y SANCIONES (ENTREGA 1 / DPOO)");
        System.out.println("==========================================================================================================");
        System.out.println(" [NOTA] Para propósitos de esta demostración interactiva, se ajusta temporalmente el mínimo de jugadores a 3.");

        // Definir deporte de Fútbol con mínimo de 3 jugadores habilitados para la prueba
        Deporte futbol = new Deporte("Fútbol", 3);

        // Equipos del club
        Equipo local = new Equipo("Los Cracks FC", 5, 18, 30, futbol);
        Equipo visitante = new Equipo("Tigres FC", 5, 18, 30, futbol);

        // Socios/Jugadores del equipo Local
        Calendar cal = Calendar.getInstance();
        cal.set(2001, Calendar.MAY, 10); Socio s1 = new Socio("s1", "pass", "Carlos Pérez", cal.getTime());
        cal.set(2000, Calendar.JUNE, 20); Socio s2 = new Socio("s2", "pass", "Andrés Gómez", cal.getTime());
        cal.set(2002, Calendar.MARCH, 15); Socio s3 = new Socio("s3", "pass", "Mateo Ríos", cal.getTime());
        cal.set(2003, Calendar.JANUARY, 1); Socio s4 = new Socio("s4", "pass", "Luis Torres", cal.getTime());

        // Inscribir jugadores en el equipo local
        local.agregarInscripcion(new InscripcionJugador("Delantero", 10, s1, local));
        local.agregarInscripcion(new InscripcionJugador("Mediocampista", 8, s2, local));
        local.agregarInscripcion(new InscripcionJugador("Defensa", 4, s3, local));
        local.agregarInscripcion(new InscripcionJugador("Volante", 6, s4, local));

        VerificacionAlineacion verificador = new VerificacionAlineacion();
        VerificacionSancion verSancion = new VerificacionSancion();

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" PASO 1: INTENTO DE PARTIDO CON EQUIPO INCOMPLETO (Rechazado por mínimo de jugadores)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        List<Socio> nominaIncompleta = Arrays.asList(s1, s2); // Solo 2 jugadores (mínimo requerido es 3)
        String motivoPaso1 = verificador.verificarPartido(local, nominaIncompleta);
        boolean paso1Exitoso = !motivoPaso1.isEmpty();
        imprimirResultado("Intento partido con 2 jugadores (Mínimo 3)", paso1Exitoso, true, motivoPaso1);


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" PASO 2 Y 3: PARTIDO 1 (Los Cracks FC vs Tigres FC) Y REGISTRO DE ESTADÍSTICAS / TARJETAS");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        List<Socio> nominaPartido1 = Arrays.asList(s1, s2, s3);
        String motivoPaso2 = verificador.verificarPartido(local, nominaPartido1);
        System.out.println("Validación previa Partido 1: " + (motivoPaso2.isEmpty() ? "APROBADO (Nómina completa y habilitada)" : "RECHAZADO"));

        // Registrar Partido 1 con marcador 2 - 1 (Local 2, Visitante 1)
        Partido partido1 = new Partido(fechaHaceDias(7), 2, 1);
        System.out.println("Marcador Final Partido 1 -> Los Cracks FC [2] vs [1] Tigres FC");

        // Registrar estadísticas individuales y tarjetas del Partido 1
        // Los goles locales suman 2 (Carlos Pérez: 1 gol, Mateo Ríos: 1 gol)
        // Carlos Pérez (s1): 1 gol, 1 asistencia, 1 amarilla
        // Andrés Gómez (s2): 0 goles, 0 asistencias, 2 amarillas (¡Acumula 2, queda suspendido!)
        // Mateo Ríos (s3): 1 gol, 0 asistencias, 0 amarillas
        Map<Socio, EstadisticaFutbol> estadisticasP1 = new HashMap<>();
        estadisticasP1.put(s1, new EstadisticaFutbol(1, 1, 1, false));
        estadisticasP1.put(s2, new EstadisticaFutbol(0, 0, 2, false));
        estadisticasP1.put(s3, new EstadisticaFutbol(1, 0, 0, false));
        estadisticasP1.put(s4, new EstadisticaFutbol(0, 0, 0, false)); // No jugó este partido, ceros

        // Alimentar sanciones automáticamente con las tarjetas del partido 1
        for (Map.Entry<Socio, EstadisticaFutbol> entry : estadisticasP1.entrySet()) {
            Socio jugador = entry.getKey();
            EstadisticaFutbol est = entry.getValue();
            if (est.getTarjetasAmarillas() > 0 || est.isTarjetaRoja()) {
                SancionDeportiva.registrarTarjetas(jugador, est.getTarjetasAmarillas(), est.isTarjetaRoja());
            }
        }

        System.out.println("\nVerificando estado de sanciones posterior al Partido 1:");
        System.out.println(" - Carlos Pérez (1 Amarilla) -> ¿Suspendido?: " + (SancionDeportiva.estaSuspendido(s1) ? "SÍ" : "NO (Habilitado)"));
        System.out.println(" - Andrés Gómez (2 Amarillas) -> ¿Suspendido?: " + (SancionDeportiva.estaSuspendido(s2) ? "SÍ (Suspendido por acumulación)" : "NO"));


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" PASO 4: INTENTO DE ALINEAR A JUGADOR SUSPENDIDO Y DEMOSTRACIÓN DE TARJETA ROJA");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        // Para el Partido 2, intentamos alinear a s1, s2 y s3 (s2 está suspendido)
        List<Socio> nominaPartido2Intento = Arrays.asList(s1, s2, s3);
        String motivoPaso4 = verificador.verificarPartido(local, nominaPartido2Intento);
        boolean paso4Rechazado = !motivoPaso4.isEmpty();
        imprimirResultado("Partido 2 intentando alinear a Andrés Gómez (Suspendido)", paso4Rechazado, true, motivoPaso4);

        // Alineación corregida sin el suspendido (s1, s3, s4)
        List<Socio> nominaPartido2Real = Arrays.asList(s1, s3, s4);
        String motivoPaso2Real = verificador.verificarPartido(local, nominaPartido2Real);
        System.out.println("Partido 2 con alineación corregida (sin sancionados): " + (motivoPaso2Real.isEmpty() ? "APROBADO" : "RECHAZADO"));

        // Demostración regla de Tarjeta Roja Directa en el Partido 2
        System.out.println("\n[SIMULACIÓN PARTIDO 2] Durante el juego, Luis Torres (s4) recibe Tarjeta Roja Directa.");
        SancionDeportiva.registrarTarjetas(s4, 0, true);
        System.out.println(" - Luis Torres (Tarjeta Roja Directa) -> ¿Suspendido?: " + (SancionDeportiva.estaSuspendido(s4) ? "SÍ (Suspendido por Roja)" : "NO"));


        System.out.println("\n==========================================================================================================");
        System.out.println(" PASO 5: TABLA FINAL DE ESTADÍSTICAS Y ESTADOS DE SUSPENSIÓN (JUGADORES INSCRITOS)");
        System.out.println("==========================================================================================================");
        System.out.printf("%-20s | %-12s | %-12s | %-18s | %-15s%n", "Jugador", "Goles", "Asistencias", "Amarillas", "Estado Sanción");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        for (InscripcionJugador ij : local.getInscripciones()) {
            Socio j = ij.getSocio();
            EstadisticaFutbol ef = estadisticasP1.getOrDefault(j, new EstadisticaFutbol(0, 0, 0, false));
            String estadoSancion = verSancion.estaHabilitado(j) ? "Habilitado" : "SUSPENDIDO";
            System.out.printf("%-20s | %-12d | %-12d | %-18d | %-15s%n", 
                j.getNombre(), ef.getGoles(), ef.getAsistencias(), ef.getTarjetasAmarillas(), estadoSancion);
        }
        System.out.println("==========================================================================================================");
    }

    private static void imprimirResultado(String caso, boolean obtenido, boolean esperado, String motivo) {
        String estado = (obtenido == esperado) ? "✅ PASÓ" : "❌ FALLÓ";
        String detalle = motivo.isEmpty() ? "Sin observaciones" : ("Motivo: " + motivo);
        System.out.printf("%-50s | %-40s | %s%n", caso, detalle, estado);
    }
}