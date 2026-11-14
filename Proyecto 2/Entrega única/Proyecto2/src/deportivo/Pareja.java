package deportivo;

import java.util.ArrayList;
import java.util.List;

import autenticacion.Empleado;
import autenticacion.Usuario;

/** Pareja para torneos de dobles. Ocupa 2 cupos. */
public class Pareja implements ParticipanteTorneo {

    private Usuario jugador1;
    private Usuario jugador2;

    public Pareja(Usuario jugador1, Usuario jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
    }

    public Usuario getJugador1() { return jugador1; }
    public Usuario getJugador2() { return jugador2; }

    @Override
    public String darNombre() {
        return jugador1.darNombre() + " y " + jugador2.darNombre();
    }

    @Override
    public List<Usuario> darIntegrantes() {
        List<Usuario> lista = new ArrayList<>();
        lista.add(jugador1);
        lista.add(jugador2);
        return lista;
    }

    // En dobles se usa el promedio de ELO de los dos
    @Override
    public double promedioElo(String disciplina) {
        return (jugador1.darElo(disciplina) + jugador2.darElo(disciplina)) / 2.0;
    }

    @Override
    public boolean incluyeEmpleado() {
        return jugador1 instanceof Empleado || jugador2 instanceof Empleado;
    }

    @Override
    public boolean contiene(String login) {
        return jugador1.getLogin().equals(login) || jugador2.getLogin().equals(login);
    }
}
