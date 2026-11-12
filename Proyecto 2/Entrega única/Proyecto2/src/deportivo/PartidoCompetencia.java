package deportivo;

import java.util.Date;

public class PartidoCompetencia extends Partido {
    private String institucionRival;
    private String escenario;

    public PartidoCompetencia(Date fecha, int marcadorLocal, int marcadorVisitante, String institucionRival, String escenario) {
        super(fecha, marcadorLocal, marcadorVisitante);
        this.institucionRival = institucionRival;
        this.escenario = escenario;
    }

    public String getInstitucionRival() {
        return institucionRival;
    }

    public String getEscenario() {
        return escenario;
    }
}