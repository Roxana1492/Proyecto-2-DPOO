package fachada;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import autenticacion.Administrador;
import autenticacion.Autenticador;
import autenticacion.Empleado;
import autenticacion.Entrenador;
import autenticacion.Estudiante;
import autenticacion.Externo;
import autenticacion.Fisioterapeuta;
import autenticacion.RegistroMatriculas;
import autenticacion.Socio;
import autenticacion.Usuario;
import comercio.RegistroTurnos;
import deportivo.BonoDescuento;
import deportivo.Categoria;
import deportivo.Encuentro;
import deportivo.EquipoTorneo;
import deportivo.EstadoTorneo;
import deportivo.GestorTorneos;
import deportivo.InscripcionTorneo;
import deportivo.Jugador;
import deportivo.Modalidad;
import deportivo.Nivel;
import deportivo.Pareja;
import deportivo.ParticipanteTorneo;
import deportivo.PosicionTorneo;
import deportivo.Premio;
import deportivo.Torneo;
import deportivo.TorneoCompetitivo;
import persistencia.PersistenciaClub;

/**
 * Implementación de FachadaTorneos. Recibe los datos de la consola como texto,
 * busca los objetos (usuarios y torneos) y le pasa el trabajo a GestorTorneos
 * y a Torneo. No imprime nada: si algo no se puede hacer, lanza
 * OperacionRechazadaException con el motivo.
 */
public class ClubFachada implements FachadaTorneos {

    private GestorTorneos gestorTorneos;
    private Autenticador autenticador;
    private RegistroMatriculas matriculas;
    private RegistroTurnos registroTurnos;
    private PersistenciaClub persistencia;
    private OperacionesComercio comercio;

    public ClubFachada(OperacionesComercio comercio, RegistroTurnos registroTurnos, PersistenciaClub persistencia) {
        this.comercio = comercio;
        this.registroTurnos = registroTurnos;
        this.persistencia = persistencia;
        this.gestorTorneos = new GestorTorneos(registroTurnos);
        this.autenticador = new Autenticador();
        this.matriculas = new RegistroMatriculas();
    }

    // ------------------------------------------------------------
    // Sesión y cuentas
    // ------------------------------------------------------------

    @Override
    public String autenticar(String login, String password) {
        Usuario usuario = autenticador.autenticar(login, password);
        if (usuario == null) {
            throw new OperacionRechazadaException("Usuario o contraseña incorrectos.");
        }
        if (usuario instanceof Administrador) return "ADMINISTRADOR";
        if (usuario instanceof Socio) return "SOCIO";
        if (usuario instanceof Estudiante) return "ESTUDIANTE";
        if (usuario instanceof Externo) return "EXTERNO";
        if (usuario instanceof Entrenador) return "ENTRENADOR";
        if (usuario instanceof Fisioterapeuta) return "FISIOTERAPEUTA";
        return "EMPLEADO";
    }

    @Override
    public String darNombreUsuario(String login) {
        return buscarUsuario(login).darNombre();
    }

    // HU-T07
    @Override
    public void registrarCuenta(String nombre, String documento, LocalDate fechaNacimiento, String nivel,
            String login, String password, String tipoUsuario, String codigoEstudiantil) {
        if (autenticador.existeLogin(login)) {
            throw new OperacionRechazadaException("El usuario " + login + " ya existe.");
        }
        if (autenticador.existeDocumento(documento)) {
            throw new OperacionRechazadaException("Ya hay una cuenta con ese documento.");
        }
        if (fechaNacimiento == null || !fechaNacimiento.isBefore(LocalDate.now())) {
            throw new OperacionRechazadaException("La fecha de nacimiento no es válida.");
        }
        Nivel n = Nivel.valueOf(nivel);
        if (tipoUsuario.equals("ESTUDIANTE")) {
            if (!matriculas.estaMatriculado(codigoEstudiantil)) {
                throw new OperacionRechazadaException("El código " + codigoEstudiantil + " no aparece en las matrículas de la universidad.");
            }
            autenticador.registrarUsuario(new Estudiante(login, password, nombre, documento, fechaNacimiento, n, codigoEstudiantil));
        } else if (tipoUsuario.equals("EXTERNO")) {
            autenticador.registrarUsuario(new Externo(login, password, nombre, documento, fechaNacimiento, n));
        } else {
            throw new OperacionRechazadaException("Solo se pueden registrar estudiantes o externos.");
        }
    }

