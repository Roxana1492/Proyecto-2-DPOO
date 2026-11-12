package consola;

import java.util.List;

import fachada.FachadaTorneos;
import fachada.OperacionesComercio;

/**
 * Consola de entrenadores y fisioterapeutas. Pueden inscribirse a torneos
 * individuales o de dobles si no tienen turno ese día.
 */
public class ConsolaEmpleado extends ConsolaBase {

    public ConsolaEmpleado(FachadaTorneos fachada, OperacionesComercio comercio, LectorEntradas lector) {
        super(fachada, comercio, lector);
    }

    @Override
    protected String titulo() {
        return "Club - Empleados";
    }

    @Override
    protected List<String> rolesPermitidos() {
        return List.of("ENTRENADOR", "FISIOTERAPEUTA");
    }

    @Override
    protected void mostrarMenu() {
        lector.mostrar("=== MENÚ EMPLEADO ===");
        lector.mostrar("  1. Inscribirme a un torneo");
        lector.mostrar("  2. Salirme de un torneo");
        lector.mostrar("  3. Ver mis torneos");
        lector.mostrar("  4. Consultar torneos");
        lector.mostrar("  5. Solicitar cambio de turno");
    }

    @Override
    protected int cantidadOpciones() {
        return 5;
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> ejecutar(this::inscribirse);
            case 2 -> ejecutar(this::desinscribirse);
            case 3 -> mostrarLista("Mis torneos", fachada.listarMisTorneos(loginActual));
            case 4 -> consultarTorneos();
            case 5 -> ejecutar(this::solicitarCambioTurno);
            default -> lector.mostrar("Opción no válida.");
        }
        return true;
    }

    private void inscribirse() {
        mostrarLista("Torneos", fachada.listarTorneos());
        String id = lector.leerTextoNoVacio("Id del torneo:");
        if (lector.leerSiNo("¿Es un torneo de dobles?")) {
            String companero = lector.leerTextoNoVacio("Usuario de su pareja:");
            String pago = null;
            if (lector.leerSiNo("¿Su pareja paga inscripción (torneo competitivo)?")) {
                pago = lector.leerTextoNoVacio("Código de confirmación del pago:");
            }
            fachada.inscribirPareja(loginActual, companero, id, pago);
        } else {
            // Los empleados no pagan inscripción
            fachada.inscribirIndividual(loginActual, id, null);
        }
        lector.mostrar("Inscripción registrada (sin costo para empleados).");
    }

    private void desinscribirse() {
        mostrarLista("Mis torneos", fachada.listarMisTorneos(loginActual));
        String id = lector.leerTextoNoVacio("Id del torneo:");
        if (lector.leerSiNo("¿Confirma que se quiere salir?")) {
            fachada.desinscribir(loginActual, id);
            lector.mostrar("Se liberó su cupo.");
        }
    }

    private void solicitarCambioTurno() {
        List<String> turnos = comercio.listarTurnos();
        mostrarLista("Turnos", turnos);
        if (turnos.isEmpty()) {
            return;
        }
        int turno = lector.leerEntero("Número del turno que quiere cambiar:", 1, turnos.size());
        String reemplazo = lector.leerTextoNoVacio("Id del empleado que lo reemplaza:");
        lector.mostrar(comercio.solicitarCambioTurno(turno, reemplazo));
    }
}
