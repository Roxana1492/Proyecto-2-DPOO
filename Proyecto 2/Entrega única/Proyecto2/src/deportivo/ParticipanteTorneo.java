package deportivo;

import java.util.List;

import autenticacion.Usuario;

/**
 * Lo que se inscribe en un torneo: un jugador, una pareja o un equipo.
 * Así Torneo, TablaTorneo y GeneradorCalendario no tienen que saber cuál es.
 */
public interface ParticipanteTorneo {

    String darNombre();

    List<Usuario> darIntegrantes();

    double promedioElo(String disciplina);

    boolean incluyeEmpleado();

    boolean contiene(String login);
}
