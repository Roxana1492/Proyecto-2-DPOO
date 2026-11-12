package deportivo;

import java.util.Comparator;
import java.util.List;

public class TablaPosiciones {
    private List<InscripcionEquipo> inscripcionesEquipos;
    private String titulo;

    public TablaPosiciones(List<InscripcionEquipo> inscripcionesEquipos) {
        this.inscripcionesEquipos = inscripcionesEquipos;
        this.titulo = "TABLA DE POSICIONES - LIGA OFICIAL";
    }

    public TablaPosiciones(List<InscripcionEquipo> inscripcionesEquipos, String titulo) {
        this.inscripcionesEquipos = inscripcionesEquipos;
        this.titulo = titulo;
    }

    public void calcularYOrdenar() {
       
        inscripcionesEquipos.sort(
            Comparator.comparingInt(InscripcionEquipo::getPuntos)
                      .thenComparingInt(InscripcionEquipo::getDiferenciaGoles)
                      .thenComparingInt(InscripcionEquipo::getGolesFavor)
                      .reversed()
        );
    }

    public void mostrarTabla() {
        calcularYOrdenar();
        System.out.println("==========================================================================================================");
        System.out.println(" " + titulo);
        System.out.println("==========================================================================================================");
        System.out.printf("%-20s | %-4s | %-4s | %-4s | %-4s | %-4s | %-4s | %-5s | %-6s%n", "Equipo", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "Puntos");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        for (InscripcionEquipo ie : inscripcionesEquipos) {
            System.out.printf("%-20s | %-4d | %-4d | %-4d | %-4d | %-4d | %-4d | %-5d | %-6d%n",
                ie.getEquipo().getNombre(),
                ie.getPartidosJugados(),
                ie.getPartidosGanados(),
                ie.getPartidosEmpatados(),
                ie.getPartidosPerdidos(),
                ie.getGolesFavor(),
                ie.getGolesContra(),
                ie.getDiferenciaGoles(),
                ie.getPuntos()
            );
        }
        System.out.println("==========================================================================================================");
    }
}