package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import comercio.BonoDescuento;
import deportivo.Encuentro;
import deportivo.EquipoTorneo;
import deportivo.EstadoTorneo;
import deportivo.ModalidadTorneo;
import deportivo.TipoTorneo;
import deportivo.Torneo;

/**
 * Guarda y carga los torneos, sus inscritos, los resultados de los encuentros,
 * los premios entregados y los bonos de descuento.
 *
 * Archivos (en la carpeta datos/):
 *  - torneos.txt: id;nombre;tipo;modalidad;disciplina;fecha;edadMaxima;nivel;cupos;tarifa;estado
 *  - inscritos.txt: idTorneo;tipo(I, P o E);participante;preferencial;pago
 *  - encuentros.txt: idTorneo;idEncuentro;marcador1;marcador2;desempate1;desempate2
 *  - premios.txt: idTorneo;descripción del premio
 *  - bonos.txt: idBono;login;porcentaje;usado
 *
 * Para no guardar los cupos y el calendario a mano, al cargar se vuelven a
 * inscribir los participantes en el mismo orden, se inicia el torneo (el
 * calendario sale igual) y se registran otra vez los resultados.
 */
public class PersistenciaTorneos {

    private final String carpeta;

    public PersistenciaTorneos(String carpeta) {
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

    // ------------------------------------------------------------------
    // Torneos
    // ------------------------------------------------------------------

    public List<Torneo> cargarTorneos() throws Exception {
        Map<String, Torneo> torneos = new LinkedHashMap<>();
        Map<String, EstadoTorneo> estados = new LinkedHashMap<>();

        for (String[] p : PersistenciaComercio.leerLineas(ruta("torneos.txt"))) {
            if (p.length < 11) {
                continue;
            }
            int edadMaxima = Integer.parseInt(p[6]);
            String nivel = p[7].equals("-") ? null : p[7];
            Torneo t = Torneo.cargarDesdeArchivo(p[0], p[1], TipoTorneo.valueOf(p[2]),
                    ModalidadTorneo.valueOf(p[3]), p[4], LocalDate.parse(p[5]), edadMaxima, nivel,
                    Integer.parseInt(p[8]), Double.parseDouble(p[9]));
            torneos.put(t.getIdTorneo(), t);
            estados.put(t.getIdTorneo(), EstadoTorneo.valueOf(p[10]));
        }

        // Se vuelven a inscribir en el mismo orden en que estaban
        for (String[] p : PersistenciaComercio.leerLineas(ruta("inscritos.txt"))) {
            Torneo t = torneos.get(p[0]);
            if (t == null || p.length < 5) {
                continue;
            }
            Object participante = textoAParticipante(p[1], p[2]);
            boolean preferencial = Boolean.parseBoolean(p[3]);
            double pago = Double.parseDouble(p[4]);
            String capitan = null;
            if (participante instanceof EquipoTorneo) {
                capitan = ((EquipoTorneo) participante).getLoginCapitan();
            }
            // pago 0 = no pagó (empleado o recreativo). Con elo 1 y 1 partido se respeta el cupo preferencial
            t.procesarInscripcion(participante, true, pago == 0, 0, t.getNivelRequerido(),
                    preferencial, 1, 1, 0, capitan);
        }

        // Los torneos que ya habían empezado se inician otra vez para generar el calendario
        for (Torneo t : torneos.values()) {
            if (estados.get(t.getIdTorneo()) != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
                t.iniciarTorneo(t.getFechaInicio());
            }
        }

        for (String[] p : PersistenciaComercio.leerLineas(ruta("encuentros.txt"))) {
            Torneo t = torneos.get(p[0]);
            if (t == null || p.length < 6) {
                continue;
            }
            int m1 = Integer.parseInt(p[2]);
            int m2 = Integer.parseInt(p[3]);
            if (p[4].isEmpty()) {
                t.registrarResultadoEncuentro(p[1], m1, m2);
            } else {
                t.registrarDesempateEncuentro(p[1], m1, m2, Integer.parseInt(p[4]), Integer.parseInt(p[5]));
            }
        }

        for (Torneo t : torneos.values()) {
            t.setEstado(estados.get(t.getIdTorneo()));
        }
        return new ArrayList<>(torneos.values());
    }

    public void guardarTorneos(List<Torneo> torneos) throws Exception {
        try (PrintWriter pw = abrir("torneos.txt")) {
            pw.println("# id;nombre;tipo;modalidad;disciplina;fecha;edadMaxima;nivel;cupos;tarifa;estado");
            for (Torneo t : torneos) {
                int edad = t.getEdadMaxima() == null ? 0 : t.getEdadMaxima();
                String nivel = t.getNivelRequerido() == null ? "-" : t.getNivelRequerido();
                pw.println(t.getIdTorneo() + ";" + t.getNombre() + ";" + t.getTipo() + ";" + t.getModalidad()
                        + ";" + t.getDisciplina() + ";" + t.getFechaInicio() + ";" + edad + ";" + nivel
                        + ";" + t.getCuposTotales() + ";" + t.getTarifaInscripcion() + ";" + t.getEstado());
            }
        }

        try (PrintWriter pw = abrir("inscritos.txt")) {
            pw.println("# idTorneo;tipo(I individual, P pareja, E equipo);participante;preferencial;pago");
            for (Torneo t : torneos) {
                for (Object p : t.getInscritos()) {
                    pw.println(t.getIdTorneo() + ";" + participanteATexto(p) + ";" + t.esPreferencial(p)
                            + ";" + t.getPagoDe(p));
                }
            }
        }

        try (PrintWriter pw = abrir("encuentros.txt")) {
            pw.println("# idTorneo;idEncuentro;marcador1;marcador2;desempate1;desempate2");
            for (Torneo t : torneos) {
                for (Encuentro e : t.getCalendario()) {
                    if (e.isJugado()) {
                        String d1 = e.getDesempate1() == null ? "" : String.valueOf(e.getDesempate1());
                        String d2 = e.getDesempate2() == null ? "" : String.valueOf(e.getDesempate2());
                        pw.println(t.getIdTorneo() + ";" + e.getIdEncuentro() + ";" + e.getMarcador1() + ";"
                                + e.getMarcador2() + ";" + d1 + ";" + d2);
                    }
                }
            }
        }
    }

    // Individual: "I;login"  Pareja: "P;login1,login2"  Equipo: "E;nombre|capitan|int1,int2"
    @SuppressWarnings("unchecked")
    private String participanteATexto(Object p) {
        if (p instanceof EquipoTorneo) {
            EquipoTorneo e = (EquipoTorneo) p;
            return "E;" + e.getNombre() + "|" + e.getLoginCapitan() + "|" + String.join(",", e.getIntegrantes());
        }
        if (p instanceof List) {
            return "P;" + String.join(",", (List<String>) p);
        }
        return "I;" + p;
    }

    private Object textoAParticipante(String tipo, String texto) {
        if (tipo.equals("P")) {
            return new ArrayList<>(Arrays.asList(texto.split(",")));
        }
        if (tipo.equals("E")) {
            String[] partes = texto.split("\\|", -1);
            List<String> integrantes = new ArrayList<>();
            if (partes.length > 2 && !partes[2].isEmpty()) {
                integrantes.addAll(Arrays.asList(partes[2].split(",")));
            }
            return new EquipoTorneo(partes[0], partes[1], integrantes);
        }
        return texto;
    }

    // ------------------------------------------------------------------
    // Premios y bonos
    // ------------------------------------------------------------------

    public Map<String, List<String>> cargarPremios() throws Exception {
        Map<String, List<String>> premios = new LinkedHashMap<>();
        for (String[] p : PersistenciaComercio.leerLineas(ruta("premios.txt"))) {
            if (p.length < 2) {
                continue;
            }
            premios.computeIfAbsent(p[0], k -> new ArrayList<>()).add(p[1]);
        }
        return premios;
    }

    public void guardarPremios(Map<String, List<String>> premios) throws Exception {
        try (PrintWriter pw = abrir("premios.txt")) {
            pw.println("# idTorneo;premio");
            for (String id : premios.keySet()) {
                for (String premio : premios.get(id)) {
                    pw.println(id + ";" + premio);
                }
            }
        }
    }

    public List<BonoDescuento> cargarBonos() throws Exception {
        List<BonoDescuento> bonos = new ArrayList<>();
        for (String[] p : PersistenciaComercio.leerLineas(ruta("bonos.txt"))) {
            if (p.length < 4) {
                continue;
            }
            BonoDescuento b = new BonoDescuento(p[0], p[1], Double.parseDouble(p[2]));
            if (Boolean.parseBoolean(p[3])) {
                b.marcarUsado();
            }
            bonos.add(b);
        }
        return bonos;
    }

    public void guardarBonos(List<BonoDescuento> bonos) throws Exception {
        try (PrintWriter pw = abrir("bonos.txt")) {
            pw.println("# idBono;login;porcentaje;usado");
            for (BonoDescuento b : bonos) {
                pw.println(b.getIdBono() + ";" + b.getLoginSocio() + ";" + b.getPorcentajeDescuento() + ";"
                        + b.isUsado());
            }
        }
    }
}
