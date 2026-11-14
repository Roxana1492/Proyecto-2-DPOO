package autenticacion;

import java.time.LocalDate;
import java.time.Period;

import deportivo.Nivel;

public class Empleado extends Usuario {
    private String nombre;
    private LocalDate fechaNacimiento;
    private Nivel nivel;

    public Empleado(String login, String password, String nombre) {
        super(login, password);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String darNombre() {
        return nombre;
    }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setNivel(Nivel nivel) { this.nivel = nivel; }

    @Override
    public Nivel darNivel() {
        return nivel;
    }

    @Override
    public int calcularEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
