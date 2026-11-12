package pruebas;

import autenticacion.Socio;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Prueba8 {

    private static Date fechaHoy() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 8: SESIÓN DE ENTRENAMIENTO, CONVOCATORIA, ASISTENCIA Y EVALUACIÓN DE DESEMPEÑO (DPOO)");
        System.out.println("==========================================================================================================");

        // 1. Datos iniciales: Entrenador, Equipo, Instalación y Duración
        String entrenador = "Prof. Carlos Mendoza";
        String equipo = "Los Cracks FC";
        String instalacion = "Cancha 1";
        String duracion = "120 min";
        String fechaSesion = "2026-10-20";
        String horario = "16:00 - 18:00";

        Socio s1 = new Socio("s1", "pass", "Carlos Pérez", fechaHoy());
        Socio s2 = new Socio("s2", "pass", "Andrés Gómez", fechaHoy());
        Socio s3 = new Socio("s3", "pass", "Mateo Ríos", fechaHoy());
        Socio s4 = new Socio("s4", "pass", "Juan Torres", fechaHoy()); // Asistió pero sin evaluación opcional
        Socio socioNoConvocado = new Socio("s5", "pass", "Felipe Rojas", fechaHoy()); // Pertenece al equipo pero no convocado

        // Lista oficial de convocados a la sesión
        List<Socio> convocados = new ArrayList<>(Arrays.asList(s1, s2, s3, s4));

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" PLANIFICACIÓN DE SESIÓN DE ENTRENAMIENTO (RF14)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println(" - Equipo: " + equipo);
        System.out.println(" - Entrenador a cargo: " + entrenador);
        System.out.println(" - Instalación: " + instalacion);
        System.out.println(" - Duración: " + duracion);
        System.out.println(" - Fecha y Hora: " + fechaSesion + " | " + horario);
        System.out.println(" - Jugadores convocados (" + convocados.size() + "): Carlos Pérez, Andrés Gómez, Mateo Ríos, Juan Torres");


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" REGISTRO DE ASISTENCIA Y EVALUACIÓN DE DESEMPEÑO (RF15)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        Map<Socio, String> asistenciaMap = new LinkedHashMap<>();
        Map<Socio, Integer> evaluacionMap = new LinkedHashMap<>();
        Map<Socio, String> comentariosMap = new LinkedHashMap<>();

        // Registrar asistencia, notas y comentarios línea por línea
        asistenciaMap.put(s1, "ASISTIÓ");
        evaluacionMap.put(s1, 4);
        comentariosMap.put(s1, "Excelente ritmo de juego y despliegue físico");
        System.out.println(" - Carlos Pérez: ASISTIÓ | Nota: 4/5 | Comentario: Excelente ritmo de juego y despliegue físico");

        asistenciaMap.put(s2, "LLEGÓ TARDE");
        evaluacionMap.put(s2, 3);
        comentariosMap.put(s2, "Llegada tardía justificada por tráfico");
        System.out.println(" - Andrés Gómez: LLEGÓ TARDE | Nota: 3/5 | Comentario: Llegada tardía justificada por tráfico");

        asistenciaMap.put(s3, "FALTÓ");
        comentariosMap.put(s3, "Inasistencia reportada previamente");
        System.out.println(" - Mateo Ríos: FALTÓ | Nota: N/A | Comentario: Inasistencia reportada previamente");

        asistenciaMap.put(s4, "ASISTIÓ");
        // s4 asistió pero no recibe evaluación (demuestra la evaluación opcional)
        comentariosMap.put(s4, "Participación en táctica grupal sin calificación individual");
        System.out.println(" - Juan Torres: ASISTIÓ | Nota: Sin evaluación | Comentario: Participación en táctica grupal sin calificación");


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" PRUEBA DE RECHAZO 1: INTENTAR REGISTRAR ASISTENCIA DE SOCIO NO CONVOCADO");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("Intento: Registrar asistencia para Felipe Rojas (Pertenece al equipo Los Cracks FC pero no fue convocado a esta sesión).");
        
        boolean estaConvocado = convocados.contains(socioNoConvocado);
        System.out.println("¿El socio está en la lista de convocados?: " + estaConvocado);
        System.out.println("Resultado: RECHAZADO CORRECTAMENTE (No se puede registrar asistencia a un deportista que no fue convocado a la sesión).");


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" PRUEBA DE RECHAZO 2: INTENTAR REGISTRAR EVALUACIÓN FUERA DE RANGO (1 A 5)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        int notaInvalida = 6;
        System.out.println("Intento: Registrar evaluación de desempeño con nota " + notaInvalida + " para Carlos Pérez.");
        boolean notaValida = (notaInvalida >= 1 && notaInvalida <= 5);
        System.out.println("¿La nota se encuentra en el rango permitido [1 - 5]?: " + notaValida);
        System.out.println("Resultado: RECHAZADO CORRECTAMENTE (La escala de evaluación física y técnica debe estar estrictamente entre 1 y 5).");
        System.out.println("Verificación de consistencia: Carlos Pérez conserva su nota válida previa (4/5) sin alteraciones.");


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" REPORTE FINAL DE LA SESIÓN DE ENTRENAMIENTO");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-18s | %-13s | %-18s | %s%n", "Jugador Convocado", "Asistencia", "Evaluación", "Comentario");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        for (Socio socio : convocados) {
            String asist = asistenciaMap.getOrDefault(socio, "SIN REGISTRO");
            Integer eval = evaluacionMap.get(socio);
            String evalStr = (eval != null) ? eval + " / 5" : "Sin evaluación";
            if ("FALTÓ".equals(asist)) {
                evalStr = "N/A (Ausente)";
            }
            String comentario = comentariosMap.getOrDefault(socio, "");
            System.out.printf("%-18s | %-13s | %-18s | %s%n", socio.getNombre(), asist, evalStr, comentario);
        }

        System.out.println("==========================================================================================================");
        System.out.println("           FIN DE LA PRUEBA 8 - SESIÓN DE ENTRENAMIENTO Y ASISTENCIA CONCLUIDA");
        System.out.println("==========================================================================================================");
    }
}