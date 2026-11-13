package comercio;

public class BonoDescuento {
    private String idBono;
    private String loginSocio;
    private double porcentajeDescuento;
    private boolean usado;

    public BonoDescuento(String idBono, String loginSocio, double porcentajeDescuento) {
        this.idBono = idBono;
        this.loginSocio = loginSocio;
        this.porcentajeDescuento = porcentajeDescuento;
        this.usado = false;
    }

    public String getIdBono() { return idBono; }
    public String getLoginSocio() { return loginSocio; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public boolean isUsado() { return usado; }
}