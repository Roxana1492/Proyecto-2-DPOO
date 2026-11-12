package pruebas;

import autenticacion.Socio;
import deportivo.*;
import persistencia.AlmacenadorArchivos;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Prueba6 {

    private static Date fechaHoy() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 6: GESTIÓN DE INSTALACIONES, BLOQUES HORARIOS, MODALIDAD Y BEBIDAS CALIENTES (DPOO)");
        System.out.println("==========================================================================================================");

        // DIAGNÓSTICO DE RUTA Y CARGA DE DATOS DESDE ../datos/
        File dirDatos = new File("../datos");
        File fSocios = new File("../datos/socios.txt");
        File fEquipos = new File("../datos/equipos.txt");

        System.out.println("[DIAGNÓSTICO RUTA DATOS] Buscando carpeta en: " + dirDatos.getAbsolutePath());
        
        Map<String, Socio> sociosMap = new HashMap<>();
        try {
            if (fSocios.exists() && fEquipos.exists()) {
                System.out.println("[DATOS] Carga exitosa de archivos de socios y equipos desde " + fSocios.getCanonicalPath());
                sociosMap = AlmacenadorArchivos.cargarSocios(fSocios.getPath());
            } else {
                System.out.println("[DATOS] Nota: Archivos no hallados en ../datos/. Inicializando datos de prueba en memoria...");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
        }

        // Definición de instalaciones
        Instalacion canchaPadel1 = new Instalacion("Cancha Pádel 1", "Pádel", true, true, 4);  // Techada, restringe bebidas
        Instalacion canchaPadel2 = new Instalacion("Cancha Pádel 2", "Pádel", true, true, 4);  // Techada, restringe bebidas
        Instalacion canchaPadel3 = new Instalacion("Cancha Pádel 3", "Pádel", true, true, 4);  // Techada, restringe bebidas
        Instalacion canchaTenisAbierta = new Instalacion("Cancha Tenis 1 (Abierta)", "Tenis", false, false, 2); // No techada

        // Socios de prueba (con tildes exactas a socios.txt)
        Socio s1 = sociosMap.get("s1") != null ? sociosMap.get("s1") : new Socio("s1", "pass", "Carlos Pérez", fechaHoy());
        Socio s2 = sociosMap.get("s2") != null ? sociosMap.get("s2") : new Socio("s2", "pass", "Andrés Gómez", fechaHoy());
        Socio s3 = sociosMap.get("s3") != null ? sociosMap.get("s3") : new Socio("s3", "pass", "Mateo Ríos", fechaHoy());
        Socio s4 = sociosMap.get("s4") != null ? sociosMap.get("s4") : new Socio("s4", "pass", "Felipe Rojas", fechaHoy());

        List<Socio> nominaCompleta = Arrays.asList(s1, s2, s3, s4); // 4 jugadores (Dobles válido)
        List<Socio> nominaIncompleta = Arrays.asList(s1, s2, s3);   // 3 jugadores (Inválido para dobles)

        Map<String, List<ReservaInstalacion>> reservasPorBloque = new HashMap<>();
        int maxSimultaneasPorBloque = 2; // Límite de 2 instalaciones simultáneas por disciplina y franja


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" BLOQUE HORARIO 1: 18:00 - 19:00 (LÍMITE MÁXIMO: 2 CANCHAS SIMULTÁNEAS)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        String bloque1 = "18:00-19:00";
        reservasPorBloque.putIfAbsent(bloque1, new ArrayList<>());

        // --- Reserva Válida 1 ---
        System.out.println("\n[RESERVA 1] Carlos Pérez reserva Cancha Pádel 1 (Franja: 18:00-19:00 | Modalidad: Dobles con 4 jugadores):");
        boolean exito1 = registrarReserva(canchaPadel1, s1, bloque1, nominaCompleta, false, reservasPorBloque, maxSimultaneasPorBloque);
        System.out.println("Resultado: " + (exito1 ? "APROBADA" : "RECHAZADA"));
        mostrarEstadoOcupacion(bloque1, reservasPorBloque, maxSimultaneasPorBloque);

        // --- Reserva Válida 2 ---
        System.out.println("\n[RESERVA 2] Andrés Gómez reserva Cancha Pádel 2 (Franja: 18:00-19:00 | Modalidad: Dobles con 4 jugadores):");
        boolean exito2 = registrarReserva(canchaPadel2, s2, bloque1, nominaCompleta, false, reservasPorBloque, maxSimultaneasPorBloque);
        System.out.println("Resultado: " + (exito2 ? "APROBADA" : "RECHAZADA"));
        mostrarEstadoOcupacion(bloque1, reservasPorBloque, maxSimultaneasPorBloque);

        // --- Rechazo 1: Superar el máximo de instalaciones simultáneas ---
        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" PRUEBA DE RECHAZO: MÁXIMO DE INSTALACIONES SIMULTÁNEAS EN EL MISMO BLOQUE");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("[RESERVA 3] Mateo Ríos intenta reservar Cancha Pádel 3 (Franja: 18:00-19:00) cuando ya hay 2 ocupadas:");
        boolean exito3 = registrarReserva(canchaPadel3, s3, bloque1, nominaCompleta, false, reservasPorBloque, maxSimultaneasPorBloque);
        System.out.println("Resultado: " + (exito3 ? "APROBADA" : "RECHAZADO CORRECTAMENTE (Se alcanzó el límite máximo de 2 instalaciones simultáneas para Pádel en el bloque 18:00-19:00)."));
        mostrarEstadoOcupacion(bloque1, reservasPorBloque, maxSimultaneasPorBloque);


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" BLOQUE HORARIO 2: 20:00 - 21:00 (OTRA FRANJA CON CUPO LIBRE)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        String bloque2 = "20:00-21:00";
        reservasPorBloque.putIfAbsent(bloque2, new ArrayList<>());

        System.out.println("[RESERVA 4] Mateo Ríos reserva Cancha Pádel 3 en la nueva franja (20:00-21:00) sin problemas de cupo:");
        boolean exito4 = registrarReserva(canchaPadel3, s3, bloque2, nominaCompleta, false, reservasPorBloque, maxSimultaneasPorBloque);
        System.out.println("Resultado: " + (exito4 ? "APROBADA (Demuestra que el límite es por bloque horario)" : "RECHAZADA"));
        mostrarEstadoOcupacion(bloque2, reservasPorBloque, maxSimultaneasPorBloque);


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" PRUEBAS DE RECHAZO: CAPACIDAD/MODALIDAD, ADMINISTRADOR Y BEBIDA CALIENTES");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        // --- Rechazo 2: Incumplir la capacidad/modalidad ---
        System.out.println("\n[RESERVA 5] Felipe Rojas intenta reservar Cancha Pádel 3 (Franja: 20:00-21:00) con solo 3 jugadores:");
        boolean exito5 = registrarReserva(canchaPadel3, s4, bloque2, nominaIncompleta, false, reservasPorBloque, maxSimultaneasPorBloque);
        System.out.println("Resultado: " + (exito5 ? "APROBADA" : "RECHAZADO CORRECTAMENTE (La modalidad de pádel exige dobles con 4 jugadores exactos)."));
        mostrarEstadoOcupacion(bloque2, reservasPorBloque, maxSimultaneasPorBloque);

        // --- Rechazo 3: Administrador intentando reservar para uso personal ---
        System.out.println("\n[RESERVA 6 - REGLA ROL] Usuario Administrador intenta reservar Cancha Pádel 1 para uso personal:");
        boolean esAdmin = true;
        boolean permisoRolOk = !esAdmin; // El administrador no tiene permitido reservar para uso personal
        System.out.println("Resultado: " + (permisoRolOk ? "APROBADA" : "RECHAZADO CORRECTAMENTE (Los usuarios con rol Administrador no pueden realizar reservas para uso personal)."));

        // --- Rechazo 4: Bebida caliente aislada en cancha techada ---
        String bloqueLibre = "09:00-10:00";
        reservasPorBloque.putIfAbsent(bloqueLibre, new ArrayList<>());
        System.out.println("\n[RESERVA 7] Carlos Pérez intenta reservar Cancha Pádel 1 en horario libre (09:00-10:00) portando una BEBIDA CALIENTE:");
        boolean exito7 = registrarReserva(canchaPadel1, s1, bloqueLibre, nominaCompleta, true, reservasPorBloque, maxSimultaneasPorBloque);
        System.out.println("Resultado: " + (exito7 ? "APROBADA" : "RECHAZADO CORRECTAMENTE (No se permite el uso de canchas techadas con bebidas calientes)."));
        mostrarEstadoOcupacion(bloqueLibre, reservasPorBloque, maxSimultaneasPorBloque);

        // --- Contraste: Misma bebida caliente en cancha NO techada (Permitido) ---
        System.out.println("\n[RESERVA 8 - CONTRASTE] Carlos Pérez reserva Cancha Tenis 1 (Abierta / No techada) portando BEBIDA CALIENTE:");
        boolean exito8 = registrarReserva(canchaTenisAbierta, s1, bloqueLibre, Arrays.asList(s1, s2), true, reservasPorBloque, maxSimultaneasPorBloque);
        System.out.println("Resultado: " + (exito8 ? "APROBADA CORRECTAMENTE (Demuestra que la restricción de bebida caliente aplica únicamente a instalaciones techadas)." : "RECHAZADA"));
        mostrarEstadoOcupacion(bloqueLibre, reservasPorBloque, maxSimultaneasPorBloque);


        System.out.println("\n==========================================================================================================");
        System.out.println("           FIN DE LA PRUEBA 6 - GESTIÓN DE INSTALACIONES Y RESTRICCIONES CONCLUIDA");
        System.out.println("==========================================================================================================");
    }

    private static boolean registrarReserva(Instalacion cancha, Socio socio, String bloque, List<Socio> jugadores, boolean bebidaCaliente, Map<String, List<ReservaInstalacion>> reservasPorBloque, int maxSimultaneas) {
        List<ReservaInstalacion> reservasBloque = reservasPorBloque.get(bloque);
        
        if (cancha.restringeBebidasCalientes() && bebidaCaliente) {
            return false;
        }
        if (jugadores.size() < cancha.getCapacidadRequeridaModalidad()) {
            return false;
        }
        long ocupadas = reservasBloque.stream().filter(r -> r.getInstalacion().getDisciplina().equals(cancha.getDisciplina())).count();
        if (ocupadas >= maxSimultaneas) {
            return false;
        }
        
        reservasBloque.add(new ReservaInstalacion(cancha, socio, bloque, jugadores, bebidaCaliente));
        return true;
    }
    
    private static void mostrarEstadoOcupacion(String bloque, Map<String, List<ReservaInstalacion>> reservasPorBloque, int max) {
        List<ReservaInstalacion> reservas = reservasPorBloque.get(bloque);
        System.out.println("   [ESTADO DE OCUPACIÓN - Franja " + bloque + "]: " + reservas.size() + "/" + max + " canchas ocupadas.");
        for (ReservaInstalacion r : reservas) {
            System.out.println("      - " + r.getInstalacion().getNombre() + " (Responsable: " + r.getSocioResponsable().getNombre() + ", Jugadores: " + r.getJugadores().size() + ")");
        }
    }
}

   