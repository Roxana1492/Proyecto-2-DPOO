package deportivo;

import java.util.ArrayList;
import java.util.List;

public class Disciplina {
    private String nombre;
    private int maxInstalacionesSimultaneas;
    private List<InscripcionesDisciplina> inscripciones;

    public Disciplina(String nombre, int maxInstalacionesSimultaneas) {
        this.nombre = nombre;
        this.maxInstalacionesSimultaneas = maxInstalacionesSimultaneas;
        this.inscripciones = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getMaxInstalacionesSimultaneas() { return maxInstalacionesSimultaneas; }
    public void setMaxInstalacionesSimultaneas(int maxInstalacionesSimultaneas) { this.maxInstalacionesSimultaneas = maxInstalacionesSimultaneas; }
    public List<InscripcionesDisciplina> getInscripciones() { return inscripciones; }
    public void agregarInscripcionesDisciplina(InscripcionesDisciplina x) { this.inscripciones.add(x); }
}
