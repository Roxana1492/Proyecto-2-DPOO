package deportivo;

import java.util.Date;

public class Partido {
    protected Date fecha;
    protected int marcadorLocal;
    protected int marcadorVisitante;

    public Partido(Date fecha, int marcadorLocal, int marcadorVisitante) {
        this.fecha = fecha;
        this.marcadorLocal = marcadorLocal;
        this.marcadorVisitante = marcadorVisitante;
    }

    public Date getFecha() {
        return fecha;
    }

    public int getMarcadorLocal() {
        return marcadorLocal;
    }

    public int getMarcadorVisitante() {
        return marcadorVisitante;
    }
}