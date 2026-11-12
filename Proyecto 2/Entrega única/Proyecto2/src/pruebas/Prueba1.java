package pruebas;

import autenticacion.Socio;
import deportivo.*;
import persistencia.AlmacenadorArchivos;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Prueba1 {

    // Método auxiliar para generar fechas dinámicas basadas en la fecha actual
    private static Date fechaNacimientoHaceAnos(int anos) {
        LocalDate fecha = LocalDate.now().minusYears(anos);
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 1: VERIFICACIÓN E INSCRIPCIÓN DE JUGADORES EN EQUIPOS (ENTREGA 1 / DPOO)");
        System.out.println("==========================================================================================================");

        Map<String, Socio> socios = new HashMap<>();
        List<Equipo> equipos = new ArrayList<>();
        Map<String, Deporte> deportes = new HashMap<>();

        // 1. Cargar datos desde ../datos/
        String rutaSocios = "../datos/socios.txt";
        String rutaEquipos = "../datos/equipos.txt";

        try {
            File fSocios = new File(rutaSocios);
            File fEquipos = new File(rutaEquipos);

            if (fSocios.exists() && fEquipos.exists()) {
                System.out.println("\n[DATOS] Carga exitosa de archivos de prueba desde " + fSocios.getCanonicalPath());
                socios = AlmacenadorArchivos.cargarSocios(rutaSocios);
                equipos = AlmacenadorArchivos.cargarEquipos(rutaEquipos, deportes);
            } else {
                System.out.println("\n[DATOS] No se encontraron archivos en ../datos/. Inicializando datos de prueba en memoria...");
                deportes.put("Fútbol", new Deporte("Fútbol", 5));
                deportes.put("Baloncesto", new Deporte("Baloncesto", 5));

                socios.put("s1", new Socio("s1", "pass", "Carlos Pérez", fechaNacimientoHaceAnos(24)));
                socios.put("s2", new Socio("s2", "pass", "Andrés Gómez", fechaNacimientoHaceAnos(26)));
                socios.put("s3", new Socio("s3", "pass", "Mateo Ríos", fechaNacimientoHaceAnos(15)));
                socios.put("s4", new Socio("s4", "pass", "Roberto Silva", fechaNacimientoHaceAnos(42)));
                socios.put("s5", new Socio("s5", "pass", "Daniela Vega", fechaNacimientoHaceAnos(23)));

                equipos.add(new Equipo("Los Cracks FC", 3, 18, 30, deportes.get("Fútbol")));
                equipos.add(new Equipo("Tigres FC", 3, 18, 30, deportes.get("Fútbol")));
                equipos.add(new Equipo("Cancha Llena FC", 1, 18, 35, deportes.get("Fútbol")));
                equipos.add(new Equipo("Eagles Basketball", 3, 18, 30, deportes.get("Baloncesto")));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar los datos: " + e.getMessage());
            return;
        }

        VerificacionInscripcion verificador = new VerificacionInscripcion();

        Equipo equipoMasc = equipos.get(0);          // Los Cracks FC (Fútbol)
        Equipo equipoSegundoFutbol = equipos.get(1); // Tigres FC (Fútbol)
        Equipo equipoLleno = equipos.get(2);         // Cancha Llena FC (Fútbol)
        Equipo equipoBaloncesto = equipos.get(3);    // Eagles Basketball (Baloncesto)

        Socio s1 = socios.get("s1");
        Socio s2 = socios.get("s2");
        Socio s3 = socios.get("s3");
        Socio s4 = socios.get("s4");
        Socio s5 = socios.get("s5");

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" EJECUCIÓN DE CASOS DE PRUEBA");
        System.out.println("----------------------------------------------------------------------------------------------------------\n");

        // CASO 1: Jugador Válido
        procesarInscripcion("Caso 1: Jugador válido en Los Cracks FC", verificador, s1, equipoMasc, 10, "Delantero", true);

        // CASO 2: Camiseta Repetida
        procesarInscripcion("Caso 2: Camiseta #10 ocupada en Los Cracks FC", verificador, s2, equipoMasc, 10, "Mediocampista", false);

        // CASO 3: Edad Menor
        procesarInscripcion("Caso 3: Edad menor (15 años) en Los Cracks FC", verificador, s3, equipoMasc, 7, "Delantero", false);

        // CASO 4: Edad Mayor
        procesarInscripcion("Caso 4: Edad mayor (" + s4.calcularEdad() + " años) en Los Cracks FC", verificador, s4, equipoMasc, 8, "Defensa", false);

        // CASO 5a y 5b: Cupo Lleno
        procesarInscripcion("Caso 5a: Inscribir titular en Cancha Llena FC", verificador, s5, equipoLleno, 1, "Portero", true);
        procesarInscripcion("Caso 5b: Rechazado por cupo en Cancha Llena FC", verificador, s2, equipoLleno, 12, "Defensa", false);

        // CASO 6: Mismo jugador en otro equipo del MISMO deporte
        procesarInscripcion("Caso 6: Mismo jugador en otro equipo de Fútbol", verificador, s1, equipoSegundoFutbol, 9, "Delantero", false);

        // CASO 7: Mismo jugador en equipo de OTRO deporte
        procesarInscripcion("Caso 7: Mismo jugador en equipo de Baloncesto", verificador, s1, equipoBaloncesto, 23, "Alero", true);

        // CASO 8: Múltiples criterios
        procesarInscripcion("Caso 8: Falla edad y camiseta simultáneamente", verificador, s3, equipoMasc, 10, "Volante", false);

        // ESTADO FINAL DE LOS EQUIPOS
        System.out.println("\n==========================================================================================================");
        System.out.println(" ESTADO FINAL DE LOS EQUIPOS Y SUS JUGADORES INSCRITOS");
        System.out.println("==========================================================================================================");
        for (Equipo eq : equipos) {
            eq.mostrarEstado();
            System.out.println();
        }
    }

    private static void procesarInscripcion(String nombreCaso, VerificacionInscripcion verificador, 
                                             Socio socio, Equipo equipo, int camiseta, String posicion, boolean esperadoAprobado) {

        String motivo = verificador.verificar(socio, equipo, camiseta);
        boolean aprobado = motivo.isEmpty();

        if (aprobado) {
            InscripcionJugador ij = new InscripcionJugador(posicion, camiseta, socio, equipo);
            equipo.agregarInscripcion(ij);
        }

        boolean cumplioExpectativa = (aprobado == esperadoAprobado);
        String resultadoStr = aprobado ? "Aprobado" : ("Rechazado (" + motivo + ")");
        String estadoPrueba = cumplioExpectativa ? "PASÓ" : "FALLÓ";

        System.out.printf("%-52s | %-52s | %s%n", nombreCaso, resultadoStr, estadoPrueba);
    }
}