package deportivo;

import java.util.List;
import autenticacion.Socio;

public class InscripcionEquipo {
    private Equipo equipo;
    private List<Socio> nominaHabilitada;
    private int puntos;
    private int partidosJugados;
    private int partidosGanados;
    private int partidosEmpatados;
    private int partidosPerdidos;
    private int golesFavor;
    private int golesContra;

    public InscripcionEquipo(Equipo equipo, List<Socio> nominaHabilitada) {
        this.equipo = equipo;
        this.nominaHabilitada = nominaHabilitada;
        this.puntos = 0;
        this.partidosJugados = 0;
        this.partidosGanados = 0;
        this.partidosEmpatados = 0;
        this.partidosPerdidos = 0;
        this.golesFavor = 0;
        this.golesContra = 0;
    }

    public Equipo getEquipo() { return equipo; }
    public List<Socio> getNominaHabilitada() { return nominaHabilitada; }
    public int getPuntos() { return puntos; }
    public int getPartidosJugados() { return partidosJugados; }
    public int getPartidosGanados() { return partidosGanados; }
    public int getPartidosEmpatados() { return partidosEmpatados; }
    public int getPartidosPerdidos() { return partidosPerdidos; }
    public int getGolesFavor() { return golesFavor; }
    public int getGolesContra() { return golesContra; }
    public int getDiferenciaGoles() { return golesFavor - golesContra; }

    public void registrarResultado(int gf, int gc) {
        partidosJugados++;
        golesFavor += gf;
        golesContra += gc;
        if (gf > gc) {
            partidosGanados++;
            puntos += 3;
        } else if (gf == gc) {
            partidosEmpatados++;
            puntos += 1;
        } else {
            partidosPerdidos++;
        }
    }
}