package deportivo;

public class SolicitudCambioTurno {
    private String idSolicitud;
    private Turno turnoOrigen;
    private String empleadoSolicitante;
    private String empleadoReemplazo;
    private String estado; // "PENDIENTE", "APROBADA", "RECHAZADA"

    public SolicitudCambioTurno(String idSolicitud, Turno turnoOrigen, String empleadoSolicitante, String empleadoReemplazo) {
        this.idSolicitud = idSolicitud;
        this.turnoOrigen = turnoOrigen;
        this.empleadoSolicitante = empleadoSolicitante;
        this.empleadoReemplazo = empleadoReemplazo;
        this.estado = "PENDIENTE";
    }

    public String getIdSolicitud() { return idSolicitud; }
    public Turno getTurnoOrigen() { return turnoOrigen; }
    public String getEmpleadoSolicitante() { return empleadoSolicitante; }
    public String getEmpleadoReemplazo() { return empleadoReemplazo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}