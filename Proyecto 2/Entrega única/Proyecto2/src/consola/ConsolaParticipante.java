package consola;

import java.util.ArrayList;
import java.util.List;

import fachada.FachadaTorneos;

/**
 * Consola para socios, estudiantes y externos: inscripción a torneos,
 * bonos y registro de cuenta nueva.
 */
public class ConsolaParticipante extends ConsolaBase {

    public ConsolaParticipante(FachadaTorneos fachada, LectorEntradas lector) {
        super(fachada, lector);
    }

    @Override
    protected String titulo() {
        return "Club - Socios, estudiantes y externos";
    }

    @Override
    protected List<String> rolesPermitidos() {
        return List.of("SOCIO", "ESTUDIANTE", "EXTERNO");
    }

    @Override
    protected boolean iniciarSesion() {
        if (lector.leerSiNo("¿Es la primera vez que entra y necesita crear una cuenta?")) {
            ejecutar(this::registrarCuenta);
        }
        return super.iniciarSesion();
    }

    @Override
    protected void mostrarMenu() {
        lector.mostrar("=== MENÚ PARTICIPANTE ===");
        lector.mostrar("  1. Inscribirme a un torneo");
        lector.mostrar("  2. Salirme de un torneo");
        lector.mostrar("  3. Ver mis torneos");
        lector.mostrar("  4. Ver mis bonos de descuento");
        lector.mostrar("  5. Consultar torneos");
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
            case 4 -> mostrarLista("Mis bonos", fachada.consultarBonos(loginActual));
            case 5 -> consultarTorneos();
            default -> lector.mostrar("Opción no válida.");
        }
        return true;
    }

    private void registrarCuenta() {
        String tipo = lector.leerOpcion("Tipo de usuario:", List.of("ESTUDIANTE", "EXTERNO"));
        String nombre = lector.leerTextoNoVacio("Nombre completo:");
        String documento = lector.leerTextoNoVacio("Documento de identidad:");
        var nacimiento = lector.leerFecha("Fecha de nacimiento");
        String nivel = lector.leerOpcion("Nivel técnico:", List.of("PRINCIPIANTE", "INTERMEDIO", "AVANZADO"));
        String codigo = null;
        if (tipo.equals("ESTUDIANTE")) {
            codigo = leerCodigoEstudiantil();
        }
        String login = lector.leerTextoNoVacio("Usuario:");
        String password = leerPasswordSegura();
        fachada.registrarCuenta(nombre, documento, nacimiento, nivel, login, password, tipo, codigo);
        lector.mostrar("Cuenta creada. Ya puede iniciar sesión.");
    }

    private String leerCodigoEstudiantil() {
        while (true) {
            String codigo = lector.leerTextoNoVacio("Código estudiantil (9 dígitos):");
            if (codigo.matches("\\d{9}")) {
                return codigo;
            }
            lector.mostrar("El código debe tener exactamente 9 dígitos.");
        }
    }

    private String leerPasswordSegura() {
        while (true) {
            String password = lector.leerTextoNoVacio("Contraseña (mín. 8 caracteres, con letras y números):");
            if (password.length() >= 8 && password.matches(".*[A-Za-z].*") && password.matches(".*\\d.*")) {
                return password;
            }
            lector.mostrar("La contraseña no cumple los requisitos.");
        }
    }

    private void inscribirse() {
        mostrarLista("Torneos", fachada.listarTorneos());
        String id = lector.leerTextoNoVacio("Id del torneo:");
        String forma = lector.leerOpcion("¿Cómo se inscribe?", List.of("Individual", "En pareja (dobles)", "Con un equipo"));
        String pago = null;
        if (lector.leerSiNo("¿El torneo es competitivo (requiere pago)?")) {
            pago = lector.leerTextoNoVacio("Código de confirmación del pago:");
        }
        switch (forma) {
            case "Individual" -> fachada.inscribirIndividual(loginActual, id, pago);
            case "En pareja (dobles)" -> {
                String companero = lector.leerTextoNoVacio("Usuario de su pareja:");
                fachada.inscribirPareja(loginActual, companero, id, pago);
            }
            default -> {
                String equipo = lector.leerTextoNoVacio("Nombre del equipo:");
                int cantidad = lector.leerEntero("¿Cuántos integrantes además de usted?", 1, 30);
                List<String> integrantes = new ArrayList<>();
                for (int i = 1; i <= cantidad; i++) {
                    integrantes.add(lector.leerTextoNoVacio("Usuario del integrante " + i + ":"));
                }
                fachada.inscribirEquipo(loginActual, id, equipo, integrantes, pago);
            }
        }
        lector.mostrar("Inscripción registrada.");
    }

    private void desinscribirse() {
        mostrarLista("Mis torneos", fachada.listarMisTorneos(loginActual));
        String id = lector.leerTextoNoVacio("Id del torneo del que se quiere salir:");
        if (lector.leerSiNo("¿Confirma que se quiere salir?")) {
            fachada.desinscribir(loginActual, id);
            lector.mostrar("Se liberó su cupo.");
        }
    }
}
