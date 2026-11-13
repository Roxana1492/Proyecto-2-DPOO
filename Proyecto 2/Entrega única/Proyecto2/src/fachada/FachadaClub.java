package fachada;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import autenticacion.Administrador;
import autenticacion.Autenticador;
import autenticacion.Empleado;
import autenticacion.Entrenador;
import autenticacion.Estudiante;
import autenticacion.Externo;
import autenticacion.Fisioterapeuta;
import autenticacion.Socio;
import autenticacion.Usuario;
import comercio.BonoDescuento;
import deportivo.Encuentro;
import deportivo.EquipoTorneo;
import deportivo.EstadoTorneo;
import deportivo.GestorTorneos;
import deportivo.ModalidadTorneo;
import deportivo.TipoTorneo;
import deportivo.Torneo;
import persistencia.PersistenciaClub;
import persistencia.PersistenciaComercio;
import persistencia.PersistenciaTorneos;

/**
 * Implementación de la fachada que usan las tres consolas.
 * Recibe los datos como texto, busca los objetos del dominio (usuarios y
 * torneos) y le pide a Torneo que haga el trabajo. Si algo no se puede hacer
 * lanza OperacionRechazadaException con el motivo.
 */
public class FachadaClub implements FachadaTorneos {

    private static final double PORCENTAJE_BONO = 20;
    private static final double[] REPARTO_EFECTIVO = { 0.60, 0.30, 0.10 };

    private final String carpetaDatos;
    private final OperacionesComercio comercio;

    private Autenticador autenticador;
    private Map<String, Usuario> usuarios;
    private GestorTorneos gestor;
    private List<BonoDescuento> bonos;
    private Map<String, List<String>> premios;
    private int siguienteTorneo;

    public FachadaClub(String carpetaDatos, OperacionesComercio comercio) {
        this.carpetaDatos = carpetaDatos;
        this.comercio = comercio;
        this.autenticador = new Autenticador();
        this.usuarios = new LinkedHashMap<>();
        this.gestor = new GestorTorneos();
        this.bonos = new ArrayList<>();
        this.premios = new LinkedHashMap<>();
        this.siguienteTorneo = 1;
    }

    // ------------------------------------------------------------
    // Sesión y cuentas
    // ------------------------------------------------------------

    public void agregarUsuario(Usuario usuario) {
        usuarios.put(usuario.getLogin(), usuario);
        autenticador.registrarUsuario(usuario);
    }

    @Override
    public String autenticar(String login, String password) {
        Usuario usuario = autenticador.autenticar(login, password);
        if (usuario == null) {
            throw new OperacionRechazadaException("Usuario o contraseña incorrectos.");
        }
        return darRol(usuario);
    }

    @Override
    public String darNombreUsuario(String login) {
        return nombreDe(login);
    }

