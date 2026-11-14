package consola;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import comercio.RegistroTurnos;
import fachada.FachadaTorneos;
import fachada.OperacionesComercio;

/**
 * Consola del administrador: crea torneos, los inicia, registra resultados,
 * los finaliza y consulta reportes.
 */
public class ConsolaAdministrador extends ConsolaBase {

    private static final List<String> TIPOS = List.of("RECREATIVO", "COMPETITIVO");
    private static final List<String> MODALIDADES = List.of("CONJUNTO", "INDIVIDUAL", "DOBLES");
    private static final List<String> NIVELES = List.of("PRINCIPIANTE", "INTERMEDIO", "AVANZADO");

    public static void main(String[] args) {
        RegistroTurnos turnos = new RegistroTurnos();
        OperacionesComercio comercio = crearComercio(turnos);
        new ConsolaAdministrador(crearFachada(comercio, turnos), comercio, new LectorEntradas()).iniciar();
    }

    public ConsolaAdministrador(FachadaTorneos fachada, OperacionesComercio comercio, LectorEntradas lector) {
        super(fachada, comercio, lector);
    }

    @Override
    protected String titulo() {
        return "Club - Administrador";
    }

    @Override
    protected List<String> rolesPermitidos() {
        return List.of("ADMINISTRADOR");
    }

    @Override
    protected void mostrarMenu() {
        lector.mostrar("=== MENÚ ADMINISTRADOR ===");
        lector.mostrar("  1. Crear torneo");
        lector.mostrar("  2. Iniciar torneo");
        lector.mostrar("  3. Registrar resultado de un encuentro");
        lector.mostrar("  4. Finalizar torneo");
        lector.mostrar("  5. Consultar torneos");
        lector.mostrar("  6. Reporte de ventas (tienda y cafetería)");
    }

    @Override
    protected int cantidadOpciones() {
        return 6;
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> ejecutar(this::crearTorneo);
            case 2 -> ejecutar(this::iniciarTorneo);
            case 3 -> ejecutar(this::registrarResultado);
            case 4 -> ejecutar(this::finalizarTorneo);
            case 5 -> consultarTorneos();
            case 6 -> ejecutar(this::generarReporte);
            default -> lector.mostrar("Opción no válida.");
        }
        return true;
    }

    private void crearTorneo() {
        String nombre = lector.leerTextoNoVacio("Nombre del torneo:");
        String tipo = lector.leerOpcion("Tipo de torneo:", TIPOS);
        String modalidad = lector.leerOpcion("Modalidad:", MODALIDADES);
        String disciplina = lector.leerTextoNoVacio("Deporte o disciplina:");
        LocalDate fecha = lector.leerFecha("Fecha del torneo");

        Integer edadMaxima = null;
        String nivel = null;
        String categoria = lector.leerOpcion("La categoría se define por:", List.of("Edad máxima", "Nivel"));
        if (categoria.equals("Edad máxima")) {
            edadMaxima = lector.leerEntero("Edad máxima permitida:", 1, 100);
        } else {
            nivel = lector.leerOpcion("Nivel requerido:", NIVELES);
        }

        int cupos = lector.leerEntero("Cupos totales:", 1, 1000);
        double tarifa = 0;
        if (tipo.equals("COMPETITIVO")) {
            tarifa = lector.leerDecimal("Tarifa de inscripción:", 1);
        }

        String id = fachada.crearTorneo(loginActual, nombre, tipo, modalidad, disciplina, fecha,
                edadMaxima, nivel, cupos, tarifa);
        lector.mostrar("Torneo creado con id " + id + ". Estado: Inscripciones abiertas.");
    }

    private void iniciarTorneo() {
        String id = pedirTorneo();
        fachada.iniciarTorneo(loginActual, id);
        lector.mostrar("El torneo quedó En curso y se generó el calendario.");
        mostrarLista("Encuentros", fachada.listarEncuentrosPendientes(id));
    }

    private void registrarResultado() {
        String id = pedirTorneo();
        List<String> pendientes = fachada.listarEncuentrosPendientes(id);
        mostrarLista("Encuentros sin resultado", pendientes);
        if (pendientes.isEmpty()) {
            return;
        }
        String encuentro = lector.leerTextoNoVacio("Id del encuentro:");
        int local = lector.leerEntero("Marcador del local:", 0, 1000);
        int visitante = lector.leerEntero("Marcador del visitante:", 0, 1000);
        fachada.registrarResultado(loginActual, id, encuentro, local, visitante);
        lector.mostrar("Resultado registrado y tabla actualizada.");
    }

    private void finalizarTorneo() {
        String id = pedirTorneo();
        if (lector.leerSiNo("Al finalizar se reparten los premios. ¿Continuar?")) {
            fachada.finalizarTorneo(loginActual, id);
            lector.mostrar(fachada.consultarDetalleTorneo(id));
        }
    }

    private void generarReporte() {
        String tipo = lector.leerOpcion("Tipo de reporte:", List.of("tienda", "cafeteria", "ventas", "todos"));
        LocalDate desde = lector.leerFecha("Fecha inicial");
        LocalDate hasta = lector.leerFecha("Fecha final");
        // El generador de reportes del P1 trabaja con Date; se toma el día completo
        Date inicio = Date.from(desde.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fin = Date.from(hasta.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        lector.mostrar(comercio.generarReporte(tipo, inicio, fin));
    }

    private String pedirTorneo() {
        mostrarLista("Torneos", fachada.listarTorneos());
        return lector.leerTextoNoVacio("Id del torneo:");
    }
}
