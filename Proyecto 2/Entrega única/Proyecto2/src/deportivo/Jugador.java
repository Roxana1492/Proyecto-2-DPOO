package deportivo;

import java.util.ArrayList;
import java.util.List;

import autenticacion.Empleado;
import autenticacion.Usuario;

public class Jugador implements ParticipanteTorneo {

    private Usuario usuario;

    public Jugador(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() { return usuario; }

    @Override
    public String darNombre() {
        return usuario.darNombre();
    }

    @Override
    public List<Usuario> darIntegrantes() {
        List<Usuario> lista = new ArrayList<>();
        lista.add(usuario);
        return lista;
    }

    @Override
    public double promedioElo(String disciplina) {
        return usuario.darElo(disciplina);
    }

    @Override
    public boolean incluyeEmpleado() {
        return usuario instanceof Empleado;
    }

    @Override
    public boolean contiene(String login) {
        return usuario.getLogin().equals(login);
    }
}
