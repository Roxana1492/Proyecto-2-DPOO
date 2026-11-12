package pruebas;

import autenticacion.Socio;
import deportivo.*;
import persistencia.AlmacenadorArchivos;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Prueba4 {

    private static Date fechaHaceDias(int dias) {
        LocalDate fecha = LocalDate.now().minusDays(dias);
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 4: TORNEO ASCUN - FASE DE GRUPOS Y ELIMINACIÓN DIRECTA (DPOO)");
        System.out.println("==========================================================================================================");

        // DIAGNÓSTICO DE RUTA Y EXISTENCIA DE ARCHIVOS
        File dirDatos = new File("../datos");
        File fSocios = new File("../datos/socios.txt");
        File fEquipos = new File("../datos/equipos.txt");
     

        Map<String, Socio> socios = new HashMap<>();
        List<Equipo> equipos = new ArrayList<>();
        Map<String, Deporte> deportes = new HashMap<>();

        try {
            if (fSocios.exists() && fEquipos.exists()) {
                System.out.println("\n[DATOS] Carga exitosa de archivos desde " + fSocios.getCanonicalPath());
                socios = AlmacenadorArchivos.cargarSocios(fSocios.getPath());
                equipos = AlmacenadorArchivos.cargarEquipos(fEquipos.getPath(), deportes);
            } else {
                System.out.println("\n[DATOS] Nota: Archivos no hallados en ../datos/. Inicializando datos base en memoria...");
                deportes.put("Fútbol", new Deporte("Fútbol", 3));

                socios.put("s1", new Socio("s1", "pass", "Carlos Pérez", fechaNacimientoHaceAnos(22)));
                socios.put("s2", new Socio("s2", "pass", "Andrés Gómez", fechaNacimientoHaceAnos(24)));
                socios.put("s3", new Socio("s3", "pass", "Mateo Ríos", fechaNacimientoHaceAnos(23)));
                socios.put("s4", new Socio("s4", "pass", "Felipe Rojas", fechaNacimientoHaceAnos(25)));

                equipos.add(new Equipo("Los Cracks FC", 5, 18, 30, deportes.get("Fútbol")));
                equipos.add(new Equipo("Tigres FC", 5, 18, 30, deportes.get("Fútbol")));
                equipos.add(new Equipo("Deportivo Central", 5, 18, 30, deportes.get("Fútbol")));
                equipos.add(new Equipo("Real Academia", 5, 18, 30, deportes.get("Fútbol")));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            return;
        }

        Deporte futbol = deportes.get("Fútbol");
        Equipo eq1 = equipos.get(0); // Los Cracks FC
        Equipo eq2 = equipos.get(1); // Tigres FC
        Equipo eq3 = equipos.get(2); // Deportivo Central
        Equipo eq4 = equipos.get(3); // Real Academia

        Calendar cal = Calendar.getInstance();
        cal.set(2001, Calendar.JANUARY, 1);
        Socio sBase = new Socio("base", "pass", "Jugador Base", cal.getTime());

        InscripcionEquipo ie1 = new InscripcionEquipo(eq1, Arrays.asList(sBase, sBase, sBase));
        InscripcionEquipo ie2 = new InscripcionEquipo(eq2, Arrays.asList(sBase, sBase, sBase));
        InscripcionEquipo ie3 = new InscripcionEquipo(eq3, Arrays.asList(sBase, sBase, sBase));
        InscripcionEquipo ie4 = new InscripcionEquipo(eq4, Arrays.asList(sBase, sBase, sBase));

        List<InscripcionEquipo> equiposGrupoA = new ArrayList<>(Arrays.asList(ie1, ie2, ie3, ie4));
        
        Competencia torneoAscun = new Competencia("Torneo ASCUN", 6);
        torneoAscun.inscribirEquipo(ie1);
        torneoAscun.inscribirEquipo(ie2);
        torneoAscun.inscribirEquipo(ie3);
        torneoAscun.inscribirEquipo(ie4);

        TablaPosiciones tablaGrupoA = new TablaPosiciones(equiposGrupoA, "Posiciones Grupo A");

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" FASE 1: REGISTRO PARCIAL DE PARTIDOS Y PRUEBA DE RECHAZO POR AVANCE PREMATURO");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        System.out.println(" - Partido 1: Los Cracks FC [2] vs [1] Tigres FC");
        ie1.registrarResultado(2, 1); ie2.registrarResultado(1, 2); torneoAscun.registrarPartidoGrupo();

        System.out.println(" - Partido 2: Deportivo Central [3] vs [0] Real Academia");
        ie3.registrarResultado(3, 0); ie4.registrarResultado(0, 3); torneoAscun.registrarPartidoGrupo();

        System.out.println(" - Partido 3: Los Cracks FC [2] vs [1] Deportivo Central");
        ie1.registrarResultado(2, 1); ie3.registrarResultado(1, 2); torneoAscun.registrarPartidoGrupo();

        System.out.println(" - Partido 4: Tigres FC [2] vs [0] Real Academia");
        ie2.registrarResultado(2, 0); ie4.registrarResultado(0, 2); torneoAscun.registrarPartidoGrupo();

        // Prueba de rechazo
        boolean avancePrematuro = torneoAscun.avanzarFase();
        System.out.println("\n-> Intentando avanzar de fase con " + torneoAscun.getPartidosRestantes() + " partidos pendientes...");
        System.out.println("¿Avanzó la fase?: " + (avancePrematuro ? "SÍ (Error)" : "✅ RECHAZADO CORRECTAMENTE (Faltan partidos por disputar en el grupo)."));


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" REGISTRO DE LOS PARTIDOS RESTANTES Y CIERRE DE GRUPO");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        System.out.println(" - Partido 5: Los Cracks FC [4] vs [0] Real Academia");
        ie1.registrarResultado(4, 0); ie4.registrarResultado(0, 4); torneoAscun.registrarPartidoGrupo();

        System.out.println(" - Partido 6: Tigres FC [1] vs [1] Deportivo Central");
        ie2.registrarResultado(1, 1); ie3.registrarResultado(1, 1); torneoAscun.registrarPartidoGrupo();

        System.out.println("\n");
     // Imprimimos la tabla (esto ejecuta calcularYOrdenar y reorganiza la lista)
        tablaGrupoA.mostrarTabla();

        torneoAscun.avanzarFase();
        System.out.println("✅ Todos los partidos jugados. Fase actual del torneo: " + torneoAscun.getFaseActual());

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" DETERMINACIÓN DE CLASIFICADOS Y CRITERIO DE DESEMPATE");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        // Extraemos DIRECTAMENTE de la lista ya ordenada por el sistema
        InscripcionEquipo clasificado1 = equiposGrupoA.get(0); // 1.° Puesto (Los Cracks FC)
        InscripcionEquipo clasificado2 = equiposGrupoA.get(1); // 2.° Puesto (Deportivo Central)

        System.out.println("1.° Puesto Clasificado: " + clasificado1.getEquipo().getNombre() + " (" + clasificado1.getPuntos() + " pts, DG: +" + clasificado1.getDiferenciaGoles() + ")");
        System.out.println("2.° Puesto Clasificado: " + clasificado2.getEquipo().getNombre() + " (" + clasificado2.getPuntos() + " pts, DG: +" + clasificado2.getDiferenciaGoles() + ")");
        System.out.println(" Criterio aplicado: Empate en puntos (4) y enfrentamiento directo (1-1). La Diferencia de Goles (+2 vs +1) otorga el 2.° puesto a " + clasificado2.getEquipo().getNombre() + ".");

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" FASE 2: FINAL DE ELIMINACIÓN DIRECTA (TORNEO ASCUN)");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        // La final usa dinámicamente al clasificado 1 y al clasificado 2
        System.out.println("Llave de la Gran Final: " + clasificado1.getEquipo().getNombre() + " vs " + clasificado2.getEquipo().getNombre());
        
        // Registro de la Gran Final con ganador definitivo
        PartidoCompetencia partidoFinal = new PartidoCompetencia(fechaHaceDias(0), 2, 0, clasificado2.getEquipo().getNombre(), "Estadio Metropolitano");
        
        System.out.println("\nResultado oficial de la Gran Final:");
        System.out.println(" " + clasificado1.getEquipo().getNombre() + " [" + partidoFinal.getMarcadorLocal() + "] vs [" + partidoFinal.getMarcadorVisitante() + "] " + clasificado2.getEquipo().getNombre() + " (Escenario: " + partidoFinal.getEscenario() + ")");
        System.out.println("¡CAMPEÓN DEL TORNEO ASCUN: " + clasificado1.getEquipo().getNombre() + "!");
        System.out.println("\n==========================================================================================================");
        System.out.println("           FIN DE LA PRUEBA 4 - FASE DE GRUPOS Y ELIMINACIÓN DIRECTA CONCLUIDAS");
        System.out.println("==========================================================================================================");
    }

    private static Date fechaNacimientoHaceAnos(int anos) {
        LocalDate fecha = LocalDate.now().minusYears(anos);
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}