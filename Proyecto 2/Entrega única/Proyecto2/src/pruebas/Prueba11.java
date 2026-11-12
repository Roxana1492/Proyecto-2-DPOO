package pruebas;

import java.util.List;

import comercio.RegistroTurnos;
import deportivo.SolicitudCambioTurno;
import deportivo.Turno;

public class Prueba11 {

    private static void solicitar(String titulo, RegistroTurnos registro, Turno turno, String reemplazo) {
        System.out.println("\n" + titulo);
        System.out.println("  Turno " + turno.getIdTurno() + " (" + turno.getDiaSemana() + " " + turno.getFranjaHoraria()
                + ") de " + turno.getNombreEmpleado() + " -> reemplazo: " + reemplazo);

        SolicitudCambioTurno solicitud = new SolicitudCambioTurno("SOL-" + turno.getIdTurno(), turno,
                turno.getEmpleadoId(), reemplazo);
        registro.resolverSolicitud(solicitud);

        System.out.println("  Estado de la solicitud: " + solicitud.getEstado());
        System.out.println("  Titular del turno ahora: " + turno.getEmpleadoId());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 11: CAMBIO DE TURNO CON COBERTURA MÍNIMA (2 ENTRENADORES Y 1 FISIOTERAPEUTA)");
        System.out.println("==========================================================================================================");

        RegistroTurnos registro = new RegistroTurnos();

        // Sábado: 3 entrenadores y 1 fisioterapeuta
        Turno sab1 = new Turno("T1", "e1", "Entrenador 1", "Sabado", "08:00-12:00", "Gimnasio", "ENTRENADOR");
        Turno sab2 = new Turno("T2", "e2", "Entrenador 2", "Sabado", "08:00-12:00", "Gimnasio", "ENTRENADOR");
        Turno sab3 = new Turno("T3", "e3", "Entrenador 3", "Sabado", "08:00-12:00", "Gimnasio", "ENTRENADOR");
        Turno sabF = new Turno("T4", "f1", "Fisio 1", "Sabado", "08:00-12:00", "Fisioterapia", "FISIOTERAPEUTA");

        // Domingo: justo el mínimo (2 entrenadores y 1 fisioterapeuta)
        Turno dom1 = new Turno("T5", "e1", "Entrenador 1", "Domingo", "08:00-12:00", "Gimnasio", "ENTRENADOR");
        Turno dom2 = new Turno("T6", "e2", "Entrenador 2", "Domingo", "08:00-12:00", "Gimnasio", "ENTRENADOR");
        Turno domF = new Turno("T7", "f1", "Fisio 1", "Domingo", "08:00-12:00", "Fisioterapia", "FISIOTERAPEUTA");

        for (Turno t : new Turno[] { sab1, sab2, sab3, sabF, dom1, dom2, domF }) {
            registro.agregarTurno(t);
        }

        List<Turno> turnos = registro.getTurnos();
        System.out.println("\nTurnos registrados: " + turnos.size());

        solicitar("CASO 1: sábado, un entrenador cede su turno a un empleado libre (APROBADA)",
                registro, sab1, "e4");

        solicitar("CASO 2: domingo, el reemplazo ya trabaja ese turno y se quedaría sin cobertura (RECHAZADA)",
                registro, dom1, "e2");

        solicitar("CASO 3: domingo, el reemplazo es el fisioterapeuta de ese turno (RECHAZADA)",
                registro, dom1, "f1");

        solicitar("CASO 4: solicitante y reemplazo son la misma persona (RECHAZADA)",
                registro, sab2, "e2");

        System.out.println("\n==========================================================================================================");
        System.out.println("Esperado: caso 1 APROBADA (titular e4) | casos 2, 3 y 4 RECHAZADA (titular sin cambio)");
        System.out.println("==========================================================================================================");
    }
}
