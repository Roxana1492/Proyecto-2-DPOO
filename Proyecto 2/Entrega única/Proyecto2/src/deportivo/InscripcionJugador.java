package deportivo;

import autenticacion.Socio;

public class InscripcionJugador {
    private String posicion;
    private int numeroCamiseta;
    private Socio socio;
    private Equipo equipo;

    public InscripcionJugador(String posicion, int numeroCamiseta, Socio socio, Equipo equipo) {
        this.posicion = posicion;
        this.numeroCamiseta = numeroCamiseta;
        this.socio = socio;
        this.equipo = equipo;
    }

    public String getPosicion() {
        return posicion;
    }

    public int getNumeroCamiseta() {
        return numeroCamiseta;
    }

    public Socio getSocio() {
        return socio;
    }

    public Equipo getEquipo() {
        return equipo;
    }
}