package consola;

import java.util.List;

import fachada.FachadaTorneos;

/**
 * Consola de entrenadores y fisioterapeutas. Pueden inscribirse a torneos
 * individuales o de dobles si no tienen turno ese día.
 */
public class ConsolaEmpleado extends ConsolaBase {

    public ConsolaEmpleado(FachadaTorneos fachada, LectorEntradas lector) {
        super(fachada, lector);
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
    }

    @Override
    protected int cantidadOpciones() {
        return 4;
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> ejecutar(this::inscribirse);
            case 2 -> ejecutar(this::desinscribirse);
            case 3 -> mostrarLista("Mis torneos", fachada.listarMisTorneos(loginActual));
            case 4 -> consultarTorneos();
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
}
