package fachada;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato entre la consola y la lógica de torneos.
 *
 * La consola solo valida el FORMATO de las entradas (que un número sea número,
 * que una fecha tenga formato AAAA-MM-DD, que una opción exista en el menú).
 * Las REGLAS del negocio (cupos, categoría, turnos, tarifas, estados) las valida
 * la implementación de esta interfaz. Si una regla no se cumple, el método lanza
 * OperacionRechazadaException con un mensaje claro para el usuario.
 *
 * Los métodos de consulta devuelven texto ya listo para imprimir, para que la
 * consola no dependa de las clases internas del dominio.
 */
public interface FachadaTorneos {

    // ------------------------------------------------------------
    // Sesión y cuentas (HU-T07 y login de los tres main)
    // ------------------------------------------------------------

    /**
     * Valida login y contraseña.
     * @return el rol del usuario: "ADMINISTRADOR", "SOCIO", "ESTUDIANTE",
     *         "EXTERNO", "ENTRENADOR" o "FISIOTERAPEUTA".
     * @throws OperacionRechazadaException si las credenciales no son válidas.
     */
    String autenticar(String login, String password);

    /** Nombre del usuario, para saludarlo en el menú. */
    String darNombreUsuario(String login);

    /**
     * HU-T07. Registra un estudiante o un externo.
     * @param tipoUsuario "ESTUDIANTE" o "EXTERNO".
     * @param nivel "PRINCIPIANTE", "INTERMEDIO" o "AVANZADO".
     * @param codigoEstudiantil 9 dígitos si es estudiante; null si es externo.
     */
    void registrarCuenta(String nombre, String documento, LocalDate fechaNacimiento,
            String nivel, String login, String password, String tipoUsuario,
            String codigoEstudiantil);

    // ------------------------------------------------------------
    // Administrador (HU-T01, T04, T08, T09)
    // ------------------------------------------------------------

    /**
     * HU-T01. Crea un torneo en estado "Inscripciones abiertas".
     * @param tipo "RECREATIVO" o "COMPETITIVO".
     * @param modalidad "CONJUNTO", "INDIVIDUAL" o "DOBLES".
     * @param edadMaxima null si la categoría es por nivel.
     * @param nivel null si la categoría es por edad.
     * @return el identificador asignado al torneo.
     */
    String crearTorneo(String loginAdmin, String nombre, String tipo, String modalidad,
            String disciplina, LocalDate fecha, Integer edadMaxima, String nivel,
            int cuposTotales, double tarifa);

    /** HU-T14. Pasa a regulares los cupos preferenciales que nadie ha ocupado. */
    void liberarCuposPreferenciales(String loginAdmin, String idTorneo);

    /** HU-T04 + HU-T13. Cambia a "En curso", libera preferenciales y genera el calendario. */
    void iniciarTorneo(String loginAdmin, String idTorneo);

    /** Encuentros del calendario que aún no tienen resultado. Una línea por encuentro. */
    List<String> listarEncuentrosPendientes(String idTorneo);

    /** HU-T08. Registra el marcador de un encuentro. */
    void registrarResultado(String loginAdmin, String idTorneo, String idEncuentro,
            int marcadorLocal, int marcadorVisitante);

    /** HU-T09 + HU-T10. Finaliza el torneo y reparte los premios. */
    void finalizarTorneo(String loginAdmin, String idTorneo);

    // ------------------------------------------------------------
    // Participantes: socio, estudiante, externo y empleado (HU-T02, T03, T05, T06)
    // ------------------------------------------------------------

    /**
     * HU-T02, T03 y T06. Inscripción individual. La lógica decide si el cupo es
     * preferencial o regular y si cobra o no (empleados no pagan).
     * @param confirmacionPago null en recreativos o si el usuario es empleado.
     */
    void inscribirIndividual(String login, String idTorneo, String confirmacionPago);

    /** Inscripción en dobles. Ocupa 2 cupos. */
    void inscribirPareja(String login, String loginCompanero, String idTorneo,
            String confirmacionPago);

    /** Inscripción de un equipo completo. El login que llama queda como capitán. */
    void inscribirEquipo(String loginCapitan, String idTorneo, String nombreEquipo,
            List<String> loginsIntegrantes, String confirmacionPago);

    /** HU-T05. Retira la inscripción (individual, pareja o equipo si es capitán). */
    void desinscribir(String login, String idTorneo);

    /** Torneos en los que el usuario está inscrito. Una línea por torneo. */
    List<String> listarMisTorneos(String login);

    // ------------------------------------------------------------
    // Bonos (HU-T11)
    // ------------------------------------------------------------

    /** Bonos disponibles del usuario. Una línea por bono, empezando por su id. */
    List<String> consultarBonos(String login);

    /**
     * Aplica un bono a una compra.
     * @param usaOtroDescuento true si la compra ya usa puntos o código de empleado.
     * @return el valor descontado.
     */
    double canjearBono(String login, String idBono, double subtotal, boolean usaOtroDescuento);

    // ------------------------------------------------------------
    // Consultas para cualquier usuario (HU-T12)
    // ------------------------------------------------------------

    /** Todos los torneos con su estado. Una línea por torneo, empezando por su id. */
    List<String> listarTorneos();

    /** Detalle completo del torneo (cupos, inscritos, encuentros, posiciones, premios). */
    String consultarDetalleTorneo(String idTorneo);

    // ------------------------------------------------------------
    // Persistencia (la implementa Roxana; la fachada solo la invoca)
    // ------------------------------------------------------------

    /** Carga los datos de la carpeta "datos/" al iniciar cualquiera de los main. */
    void cargarDatos();

    /** Guarda todo en los mismos archivos al cerrar el programa. */
    void guardarDatos();
}
