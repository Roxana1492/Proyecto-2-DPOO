package deportivo;

public class EstadisticaFutbol extends EstadisticaPartido {
    private int goles;
    private int asistencias;
    private int tarjetasAmarillas;
    private boolean tarjetaRoja;

    public EstadisticaFutbol(int goles, int asistencias, int tarjetasAmarillas, boolean tarjetaRoja) {
        this.goles = goles;
        this.asistencias = asistencias;
        this.tarjetasAmarillas = tarjetasAmarillas;
        this.tarjetaRoja = tarjetaRoja;
    }

    public int getGoles() { return goles; }
    public int getAsistencias() { return asistencias; }
    public int getTarjetasAmarillas() { return tarjetasAmarillas; }
    public boolean isTarjetaRoja() { return tarjetaRoja; }
}