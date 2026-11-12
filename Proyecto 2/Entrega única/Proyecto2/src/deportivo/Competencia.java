package deportivo;

import java.util.ArrayList;
import java.util.List;

public class Competencia {
    private String nombre;
    private List<InscripcionEquipo> equiposInscritos;
    private int totalPartidosProgramadosGrupo;
    private int partidosJugadosGrupo;
    private String faseActual;

    // Constructor simple (Usado en la Prueba 3)
    public Competencia(String nombre) {
        this.nombre = nombre;
        this.equiposInscritos = new ArrayList<>();
        this.totalPartidosProgramadosGrupo = 0;
        this.partidosJugadosGrupo = 0;
        this.faseActual = "Liga";
    }

    // Constructor avanzado con control de grupos (Usado en la Prueba 4)
    public Competencia(String nombre, int totalPartidosProgramadosGrupo) {
        this.nombre = nombre;
        this.equiposInscritos = new ArrayList<>();
        this.totalPartidosProgramadosGrupo = totalPartidosProgramadosGrupo;
        this.partidosJugadosGrupo = 0;
        this.faseActual = "Fase de Grupos";
    }

    public void inscribirEquipo(InscripcionEquipo inscripcion) {
        equiposInscritos.add(inscripcion);
    }

    public void registrarPartidoGrupo() {
        this.partidosJugadosGrupo++;
    }

    public boolean avanzarFase() {
        if (totalPartidosProgramadosGrupo > 0 && partidosJugadosGrupo < totalPartidosProgramadosGrupo) {
            return false; // Rechazado: Faltan partidos por jugar en el grupo
        }
        this.faseActual = "Eliminación Directa";
        return true;
    }

    public String getFaseActual() {
        return faseActual;
    }

    public int getPartidosRestantes() {
        return totalPartidosProgramadosGrupo - partidosJugadosGrupo;
    }

    public List<InscripcionEquipo> getEquiposInscritos() {
        return equiposInscritos;
    }

    public String getNombre() {
        return nombre;
    }
}