package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import deportivo.Premio;
import deportivo.Torneo;
import deportivo.TorneoCompetitivo;
import deportivo.TorneoRecreativo;
import fachada.OperacionesComercio;

/**
 * Carga y guarda los datos del Proyecto 2 en la carpeta datos/.
 * Es la única clase (junto con AlmacenadorArchivos y PersistenciaComercio)
 * que lee o escribe archivos.
 *
 * Archivos: usuarios.txt, matriculas.txt, torneos.txt, inscripciones.txt,
 * encuentros.txt, premios.txt y bonos.txt. La parte comercial la maneja
 * PersistenciaComercio con los archivos del Proyecto 1.
 *
 * Los torneos no guardan los cupos, el calendario ni la tabla: al cargar se
 * vuelven a inscribir los participantes en el mismo orden, se inicia el torneo
 * (el calendario sale igual) y se registran otra vez los resultados. Así los
 * cupos, la tabla, el ELO y los premios quedan igual que antes de cerrar.
 */
public class PersistenciaClub {

    public static final String CARPETA = "datos";

    private final String carpeta;

    public PersistenciaClub() {
        this(CARPETA);
    }

    public PersistenciaClub(String carpeta) {
        this.carpeta = carpeta;
    }

    private String ruta(String archivo) {
        return carpeta + File.separator + archivo;
    }