    // ------------------------------------------------------------
    // Administrador
    // ------------------------------------------------------------

    @Override
    public String crearTorneo(String loginAdmin, String nombre, String tipo, String modalidad, String disciplina,
            LocalDate fecha, Integer edadMaxima, String nivel, int cuposTotales, double tarifa) {
        validarAdministrador(loginAdmin);
        Nivel n = (nivel == null) ? null : Nivel.valueOf(nivel);
        Torneo t = gestorTorneos.crearTorneo(nombre, tipo, Modalidad.valueOf(modalidad), disciplina, fecha,
                new Categoria(edadMaxima, n), cuposTotales, tarifa);
        return t.getId();
    }

    @Override
    public void liberarCuposPreferenciales(String loginAdmin, String idTorneo) {
        validarAdministrador(loginAdmin);
        gestorTorneos.buscar(idTorneo).liberarCuposPreferenciales();
    }

    @Override
    public void iniciarTorneo(String loginAdmin, String idTorneo) {
        validarAdministrador(loginAdmin);
        gestorTorneos.buscar(idTorneo).iniciar(LocalDate.now());
    }

    @Override
    public List<String> listarEncuentrosPendientes(String idTorneo) {
        List<String> lineas = new ArrayList<>();
        for (Encuentro e : gestorTorneos.buscar(idTorneo).darEncuentrosPendientes()) {
            lineas.add(e.getId() + " | Ronda " + e.getRonda() + " | " + e.getLocal().darNombre() + " vs "
                    + e.getVisitante().darNombre());
        }
        return lineas;
    }

    @Override
    public void registrarResultado(String loginAdmin, String idTorneo, String idEncuentro, int marcadorLocal,
            int marcadorVisitante) {
        validarAdministrador(loginAdmin);
        gestorTorneos.buscar(idTorneo).registrarResultado(idEncuentro, marcadorLocal, marcadorVisitante);
    }

    @Override
    public void finalizarTorneo(String loginAdmin, String idTorneo) {
        validarAdministrador(loginAdmin);
        gestorTorneos.buscar(idTorneo).finalizar();
    }

    // ------------------------------------------------------------
    // Inscripciones (HU-T02, T03, T05, T06)
    // ------------------------------------------------------------

    @Override
    public void inscribirIndividual(String login, String idTorneo, String confirmacionPago) {
        Torneo t = gestorTorneos.buscar(idTorneo);
        validarModalidad(t, Modalidad.INDIVIDUAL);
        inscribir(t, new Jugador(buscarParticipante(login)), confirmacionPago);
    }

    @Override
    public void inscribirPareja(String login, String loginCompanero, String idTorneo, String confirmacionPago) {
        Torneo t = gestorTorneos.buscar(idTorneo);
        validarModalidad(t, Modalidad.DOBLES);
        if (login.equals(loginCompanero)) {
            throw new OperacionRechazadaException("Su pareja tiene que ser otra persona.");
        }
        inscribir(t, new Pareja(buscarParticipante(login), buscarParticipante(loginCompanero)), confirmacionPago);
    }

    @Override
    public void inscribirEquipo(String loginCapitan, String idTorneo, String nombreEquipo,
            List<String> loginsIntegrantes, String confirmacionPago) {
        Torneo t = gestorTorneos.buscar(idTorneo);
        validarModalidad(t, Modalidad.CONJUNTO);
        Socio capitan = buscarSocio(loginCapitan);
        List<Socio> integrantes = new ArrayList<>();
        for (String login : loginsIntegrantes) {
            if (login.equals(loginCapitan)) {
                throw new OperacionRechazadaException("El capitán ya hace parte del equipo, no lo repita.");
            }
            Socio s = buscarSocio(login);
            if (integrantes.contains(s)) {
                throw new OperacionRechazadaException("El integrante " + login + " está repetido.");
            }
            integrantes.add(s);
        }
        inscribir(t, new EquipoTorneo(nombreEquipo, capitan, integrantes), confirmacionPago);
    }

