package deportivo;

import fachada.OperacionRechazadaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Torneo {
    private String idTorneo;
    private String nombre;
    private TipoTorneo tipo;
    private ModalidadTorneo modalidad;
    private String disciplina;
    private LocalDate fechaInicio;
    private Integer edadMaxima;          // null = sin límite de edad
    private String nivelRequerido;       // null = sin nivel requerido
    private int cuposTotales;
    private int cuposPreferencialesDisponibles;
    private int cuposRegularesDisponibles;
    private double tarifaInscripcion;
    private double fondoPremios;
    private EstadoTorneo estado;

    private Set<Object> empleadosEnTurno;
    private List<Object> inscritos;
    private List<Encuentro> calendario;
    private Map<Object, Double> pagosPorParticipante;
    private Map<Object, Boolean> esPreferencialPorParticipante;

    // HU-T01: creación y configuración del torneo.
    // Orden de los parámetros: ..., fechaInicio, edadMaxima, nivelRequerido, cuposTotales, tarifaInscripcion
    public Torneo(String idTorneo, String nombre, TipoTorneo tipo, ModalidadTorneo modalidad,
                  String disciplina, LocalDate fechaInicio, int edadMaxima, String nivelRequerido,
                  int cuposTotales, double tarifaInscripcion) {
        this(idTorneo, nombre, tipo, modalidad, disciplina, fechaInicio, edadMaxima, nivelRequerido,
             cuposTotales, tarifaInscripcion, true);
    }

    // Se usa al cargar torneos del archivo: un torneo viejo ya tiene fecha pasada,
    // entonces no se vuelve a validar la fecha
    public static Torneo cargarDesdeArchivo(String idTorneo, String nombre, TipoTorneo tipo,
                                            ModalidadTorneo modalidad, String disciplina,
                                            LocalDate fechaInicio, int edadMaxima, String nivelRequerido,
                                            int cuposTotales, double tarifaInscripcion) {
        return new Torneo(idTorneo, nombre, tipo, modalidad, disciplina, fechaInicio, edadMaxima,
                          nivelRequerido, cuposTotales, tarifaInscripcion, false);
    }

    private Torneo(String idTorneo, String nombre, TipoTorneo tipo, ModalidadTorneo modalidad,
                   String disciplina, LocalDate fechaInicio, int edadMaxima, String nivelRequerido,
                   int cuposTotales, double tarifaInscripcion, boolean validarFecha) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new OperacionRechazadaException("El nombre del torneo no puede estar vacío.");
        }
        if (fechaInicio == null || (validarFecha && fechaInicio.isBefore(LocalDate.now()))) {
            throw new OperacionRechazadaException("La fecha de inicio no puede ser anterior a la fecha actual.");
        }
        if (cuposTotales <= 0) {
            throw new OperacionRechazadaException("Los cupos totales deben ser mayores a 0.");
        }
        if (modalidad == ModalidadTorneo.DOBLES && cuposTotales % 2 != 0) {
            throw new OperacionRechazadaException("Los torneos de dobles exigen un número par de cupos.");
        }
        if (tipo == TipoTorneo.RECREATIVO && tarifaInscripcion != 0.0) {
            throw new OperacionRechazadaException("Un torneo recreativo debe tener tarifa 0.");
        }
        if (tipo == TipoTorneo.COMPETITIVO && tarifaInscripcion <= 0.0) {
            throw new OperacionRechazadaException("Un torneo competitivo debe tener tarifa mayor a 0.");
        }

        this.idTorneo = idTorneo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.modalidad = modalidad;
        this.disciplina = disciplina;
        this.fechaInicio = fechaInicio;
        this.edadMaxima = (edadMaxima > 0) ? Integer.valueOf(edadMaxima) : null;
        this.nivelRequerido = nivelRequerido;
        this.cuposTotales = cuposTotales;
        this.tarifaInscripcion = tarifaInscripcion;
        this.fondoPremios = 0.0;
        this.estado = EstadoTorneo.INSCRIPCIONES_ABIERTAS;

        this.empleadosEnTurno = new HashSet<>();
        this.inscritos = new ArrayList<>();
        this.calendario = new ArrayList<>();
        this.pagosPorParticipante = new HashMap<>();
        this.esPreferencialPorParticipante = new HashMap<>();

        calcularCuposIniciales();
    }

    // Cálculo inicial de cupos preferenciales (20% redondeado hacia arriba) y regulares (HU-T01)
    private void calcularCuposIniciales() {
        if (this.modalidad == ModalidadTorneo.DOBLES) {
            // En dobles el 20% se calcula sobre las parejas y cada pareja ocupa 2 cupos
            int parejasTotales = this.cuposTotales / 2;
            int parejasPreferenciales = (int) Math.ceil(parejasTotales * 0.20);
            this.cuposPreferencialesDisponibles = parejasPreferenciales * 2;
        } else {
            this.cuposPreferencialesDisponibles = (int) Math.ceil(this.cuposTotales * 0.20);
        }
        this.cuposRegularesDisponibles = this.cuposTotales - this.cuposPreferencialesDisponibles;
    }

    // ÚNICO PUNTO DE ENTRADA ATÓMICO PARA INSCRIPCIÓN (HU-T02 & HU-T03)
    public boolean procesarInscripcion(Object participante, boolean pagoConfirmado, boolean esEmpleado,
                                       int edad, String nivel, boolean solicitaPreferencial,
                                       double eloJugador, int partidosJugados, double eloKecimo,
                                       Object capitanEquipo) {
        // 1. Validar estado del torneo y participante no nulo
        if (this.estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS || participante == null) {
            return false;
        }

        // 2. Validar duplicidad
        if (this.inscritos.contains(participante)) {
            return false;
        }

        // 3. Validar restricciones de edad máxima y nivel requerido
        if (this.edadMaxima != null && edad > this.edadMaxima) {
            return false;
        }
        if (this.nivelRequerido != null && !this.nivelRequerido.equalsIgnoreCase(nivel)) {
            return false;
        }

        // 4. Validar reglas por modalidad y cantidad de cupos requeridos
        int cuposRequeridos = 1;
        if (this.modalidad == ModalidadTorneo.DOBLES) {
            if (!(participante instanceof List) || ((List<?>) participante).size() != 2) {
                return false;
            }
            cuposRequeridos = 2;
        } else if (this.modalidad == ModalidadTorneo.CONJUNTO) {
            if (capitanEquipo == null) {
                return false;
            }
            cuposRequeridos = 1;
        }

        // 5. Validar pago en torneos competitivos (los empleados están exentos)
        if (!esEmpleado && this.tipo == TipoTorneo.COMPETITIVO && !pagoConfirmado && this.tarifaInscripcion > 0) {
            return false;
        }

        // 6. Evaluar disponibilidad y asignación de cupo preferencial vs regular
        boolean asignarPreferencial = false;
        if (solicitaPreferencial) {
            boolean calificaElo = (partidosJugados > 0 && eloJugador >= eloKecimo);
            if (calificaElo && this.cuposPreferencialesDisponibles >= cuposRequeridos) {
                asignarPreferencial = true;
            } else if (this.cuposRegularesDisponibles >= cuposRequeridos) {
                asignarPreferencial = false;
            } else {
                return false;
            }
        } else {
            if (this.cuposRegularesDisponibles < cuposRequeridos) {
                return false; // Cupos regulares agotados aunque queden preferenciales reservados
            }
        }

        // 7. Aplicar cambios (si llegó hasta aquí, todas las validaciones pasaron)
        this.inscritos.add(participante);
        if (asignarPreferencial) {
            this.cuposPreferencialesDisponibles -= cuposRequeridos;
        } else {
            this.cuposRegularesDisponibles -= cuposRequeridos;
        }

        double montoPagado = esEmpleado ? 0.0 : this.tarifaInscripcion;
        this.fondoPremios += montoPagado;

        this.pagosPorParticipante.put(participante, montoPagado);
        this.esPreferencialPorParticipante.put(participante, asignarPreferencial);

        return true;
    }

    // --- Métodos de apoyo (atajos sobre procesarInscripcion) ---

    public boolean inscribirParticipante(Object participante, boolean pagoConfirmado) {
        return procesarInscripcion(participante, pagoConfirmado, false, 20, nivelPorDefecto(),
                                   false, 0.0, 10, 0.0, "CapitanDefault");
    }

    public boolean inscribirParticipanteConPreferencia(Object participante, boolean pagoConfirmado,
                                                       double elo, int partidosJugados) {
        return procesarInscripcion(participante, pagoConfirmado, false, 20, nivelPorDefecto(),
                                   true, elo, partidosJugados, 0.0, "CapitanDefault");
    }

    public boolean inscribirParticipanteConPreferencia(Object participante, boolean pagoConfirmado,
                                                       double elo, int partidosJugados, double eloKecimo) {
        return procesarInscripcion(participante, pagoConfirmado, false, 20, nivelPorDefecto(),
                                   true, elo, partidosJugados, eloKecimo, "CapitanDefault");
    }

    public boolean inscribirParticipanteConPreferencia(Object participante, boolean pagoConfirmado,
                                                       boolean solicitaPreferencial, double elo, int partidosJugados) {
        if (solicitaPreferencial && partidosJugados <= 0) {
            return false; // Sin historial no puede ocupar un cupo preferencial
        }
        return procesarInscripcion(participante, pagoConfirmado, false, 20, nivelPorDefecto(),
                                   solicitaPreferencial, elo, partidosJugados, 0.0, "CapitanDefault");
    }

    public boolean inscribirParticipanteConReferencia(Object participante, boolean pagoConfirmado,
                                                      double elo, int partidosJugados) {
        return inscribirParticipanteConPreferencia(participante, pagoConfirmado, elo, partidosJugados);
    }

    public boolean inscribirParticipanteConReferencia(Object participante, boolean pagoConfirmado,
                                                      double elo, int partidosJugados, double eloKecimo) {
        return inscribirParticipanteConPreferencia(participante, pagoConfirmado, elo, partidosJugados, eloKecimo);
    }

    public boolean inscribirParticipanteConRestricciones(Object participante, boolean pagoConfirmado,
                                                         int edadParticipante, String nivelParticipante) {
        return procesarInscripcion(participante, pagoConfirmado, false, edadParticipante, nivelParticipante,
                                   false, 0.0, 10, 0.0, "CapitanDefault");
    }

    private String nivelPorDefecto() {
        return (this.nivelRequerido != null) ? this.nivelRequerido : "INTERMEDIO";
    }

    // Desinscripción con liberación exacta de cupos y ajuste del fondo (HU-T05)
    public boolean desinscribirParticipante(Object participante) {
        if (this.estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            return false;
        }
        if (participante == null || !this.inscritos.contains(participante)) {
            return false;
        }

        this.inscritos.remove(participante);

        // Se devuelven los cupos del mismo tipo que ocupaba (preferencial o regular)
        int cuposLiberados = (this.modalidad == ModalidadTorneo.DOBLES) ? 2 : 1;
        Boolean eraPreferencial = this.esPreferencialPorParticipante.remove(participante);
        if (eraPreferencial != null && eraPreferencial) {
            this.cuposPreferencialesDisponibles += cuposLiberados;
        } else {
            this.cuposRegularesDisponibles += cuposLiberados;
        }

        // Se resta del fondo lo que realmente pagó (un empleado pagó 0)
        Double pagado = this.pagosPorParticipante.remove(participante);
        if (pagado != null) {
            this.fondoPremios = Math.max(0.0, this.fondoPremios - pagado);
        }
        return true;
    }

    // Generación de calendario Round-Robin: todos contra todos (HU-T13)
    public boolean generarCalendario() {
        if (this.inscritos == null || this.inscritos.size() < 2) {
            return false;
        }

        this.calendario.clear();

        List<Object> participantes = new ArrayList<>(this.inscritos);
        int n = participantes.size();

        if (n % 2 != 0) {
            participantes.add(null); // Descanso (bye) para número impar
            n = participantes.size();
        }

        int totalRondas = n - 1;
        int partidosPorRonda = n / 2;
        int contadorEncuentros = 1;

        for (int ronda = 1; ronda <= totalRondas; ronda++) {
            for (int i = 0; i < partidosPorRonda; i++) {
                Object p1 = participantes.get(i);
                Object p2 = participantes.get(n - 1 - i);

                if (p1 != null && p2 != null) {
                    String idEnc = this.idTorneo + "-R" + ronda + "-ENC-" + contadorEncuentros;
                    this.calendario.add(new Encuentro(idEnc, ronda, p1, p2));
                    contadorEncuentros++;
                }
            }

            Object ultimo = participantes.remove(n - 1);
            participantes.add(1, ultimo);
        }

        return !this.calendario.isEmpty();
    }

    // HU-T14: Liberación de cupos preferenciales no utilizados hacia cupos regulares
    public boolean liberarCuposPreferenciales() {
        if (this.estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            return false;
        }
        if (this.cuposPreferencialesDisponibles <= 0) {
            return false;
        }

        this.cuposRegularesDisponibles += this.cuposPreferencialesDisponibles;
        this.cuposPreferencialesDisponibles = 0;
        return true;
    }

    // HU-T04: Inicio oficial del torneo con validaciones de fecha y mínimo de participantes
    public boolean iniciarTorneo(LocalDate fechaActual) {
        if (this.estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            return false;
        }
        if (fechaActual == null || fechaActual.isBefore(this.fechaInicio)) {
            return false;
        }
        if (this.inscritos.size() < 2) {
            return false;
        }

        liberarCuposPreferenciales();
        this.estado = EstadoTorneo.EN_CURSO;
        generarCalendario();
        return true;
    }

    // HU-T08: Registrar resultado de un encuentro validando estado EN_CURSO y regla de empates por modalidad
    public boolean registrarResultadoEncuentro(String idEncuentro, int marcador1, int marcador2) {
        if (this.estado != EstadoTorneo.EN_CURSO || idEncuentro == null) {
            return false;
        }

        Encuentro encuentro = buscarEncuentro(idEncuentro);
        if (encuentro == null) {
            return false;
        }

        // En modalidad CONJUNTO se permiten empates; en INDIVIDUAL/DOBLES se exige ganador
        boolean permiteEmpate = (this.modalidad == ModalidadTorneo.CONJUNTO);
        boolean registrado = encuentro.registrarMarcador(marcador1, marcador2, permiteEmpate);

        if (registrado) {
            verificarFinalizacionTorneo();
        }
        return registrado;
    }

    // HU-T09: Registrar resultado con instancia de desempate (penales / tie-break)
    public boolean registrarDesempateEncuentro(String idEncuentro, int marcador1, int marcador2,
                                               int desempate1, int desempate2) {
        if (this.estado != EstadoTorneo.EN_CURSO || idEncuentro == null) {
            return false;
        }

        Encuentro encuentro = buscarEncuentro(idEncuentro);
        if (encuentro == null) {
            return false;
        }

        boolean registrado = encuentro.registrarMarcadorConDesempate(marcador1, marcador2, desempate1, desempate2);
        if (registrado) {
            verificarFinalizacionTorneo();
        }
        return registrado;
    }

    private Encuentro buscarEncuentro(String idEncuentro) {
        for (Encuentro e : this.calendario) {
            if (e.getIdEncuentro().equals(idEncuentro)) {
                return e;
            }
        }
        return null;
    }

    // Si todos los encuentros del calendario ya fueron jugados, el torneo pasa a FINALIZADO
    private void verificarFinalizacionTorneo() {
        if (this.calendario.isEmpty()) {
            return;
        }
        for (Encuentro e : this.calendario) {
            if (!e.isJugado()) {
                return;
            }
        }
        this.estado = EstadoTorneo.FINALIZADO;
    }

    // HU-T06: Asignar empleado a turno/arbitraje del torneo (no puede estar inscrito como jugador)
    public boolean asignarEmpleadoATurno(Object empleado) {
        if (empleado == null || this.inscritos.contains(empleado)) {
            return false; // Conflicto: ya está inscrito como participante
        }
        return this.empleadosEnTurno.add(empleado);
    }

    // HU-T06 & HU-T07: Inscripción validando el rol (SOCIO, EMPLEADO, ESTUDIANTE, EXTERNO)
    public boolean inscribirPorRol(Object participante, RolParticipante rol, boolean pagoConfirmado,
                                   boolean autorizadoOVerificado, int edad, String nivel) {
        if (rol == null || participante == null) {
            return false;
        }
        // Un empleado con turno asignado en este torneo no puede competir
        if (rol == RolParticipante.EMPLEADO && this.empleadosEnTurno.contains(participante)) {
            return false;
        }
        // Externos y estudiantes requieren autorización / verificación previa
        if ((rol == RolParticipante.EXTERNO || rol == RolParticipante.ESTUDIANTE) && !autorizadoOVerificado) {
            return false;
        }

        // El empleado se inscribe gratis (exento de pago y de aporte al fondo)
        boolean esEmpleado = (rol == RolParticipante.EMPLEADO);
        return procesarInscripcion(participante, pagoConfirmado, esEmpleado, edad, nivel,
                                   false, 0.0, 10, 0.0, "CapitanDefault");
    }

    // Variante con edad y nivel por defecto (usada por la fachada)
    public boolean inscribirPorRolEspecifico(Object participante, RolParticipante rol,
                                             boolean verificadoOAutorizado, boolean pagoConfirmado) {
        return inscribirPorRol(participante, rol, pagoConfirmado, verificadoOAutorizado, 25, nivelPorDefecto());
    }

    // HU-T12: Consultas de encuentros por ronda, por participante y pendientes
    public List<Encuentro> consultarEncuentrosPorRonda(int ronda) {
        List<Encuentro> resultado = new ArrayList<>();
        for (Encuentro e : this.calendario) {
            if (e.getRonda() == ronda) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public List<Encuentro> consultarEncuentrosDeParticipante(Object participante) {
        List<Encuentro> resultado = new ArrayList<>();
        if (participante == null) {
            return resultado;
        }
        for (Encuentro e : this.calendario) {
            if (participante.equals(e.getParticipante1()) || participante.equals(e.getParticipante2())) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public List<Encuentro> consultarEncuentrosPendientes() {
        List<Encuentro> pendientes = new ArrayList<>();
        for (Encuentro e : this.calendario) {
            if (!e.isJugado()) {
                pendientes.add(e);
            }
        }
        return pendientes;
    }

    // Getters y Setters
    public String getIdTorneo() { return idTorneo; }
    public String getId() { return idTorneo; }
    public String getNombre() { return nombre; }
    public TipoTorneo getTipo() { return tipo; }
    public ModalidadTorneo getModalidad() { return modalidad; }
    public String getDisciplina() { return disciplina; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public Integer getEdadMaxima() { return edadMaxima; }
    public String getNivelRequerido() { return nivelRequerido; }
    public int getCuposTotales() { return cuposTotales; }
    public int getCuposPreferencialesDisponibles() { return cuposPreferencialesDisponibles; }
    public int getCuposRegularesDisponibles() { return cuposRegularesDisponibles; }
    public double getTarifaInscripcion() { return tarifaInscripcion; }
    public double getFondoPremios() { return fondoPremios; }
    public EstadoTorneo getEstado() { return estado; }
    public void setEstado(EstadoTorneo estado) { this.estado = estado; }
    public List<Object> getInscritos() { return inscritos; }
    public List<Encuentro> getCalendario() { return calendario; }
    public boolean esPreferencial(Object participante) {
        Boolean valor = esPreferencialPorParticipante.get(participante);
        return valor != null && valor;
    }
    public double getPagoDe(Object participante) {
        Double valor = pagosPorParticipante.get(participante);
        return valor == null ? 0.0 : valor;
    }
}