package pruebas;

import autenticacion.Socio;
import deportivo.*;
import persistencia.AlmacenadorArchivos;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Prueba3 {

    private static Date fechaHaceDias(int dias) {
        LocalDate fecha = LocalDate.now().minusDays(dias);
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 3: TORNEO DE LOS CERROS - COMPETENCIA TIPO LIGA Y TABLA DE POSICIONES (DPOO)");
        System.out.println("==========================================================================================================");

        Map<String, Socio> socios = new HashMap<>();
        List<Equipo> equipos = new ArrayList<>();
        Map<String, Deporte> deportes = new HashMap<>();

        // 1. Carga desde ../datos/ o respaldo en memoria
        String rutaSocios = "../datos/socios.txt";
        String rutaEquipos = "../datos/equipos.txt";

        try {
            File fSocios = new File(rutaSocios);
            File fEquipos = new File(rutaEquipos);

            if (fSocios.exists() && fEquipos.exists()) {
                System.out.println("\n[DATOS] Carga exitosa desde " + fSocios.getCanonicalPath());
                socios = AlmacenadorArchivos.cargarSocios(rutaSocios);
                equipos = AlmacenadorArchivos.cargarEquipos(rutaEquipos, deportes);
            } else {
                System.out.println("\n[DATOS] Archivos no hallados en ../datos/. Inicializando datos de prueba...");
                deportes.put("Fútbol", new Deporte("Fútbol", 3)); // Mínimo 3 para demostración

                socios.put("s1", new Socio("s1", "pass", "Carlos Pérez", fechaNacimientoHaceAnos(22)));
                socios.put("s2", new Socio("s2", "pass", "Andrés Gómez", fechaNacimientoHaceAnos(24)));
                socios.put("s3", new Socio("s3", "pass", "Mateo Ríos", fechaNacimientoHaceAnos(23)));
                socios.put("s4", new Socio("s4", "pass", "Luis Torres", fechaNacimientoHaceAnos(25))); // Pertenece al equipo pero no a la nómina de la liga
                socios.put("s5", new Socio("s5", "pass", "Felipe Rojas", fechaNacimientoHaceAnos(25)));
                socios.put("s6", new Socio("s6", "pass", "Juan Morales", fechaNacimientoHaceAnos(21)));
                socios.put("s7", new Socio("s7", "pass", "David Castro", fechaNacimientoHaceAnos(22)));
                socios.put("s8", new Socio("s8", "pass", "Esteban Parra", fechaNacimientoHaceAnos(24)));
                socios.put("s9", new Socio("s9", "pass", "Camilo Torres", fechaNacimientoHaceAnos(20)));
                socios.put("s10", new Socio("s10", "pass", "Julian Gómez", fechaNacimientoHaceAnos(21)));

                equipos.add(new Equipo("Los Cracks FC", 5, 18, 30, deportes.get("Fútbol")));
                equipos.add(new Equipo("Tigres FC", 5, 18, 30, deportes.get("Fútbol")));
                equipos.add(new Equipo("Deportivo Central", 5, 18, 30, deportes.get("Fútbol")));
            }
        } catch (Exception e) {
            System.err.println("Error cargando datos: " + e.getMessage());
            return;
        }

        Deporte futbol = deportes.get("Fútbol");
        Equipo eq1 = equipos.get(0); // Los Cracks FC
        Equipo eq2 = equipos.get(1); // Tigres FC
        Equipo eq3 = equipos.get(2); // Deportivo Central

        // Asignar socios a equipos si se cargaron de memoria
        Socio s1 = socios.get("s1"); Socio s2 = socios.get("s2"); Socio s3 = socios.get("s3");
        Socio s4 = socios.get("s4"); // Luis Torres (En el equipo pero excluido de la nómina del torneo)
        Socio s5 = socios.get("s5"); Socio s6 = socios.get("s6"); Socio s7 = socios.get("s7");
        Socio s8 = socios.get("s8"); Socio s9 = socios.get("s9"); Socio s10 = socios.get("s10");

        if (eq1.getInscripciones().isEmpty()) {
            eq1.agregarInscripcion(new InscripcionJugador("Delantero", 10, s1, eq1));
            eq1.agregarInscripcion(new InscripcionJugador("Mediocampista", 8, s2, eq1));
            eq1.agregarInscripcion(new InscripcionJugador("Defensa", 4, s3, eq1));
            eq1.agregarInscripcion(new InscripcionJugador("Suplente", 5, s4, eq1)); // Luis Torres está inscrito en el equipo

            eq2.agregarInscripcion(new InscripcionJugador("Delantero", 9, s5, eq2));
            eq2.agregarInscripcion(new InscripcionJugador("Mediocampista", 7, s6, eq2));
            eq2.agregarInscripcion(new InscripcionJugador("Portero", 1, s7, eq2));

            eq3.agregarInscripcion(new InscripcionJugador("Delantero", 11, s8, eq3));
            eq3.agregarInscripcion(new InscripcionJugador("Defensa", 3, s9, eq3));
            eq3.agregarInscripcion(new InscripcionJugador("Portero", 1, s10, eq3));
        }

        VerificacionAlineacion verificador = new VerificacionAlineacion();

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" PRUEBA DE RECHAZOS OBLIGATORIOS PARA LA COMPETENCIA");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        // 1. RECHAZO: Intento de inscribir un equipo con nómina incompleta (menos del mínimo requerido)
        List<Socio> nominaIncompleta = Arrays.asList(s1); // Solo 1 jugador cuando el mínimo es 3
        String motivoMinimo = verificador.verificarPartido(eq1, nominaIncompleta);
        imprimirResultado("Inscripción equipo con nómina por debajo del mínimo", !motivoMinimo.isEmpty(), true, motivoMinimo);

        // 2. RECHAZO: Intentar alinear a un jugador que SÍ pertenece al equipo (Luis Torres / s4) pero NO está en la nómina oficial del torneo
        InscripcionEquipo iePruebaInvalida = new InscripcionEquipo(eq1, Arrays.asList(s1, s2, s3)); // s4 (Luis Torres) no está aquí
        List<Socio> nominaConJugadorFueraDeTorneo = Arrays.asList(s1, s2, s4);
        String motivoNominaTorneo = verificador.verificarNominaCompetencia(iePruebaInvalida, nominaConJugadorFueraDeTorneo);
        imprimirResultado("Alinear jugador del equipo (Luis Torres) fuera de la nómina del torneo", !motivoNominaTorneo.isEmpty(), true, motivoNominaTorneo);


        System.out.println("\n==========================================================================================================");
        System.out.println(" INICIO DEL TORNEO DE LOS CERROS - INSCRIPCIÓN Y CALENDARIO VálIDO");
        System.out.println("==========================================================================================================");

        Competencia torneoCerros = new Competencia("Torneo de los Cerros");
        
        // Nóminas oficiales inscritas en la competencia
        InscripcionEquipo ie1 = new InscripcionEquipo(eq1, Arrays.asList(s1, s2, s3));
        InscripcionEquipo ie2 = new InscripcionEquipo(eq2, Arrays.asList(s5, s6, s7));
        InscripcionEquipo ie3 = new InscripcionEquipo(eq3, Arrays.asList(s8, s9, s10));

        torneoCerros.inscribirEquipo(ie1);
        torneoCerros.inscribirEquipo(ie2);
        torneoCerros.inscribirEquipo(ie3);

        System.out.println("Equipos Válidos Inscritos en el Torneo:");
        System.out.println(" - " + eq1.getNombre() + " (Nómina oficial: " + ie1.getNominaHabilitada().size() + " jugadores)");
        System.out.println(" - " + eq2.getNombre() + " (Nómina oficial: " + ie2.getNominaHabilitada().size() + " jugadores)");
        System.out.println(" - " + eq3.getNombre() + " (Nómina oficial: " + ie3.getNominaHabilitada().size() + " jugadores)\n");

        TablaPosiciones tabla = new TablaPosiciones(torneoCerros.getEquiposInscritos());

        // JORNADA 1
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println(" --- JORNADA 1 ---");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("Partido Oficial: Los Cracks FC [2] vs [1] Tigres FC (Rival institucional - Estadio El Campincito)");
        Partido p1 = new PartidoCompetencia(fechaHaceDias(5), 2, 1, "Tigres FC", "Estadio El Campincito");
        ie1.registrarResultado(p1.getMarcadorLocal(), p1.getMarcadorVisitante());
        ie2.registrarResultado(p1.getMarcadorVisitante(), p1.getMarcadorLocal());
        tabla.mostrarTabla();

        // JORNADA 2
        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" --- JORNADA 2 ---");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("Partido Oficial: Tigres FC [1] vs [1] Deportivo Central (Rival institucional - Cancha Universidad)");
        Partido p2 = new PartidoCompetencia(fechaHaceDias(3), 1, 1, "Deportivo Central", "Cancha Universidad");
        ie2.registrarResultado(p2.getMarcadorLocal(), p2.getMarcadorVisitante());
        ie3.registrarResultado(p2.getMarcadorVisitante(), p2.getMarcadorLocal());
        tabla.mostrarTabla();

        // JORNADA 3
        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" --- JORNADA 3 ---");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("Partido Oficial: Los Cracks FC [0] vs [3] Deportivo Central (Rival institucional - Coliseo Central)");
        Partido p3 = new PartidoCompetencia(fechaHaceDias(1), 0, 3, "Deportivo Central", "Coliseo Central");
        ie1.registrarResultado(p3.getMarcadorLocal(), p3.getMarcadorVisitante());
        ie3.registrarResultado(p3.getMarcadorVisitante(), p3.getMarcadorLocal());
        tabla.mostrarTabla();

        System.out.println("\n==========================================================================================================");
        System.out.println("           FIN DE LA PRUEBA 3 - TORNEO DE LOS CERROS FINALIZADO CON ÉXITO");
        System.out.println("==========================================================================================================");
    }

    private static Date fechaNacimientoHaceAnos(int anos) {
        LocalDate fecha = LocalDate.now().minusYears(anos);
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static void imprimirResultado(String caso, boolean obtenido, boolean esperado, String motivo) {
        String estado = (obtenido == esperado) ? "✅ PASÓ" : "❌ FALLÓ";
        String detalle = motivo.isEmpty() ? "Sin observaciones" : ("Motivo: " + motivo);
        System.out.printf("%-65s | %-55s | %s%n", caso, detalle, estado);
    }
}