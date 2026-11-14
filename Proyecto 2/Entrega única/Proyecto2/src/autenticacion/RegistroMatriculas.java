package autenticacion;

import java.util.HashSet;
import java.util.Set;

/** Códigos de los estudiantes matriculados en la universidad (archivo matriculas.txt). */
public class RegistroMatriculas {

    private Set<String> codigos;

    public RegistroMatriculas() {
        this.codigos = new HashSet<>();
    }

    public void agregarCodigo(String codigo) {
        codigos.add(codigo);
    }

    public boolean estaMatriculado(String codigo) {
        return codigos.contains(codigo);
    }
}
