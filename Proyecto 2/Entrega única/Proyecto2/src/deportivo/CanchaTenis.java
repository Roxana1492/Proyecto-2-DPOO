package deportivo;

public class CanchaTenis extends Instalacion {
    private String modalidad;

    public CanchaTenis(String nombre, String disciplina, boolean techada, boolean restringeBebidas, int capacidadRequeridaModalidad, String modalidad) {
        super(nombre, disciplina, techada, restringeBebidas, capacidadRequeridaModalidad);
        this.modalidad = modalidad;
    }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
}