    private void inscribir(Torneo t, ParticipanteTorneo p, String confirmacionPago) {
        // Los empleados no pueden jugar si tienen turno ese día
        for (Usuario u : p.darIntegrantes()) {
            if (u instanceof Empleado && !gestorTorneos.empleadoLibre((Empleado) u, t.getFecha())) {
                throw new OperacionRechazadaException(u.darNombre() + " tiene turno el día del torneo.");
            }
        }
        double cobro = t.calcularCobro(p);
        boolean pago = confirmacionPago != null && !confirmacionPago.trim().isEmpty();
        if (cobro > 0 && !pago) {
            throw new OperacionRechazadaException("El torneo cobra $" + String.format("%.0f", cobro)
                    + ": ingrese la confirmación del pago.");
        }
        boolean preferencial = gestorTorneos.esCupoPreferencial(p, t);
        t.inscribir(new InscripcionTorneo(p, preferencial, cobro, pago ? confirmacionPago : null, 0));
    }

    @Override
    public void desinscribir(String login, String idTorneo) {
        gestorTorneos.buscar(idTorneo).desinscribir(login);
    }

    @Override
    public List<String> listarMisTorneos(String login) {
        List<String> lineas = new ArrayList<>();
        for (Torneo t : gestorTorneos.listar()) {
            InscripcionTorneo i = t.buscarInscripcion(login);
            if (i != null) {
                lineas.add(t.getId() + " | " + t.getNombre() + " | " + textoEstado(t.getEstado())
                        + " | inscrito como: " + i.getParticipante().darNombre()
                        + (i.isPreferencial() ? " (cupo preferencial)" : ""));
            }
        }
        return lineas;
    }

    // ------------------------------------------------------------
    // Bonos (HU-T11)
    // ------------------------------------------------------------

    @Override
    public List<String> consultarBonos(String login) {
        List<String> lineas = new ArrayList<>();
        for (BonoDescuento b : buscarUsuario(login).darBonosDisponibles()) {
            lineas.add(b.getId() + " | " + String.format("%.0f", b.getPorcentaje()) + "% de descuento");
        }
        return lineas;
    }

    @Override
    public double canjearBono(String login, String idBono, double subtotal, boolean usaOtroDescuento) {
        for (BonoDescuento b : buscarUsuario(login).darBonos()) {
            if (b.getId().equals(idBono)) {
                return b.canjear(login, subtotal, usaOtroDescuento);
            }
        }
        throw new OperacionRechazadaException("No tiene un bono con id " + idBono + ".");
    }

    // ------------------------------------------------------------
    // Consultas (HU-T12)
    // ------------------------------------------------------------

    @Override
    public List<String> listarTorneos() {
        List<String> lineas = new ArrayList<>();
        for (Torneo t : gestorTorneos.listar()) {
            String tipo = t instanceof TorneoCompetitivo ? "Competitivo" : "Recreativo";
            lineas.add(t.getId() + " | " + t.getNombre() + " | " + t.getDisciplina() + " | " + tipo + " | "
                    + t.getModalidad() + " | " + t.getFecha() + " | " + textoEstado(t.getEstado()));
        }
        return lineas;
    }

