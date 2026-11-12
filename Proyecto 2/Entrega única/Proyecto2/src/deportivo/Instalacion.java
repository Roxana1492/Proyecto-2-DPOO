package deportivo;

public class Instalacion {
    private String nombre;
    private String disciplina;
    private boolean techada;
    private boolean restringeBebidas;
    private int capacidadRequeridaModalidad;

    public Instalacion(String nombre, String disciplina, boolean techada, boolean restringeBebidas, int capacidadRequeridaModalidad) {
        this.nombre = nombre;
        this.disciplina = disciplina;
        this.techada = techada;
        this.restringeBebidas = restringeBebidas;
        this.capacidadRequeridaModalidad = capacidadRequeridaModalidad;
    }

    public boolean restringeBebidasCalientes() {
        return techada && restringeBebidas;
    }

    public String getNombre() { 
        return nombre; 
    }
    
    public String getDisciplina() { 
        return disciplina; 
    }
    
    public int getCapacidadRequeridaModalidad() { 
        return capacidadRequeridaModalidad; 
    }
}