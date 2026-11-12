package comercio;

import java.util.ArrayList;
import java.util.List;

public class Snack extends AlimentoCafeteria {
    private List<Alergeno> alergenos;

    public Snack(String nombre, double precio) {
        super(nombre, precio);
        this.alergenos = new ArrayList<>();
    }

    public List<Alergeno> getAlergenos() { return alergenos; }
    public void agregarAlergeno(Alergeno x) { this.alergenos.add(x); }
}
