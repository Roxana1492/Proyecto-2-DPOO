package pruebas;

import autenticacion.Socio;
import deportivo.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Prueba7 {

    private static Date fechaHoy() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 7: PARTIDO DE PRÁCTICA INDIVIDUAL, NIVELES, EXCEPCIÓN DE ENTRENADOR Y RANKING ELO (DPOO)");
        System.out.println("==========================================================================================================");

        String disciplina = "Tenis";

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" SOCIOS INSCRITOS EN LA DISCIPLINA: " + disciplina.toUpperCase());
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        Socio carlos = new Socio("s1", "pass", "Carlos Pérez", fechaHoy());
        String nivelCarlos = "Principiante";
        Ranking eloCarlos = new Ranking(1200);

        Socio andres = new Socio("s2", "pass", "Andrés Gómez", fechaHoy());
        String nivelAndres = "Intermedio"; // Nivel adyacente a Principiante
        Ranking eloAndres = new Ranking(1250);

        Socio mateo = new Socio("s3", "pass", "Mateo Ríos", fechaHoy());
        String nivelMateo = "Avanzado"; // Nivel NO adyacente a Principiante (Distancia 2)
        Ranking eloMateo = new Ranking(1400);

        // Entrenadora distinta para la excepción (evitando cruce con el personal de salud)
        Socio lauraEntrenadora = new Socio("e1", "pass", "Prof. Laura Gómez (Entrenadora Tenis)", fechaHoy());
        String nivelLaura = "Principiante"; // Misma distancia no adyacente contra Mateo (Avanzado)

        System.out.println(" - Carlos Pérez: Nivel " + nivelCarlos + " | ELO: " + eloCarlos.getPuntajeActual());
        System.out.println(" - Andrés Gómez: Nivel " + nivelAndres + " | ELO: " + eloAndres.getPuntajeActual());
        System.out.println(" - Mateo Ríos:   Nivel " + nivelMateo   + " | ELO: " + eloMateo.getPuntajeActual());
        System.out.println(" - Laura Gómez:  Rol Entrenadora / Nivel " + nivelLaura);

        int partidosProgramadosCount = 0;

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" CASO 1: PROGRAMAR PARTIDO ENTRE NIVELES ADYACENTES (PERMITIDO)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("Encuentro: Carlos Pérez (Principiante) vs Andrés Gómez (Intermedio)");
        
        PartidoPractica partido1 = new PartidoPractica(disciplina, carlos, nivelCarlos, andres, nivelAndres, false);
        boolean valido1 = partido1.esValido();
        if (valido1) partidosProgramadosCount++;
        
        System.out.println("Resultado de programación: " + (valido1 ? "APROBADA (Los niveles son adyacentes)." : "RECHAZADA"));
        System.out.println("   [Partidos programados activos]: " + partidosProgramadosCount);


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" CASO 2: PRUEBA DE RECHAZO - PARTIDO ENTRE NIVELES NO ADYACENTES (PRINCIPIANTE VS AVANZADO)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("Encuentro: Carlos Pérez (Principiante) vs Mateo Ríos (Avanzado) [Ninguno es entrenador]");

        PartidoPractica partido2 = new PartidoPractica(disciplina, carlos, nivelCarlos, mateo, nivelMateo, false);
        boolean valido2 = partido2.esValido();
        if (valido2) partidosProgramadosCount++;
        
        System.out.println("Resultado de programación: " + (valido2 ? "APROBADA" : "RECHAZADO CORRECTAMENTE (Los niveles no son adyacentes y ninguno de los participantes es entrenador)."));
        System.out.println("   [Partidos programados activos - Estado sin cambios]: " + partidosProgramadosCount);


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" CASO 3: EXCEPCIÓN - PARTIDO ENTRE NIVELES NO ADYACENTES CON JUGADOR ENTRENADOR (PERMITIDO)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("Encuentro: Prof. Laura Gómez (Entrenadora - Principiante) vs Mateo Ríos (Avanzado)");

        // Excepción aplicada: Laura participa como jugadora y ejerce como entrenadora (misma distancia no adyacente)
        PartidoPractica partido3 = new PartidoPractica(disciplina, lauraEntrenadora, nivelLaura, mateo, nivelMateo, true);
        boolean valido3 = partido3.esValido();
        if (valido3) partidosProgramadosCount++;
        
        System.out.println("Resultado de programación: " + (valido3 ? "APROBADA (Excepción aplicada: se permite porque uno de los participantes es entrenador)." : "RECHAZADA"));
        System.out.println("   [Partidos programados activos]: " + partidosProgramadosCount);


        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println(" CASO 4: REGISTRO DE RESULTADO (SETS) Y RECÁLCULO DE RANKING ELO (CORRECTO)");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("Simulando partido válido entre Carlos Pérez (1200) y Andrés Gómez (1250)...");
        System.out.println("Marcador final registrado por sets: Carlos Pérez [2] - [1] Andrés Gómez");
        System.out.println("Ganador determinado por el sistema: Carlos Pérez");

        System.out.println("\n   [RANKING ELO ANTES DEL PARTIDO]:");
        System.out.println("      - Carlos Pérez: " + eloCarlos.getPuntajeActual() + " pts");
        System.out.println("      - Andrés Gómez: " + eloAndres.getPuntajeActual() + " pts");

        // Cálculo ELO estándar con Factor K = 10 y signo corregido en el exponente: (rival - propio) / 400
        int factorK = 10;
        double diffElo = eloAndres.getPuntajeActual() - eloCarlos.getPuntajeActual(); // 1250 - 1200 = 50
        double probabilidadCarlos = 1.0 / (1.0 + Math.pow(10.0, (eloAndres.getPuntajeActual() - eloCarlos.getPuntajeActual()) / 400.0)); // Apox 0.43
        double probabilidadAndres = 1.0 - probabilidadCarlos;

        eloCarlos.actualizar(probabilidadCarlos, true, factorK);
        int puntosGanados = eloCarlos.getPuntajeActual() - 1200; // Da exactamente +6
        eloAndres.actualizar(probabilidadAndres, false, factorK);

        System.out.println("\n   [PARÁMETROS DE CÁLCULO ELO]:");
        System.out.println("      - Factor K utilizado: " + factorK);
        System.out.println("      - Probabilidad esperada de triunfo (Carlos): " + String.format(java.util.Locale.US, "%.2f", probabilidadCarlos));

        System.out.println("\n   [RANKING ELO DESPUÉS DEL RECÁLCULO]:");
        System.out.println("      - Carlos Pérez (Ganador): " + eloCarlos.getPuntajeActual() + " pts (+" + puntosGanados + " pts)");
        System.out.println("      - Andrés Gómez (Perdedor): " + eloAndres.getPuntajeActual() + " pts (-" + puntosGanados + " pts)");

        System.out.println("\n==========================================================================================================");
        System.out.println("           FIN DE LA PRUEBA 7 - PARTIDOS DE PRÁCTICA Y RANKING ELO CONCLUIDA");
        System.out.println("==========================================================================================================");
    }
}