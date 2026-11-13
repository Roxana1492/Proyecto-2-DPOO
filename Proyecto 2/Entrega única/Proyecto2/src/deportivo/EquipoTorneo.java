package deportivo;

import java.util.ArrayList;
import java.util.List;

/**
 * Equipo inscrito en un torneo de modalidad Conjunto.
 * Se guarda el login del capitán y de los demás integrantes.
 */
public class EquipoTorneo {

    private String nombre;
    private String loginCapitan;
    private List<String> integrantes;

    public EquipoTorneo(String nombre, String loginCapitan, List<String> integrantes) {
        this.nombre = nombre;
        this.loginCapitan = loginCapitan;
        this.integrantes = new ArrayList<>(integrantes);
    }

    public String getNombre() { return nombre; }
    public String getLoginCapitan() { return loginCapitan; }
    public List<String> getIntegrantes() { return integrantes; }

    // El capitán también cuenta como integrante
    public boolean tieneIntegrante(String login) {
        return loginCapitan.equals(login) || integrantes.contains(login);
    }

    public List<String> todosLosLogins() {
        List<String> logins = new ArrayList<>();
        logins.add(loginCapitan);
        logins.addAll(integrantes);
        return logins;
    }
}
