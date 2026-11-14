package autenticacion;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Autenticador {

    private Map<String, Usuario> usuarios;

    public Autenticador() {
        usuarios = new LinkedHashMap<>();
    }

    public void registrarUsuario(Usuario usuario) {
        if (usuario != null && usuario.getLogin() != null) {
            usuarios.put(usuario.getLogin(), usuario);
        }
    }

    public Usuario autenticar(String login, String password) {
        if (login == null || password == null) {
            return null;
        }

        Usuario usuario = usuarios.get(login);

        if (usuario != null && password.equals(usuario.getPassword())) {
            return usuario;
        }

        return null;
    }

    public boolean existeLogin(String login) {
        return usuarios.containsKey(login);
    }

    public boolean existeDocumento(String documento) {
        for (Usuario u : usuarios.values()) {
            if (u instanceof Estudiante && ((Estudiante) u).getDocumentoIdentidad().equals(documento)) {
                return true;
            }
            if (u instanceof Externo && ((Externo) u).getDocumentoIdentidad().equals(documento)) {
                return true;
            }
        }
        return false;
    }

    public Usuario buscar(String login) {
        return usuarios.get(login);
    }

    public Collection<Usuario> darUsuarios() {
        return usuarios.values();
    }
}
