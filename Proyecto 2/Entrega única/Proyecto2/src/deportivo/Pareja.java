package deportivo;

public class Pareja {
    private String idPareja;
    private Object jugador1; 
    private Object jugador2;

    public Pareja(String idPareja, Object jugador1, Object jugador2) {
        this.idPareja = idPareja;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
    }

    public String getIdPareja() { return idPareja; }
    public Object getJugador1() { return jugador1; }
    public Object getJugador2() { return jugador2; }
}