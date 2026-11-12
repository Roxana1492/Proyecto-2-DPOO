package consola;

import java.util.List;

import fachada.FachadaTorneos;
import fachada.OperacionesComercio;
import fachada.OperacionRechazadaException;

/**
 * Parte común de las tres consolas: inicio de sesión, ciclo del menú,
 * manejo de errores y guardado de los datos al salir.
 * Cada consola solo define qué roles acepta, su menú y qué hace cada opción.
 */
public abstract class ConsolaBase {

    private static final int MAX_INTENTOS = 3;

    protected final FachadaTorneos fachada;
    protected final OperacionesComercio comercio;
    protected final LectorEntradas lector;
    protected String loginActual;

    protected ConsolaBase(FachadaTorneos fachada, OperacionesComercio comercio, LectorEntradas lector) {
        this.fachada = fachada;
        this.comercio = comercio;
        this.lector = lector;
    }

    /** Punto de entrada que llama cada main. */
    public void iniciar() {
        fachada.cargarDatos();
        try {
            if (iniciarSesion()) {
                boolean seguir = true;
                while (seguir) {
                    lector.mostrar("");
                    mostrarMenu();
                    lector.mostrar("  0. Salir");
                    int opcion = lector.leerEntero("Seleccione una opción:", 0, cantidadOpciones());
                    if (opcion == 0) {
                        seguir = false;
                    } else {
                        seguir = ejecutarOpcion(opcion);
                    }
                }
            }
        } finally {
            fachada.guardarDatos();
            lector.mostrar("Datos guardados. Hasta pronto.");
        }
    }

    /** Pide usuario y contraseña. Solo deja entrar a los roles de esta consola. */
    protected boolean iniciarSesion() {
        lector.mostrar("=== " + titulo() + " ===");
        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
            String login = lector.leerTextoNoVacio("Usuario:");
            String password = lector.leerTextoNoVacio("Contraseña:");
            try {
                String rol = fachada.autenticar(login, password);
                if (rolesPermitidos().contains(rol)) {
                    loginActual = login;
                    lector.mostrar("Bienvenido(a), " + fachada.darNombreUsuario(login) + ".");
                    return true;
                }
                lector.mostrar("Su usuario (" + rol + ") no tiene acceso a esta aplicación.");
                return false;
            } catch (OperacionRechazadaException e) {
                lector.mostrar(e.getMessage() + " Intentos restantes: " + (MAX_INTENTOS - intento));
            }
        }
        lector.mostrar("Se superó el número de intentos.");
        return false;
    }

    /**
     * Ejecuta una acción y, si la lógica la rechaza, muestra el motivo
     * en vez de cerrar el programa.
     */
    protected void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (OperacionRechazadaException e) {
            lector.mostrar("Operación rechazada: " + e.getMessage());
        }
    }

    protected void mostrarLista(String titulo, List<String> lineas) {
        lector.mostrar("--- " + titulo + " ---");
        if (lineas == null || lineas.isEmpty()) {
            lector.mostrar("(no hay registros)");
            return;
        }
        for (String linea : lineas) {
            lector.mostrar(linea);
        }
    }

    // Opciones comunes a todas las consolas

    protected void consultarTorneos() {
        mostrarLista("Torneos", fachada.listarTorneos());
        if (lector.leerSiNo("¿Desea ver el detalle de alguno?")) {
            String id = lector.leerTextoNoVacio("Id del torneo:");
            ejecutar(() -> lector.mostrar(fachada.consultarDetalleTorneo(id)));
        }
    }

    protected abstract String titulo();

    protected abstract List<String> rolesPermitidos();

    /** Imprime las opciones del menú, numeradas desde 1. */
    protected abstract void mostrarMenu();

    protected abstract int cantidadOpciones();

    /** @return false si la opción cierra la aplicación. */
    protected abstract boolean ejecutarOpcion(int opcion);
}
