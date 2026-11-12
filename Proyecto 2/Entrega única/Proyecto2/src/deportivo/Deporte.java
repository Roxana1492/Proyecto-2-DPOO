package deportivo;

public class Deporte {
    private String nombre;
    private int minJugadoresHabilitados;

    public Deporte(String nombre, int minJugadoresHabilitados) {
        this.nombre = nombre;
        this.minJugadoresHabilitados = minJugadoresHabilitados;
    }

    public String getNombre() {
        return nombre;
    }

    public int getMinJugadoresHabilitados() {
        return minJugadoresHabilitados;
    }
}