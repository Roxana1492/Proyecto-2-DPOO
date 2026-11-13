package autenticacion;

import java.time.LocalDate;

public class Externo extends Usuario {
    private String nombre;
    private String documentoIdentidad;
    private LocalDate fechaNacimiento;
    private String nivelTecnico;

    public Externo(String login, String password, String nombre, String documentoIdentidad,
                   LocalDate fechaNacimiento, String nivelTecnico) {
        super(login, password);
        this.nombre = nombre;
        this.documentoIdentidad = documentoIdentidad;
        this.fechaNacimiento = fechaNacimiento;
        this.nivelTecnico = nivelTecnico;
    }

    public String getNombre() { return nombre; }
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getNivelTecnico() { return nivelTecnico; }
}
