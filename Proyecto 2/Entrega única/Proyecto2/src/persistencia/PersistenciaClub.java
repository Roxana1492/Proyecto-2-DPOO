package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import autenticacion.Administrador;
import autenticacion.Empleado;
import autenticacion.Entrenador;
import autenticacion.Estudiante;
import autenticacion.Externo;
import autenticacion.Fisioterapeuta;
import autenticacion.Socio;
import autenticacion.Usuario;

/**
 * Carga y guarda los datos nuevos del Proyecto 2 en la carpeta datos/.
 * Maneja los usuarios del Proyecto 1 (administrador, socios y empleados) y
 * los nuevos del Proyecto 2 (estudiantes y externos).
 * Los torneos, bonos y premios se guardan en PersistenciaTorneos.
 */
public class PersistenciaClub {

    private final String carpeta;

    public PersistenciaClub(String carpeta) {
        this.carpeta = carpeta;
    }

    private String ruta(String archivo) {
        return carpeta + File.separator + archivo;
    }

    // Formato: tipo;login;password;nombre;fechaNacimiento(yyyy-MM-dd)
    // tipo: ADMINISTRADOR, SOCIO, ENTRENADOR, FISIOTERAPEUTA o EMPLEADO
    // Estudiantes y externos tienen además: ;documento;nivel;codigoEstudiantil
    public List<Usuario> cargarUsuarios() throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String[] p : PersistenciaComercio.leerLineas(ruta("usuarios.txt"))) {
            if (p.length < 4) {
                continue;
            }
            String tipo = p[0].toUpperCase();
            switch (tipo) {
                case "ADMINISTRADOR":
                    usuarios.add(new Administrador(p[1], p[2]));
                    break;
                case "SOCIO":
                    Date nacimiento = (p.length > 4 && !p[4].isEmpty()) ? sdf.parse(p[4]) : new Date();
                    usuarios.add(new Socio(p[1], p[2], p[3], nacimiento));
                    break;
                case "ENTRENADOR":
                    usuarios.add(new Entrenador(p[1], p[2], p[3]));
                    break;
                case "FISIOTERAPEUTA":
                    usuarios.add(new Fisioterapeuta(p[1], p[2], p[3]));
                    break;
                case "EMPLEADO":
                    usuarios.add(new Empleado(p[1], p[2], p[3]));
                    break;
                case "ESTUDIANTE":
                    if (p.length >= 8) {
                        usuarios.add(new Estudiante(p[1], p[2], p[3], p[5], LocalDate.parse(p[4]), p[6], p[7]));
                    }
                    break;
                case "EXTERNO":
                    if (p.length >= 7) {
                        usuarios.add(new Externo(p[1], p[2], p[3], p[5], LocalDate.parse(p[4]), p[6]));
                    }
                    break;
                default:
                    // Tipo que todavía no se maneja: se ignora la línea
                    break;
            }
        }
        return usuarios;
    }

    public void guardarUsuarios(Collection<Usuario> usuarios) throws Exception {
        File archivo = new File(ruta("usuarios.txt"));
        if (archivo.getParentFile() != null) {
            archivo.getParentFile().mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("# tipo;login;password;nombre;fechaNacimiento");
            for (Usuario u : usuarios) {
                if (u instanceof Administrador) {
                    pw.println("ADMINISTRADOR;" + u.getLogin() + ";" + u.getPassword() + ";Administrador;");
                } else if (u instanceof Socio) {
                    Socio s = (Socio) u;
                    pw.println("SOCIO;" + s.getLogin() + ";" + s.getPassword() + ";" + s.getNombre() + ";"
                            + sdf.format(s.getFechaNacimiento()));
                } else if (u instanceof Entrenador) {
                    pw.println("ENTRENADOR;" + u.getLogin() + ";" + u.getPassword() + ";"
                            + ((Empleado) u).getNombre() + ";");
                } else if (u instanceof Fisioterapeuta) {
                    pw.println("FISIOTERAPEUTA;" + u.getLogin() + ";" + u.getPassword() + ";"
                            + ((Empleado) u).getNombre() + ";");
                } else if (u instanceof Estudiante) {
                    Estudiante e = (Estudiante) u;
                    pw.println("ESTUDIANTE;" + e.getLogin() + ";" + e.getPassword() + ";" + e.getNombre() + ";"
                            + e.getFechaNacimiento() + ";" + e.getDocumentoIdentidad() + ";"
                            + e.getNivelTecnico() + ";" + e.getCodigoEstudiantil());
                } else if (u instanceof Externo) {
                    Externo e = (Externo) u;
                    pw.println("EXTERNO;" + e.getLogin() + ";" + e.getPassword() + ";" + e.getNombre() + ";"
                            + e.getFechaNacimiento() + ";" + e.getDocumentoIdentidad() + ";"
                            + e.getNivelTecnico());
                } else if (u instanceof Empleado) {
                    pw.println("EMPLEADO;" + u.getLogin() + ";" + u.getPassword() + ";"
                            + ((Empleado) u).getNombre() + ";");
                }
            }
        }
    }
}
