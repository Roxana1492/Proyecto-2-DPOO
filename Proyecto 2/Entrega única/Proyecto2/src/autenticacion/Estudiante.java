package autenticacion;

import java.time.LocalDate;

public class Estudiante extends Usuario { 
    private String documentoIdentidad;
    private LocalDate fechaNacimiento;
    private String nivelTecnico;
    private String codigoEstudiantil;

    public Estudiante(String login, String password, String nombre, String documentoIdentidad,
                      LocalDate fechaNacimiento, String nivelTecnico, String codigoEstudiantil) {
        super(login, password); 
        this.documentoIdentidad = documentoIdentidad;
        this.fechaNacimiento = fechaNacimiento;
        this.nivelTecnico = nivelTecnico;
        this.codigoEstudiantil = codigoEstudiantil;
    }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getNivelTecnico() { return nivelTecnico; }
    public String getCodigoEstudiantil() { return codigoEstudiantil; }
}
