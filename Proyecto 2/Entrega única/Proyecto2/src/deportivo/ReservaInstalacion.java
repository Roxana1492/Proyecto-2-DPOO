package deportivo;

import autenticacion.Socio;
import java.util.List;

public class ReservaInstalacion {
    private Instalacion instalacion;
    private Socio socioResponsable;
    private String bloqueHorario;
    private List<Socio> jugadores;
    private boolean tieneBebidaCaliente;

    public ReservaInstalacion(Instalacion instalacion, Socio socioResponsable, String bloqueHorario, List<Socio> jugadores, boolean tieneBebidaCaliente) {
        this.instalacion = instalacion;
        this.socioResponsable = socioResponsable;
        this.bloqueHorario = bloqueHorario;
        this.jugadores = jugadores;
        this.tieneBebidaCaliente = tieneBebidaCaliente;
    }

    public Instalacion getInstalacion() { return instalacion; }
    public String getBloqueHorario() { return bloqueHorario; }
    public Socio getSocioResponsable() { return socioResponsable; }
    public List<Socio> getJugadores() { return jugadores; }
    public boolean tieneBebidaCaliente() { return tieneBebidaCaliente; }
}