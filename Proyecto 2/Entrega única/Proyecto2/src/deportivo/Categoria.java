package deportivo;

/**
 * Categoría de un torneo: por edad máxima o por nivel técnico.
 * Si un campo es null, esa restricción no aplica.
 */
public class Categoria {

    private Integer edadMaxima;
    private Nivel nivel;

    public Categoria(Integer edadMaxima, Nivel nivel) {
        this.edadMaxima = edadMaxima;
        this.nivel = nivel;
    }

    public boolean admite(int edad, Nivel nivelJugador) {
        if (edadMaxima != null && edad > edadMaxima) {
            return false;
        }
        if (nivel != null && nivel != nivelJugador) {
            return false;
        }
        return true;
    }

    public Integer getEdadMaxima() { return edadMaxima; }
    public Nivel getNivel() { return nivel; }

    public String describir() {
        if (edadMaxima != null) {
            return "hasta " + edadMaxima + " años";
        }
        if (nivel != null) {
            return "nivel " + nivel;
        }
        return "libre";
    }
}
