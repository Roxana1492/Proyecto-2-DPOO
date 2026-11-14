package deportivo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import autenticacion.Usuario;
import fachada.OperacionRechazadaException;

/**
 * Datos y reglas comunes a todos los torneos. Lo que cambia entre un torneo
 * recreativo y uno competitivo (el cobro y los premios) lo definen las subclases.
 */
public abstract class Torneo {

    private static final int K_ELO = 32;

    private String id;
    private String nombre;
    private String disciplina;
    private Modalidad modalidad;
    private LocalDate fecha;
    private Categoria categoria;
    private int cuposTotales;
    private int cuposPreferenciales;
    private EstadoTorneo estado;
    private boolean premiosEntregados;

    private List<InscripcionTorneo> inscripciones;
    private List<Encuentro> encuentros;
    private TablaTorneo tabla;
    protected List<Premio> premios;

    public Torneo(String id, String nombre, String disciplina, Modalidad modalidad, LocalDate fecha,
            Categoria categoria, int cuposTotales) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new OperacionRechazadaException("El nombre del torneo no puede estar vacío.");
        }
        if (cuposTotales <= 0) {
            throw new OperacionRechazadaException("Los cupos totales deben ser mayores a 0.");
        }
        if (modalidad == Modalidad.DOBLES && cuposTotales % 2 != 0) {
            throw new OperacionRechazadaException("En dobles el número de cupos tiene que ser par.");
        }
        this.id = id;
        this.nombre = nombre;
        this.disciplina = disciplina;
        this.modalidad = modalidad;
        this.fecha = fecha;
        this.categoria = categoria;
        this.cuposTotales = cuposTotales;
        this.cuposPreferenciales = calcularCuposPreferenciales();
        this.estado = EstadoTorneo.INSCRIPCIONES_ABIERTAS;
        this.premiosEntregados = false;
        this.inscripciones = new ArrayList<>();
        this.encuentros = new ArrayList<>();
        this.premios = new ArrayList<>();
    }

    // HU-T01: 20% redondeado hacia arriba. En dobles se calcula sobre las parejas.
    public int calcularCuposPreferenciales() {
        if (modalidad == Modalidad.DOBLES) {
            int parejas = cuposTotales / 2;
            return (int) Math.ceil(parejas * 0.2) * 2;
        }
        return (int) Math.ceil(cuposTotales * 0.2);
    }

    public int cuposPreferencialesLibres() {
        int usados = 0;
        for (InscripcionTorneo i : inscripciones) {
            if (i.isPreferencial()) {
                usados += i.cuposQueOcupa();
            }
        }
        return cuposPreferenciales - usados;
    }

    public int cuposRegularesLibres() {
        int usados = 0;
        for (InscripcionTorneo i : inscripciones) {
            if (!i.isPreferencial()) {
                usados += i.cuposQueOcupa();
            }
        }
        return (cuposTotales - cuposPreferenciales) - usados;
    }

    // HU-T02 y HU-T03: valida todo antes de guardar la inscripción
    public void inscribir(InscripcionTorneo inscripcion) {
        if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            throw new OperacionRechazadaException("El torneo no tiene inscripciones abiertas.");
        }
        ParticipanteTorneo participante = inscripcion.getParticipante();
        for (Usuario u : participante.darIntegrantes()) {
            if (buscarInscripcion(u.getLogin()) != null) {
                throw new OperacionRechazadaException(u.darNombre() + " ya está inscrito(a) en este torneo.");
            }
            if (!categoria.admite(u.calcularEdad(), u.darNivel())) {
                throw new OperacionRechazadaException(u.darNombre() + " no cumple la categoría del torneo ("
                        + categoria.describir() + ").");
            }
        }

        int cupos = inscripcion.cuposQueOcupa();
        // Si pidió preferencial y ya no quedan, pasa a regular
        if (inscripcion.isPreferencial() && cuposPreferencialesLibres() < cupos) {
            inscripcion.setPreferencial(false);
        }
        if (!inscripcion.isPreferencial() && cuposRegularesLibres() < cupos) {
            throw new OperacionRechazadaException("No quedan cupos disponibles en este torneo.");
        }
        inscripcion.setOrden(inscripciones.size() + 1);
        inscripciones.add(inscripcion);
    }

    // HU-T05: solo antes de que empiece, y libera el mismo tipo de cupo que tenía
    public InscripcionTorneo desinscribir(String login) {
        if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            throw new OperacionRechazadaException("Ya no se puede retirar: el torneo empezó.");
        }
        InscripcionTorneo inscripcion = buscarInscripcion(login);
        if (inscripcion == null) {
            throw new OperacionRechazadaException("No está inscrito en este torneo.");
        }
        ParticipanteTorneo p = inscripcion.getParticipante();
        if (p instanceof EquipoTorneo && !((EquipoTorneo) p).esCapitan(login)) {
            throw new OperacionRechazadaException("Solo el capitán puede retirar al equipo.");
        }
        inscripciones.remove(inscripcion);
        return inscripcion;
    }

    // HU-T14: los preferenciales que nadie ocupó pasan a ser regulares
    public void liberarCuposPreferenciales() {
        if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            throw new OperacionRechazadaException("Solo se pueden liberar cupos con las inscripciones abiertas.");
        }
        int libres = cuposPreferencialesLibres();
        if (libres == 0) {
            throw new OperacionRechazadaException("No hay cupos preferenciales pendientes por liberar.");
        }
        cuposPreferenciales -= libres;
    }

    // HU-T04 y HU-T13: cambia a En curso y arma el calendario
    public void iniciar(LocalDate hoy) {
        if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            throw new OperacionRechazadaException("El torneo ya fue iniciado.");
        }
        if (hoy.isBefore(fecha)) {
            throw new OperacionRechazadaException("El torneo empieza el " + fecha + ", todavía no se puede iniciar.");
        }
        if (inscripciones.size() < 2) {
            throw new OperacionRechazadaException("Se necesitan al menos 2 inscritos para iniciar.");
        }
        List<ParticipanteTorneo> participantes = darParticipantes();
        encuentros = new GeneradorCalendario().generarLiga(participantes);
        tabla = new TablaTorneo(participantes);
        estado = EstadoTorneo.EN_CURSO;
    }

    // HU-T08
    public void registrarResultado(String idEncuentro, int local, int visitante) {
        if (estado != EstadoTorneo.EN_CURSO) {
            throw new OperacionRechazadaException("El torneo no está en curso.");
        }
        Encuentro e = buscarEncuentro(idEncuentro);
        if (e == null || e.esDescanso()) {
            throw new OperacionRechazadaException("No existe el encuentro " + idEncuentro + ".");
        }
        if (e.isJugado()) {
            throw new OperacionRechazadaException("Ese encuentro ya tiene resultado.");
        }
        if (local < 0 || visitante < 0) {
            throw new OperacionRechazadaException("El marcador no puede ser negativo.");
        }
        if (local == visitante && !permiteEmpates()) {
            throw new OperacionRechazadaException("En individual y dobles no puede haber empate.");
        }
        e.registrarResultado(local, visitante);
        tabla.actualizar(e, permiteEmpates());
        actualizarElo(e);
    }

    // HU-T09: fija posiciones y reparte premios una sola vez
    public void finalizar() {
        if (estado == EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            throw new OperacionRechazadaException("El torneo todavía no ha empezado.");
        }
        if (premiosEntregados) {
            throw new OperacionRechazadaException("Los premios de este torneo ya fueron entregados.");
        }
        if (!darEncuentrosPendientes().isEmpty()) {
            throw new OperacionRechazadaException("Faltan resultados por ingresar.");
        }
        repartirPremios(tabla.ordenarConDesempate());
        premiosEntregados = true;
        estado = EstadoTorneo.FINALIZADO;
    }

    // Los empates solo se permiten en deportes de conjunto
    public boolean permiteEmpates() {
        return modalidad == Modalidad.CONJUNTO;
    }

    public abstract double calcularCobro(ParticipanteTorneo p);

    public abstract void repartirPremios(List<PosicionTorneo> tabla);

    // Actualiza el ELO (Ranking del P1) de cada integrante después de un encuentro
    private void actualizarElo(Encuentro e) {
        ParticipanteTorneo ganador = e.ganador();
        if (ganador == null) {
            return;
        }
        double eloLocal = e.getLocal().promedioElo(disciplina);
        double eloVisitante = e.getVisitante().promedioElo(disciplina);
        double esperadoLocal = 1 / (1 + Math.pow(10, (eloVisitante - eloLocal) / 400));
        for (Usuario u : e.getLocal().darIntegrantes()) {
            u.darRanking(disciplina).actualizar(esperadoLocal, ganador == e.getLocal(), K_ELO);
        }
        for (Usuario u : e.getVisitante().darIntegrantes()) {
            u.darRanking(disciplina).actualizar(1 - esperadoLocal, ganador == e.getVisitante(), K_ELO);
        }
    }

    // ------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------

    public InscripcionTorneo buscarInscripcion(String login) {
        for (InscripcionTorneo i : inscripciones) {
            if (i.getParticipante().contiene(login)) {
                return i;
            }
        }
        return null;
    }

    public Encuentro buscarEncuentro(String idEncuentro) {
        for (Encuentro e : encuentros) {
            if (e.getId().equals(idEncuentro)) {
                return e;
            }
        }
        return null;
    }

    public List<Encuentro> darEncuentrosPendientes() {
        List<Encuentro> pendientes = new ArrayList<>();
        for (Encuentro e : encuentros) {
            if (!e.esDescanso() && !e.isJugado()) {
                pendientes.add(e);
            }
        }
        return pendientes;
    }

    public List<ParticipanteTorneo> darParticipantes() {
        List<ParticipanteTorneo> lista = new ArrayList<>();
        for (InscripcionTorneo i : inscripciones) {
            lista.add(i.getParticipante());
        }
        return lista;
    }

    public List<PosicionTorneo> darPosiciones() {
        if (tabla == null) {
            return new ArrayList<>();
        }
        return tabla.ordenarConDesempate();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDisciplina() { return disciplina; }
    public Modalidad getModalidad() { return modalidad; }
    public LocalDate getFecha() { return fecha; }
    public Categoria getCategoria() { return categoria; }
    public int getCuposTotales() { return cuposTotales; }
    public int getCuposPreferenciales() { return cuposPreferenciales; }
    public void setCuposPreferenciales(int cuposPreferenciales) { this.cuposPreferenciales = cuposPreferenciales; }
    public EstadoTorneo getEstado() { return estado; }
    public void setEstado(EstadoTorneo estado) { this.estado = estado; }
    public boolean isPremiosEntregados() { return premiosEntregados; }
    public List<InscripcionTorneo> getInscripciones() { return inscripciones; }
    public List<Encuentro> getEncuentros() { return encuentros; }
    public List<Premio> getPremios() { return premios; }
}
