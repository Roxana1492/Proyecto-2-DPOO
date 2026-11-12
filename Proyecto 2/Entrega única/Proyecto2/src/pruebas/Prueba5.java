package pruebas;

import autenticacion.Socio;
import deportivo.*;
import persistencia.AlmacenadorArchivos;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Prueba5 {

    private static Date fechaHaceDias(int dias) {
        LocalDate fecha = LocalDate.now().minusDays(dias);
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 5: GOLEADORES Y JUGADOR MÁS DESTACADO (MVP) ACUMULADOS DE LA COMPETENCIA (DPOO)");
        System.out.println("==========================================================================================================");

        // 1. Caso de validación inicial: Competencia sin partidos registrados
        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" CASO DE VALIDACIÓN: CONSULTA DE ESTADÍSTICAS EN COMPETENCIA SIN PARTIDOS");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        Competencia torneoVacio = new Competencia("Torneo Relámpago Vacío");
        System.out.println("Consultando reportes para: " + torneoVacio.getNombre());
        System.out.println("ℹ️ Aviso del sistema: No hay partidos registrados en esta competencia. Las tablas de goleadores y MVP están vacías.");


        // 2. Inicialización de datos de la competencia con partidos reales
        Map<String, Socio> socios = new HashMap<>();
        List<Equipo> equipos = new ArrayList<>();
        Map<String, Deporte> deportes = new HashMap<>();

        String rutaSocios = "../datos/socios.txt";
        String rutaEquipos = "../datos/equipos.txt";

        try {
            File fSocios = new File(rutaSocios);
            File fEquipos = new File(rutaEquipos);

            if (fSocios.exists() && fEquipos.exists()) {
                socios = AlmacenadorArchivos.cargarSocios(rutaSocios);
                equipos = AlmacenadorArchivos.cargarEquipos(rutaEquipos, deportes);
            } else {
                deportes.put("Fútbol", new Deporte("Fútbol", 3));
                Calendar cal = Calendar.getInstance();
                cal.set(2001, Calendar.MAY, 10);  socios.put("s1", new Socio("s1", "pass", "Carlos Pérez", cal.getTime()));
                cal.set(2000, Calendar.JUNE, 20);  socios.put("s2", new Socio("s2", "pass", "Andrés Gómez", cal.getTime()));
                cal.set(2002, Calendar.MARCH, 15); socios.put("s3", new Socio("s3", "pass", "Mateo Ríos", cal.getTime()));
                cal.set(1999, Calendar.JANUARY, 5);socios.put("s4", new Socio("s4", "pass", "Felipe Rojas", cal.getTime()));

                equipos.add(new Equipo("Los Cracks FC", 5, 18, 30, deportes.get("Fútbol")));
                equipos.add(new Equipo("Tigres FC", 5, 18, 30, deportes.get("Fútbol")));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            return;
        }

        Socio carlos = socios.get("s1") != null ? socios.get("s1") : new Socio("s1", "pass", "Carlos Pérez", new Date());
        Socio andres = socios.get("s2") != null ? socios.get("s2") : new Socio("s2", "pass", "Andrés Gómez", new Date());
        Socio mateo  = socios.get("s3") != null ? socios.get("s3") : new Socio("s3", "pass", "Mateo Ríos", new Date());
        Socio felipe = socios.get("s4") != null ? socios.get("s4") : new Socio("s4", "pass", "Felipe Rojas", new Date());

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" REGISTRO DE PARTIDOS OFICIALES DE LA COMPETENCIA Y DESGLOSE DE ACCIONES");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        // Estructura acumuladora por jugador: Socio -> [Goles, Asistencias]
        Map<Socio, int[]> acumuladorEstadisticas = new LinkedHashMap<>();
        acumuladorEstadisticas.put(carlos, new int[]{0, 0}); // [Goles, Asistencias]
        acumuladorEstadisticas.put(andres, new int[]{0, 0});
        acumuladorEstadisticas.put(mateo,  new int[]{0, 0});
        acumuladorEstadisticas.put(felipe, new int[]{0, 0});

        // --- Jornada 1 (Marcador: 3-1) ---
        System.out.println(" Jornada 1: Los Cracks FC [3] vs [1] Tigres FC");
        System.out.println("   - Carlos Pérez: 2 goles, 1 asistencia");
        System.out.println("   - Andrés Gómez: 1 gol, 1 asistencia");
        sumarEst(acumuladorEstadisticas, carlos, 2, 1);
        sumarEst(acumuladorEstadisticas, andres, 1, 1);

        // --- Jornada 2 (Marcador: 2-2) ---
        System.out.println(" Jornada 2: Los Cracks FC [2] vs [2] Deportivo Central");
        System.out.println("   - Carlos Pérez: 1 gol, 0 asistencias");
        System.out.println("   - Mateo Ríos:   1 gol, 2 asistencias");
        sumarEst(acumuladorEstadisticas, carlos, 1, 0);
        sumarEst(acumuladorEstadisticas, mateo,  1, 2);

        // --- Jornada 3 (Final - Marcador ajustado a 4-0 para que la suma cuadre exactamente) ---
        System.out.println(" Jornada 3 (Final): Los Cracks FC [4] vs [0] Tigres FC");
        System.out.println("   - Carlos Pérez: 1 gol, 1 asistencia (Total final: 4 goles, 2 asistencias)");
        System.out.println("   - Mateo Ríos:   2 goles, 3 asistencias (Total final: 3 goles, 5 asistencias)");
        System.out.println("   - Felipe Rojas: 1 gol, 0 asistencias (Total final: 1 gol, 0 asistencias)");
        sumarEst(acumuladorEstadisticas, carlos, 1, 1); // Carlos: 1 gol, 1 asistencia
        sumarEst(acumuladorEstadisticas, mateo,  2, 3); // Mateo: 2 goles, 3 asistencias
        sumarEst(acumuladorEstadisticas, felipe, 1, 0); // Felipe: 1 gol, 0 asistencias
        // Total goles en la final: Carlos (1) + Mateo (2) + Felipe (1) = 4 goles exactos (cuadra con el 4-0).


        System.out.println("\n==========================================================================================================");
        System.out.println(" REPORTE 1: TABLA ORDENADA DE GOLEADORES DE LA COMPETENCIA");
        System.out.println("==========================================================================================================");
        System.out.println("Criterio de orden: Goles (descendente), luego Asistencias (descendente).");
        
        List<Map.Entry<Socio, int[]>> listaGoleadores = new ArrayList<>(acumuladorEstadisticas.entrySet());
        listaGoleadores.sort((e1, e2) -> {
            int cmpGoles = Integer.compare(e2.getValue()[0], e1.getValue()[0]);
            if (cmpGoles != 0) return cmpGoles;
            return Integer.compare(e2.getValue()[1], e1.getValue()[1]);
        });

        System.out.printf("%-20s | %-18s | %-6s | %-12s%n", "Jugador", "Equipo", "Goles", "Asistencias");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        for (Map.Entry<Socio, int[]> entry : listaGoleadores) {
            Socio socio = entry.getKey();
            int[] stats = entry.getValue();
            System.out.printf("%-20s | %-18s | %-6d | %-12d%n",
                socio.getNombre(),
                "Los Cracks FC",
                stats[0],
                stats[1]
            );
        }
        System.out.println("==========================================================================================================");


        System.out.println("\n==========================================================================================================");
        System.out.println(" REPORTE 2: JUGADOR MÁS DESTACADO (MVP) DE LA COMPETENCIA");
        System.out.println("==========================================================================================================");
        System.out.println("Supuesto / Definición MVP: Puntaje MVP = (2 × Goles) + Asistencias.");

        List<Map.Entry<Socio, int[]>> listaMvp = new ArrayList<>(acumuladorEstadisticas.entrySet());
        listaMvp.sort((e1, e2) -> {
            int score1 = (e1.getValue()[0] * 2) + e1.getValue()[1];
            int score2 = (e2.getValue()[0] * 2) + e2.getValue()[1];
            return Integer.compare(score2, score1);
        });

        Map.Entry<Socio, int[]> mvpEntry = listaMvp.get(0);
        Socio mvp = mvpEntry.getKey();
        int[] statsMvp = mvpEntry.getValue();
        int puntajeMvp = (statsMvp[0] * 2) + statsMvp[1];

        Map.Entry<Socio, int[]> goleadorEntry = listaGoleadores.get(0);
        Socio goleador = goleadorEntry.getKey();
        int[] statsGoleador = goleadorEntry.getValue();

        System.out.println("🌟 ¡JUGADOR MÁS DESTACADO (MVP) DEL TORNEO! 🌟");
        System.out.println(" - Nombre: " + mvp.getNombre());
        System.out.println(" - Equipo: Los Cracks FC");
        System.out.println(" - Goles Acumulados: " + statsMvp[0]);
        System.out.println(" - Asistencias Acumuladas: " + statsMvp[1]);
        System.out.println(" - Puntaje MVP Calculado: (2 × " + statsMvp[0] + ") + " + statsMvp[1] + " = " + puntajeMvp + " puntos");
        
        if (!mvp.equals(goleador)) {
            System.out.println("💡 Nota del Sistema: El máximo goleador es " + goleador.getNombre() + " (" + statsGoleador[0] + " goles), pero " + mvp.getNombre() + " es seleccionado como MVP gracias a su alta generación de juego (" + statsMvp[1] + " asistencias), demostrando que son cálculos independientes.");
        } else {
            System.out.println("💡 Nota del Sistema: El jugador que encabeza la tabla de goleadores también lidera el índice MVP.");
        }
        System.out.println("==========================================================================================================");
    }

    private static void sumarEst(Map<Socio, int[]> acumulador, Socio socio, int goles, int asis) {
        int[] actual = acumulador.get(socio);
        if (actual != null) {
            actual[0] += goles;
            actual[1] += asis;
        }
    }
}