    @Override
    public void registrarCuenta(String nombre, String documento, LocalDate fechaNacimiento, String nivel,
            String login, String password, String tipoUsuario, String codigoEstudiantil) {
        if (usuarios.containsKey(login)) {
            throw new OperacionRechazadaException("El usuario " + login + " ya existe.");
        }
        if (fechaNacimiento == null || !fechaNacimiento.isBefore(LocalDate.now())) {
            throw new OperacionRechazadaException("La fecha de nacimiento no es válida.");
        }
        for (Usuario u : usuarios.values()) {
            if (documento.equals(documentoDe(u))) {
                throw new OperacionRechazadaException("Ya hay una cuenta con ese documento.");
            }
        }
        if (tipoUsuario.equals("ESTUDIANTE")) {
            if (codigoEstudiantil == null || !codigoEstudiantil.matches("\\d{9}")) {
                throw new OperacionRechazadaException("El código estudiantil debe tener 9 dígitos.");
            }
            agregarUsuario(new Estudiante(login, password, nombre, documento, fechaNacimiento, nivel, codigoEstudiantil));
        } else if (tipoUsuario.equals("EXTERNO")) {
            agregarUsuario(new Externo(login, password, nombre, documento, fechaNacimiento, nivel));
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
        String id = "T" + siguienteTorneo;
        int edad = (edadMaxima == null) ? 0 : edadMaxima;
        gestor.crearTorneo(id, nombre, TipoTorneo.valueOf(tipo), ModalidadTorneo.valueOf(modalidad), disciplina,
                fecha, edad, nivel, cuposTotales, tarifa);
        siguienteTorneo++;
        return id;
    }

    @Override
    public void iniciarTorneo(String loginAdmin, String idTorneo) {
        validarAdministrador(loginAdmin);
        Torneo t = gestor.buscarTorneo(idTorneo);
        if (t.getEstado() != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            throw new OperacionRechazadaException("El torneo ya fue iniciado.");
        }
        if (LocalDate.now().isBefore(t.getFechaInicio())) {
            throw new OperacionRechazadaException("El torneo empieza el " + t.getFechaInicio() + ", todavía no se puede iniciar.");
        }
        if (t.getInscritos().size() < 2) {
            throw new OperacionRechazadaException("Se necesitan al menos 2 inscritos para iniciar.");
        }
        t.iniciarTorneo(LocalDate.now());
    }

    @Override
    public List<String> listarEncuentrosPendientes(String idTorneo) {
        Torneo t = gestor.buscarTorneo(idTorneo);
        List<String> lineas = new ArrayList<>();
        for (Encuentro e : t.consultarEncuentrosPendientes()) {
            lineas.add(e.getIdEncuentro() + " | Ronda " + e.getRonda() + " | "
                    + nombreParticipante(e.getParticipante1()) + " vs " + nombreParticipante(e.getParticipante2()));
        }
        return lineas;
    }

    @Override
    public void registrarResultado(String loginAdmin, String idTorneo, String idEncuentro, int marcadorLocal,
            int marcadorVisitante) {
        validarAdministrador(loginAdmin);
        Torneo t = gestor.buscarTorneo(idTorneo);
        if (t.getEstado() != EstadoTorneo.EN_CURSO) {
            throw new OperacionRechazadaException("El torneo no está en curso.");
        }
        if (marcadorLocal == marcadorVisitante && t.getModalidad() != ModalidadTorneo.CONJUNTO) {
            throw new OperacionRechazadaException("En individual y dobles no puede haber empate.");
        }
        if (!t.registrarResultadoEncuentro(idEncuentro, marcadorLocal, marcadorVisitante)) {
            throw new OperacionRechazadaException("No se encontró el encuentro o ya tiene resultado.");
        }
    }

    @Override
    public void finalizarTorneo(String loginAdmin, String idTorneo) {
        validarAdministrador(loginAdmin);
        Torneo t = gestor.buscarTorneo(idTorneo);
        if (t.getEstado() == EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            throw new OperacionRechazadaException("El torneo todavía no ha empezado.");
        }
        if (!t.consultarEncuentrosPendientes().isEmpty()) {
            throw new OperacionRechazadaException("Faltan resultados por ingresar.");
        }
        if (premios.containsKey(idTorneo)) {
            throw new OperacionRechazadaException("Los premios de este torneo ya fueron entregados.");
        }
        t.setEstado(EstadoTorneo.FINALIZADO);
        repartirPremios(t);
    }

    // HU-T10: recreativo = bono del 20% para el ganador; competitivo = fondo 60/30/10
    private void repartirPremios(Torneo t) {
        List<Object> posiciones = calcularPosiciones(t);
        List<String> lista = new ArrayList<>();

        if (t.getTipo() == TipoTorneo.RECREATIVO) {
            Object ganador = posiciones.get(0);
            for (String login : loginsDe(ganador)) {
                String idBono = "B" + (bonos.size() + 1);
                bonos.add(new BonoDescuento(idBono, login, PORCENTAJE_BONO));
                lista.add("Bono " + idBono + " del 20% para " + nombreDe(login));
            }
        } else {
            double fondo = t.getFondoPremios();
            if (fondo == 0) {
                lista.add("No hay fondo para repartir (todos los inscritos eran empleados).");
            } else {
                // Los empleados (o parejas con un empleado) no reciben efectivo: suben los siguientes
                int puesto = 0;
                for (Object p : posiciones) {
                    if (puesto < REPARTO_EFECTIVO.length && !tieneEmpleado(p)) {
                        double monto = fondo * REPARTO_EFECTIVO[puesto];
                        String quien = nombreParticipante(p);
                        if (p instanceof EquipoTorneo) {
                            quien = quien + " (se entrega al capitán " + nombreDe(((EquipoTorneo) p).getLoginCapitan()) + ")";
                        }
                        lista.add((puesto + 1) + ". puesto: $" + String.format("%.0f", monto) + " para " + quien);
                        puesto++;
                    }
                }
                if (puesto < REPARTO_EFECTIVO.length) {
                    lista.add("Lo que no se asignó queda en el fondo general del club.");
                }
            }
        }
        premios.put(t.getIdTorneo(), lista);
    }

    // HU-T09: puntos, diferencia de marcador, marcador a favor y orden de inscripción
    private List<Object> calcularPosiciones(Torneo t) {
        List<Object> participantes = new ArrayList<>(t.getInscritos());
        Map<Object, int[]> tabla = calcularTabla(t);
        // sort es estable, entonces si todo es igual se respeta el orden de inscripción
        participantes.sort((a, b) -> {
            int[] x = tabla.get(a);
            int[] y = tabla.get(b);
            if (x[0] != y[0]) {
                return y[0] - x[0];
            }
            int difX = x[1] - x[2];
            int difY = y[1] - y[2];
            if (difX != difY) {
                return difY - difX;
            }
            return y[1] - x[1];
        });
        return participantes;
    }

    // Para cada participante: {puntos, marcador a favor, marcador en contra}
    private Map<Object, int[]> calcularTabla(Torneo t) {
        Map<Object, int[]> tabla = new LinkedHashMap<>();
        for (Object p : t.getInscritos()) {
            tabla.put(p, new int[3]);
        }
        for (Encuentro e : t.getCalendario()) {
            if (!e.isJugado()) {
                continue;
            }
            int[] uno = tabla.get(e.getParticipante1());
            int[] dos = tabla.get(e.getParticipante2());
            uno[1] += e.getMarcador1();
            uno[2] += e.getMarcador2();
            dos[1] += e.getMarcador2();
            dos[2] += e.getMarcador1();
            Object ganador = e.getGanador();
            if (ganador == null) {
                uno[0] += 1;
                dos[0] += 1;
            } else if (ganador == e.getParticipante1()) {
                uno[0] += 3;
            } else {
                dos[0] += 3;
            }
        }
        return tabla;
    }

    // ------------------------------------------------------------
    // Inscripciones
    // ------------------------------------------------------------

    @Override
    public void inscribirIndividual(String login, String idTorneo, String confirmacionPago) {
        Torneo t = gestor.buscarTorneo(idTorneo);
        validarModalidad(t, ModalidadTorneo.INDIVIDUAL);
        inscribir(t, login, login, confirmacionPago, null);
    }

    @Override
    public void inscribirPareja(String login, String loginCompanero, String idTorneo, String confirmacionPago) {
        Torneo t = gestor.buscarTorneo(idTorneo);
        validarModalidad(t, ModalidadTorneo.DOBLES);
        if (login.equals(loginCompanero)) {
            throw new OperacionRechazadaException("Su pareja tiene que ser otra persona.");
        }
        validarParticipante(loginCompanero);
        validarNoInscrito(t, loginCompanero);
        List<String> pareja = new ArrayList<>();
        pareja.add(login);
        pareja.add(loginCompanero);
        inscribir(t, login, pareja, confirmacionPago, null);
    }

    @Override
    public void inscribirEquipo(String loginCapitan, String idTorneo, String nombreEquipo,
            List<String> loginsIntegrantes, String confirmacionPago) {
        Torneo t = gestor.buscarTorneo(idTorneo);
        validarModalidad(t, ModalidadTorneo.CONJUNTO);
        // Los equipos son solo de socios
        for (String login : loginsIntegrantes) {
            if (!(usuarios.get(login) instanceof Socio)) {
                throw new OperacionRechazadaException("El integrante " + login + " no es socio del club.");
            }
            if (login.equals(loginCapitan)) {
                throw new OperacionRechazadaException("El capitán ya hace parte del equipo, no lo repita.");
            }
            validarNoInscrito(t, login);
        }
        if (!(usuarios.get(loginCapitan) instanceof Socio)) {
            throw new OperacionRechazadaException("Solo los socios pueden inscribir equipos.");
        }
        EquipoTorneo equipo = new EquipoTorneo(nombreEquipo, loginCapitan, loginsIntegrantes);
        inscribir(t, loginCapitan, equipo, confirmacionPago, loginCapitan);
    }

    // Validaciones comunes y llamada a Torneo
    private void inscribir(Torneo t, String login, Object participante, String confirmacionPago, String capitan) {
        Usuario usuario = validarParticipante(login);
        validarNoInscrito(t, login);
        if (t.getEstado() != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            throw new OperacionRechazadaException("El torneo no tiene inscripciones abiertas.");
        }

        // No paga si todos son empleados (en dobles con un socio, la pareja sí paga)
        boolean esEmpleado = true;
        for (String l : loginsDe(participante)) {
            if (!(usuarios.get(l) instanceof Empleado)) {
                esEmpleado = false;
            }
        }
        boolean pagoConfirmado = confirmacionPago != null && !confirmacionPago.trim().isEmpty();
        if (t.getTipo() == TipoTorneo.COMPETITIVO && !esEmpleado && !pagoConfirmado) {
            throw new OperacionRechazadaException("El torneo es competitivo: debe ingresar la confirmación del pago.");
        }

        int edad = calcularEdad(usuario);
        if (t.getEdadMaxima() != null && edad > t.getEdadMaxima()) {
            throw new OperacionRechazadaException("Supera la edad máxima del torneo (" + t.getEdadMaxima() + " años).");
        }
        String nivel = nivelDe(usuario);
        if (nivel == null) {
            nivel = t.getNivelRequerido(); // socios y empleados no tienen nivel registrado
        }
        if (t.getNivelRequerido() != null && !t.getNivelRequerido().equalsIgnoreCase(nivel)) {
            throw new OperacionRechazadaException("El torneo es solo para nivel " + t.getNivelRequerido() + ".");
        }

        // Cupo preferencial: se pide para quien ya ganó encuentros en otros torneos del mismo deporte
        int partidos = partidosJugados(login, t.getDisciplina());
        int victorias = victorias(login, t.getDisciplina());
        boolean pidePreferencial = victorias > 0;

        boolean ok = t.procesarInscripcion(participante, pagoConfirmado, esEmpleado, edad, nivel,
                pidePreferencial, victorias, partidos, 1, capitan);
        if (!ok) {
            throw new OperacionRechazadaException("No hay cupos disponibles en este torneo.");
        }
    }

    @Override
    public void desinscribir(String login, String idTorneo) {
        Torneo t = gestor.buscarTorneo(idTorneo);
        Object participante = buscarParticipante(t, login);
        if (participante == null) {
            throw new OperacionRechazadaException("Usted no está inscrito en este torneo.");
        }
        if (participante instanceof EquipoTorneo && !((EquipoTorneo) participante).getLoginCapitan().equals(login)) {
            throw new OperacionRechazadaException("Solo el capitán puede retirar al equipo.");
        }
        if (!t.desinscribirParticipante(participante)) {
            throw new OperacionRechazadaException("Ya no se puede retirar: el torneo empezó.");
        }
    }

    @Override
    public List<String> listarMisTorneos(String login) {
        List<String> lineas = new ArrayList<>();
        for (Torneo t : gestor.getTorneos()) {
            Object p = buscarParticipante(t, login);
            if (p != null) {
                lineas.add(t.getIdTorneo() + " | " + t.getNombre() + " | " + textoEstado(t.getEstado())
                        + " | inscrito como: " + nombreParticipante(p));
            }
        }
        return lineas;
    }

    // ------------------------------------------------------------
    // Bonos
    // ------------------------------------------------------------

    @Override
    public List<String> consultarBonos(String login) {
        List<String> lineas = new ArrayList<>();
        for (BonoDescuento b : bonos) {
            if (b.getLoginSocio().equals(login) && !b.isUsado()) {
                lineas.add(b.getIdBono() + " | " + String.format("%.0f", b.getPorcentajeDescuento()) + "% de descuento");
            }
        }
        return lineas;
    }

    @Override
    public double canjearBono(String login, String idBono, double subtotal, boolean usaOtroDescuento) {
        if (usaOtroDescuento) {
            throw new OperacionRechazadaException("El bono no se puede combinar con puntos ni códigos de empleado.");
        }
        for (BonoDescuento b : bonos) {
            if (b.getIdBono().equals(idBono) && b.getLoginSocio().equals(login)) {
                if (b.isUsado()) {
                    throw new OperacionRechazadaException("Ese bono ya fue usado.");
                }
                b.marcarUsado();
                return subtotal * b.getPorcentajeDescuento() / 100;
            }
        }
        throw new OperacionRechazadaException("No tiene un bono con id " + idBono + ".");
    }

    // ------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------

    @Override
    public List<String> listarTorneos() {
        List<String> lineas = new ArrayList<>();
        for (Torneo t : gestor.getTorneos()) {
            lineas.add(t.getIdTorneo() + " | " + t.getNombre() + " | " + t.getDisciplina() + " | " + t.getTipo()
                    + " | " + t.getModalidad() + " | " + t.getFechaInicio() + " | " + textoEstado(t.getEstado()));
        }
        return lineas;
    }

    @Override
    public String consultarDetalleTorneo(String idTorneo) {
        Torneo t = gestor.buscarTorneo(idTorneo);
        StringBuilder sb = new StringBuilder();
        sb.append("Torneo ").append(t.getIdTorneo()).append(": ").append(t.getNombre()).append("\n");
        sb.append("Deporte: ").append(t.getDisciplina()).append(" | ").append(t.getTipo()).append(" | ")
                .append(t.getModalidad()).append("\n");
        sb.append("Fecha: ").append(t.getFechaInicio()).append(" | Estado: ").append(textoEstado(t.getEstado())).append("\n");
        if (t.getEdadMaxima() != null) {
            sb.append("Categoría: hasta ").append(t.getEdadMaxima()).append(" años\n");
        }
        if (t.getNivelRequerido() != null) {
            sb.append("Categoría: nivel ").append(t.getNivelRequerido()).append("\n");
        }
        if (t.getTipo() == TipoTorneo.COMPETITIVO) {
            sb.append("Tarifa: $").append(String.format("%.0f", t.getTarifaInscripcion()))
                    .append(" | Fondo de premios: $").append(String.format("%.0f", t.getFondoPremios())).append("\n");
        }
        if (t.getEstado() == EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            sb.append("Cupos libres: ").append(t.getCuposRegularesDisponibles()).append(" regulares y ")
                    .append(t.getCuposPreferencialesDisponibles()).append(" preferenciales (de ")
                    .append(t.getCuposTotales()).append(")\n");
        }

        sb.append("Inscritos:\n");
        if (t.getInscritos().isEmpty()) {
            sb.append("  (ninguno)\n");
        }
        for (Object p : t.getInscritos()) {
            sb.append("  - ").append(nombreParticipante(p)).append("\n");
        }

        if (!t.getCalendario().isEmpty()) {
            sb.append("Encuentros:\n");
            for (Encuentro e : t.getCalendario()) {
                sb.append("  ").append(e.getIdEncuentro()).append(": ").append(nombreParticipante(e.getParticipante1()))
                        .append(" vs ").append(nombreParticipante(e.getParticipante2()));
                if (e.isJugado()) {
                    sb.append("  ").append(e.getMarcador1()).append("-").append(e.getMarcador2());
                } else {
                    sb.append("  (pendiente)");
                }
                sb.append("\n");
            }
            sb.append("Posiciones (puntos | a favor | en contra):\n");
            Map<Object, int[]> tabla = calcularTabla(t);
            int puesto = 1;
            for (Object p : calcularPosiciones(t)) {
                int[] fila = tabla.get(p);
                sb.append("  ").append(puesto).append(". ").append(nombreParticipante(p)).append(" | ")
                        .append(fila[0]).append(" | ").append(fila[1]).append(" | ").append(fila[2]).append("\n");
                puesto++;
            }
        }

        List<String> entregados = premios.get(t.getIdTorneo());
        if (entregados != null) {
            sb.append("Premios:\n");
            for (String premio : entregados) {
                sb.append("  ").append(premio).append("\n");
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
            PersistenciaClub persistenciaClub = new PersistenciaClub(carpetaDatos);
            List<Usuario> lista = persistenciaClub.cargarUsuarios();
            for (Usuario u : lista) {
                agregarUsuario(u);
            }
            new PersistenciaComercio(carpetaDatos).cargar(comercio, lista);

            PersistenciaTorneos persistenciaTorneos = new PersistenciaTorneos(carpetaDatos);
            for (Torneo t : persistenciaTorneos.cargarTorneos()) {
                gestor.agregarTorneo(t);
                // El siguiente id tiene que ser mayor que los que ya existen
                int numero = Integer.parseInt(t.getIdTorneo().substring(1));
                if (numero >= siguienteTorneo) {
                    siguienteTorneo = numero + 1;
                }
            }
            bonos = persistenciaTorneos.cargarBonos();
            premios = persistenciaTorneos.cargarPremios();
        } catch (Exception e) {
            System.out.println("Error cargando los datos: " + e.getMessage());
        }
    }

    @Override
    public void guardarDatos() {
        try {
            new PersistenciaClub(carpetaDatos).guardarUsuarios(usuarios.values());
            new PersistenciaComercio(carpetaDatos).guardar(comercio);
            PersistenciaTorneos persistenciaTorneos = new PersistenciaTorneos(carpetaDatos);
            persistenciaTorneos.guardarTorneos(gestor.getTorneos());
            persistenciaTorneos.guardarBonos(bonos);
            persistenciaTorneos.guardarPremios(premios);
        } catch (Exception e) {
            System.out.println("Error guardando los datos: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // Métodos de apoyo
    // ------------------------------------------------------------

    private void validarAdministrador(String login) {
        if (!(usuarios.get(login) instanceof Administrador)) {
            throw new OperacionRechazadaException("Solo el administrador puede hacer esta operación.");
        }
    }

    private Usuario validarParticipante(String login) {
        Usuario u = usuarios.get(login);
        if (u == null) {
            throw new OperacionRechazadaException("No existe el usuario " + login + ".");
        }
        if (u instanceof Administrador) {
            throw new OperacionRechazadaException("El administrador no puede inscribirse a torneos.");
        }
        return u;
    }

    private void validarNoInscrito(Torneo t, String login) {
        if (buscarParticipante(t, login) != null) {
            throw new OperacionRechazadaException(nombreDe(login) + " ya está inscrito(a) en este torneo.");
        }
    }

    private void validarModalidad(Torneo t, ModalidadTorneo modalidad) {
        if (t.getModalidad() != modalidad) {
            throw new OperacionRechazadaException("Este torneo es de modalidad " + t.getModalidad() + ".");
        }
    }

    // Busca si el login está en el torneo, solo, en pareja o en un equipo
    private Object buscarParticipante(Torneo t, String login) {
        for (Object p : t.getInscritos()) {
            if (loginsDe(p).contains(login)) {
                return p;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<String> loginsDe(Object participante) {
        List<String> logins = new ArrayList<>();
        if (participante instanceof EquipoTorneo) {
            logins.addAll(((EquipoTorneo) participante).todosLosLogins());
        } else if (participante instanceof List) {
            logins.addAll((List<String>) participante);
        } else if (participante != null) {
            logins.add(participante.toString());
        }
        return logins;
    }

    private boolean tieneEmpleado(Object participante) {
        for (String login : loginsDe(participante)) {
            if (usuarios.get(login) instanceof Empleado) {
                return true;
            }
        }
        return false;
    }

    private String nombreParticipante(Object participante) {
        if (participante instanceof EquipoTorneo) {
            return "Equipo " + ((EquipoTorneo) participante).getNombre();
        }
        List<String> nombres = new ArrayList<>();
        for (String login : loginsDe(participante)) {
            nombres.add(nombreDe(login));
        }
        return String.join(" y ", nombres);
    }

    private int partidosJugados(String login, String disciplina) {
        int partidos = 0;
        for (Torneo t : gestor.getTorneos()) {
            if (t.getDisciplina().equalsIgnoreCase(disciplina)) {
                for (Encuentro e : t.getCalendario()) {
                    if (e.isJugado() && (loginsDe(e.getParticipante1()).contains(login)
                            || loginsDe(e.getParticipante2()).contains(login))) {
                        partidos++;
                    }
                }
            }
        }
        return partidos;
    }

    private int victorias(String login, String disciplina) {
        int victorias = 0;
        for (Torneo t : gestor.getTorneos()) {
            if (t.getDisciplina().equalsIgnoreCase(disciplina)) {
                for (Encuentro e : t.getCalendario()) {
                    if (loginsDe(e.getGanador()).contains(login)) {
                        victorias++;
                    }
                }
            }
        }
        return victorias;
    }

    private String darRol(Usuario u) {
        if (u instanceof Administrador) return "ADMINISTRADOR";
        if (u instanceof Socio) return "SOCIO";
        if (u instanceof Estudiante) return "ESTUDIANTE";
        if (u instanceof Externo) return "EXTERNO";
        if (u instanceof Entrenador) return "ENTRENADOR";
        if (u instanceof Fisioterapeuta) return "FISIOTERAPEUTA";
        return "EMPLEADO";
    }

    private String nombreDe(String login) {
        Usuario u = usuarios.get(login);
        if (u instanceof Socio) return ((Socio) u).getNombre();
        if (u instanceof Empleado) return ((Empleado) u).getNombre();
        if (u instanceof Estudiante) return ((Estudiante) u).getNombre();
        if (u instanceof Externo) return ((Externo) u).getNombre();
        if (u instanceof Administrador) return "Administrador";
        return login;
    }

    private String documentoDe(Usuario u) {
        if (u instanceof Estudiante) return ((Estudiante) u).getDocumentoIdentidad();
        if (u instanceof Externo) return ((Externo) u).getDocumentoIdentidad();
        return null;
    }

    private String nivelDe(Usuario u) {
        if (u instanceof Estudiante) return ((Estudiante) u).getNivelTecnico();
        if (u instanceof Externo) return ((Externo) u).getNivelTecnico();
        return null;
    }

    // Los empleados no tienen fecha de nacimiento: se toma 0 para que no los bloquee
    private int calcularEdad(Usuario u) {
        LocalDate nacimiento = null;
        if (u instanceof Socio && ((Socio) u).getFechaNacimiento() != null) {
            nacimiento = ((Socio) u).getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (u instanceof Estudiante) {
            nacimiento = ((Estudiante) u).getFechaNacimiento();
        } else if (u instanceof Externo) {
            nacimiento = ((Externo) u).getFechaNacimiento();
        }
        if (nacimiento == null) {
            return 0;
        }
        return Period.between(nacimiento, LocalDate.now()).getYears();
    }

    private String textoEstado(EstadoTorneo estado) {
        if (estado == EstadoTorneo.INSCRIPCIONES_ABIERTAS) return "Inscripciones abiertas";
        if (estado == EstadoTorneo.EN_CURSO) return "En curso";
        return "Finalizado";
    }
}
