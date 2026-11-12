package comercio;

public class Bebida extends AlimentoCafeteria {
    private String tipo;

    public Bebida(String nombre, double precio, String tipo) {
        super(nombre, precio);
        this.tipo = tipo;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
