package deportivo;

import java.util.ArrayList;
import java.util.List;

import autenticacion.Socio;
import autenticacion.Usuario;

/** Equipo inscrito en un torneo de conjunto. Solo puede tener socios. */
public class EquipoTorneo implements ParticipanteTorneo {

    private String nombre;
    private Socio capitan;
    private List<Socio> integrantes;

    public EquipoTorneo(String nombre, Socio capitan, List<Socio> integrantes) {
        this.nombre = nombre;
        this.capitan = capitan;
        this.integrantes = new ArrayList<>(integrantes);
    }

    public String getNombre() { return nombre; }
    public Socio getCapitan() { return capitan; }
    public List<Socio> getIntegrantes() { return integrantes; }

    public boolean esCapitan(String login) {
        return capitan.getLogin().equals(login);
    }

    @Override
    public String darNombre() {
        return "Equipo " + nombre;
    }

    // El capitán también juega, entonces va de primero en la lista
    @Override
    public List<Usuario> darIntegrantes() {
        List<Usuario> lista = new ArrayList<>();
        lista.add(capitan);
        lista.addAll(integrantes);
        return lista;
    }

    @Override
    public double promedioElo(String disciplina) {
        List<Usuario> todos = darIntegrantes();
        double suma = 0;
        for (Usuario u : todos) {
            suma += u.darElo(disciplina);
        }
        return suma / todos.size();
    }

    // Un equipo es solo de socios
    @Override
    public boolean incluyeEmpleado() {
        return false;
    }

    @Override
    public boolean contiene(String login) {
        for (Usuario u : darIntegrantes()) {
            if (u.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }
}