    @Override
    public String consultarDetalleTorneo(String idTorneo) {
        Torneo t = gestorTorneos.buscar(idTorneo);
        StringBuilder sb = new StringBuilder();
        sb.append("Torneo ").append(t.getId()).append(": ").append(t.getNombre()).append("\n");
        sb.append("Deporte: ").append(t.getDisciplina()).append(" | ").append(t.getModalidad())
                .append(" | Categoría: ").append(t.getCategoria().describir()).append("\n");
        sb.append("Fecha: ").append(t.getFecha()).append(" | Estado: ").append(textoEstado(t.getEstado())).append("\n");
        if (t instanceof TorneoCompetitivo) {
            TorneoCompetitivo c = (TorneoCompetitivo) t;
            sb.append("Tarifa: $").append(String.format("%.0f", c.getTarifa())).append(" | Fondo de premios: $")
                    .append(String.format("%.0f", c.getFondoPremios())).append("\n");
        } else {
            sb.append("Sin tarifa. Premio: bono del 20% para el ganador\n");
        }
        if (t.getEstado() == EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            sb.append("Cupos libres: ").append(t.cuposRegularesLibres()).append(" regulares y ")
                    .append(t.cuposPreferencialesLibres()).append(" preferenciales (de ").append(t.getCuposTotales())
                    .append(")\n");
        }

        sb.append("Inscritos:\n");
        if (t.getInscripciones().isEmpty()) {
            sb.append("  (ninguno)\n");
        }
        for (InscripcionTorneo i : t.getInscripciones()) {
            sb.append("  - ").append(i.getParticipante().darNombre()).append("\n");
        }

        if (!t.getEncuentros().isEmpty()) {
            sb.append("Encuentros:\n");
            for (Encuentro e : t.getEncuentros()) {
                sb.append("  Ronda ").append(e.getRonda()).append(" ").append(e.getId()).append(": ");
                if (e.esDescanso()) {
                    sb.append(e.getLocal().darNombre()).append(" descansa\n");
                    continue;
                }
                sb.append(e.getLocal().darNombre()).append(" vs ").append(e.getVisitante().darNombre());
                sb.append(e.isJugado() ? "  " + e.getMarcadorLocal() + "-" + e.getMarcadorVisitante() : "  (pendiente)");
                sb.append("\n");
            }
            sb.append("Posiciones (puntos | a favor | en contra):\n");
            int puesto = 1;
            for (PosicionTorneo p : t.darPosiciones()) {
                sb.append("  ").append(puesto).append(". ").append(p.getParticipante().darNombre()).append(" | ")
                        .append(p.getPuntos()).append(" | ").append(p.getMarcadorFavor()).append(" | ")
                        .append(p.getMarcadorContra()).append("\n");
                puesto++;
            }
        }

        if (t.isPremiosEntregados()) {
            sb.append("Premios:\n");
            if (t.getPremios().isEmpty()) {
                sb.append("  No había fondo para repartir (todos los inscritos eran empleados).\n");
            }
            for (Premio premio : t.getPremios()) {
                sb.append("  ").append(premio.describir()).append("\n");
            }
        }
        return sb.toString();
    }

    // ------------------------------------------------------------
    // Persistencia
    // ------------------------------------------------------------

    @Override
    public void cargarDatos() {
        try {
            persistencia.cargarUsuarios(autenticador);
            matriculas = persistencia.cargarMatriculas();
            persistencia.cargarComercio(comercio, autenticador);
            persistencia.cargarTorneos(gestorTorneos, autenticador);
            persistencia.cargarBonos(autenticador);
        } catch (Exception e) {
            System.out.println("Error cargando los datos: " + e.getMessage());
        }
    }

    @Override
    public void guardarDatos() {
        try {
            persistencia.guardarUsuarios(autenticador);
            persistencia.guardarComercio(comercio);
            persistencia.guardarTorneos(gestorTorneos);
            persistencia.guardarBonos(autenticador);
        } catch (Exception e) {
            System.out.println("Error guardando los datos: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // Para las pruebas
    // ------------------------------------------------------------

    public Autenticador getAutenticador() { return autenticador; }
    public GestorTorneos getGestorTorneos() { return gestorTorneos; }
    public RegistroMatriculas getMatriculas() { return matriculas; }

    // ------------------------------------------------------------
    // Métodos de apoyo
    // ------------------------------------------------------------

    private Usuario buscarUsuario(String login) {
        Usuario u = autenticador.buscar(login);
        if (u == null) {
            throw new OperacionRechazadaException("No existe el usuario " + login + ".");
        }
        return u;
    }

    private Usuario buscarParticipante(String login) {
        Usuario u = buscarUsuario(login);
        if (u instanceof Administrador) {
            throw new OperacionRechazadaException("El administrador no puede inscribirse a torneos.");
        }
        return u;
    }

    private Socio buscarSocio(String login) {
        Usuario u = buscarUsuario(login);
        if (!(u instanceof Socio)) {
            throw new OperacionRechazadaException(login + " no es socio: los equipos son solo de socios.");
        }
        return (Socio) u;
    }

    private void validarAdministrador(String login) {
        if (!(autenticador.buscar(login) instanceof Administrador)) {
            throw new OperacionRechazadaException("Solo el administrador puede hacer esta operación.");
        }
    }

    private void validarModalidad(Torneo t, Modalidad modalidad) {
        if (t.getModalidad() != modalidad) {
            throw new OperacionRechazadaException("Este torneo es de modalidad " + t.getModalidad() + ".");
        }
    }

    private String textoEstado(EstadoTorneo estado) {
        if (estado == EstadoTorneo.INSCRIPCIONES_ABIERTAS) return "Inscripciones abiertas";
        if (estado == EstadoTorneo.EN_CURSO) return "En curso";
        return "Finalizado";
    }
}
