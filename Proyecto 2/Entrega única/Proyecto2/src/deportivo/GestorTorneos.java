package deportivo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import autenticacion.Empleado;
import autenticacion.Usuario;
import comercio.RegistroTurnos;
import fachada.OperacionRechazadaException;

/**
 * Guarda los torneos y toma las decisiones que necesitan mirar más de un
 * torneo: si un participante va por cupo preferencial (ranking) y si un
 * empleado tiene turno el día del torneo.
 */
public class GestorTorneos {

    private Map<String, Torneo> torneos;
    private int consecutivo;
    private int topK;
    private RegistroTurnos registroTurnos;

    public GestorTorneos(RegistroTurnos registroTurnos) {
        this.torneos = new LinkedHashMap<>();
        this.consecutivo = 1;
        this.topK = 5;
        this.registroTurnos = registroTurnos;
    }

    // HU-T01
    public Torneo crearTorneo(String nombre, String tipo, Modalidad modalidad, String disciplina, LocalDate fecha,
            Categoria categoria, int cuposTotales, double tarifa) {
        if (fecha == null || fecha.isBefore(LocalDate.now())) {
            throw new OperacionRechazadaException("La fecha del torneo no puede ser anterior a hoy.");
        }
        String id = "T" + consecutivo;
        Torneo t;
        if (tipo.equals("COMPETITIVO")) {
            t = new TorneoCompetitivo(id, nombre, disciplina, modalidad, fecha, categoria, cuposTotales, tarifa);
        } else {
            t = new TorneoRecreativo(id, nombre, disciplina, modalidad, fecha, categoria, cuposTotales);
        }
        agregar(t);
        return t;
    }

    /** Agrega un torneo ya creado (lo usa la persistencia al cargar). */
    public void agregar(Torneo t) {
        torneos.put(t.getId(), t);
        int numero = Integer.parseInt(t.getId().substring(1));
        if (numero >= consecutivo) {
            consecutivo = numero + 1;
        }
    }

    public Torneo buscar(String id) {
        Torneo t = torneos.get(id);
        if (t == null) {
            throw new OperacionRechazadaException("El torneo " + id + " no existe.");
        }
        return t;
    }

    public List<Torneo> listar() {
        return new ArrayList<>(torneos.values());
    }

    /**
     * HU-T03: va por cupo preferencial si su ELO (promedio en parejas y equipos)
     * está dentro de los topK mejores de la disciplina. Sin historial el ELO es 0
     * y nunca es preferencial.
     */
    public boolean esCupoPreferencial(ParticipanteTorneo p, Torneo t) {
        double elo = p.promedioElo(t.getDisciplina());
        if (elo <= 0) {
            return false;
        }
        // Se juntan los ELO de todos los que han jugado torneos de esa disciplina
        List<Usuario> jugadores = new ArrayList<>();
        for (Torneo otro : torneos.values()) {
            if (otro.getDisciplina().equalsIgnoreCase(t.getDisciplina())) {
                for (ParticipanteTorneo participante : otro.darParticipantes()) {
                    for (Usuario u : participante.darIntegrantes()) {
                        if (!jugadores.contains(u) && u.darElo(t.getDisciplina()) > 0) {
                            jugadores.add(u);
                        }
                    }
                }
            }
        }
        // Cuántos tienen un ELO mayor: si son menos que topK, está en el top
        int mejores = 0;
        for (Usuario u : jugadores) {
            if (u.darElo(t.getDisciplina()) > elo) {
                mejores++;
            }
        }
        return mejores < topK;
    }

    /** HU-T06: el empleado no puede tener turno el día de la semana del torneo. */
    public boolean empleadoLibre(Empleado e, LocalDate fecha) {
        String dia = nombreDia(fecha.getDayOfWeek());
        for (Turno turno : registroTurnos.getTurnos()) {
            if (turno.getEmpleadoId().equals(e.getLogin()) && sinTildes(turno.getDiaSemana()).equalsIgnoreCase(dia)) {
                return false;
            }
        }
        return true;
    }

    private String nombreDia(DayOfWeek dia) {
        switch (dia) {
            case MONDAY: return "Lunes";
            case TUESDAY: return "Martes";
            case WEDNESDAY: return "Miercoles";
            case THURSDAY: return "Jueves";
            case FRIDAY: return "Viernes";
            case SATURDAY: return "Sabado";
            default: return "Domingo";
        }
    }

    private String sinTildes(String texto) {
        return texto.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                .replace("Á", "A").replace("É", "E");
    }

    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
}