    private PrintWriter abrir(String archivo) throws Exception {
        File f = new File(ruta(archivo));
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }
        return new PrintWriter(new FileWriter(f));
    }

    private Nivel leerNivel(String texto) {
        if (texto == null || texto.isEmpty() || texto.equals("-")) {
            return null;
        }
        return Nivel.valueOf(texto);
    }

    private String escribirNivel(Nivel nivel) {
        return nivel == null ? "-" : nivel.name();
    }

    // ------------------------------------------------------------------
    // Usuarios y matrículas
    // ------------------------------------------------------------------

    // Formato: tipo;login;password;nombre;fechaNacimiento;nivel;documento;codigoEstudiantil
    // (las columnas que no aplican quedan vacías)
    public void cargarUsuarios(Autenticador autenticador) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String[] p : PersistenciaComercio.leerLineas(ruta("usuarios.txt"))) {
            if (p.length < 4) {
                continue;
            }
            String fecha = p.length > 4 ? p[4] : "";
            Nivel nivel = p.length > 5 ? leerNivel(p[5]) : null;
            String documento = p.length > 6 ? p[6] : "";
            String codigo = p.length > 7 ? p[7] : "";
            Usuario u = null;
            switch (p[0].toUpperCase()) {
                case "ADMINISTRADOR":
                    u = new Administrador(p[1], p[2]);
                    break;
                case "SOCIO":
                    Date nacimiento = fecha.isEmpty() ? new Date() : sdf.parse(fecha);
                    Socio socio = new Socio(p[1], p[2], p[3], nacimiento);
                    socio.setNivel(nivel);
                    u = socio;
                    break;
                case "ENTRENADOR":
                case "FISIOTERAPEUTA":
                case "EMPLEADO":
                    Empleado e;
                    if (p[0].equalsIgnoreCase("ENTRENADOR")) {
                        e = new Entrenador(p[1], p[2], p[3]);
                    } else if (p[0].equalsIgnoreCase("FISIOTERAPEUTA")) {
                        e = new Fisioterapeuta(p[1], p[2], p[3]);
                    } else {
                        e = new Empleado(p[1], p[2], p[3]);
                    }
                    if (!fecha.isEmpty()) {
                        e.setFechaNacimiento(LocalDate.parse(fecha));
                    }
                    e.setNivel(nivel);
                    u = e;
                    break;
                case "ESTUDIANTE":
                    u = new Estudiante(p[1], p[2], p[3], documento, LocalDate.parse(fecha), nivel, codigo);
                    break;
                case "EXTERNO":
                    u = new Externo(p[1], p[2], p[3], documento, LocalDate.parse(fecha), nivel);
                    break;
                default:
                    break;
            }
            autenticador.registrarUsuario(u);
        }
    }

    public void guardarUsuarios(Autenticador autenticador) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try (PrintWriter pw = abrir("usuarios.txt")) {
            pw.println("# tipo;login;password;nombre;fechaNacimiento;nivel;documento;codigoEstudiantil");
            for (Usuario u : autenticador.darUsuarios()) {
                String inicio = u.getLogin() + ";" + u.getPassword() + ";" + u.darNombre() + ";";
                if (u instanceof Administrador) {
                    pw.println("ADMINISTRADOR;" + inicio);
                } else if (u instanceof Socio) {
                    Socio s = (Socio) u;
                    pw.println("SOCIO;" + inicio + sdf.format(s.getFechaNacimiento()) + ";" + escribirNivel(s.darNivel()));
                } else if (u instanceof Empleado) {
                    Empleado e = (Empleado) u;
                    String tipo = "EMPLEADO";
                    if (e instanceof Entrenador) {
                        tipo = "ENTRENADOR";
                    } else if (e instanceof Fisioterapeuta) {
                        tipo = "FISIOTERAPEUTA";
                    }
                    String fecha = e.getFechaNacimiento() == null ? "" : e.getFechaNacimiento().toString();
                    pw.println(tipo + ";" + inicio + fecha + ";" + escribirNivel(e.darNivel()));
                } else if (u instanceof Estudiante) {
                    Estudiante e = (Estudiante) u;
                    pw.println("ESTUDIANTE;" + inicio + e.getFechaNacimiento() + ";" + escribirNivel(e.darNivel()) + ";"
                            + e.getDocumentoIdentidad() + ";" + e.getCodigoEstudiantil());
                } else if (u instanceof Externo) {
                    Externo e = (Externo) u;
                    pw.println("EXTERNO;" + inicio + e.getFechaNacimiento() + ";" + escribirNivel(e.darNivel()) + ";"
                            + e.getDocumentoIdentidad());
                }
            }
        }
    }

    public RegistroMatriculas cargarMatriculas() throws Exception {
        RegistroMatriculas matriculas = new RegistroMatriculas();
        for (String[] p : PersistenciaComercio.leerLineas(ruta("matriculas.txt"))) {
            matriculas.agregarCodigo(p[0]);
        }
        return matriculas;
    }

    // ------------------------------------------------------------------
    // Torneos
    // ------------------------------------------------------------------

    public void cargarTorneos(GestorTorneos gestor, Autenticador autenticador) throws Exception {
        Map<String, Torneo> torneos = new HashMap<>();
        Map<String, String[]> lineas = new HashMap<>();

        // id;tipo;nombre;disciplina;modalidad;fecha;edadMaxima;nivel;cupos;tarifa;estado
        for (String[] p : PersistenciaComercio.leerLineas(ruta("torneos.txt"))) {
            if (p.length < 11) {
                continue;
            }
            Integer edad = p[6].isEmpty() ? null : Integer.valueOf(p[6]);
            Categoria categoria = new Categoria(edad, leerNivel(p[7]));
            Modalidad modalidad = Modalidad.valueOf(p[4]);
            LocalDate fecha = LocalDate.parse(p[5]);
            int cupos = Integer.parseInt(p[8]);
            Torneo t;
            if (p[1].equals("COMPETITIVO")) {
                t = new TorneoCompetitivo(p[0], p[2], p[3], modalidad, fecha, categoria, cupos, Double.parseDouble(p[9]));
            } else {
                t = new TorneoRecreativo(p[0], p[2], p[3], modalidad, fecha, categoria, cupos);
            }
            gestor.agregar(t);
            torneos.put(t.getId(), t);
            lineas.put(t.getId(), p);
        }

        // idTorneo;tipo(J, P o E);participante;preferencial;montoPagado;confirmacionPago
        for (String[] p : PersistenciaComercio.leerLineas(ruta("inscripciones.txt"))) {
            Torneo t = torneos.get(p[0]);
            if (t == null || p.length < 6) {
                continue;
            }
            ParticipanteTorneo participante = textoAParticipante(p[1], p[2], autenticador);
            String confirmacion = p[5].isEmpty() ? null : p[5];
            t.inscribir(new InscripcionTorneo(participante, Boolean.parseBoolean(p[3]),
                    Double.parseDouble(p[4]), confirmacion, 0));
        }

        for (Torneo t : torneos.values()) {
            if (!lineas.get(t.getId())[10].equals(EstadoTorneo.INSCRIPCIONES_ABIERTAS.name())) {
                t.iniciar(t.getFecha());
            }
        }

        // idTorneo;idEncuentro;marcadorLocal;marcadorVisitante
        for (String[] p : PersistenciaComercio.leerLineas(ruta("encuentros.txt"))) {
            Torneo t = torneos.get(p[0]);
            if (t != null && p.length >= 4) {
                t.registrarResultado(p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]));
            }
        }

        // Al finalizar otra vez se vuelven a crear los mismos premios y bonos
        for (Torneo t : torneos.values()) {
            if (lineas.get(t.getId())[10].equals(EstadoTorneo.FINALIZADO.name())) {
                t.finalizar();
            }
        }
    }

    public void guardarTorneos(GestorTorneos gestor) throws Exception {
        List<Torneo> torneos = gestor.listar();
        try (PrintWriter pw = abrir("torneos.txt")) {
            pw.println("# id;tipo;nombre;disciplina;modalidad;fecha;edadMaxima;nivel;cupos;tarifa;estado");
            for (Torneo t : torneos) {
                String tipo = t instanceof TorneoCompetitivo ? "COMPETITIVO" : "RECREATIVO";
                double tarifa = t instanceof TorneoCompetitivo ? ((TorneoCompetitivo) t).getTarifa() : 0;
                Integer edad = t.getCategoria().getEdadMaxima();
                pw.println(t.getId() + ";" + tipo + ";" + t.getNombre() + ";" + t.getDisciplina() + ";"
                        + t.getModalidad() + ";" + t.getFecha() + ";" + (edad == null ? "" : edad) + ";"
                        + escribirNivel(t.getCategoria().getNivel()) + ";" + t.getCuposTotales() + ";" + tarifa
                        + ";" + t.getEstado());
            }
        }
        try (PrintWriter pw = abrir("inscripciones.txt")) {
            pw.println("# idTorneo;tipo(J jugador, P pareja, E equipo);participante;preferencial;montoPagado;confirmacionPago");
            for (Torneo t : torneos) {
                for (InscripcionTorneo i : t.getInscripciones()) {
                    String confirmacion = i.getConfirmacionPago() == null ? "" : i.getConfirmacionPago();
                    pw.println(t.getId() + ";" + participanteATexto(i.getParticipante()) + ";" + i.isPreferencial()
                            + ";" + i.getMontoPagado() + ";" + confirmacion);
                }
            }
        }
        try (PrintWriter pw = abrir("encuentros.txt")) {
            pw.println("# idTorneo;idEncuentro;marcadorLocal;marcadorVisitante");
            for (Torneo t : torneos) {
                for (Encuentro e : t.getEncuentros()) {
                    if (e.isJugado()) {
                        pw.println(t.getId() + ";" + e.getId() + ";" + e.getMarcadorLocal() + ";" + e.getMarcadorVisitante());
                    }
                }
            }
        }
        // premios.txt es solo un registro para consultar; al cargar se recalculan
        try (PrintWriter pw = abrir("premios.txt")) {
            pw.println("# idTorneo;premio");
            for (Torneo t : torneos) {
                for (Premio premio : t.getPremios()) {
                    pw.println(t.getId() + ";" + premio.describir());
                }
            }
        }
    }

    // Jugador: "J;login"  Pareja: "P;login1,login2"  Equipo: "E;nombre|capitan|socio1,socio2"
    private String participanteATexto(ParticipanteTorneo p) {
        if (p instanceof EquipoTorneo) {
            EquipoTorneo e = (EquipoTorneo) p;
            List<String> logins = new ArrayList<>();
            for (Socio s : e.getIntegrantes()) {
                logins.add(s.getLogin());
            }
            return "E;" + e.getNombre() + "|" + e.getCapitan().getLogin() + "|" + String.join(",", logins);
        }
        if (p instanceof Pareja) {
            Pareja pa = (Pareja) p;
            return "P;" + pa.getJugador1().getLogin() + "," + pa.getJugador2().getLogin();
        }
        return "J;" + ((Jugador) p).getUsuario().getLogin();
    }

    private ParticipanteTorneo textoAParticipante(String tipo, String texto, Autenticador autenticador) {
        if (tipo.equals("P")) {
            String[] logins = texto.split(",");
            return new Pareja(autenticador.buscar(logins[0]), autenticador.buscar(logins[1]));
        }
        if (tipo.equals("E")) {
            String[] partes = texto.split("\\|", -1);
            List<Socio> integrantes = new ArrayList<>();
            if (!partes[2].isEmpty()) {
                for (String login : partes[2].split(",")) {
                    integrantes.add((Socio) autenticador.buscar(login));
                }
            }
            return new EquipoTorneo(partes[0], (Socio) autenticador.buscar(partes[1]), integrantes);
        }
        return new Jugador(autenticador.buscar(texto));
    }

    // ------------------------------------------------------------------
    // Bonos: se crean otra vez al cargar los torneos; aquí solo se guarda si ya se usaron
    // ------------------------------------------------------------------

    public void cargarBonos(Autenticador autenticador) throws Exception {
        Map<String, Boolean> usados = new HashMap<>();
        for (String[] p : PersistenciaComercio.leerLineas(ruta("bonos.txt"))) {
            if (p.length >= 4) {
                usados.put(p[0], Boolean.parseBoolean(p[3]));
            }
        }
        for (Usuario u : autenticador.darUsuarios()) {
            for (BonoDescuento b : u.darBonos()) {
                Boolean usado = usados.get(b.getId());
                if (usado != null && usado) {
                    b.marcarUsado();
                }
            }
        }
    }

    public void guardarBonos(Autenticador autenticador) throws Exception {
        try (PrintWriter pw = abrir("bonos.txt")) {
            pw.println("# idBono;login;porcentaje;usado");
            for (Usuario u : autenticador.darUsuarios()) {
                for (BonoDescuento b : u.darBonos()) {
                    pw.println(b.getId() + ";" + u.getLogin() + ";" + b.getPorcentaje() + ";" + b.isUsado());
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Comercio (archivos del Proyecto 1)
    // ------------------------------------------------------------------

    public void cargarComercio(OperacionesComercio comercio, Autenticador autenticador) throws Exception {
        new PersistenciaComercio(carpeta).cargar(comercio, new ArrayList<>(autenticador.darUsuarios()));
    }

    public void guardarComercio(OperacionesComercio comercio) throws Exception {
        new PersistenciaComercio(carpeta).guardar(comercio);
    }
}
