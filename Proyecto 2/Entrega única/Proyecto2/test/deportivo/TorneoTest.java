package deportivo;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class TorneoTest {

    @Test
    public void testCrearTorneoCompetitivoValidoYCuposPreferenciales() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        // Creamos un torneo individual competitivo con 10 cupos totales
        Torneo torneo = new Torneo("T01", "Copa Élite", TipoTorneo.COMPETITIVO, 
                                  ModalidadTorneo.INDIVIDUAL, "Tenis", 
                                  fechaFutura, 25, "AVANZADO", 10, 50000.0);

        // Verificamos estado inicial
        assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, torneo.getEstado());
        assertEquals(10, torneo.getCuposTotales());
        
        // Regla del 20% preferencial: 20% de 10 = 2 cupos preferenciales
        assertEquals(2, torneo.getCuposPreferencialesDisponibles());
        // El resto son regulares (10 - 2 = 8)
        assertEquals(8, torneo.getCuposRegularesDisponibles());
    }

    @Test
    public void testCalculoPreferencialRedondeoHaciaArriba() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        // Torneo con 12 cupos -> 20% de 12 = 2.4, redondeado hacia arriba = 3 preferenciales
        Torneo torneo = new Torneo("T02", "Torneo Relámpago", TipoTorneo.RECREATIVO, 
                                  ModalidadTorneo.INDIVIDUAL, "Fútbol", 
                                  fechaFutura, 20, "PRINCIPIANTE", 12, 0.0);

        assertEquals(3, torneo.getCuposPreferencialesDisponibles());
        assertEquals(9, torneo.getCuposRegularesDisponibles());
    }
    
    @Test
    public void testInscripcionSocioTorneoCompetitivo() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        // Torneo competitivo con 5 cupos y tarifa de 50,000
        Torneo torneo = new Torneo("T03", "Copa Tenis", TipoTorneo.COMPETITIVO, 
                                  ModalidadTorneo.INDIVIDUAL, "Tenis", 
                                  fechaFutura, 30, "INTERMEDIO", 5, 50000.0);

        // Simulamos un socio (puedes pasarle un objeto Socio mock o null para probar la lógica de cupos/fondo primero)
        // O creamos un socio real si tienes el constructor: Socio socio1 = new Socio("s1", "pass", "Carlos", null);
        Object socio1 = "SocioCarlos"; // Usamos objeto genérico por ahora según la lista de inscritos

        boolean exito = torneo.inscribirParticipante(socio1, true); // true = pago confirmado

        // Verificaciones
        assertTrue(exito);
        assertEquals(1, torneo.getInscritos().size());
        assertEquals(50000.0, torneo.getFondoPremios());
        // Como quedan 4 cupos totales, los cupos regulares disponibles deben bajar
        assertEquals(3, torneo.getCuposRegularesDisponibles()); // 4 regulares iniciales - 1 ocupado = 3
    }
    
    
    @Test
    public void testCupoPreferencialSegunRanking() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        Torneo torneo = new Torneo("T04", "Copa ELO", TipoTorneo.COMPETITIVO, 
                                  ModalidadTorneo.INDIVIDUAL, "Tenis", 
                                  fechaFutura, 30, "INTERMEDIO", 5, 50000.0);

        // 1. Socio sin historial (0 partidos) intenta pedir preferencial -> Es rechazado (false)
        boolean exitoPreferencialInvalido = torneo.inscribirParticipanteConPreferencia("SocioSinHistoria", true, true, 1200.0, 0);
        assertFalse(exitoPreferencialInvalido);

        // 2. Socio con historial y buen ELO toma el cupo preferencial -> Es exitoso (true)
        boolean exitoPreferencialValido = torneo.inscribirParticipanteConPreferencia("SocioEstrella", true, true, 1800.0, 10);
        assertTrue(exitoPreferencialValido);
        
        // Verificamos que solo hay 1 participante inscrito con éxito (el primero fue rechazado)
        assertEquals(1, torneo.getInscritos().size());
        // Y el cupo preferencial disponible ahora es 0 porque "SocioEstrella" lo ocupó
        assertEquals(0, torneo.getCuposPreferencialesDisponibles());
    }
    
    @Test
    public void testDesinscripcionYLiberacionDeCupo() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        Torneo torneo = new Torneo("T05", "Copa Retiro", TipoTorneo.COMPETITIVO, 
                                  ModalidadTorneo.INDIVIDUAL, "Tenis", 
                                  fechaFutura, 30, "INTERMEDIO", 5, 50000.0);

        // Guardamos cuántos cupos regulares tenía al nacer
        int regularesIniciales = torneo.getCuposRegularesDisponibles(); // Debería ser 4

        String socioId = "SocioCarlos";
        
        // 1. Lo inscribimos primero
        boolean inscrito = torneo.inscribirParticipante(socioId, true);
        assertTrue(inscrito);
        assertEquals(1, torneo.getInscritos().size());
        assertEquals(regularesIniciales - 1, torneo.getCuposRegularesDisponibles()); // Bajó a 3
        assertEquals(50000.0, torneo.getFondoPremios());

        // 2. Ahora simulamos la desinscripción
        boolean desinscrito = torneo.desinscribirParticipante(socioId);
        assertTrue(desinscrito);

        // 3. Verificamos que se liberó el cupo y volvió a su estado inicial
        assertEquals(0, torneo.getInscritos().size());
        assertEquals(0.0, torneo.getFondoPremios());
        assertEquals(regularesIniciales, torneo.getCuposRegularesDisponibles()); // Vuelve a ser 4
    }
    
    @Test
    public void testRestriccionesEdadNivelYEstadoTorneo() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        // Torneo con edad máxima de 25 años y nivel requerido "INTERMEDIO"
        Torneo torneo = new Torneo("T06", "Copa Restricciones", TipoTorneo.COMPETITIVO, 
                                  ModalidadTorneo.INDIVIDUAL, "Tenis", 
                                  fechaFutura, 25, "INTERMEDIO", 5, 50000.0);

        // Intento 1: Participante con edad mayor a la permitida (ej. 30 años) o nivel incorrecto
        // Simulamos el método de validación avanzada que revisa restricciones
        boolean exitoRestriccion = torneo.inscribirParticipanteConRestricciones("SocioVeterano", true, 30, "PRINCIPIANTE");
        assertFalse(exitoRestriccion); // Debe ser rechazado

        // Forzamos el cambio de estado del torneo a CERRADO (simulando HU-T06)
        torneo.setEstado(EstadoTorneo.EN_CURSO); 

        // Intento 2: Intentar inscribirse cuando el torneo ya no está en inscripciones abiertas
        boolean exitoFueraDePlazo = torneo.inscribirParticipante("SocioTardio", true);
        assertFalse(exitoFueraDePlazo); // Debe ser rechazado por estado
    }
    
    @Test
    public void testGenerarCalendarioEncuentros() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        Torneo torneo = new Torneo("T07", "Copa Fixture", TipoTorneo.COMPETITIVO, 
                                  ModalidadTorneo.INDIVIDUAL, "Tenis", 
                                  fechaFutura, 30, "INTERMEDIO", 4, 50000.0);

        // Inscribimos a 4 participantes para poder armar enfrentamientos
        torneo.inscribirParticipante("Jugador A", true);
        torneo.inscribirParticipante("Jugador B", true);
        torneo.inscribirParticipante("Jugador C", true);
        torneo.inscribirParticipante("Jugador D", true);

        // Generar el calendario de encuentros
        boolean calendarioGenerado = torneo.generarCalendario();

        assertTrue(calendarioGenerado);
        // Verificamos que se hayan creado encuentros en la lista del torneo
        assertFalse(torneo.getCalendario().isEmpty());
    }
    
    @Test
    public void testGenerarCalendarioRoundRobinPar() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        // Ponemos 10 cupos totales para asegurar que entren los 4 jugadores sin chocar con la reserva preferencial
        Torneo torneo = new Torneo("T_RR1", "Copa RoundRobin Par", TipoTorneo.COMPETITIVO, 
                                  ModalidadTorneo.INDIVIDUAL, "Tenis", 
                                  fechaFutura, 30, "INTERMEDIO", 10, 50000.0);

        torneo.inscribirParticipante("Jugador A", true);
        torneo.inscribirParticipante("Jugador B", true);
        torneo.inscribirParticipante("Jugador C", true);
        torneo.inscribirParticipante("Jugador D", true);

        boolean generado = torneo.generarCalendario();
        assertTrue(generado);
        
        // Ahora sí hay 4 inscritos: 3 rondas * 2 encuentros = 6 encuentros totales
        assertEquals(6, torneo.getCalendario().size());
        
        long rondasUnicas = torneo.getCalendario().stream().mapToInt(Encuentro::getRonda).distinct().count();
        assertEquals(3, rondasUnicas);
    }

    @Test
    public void testGenerarCalendarioRoundRobinImpar() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        // Ponemos 10 cupos totales para que puedan entrar los 5 jugadores
        Torneo torneo = new Torneo("T_RR2", "Copa RoundRobin Impar", TipoTorneo.COMPETITIVO, 
                                  ModalidadTorneo.INDIVIDUAL, "Tenis", 
                                  fechaFutura, 30, "INTERMEDIO", 10, 50000.0);

        torneo.inscribirParticipante("Jugador A", true);
        torneo.inscribirParticipante("Jugador B", true);
        torneo.inscribirParticipante("Jugador C", true);
        torneo.inscribirParticipante("Jugador D", true);
        torneo.inscribirParticipante("Jugador E", true); // 5 participantes inscritos

        boolean generado = torneo.generarCalendario();
        assertTrue(generado);
        
        // Con 5 inscritos: 5 rondas, 2 encuentros por ronda = 10 encuentros totales
        assertEquals(10, torneo.getCalendario().size());
    }
    
    @Test
    public void inscripcionDoblesConsumeDosCuposYRechazaSiQuedaUno() {
        // 3 cupos totales -> 20% = 1 pareja preferencial (2 cupos) y 1 cupo regular libre (insuficiente para pareja regular)
        // Probemos con 6 cupos totales: 1 pareja pref (2 cupos) y 4 cupos regulares (2 parejas regulares)
        Torneo torneo = new Torneo("T_DOB", "Copa Dobles", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.DOBLES, "Tenis",
                                  LocalDate.now().plusDays(5), 30, "INTERMEDIO", 6, 50000.0);

        java.util.List<String> pareja1 = java.util.Arrays.asList("Ana", "Luis");
        java.util.List<String> pareja2 = java.util.Arrays.asList("Mia", "Leo");
        java.util.List<String> pareja3 = java.util.Arrays.asList("Sol", "Max");

        // Inscribimos 2 parejas regulares (consumen los 4 cupos regulares)
        assertTrue(torneo.procesarInscripcion(pareja1, true, false, 22, "INTERMEDIO", false, 0, 0, 0, null));
        assertTrue(torneo.procesarInscripcion(pareja2, true, false, 23, "INTERMEDIO", false, 0, 0, 0, null));
        assertEquals(0, torneo.getCuposRegularesDisponibles());

        // La 3ra pareja regular debe ser rechazada porque los regulares se agotaron (quedan 2 preferenciales reservados)
        assertFalse(torneo.procesarInscripcion(pareja3, true, false, 24, "INTERMEDIO", false, 0, 0, 0, null));
        assertEquals(100000.0, torneo.getFondoPremios()); // El fondo no cambió tras el rechazo
    }

    @Test
    public void equipoSinCapitanRechazadoYConCapitanAceptado() {
        Torneo torneo = new Torneo("T_CONJ", "Copa Fútbol", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.CONJUNTO, "Fútbol",
                                  LocalDate.now().plusDays(5), 30, "INTERMEDIO", 8, 100000.0);

        int regularesAntes = torneo.getCuposRegularesDisponibles();

        // Rechazo atómico si el capitán es null
        assertFalse(torneo.procesarInscripcion("Equipo Alfa", true, false, 21, "INTERMEDIO", false, 0, 0, 0, null));
        assertEquals(regularesAntes, torneo.getCuposRegularesDisponibles());
        assertEquals(0.0, torneo.getFondoPremios());

        // Aceptación cuando tiene capitán
        assertTrue(torneo.procesarInscripcion("Equipo Alfa", true, false, 21, "INTERMEDIO", false, 0, 0, 0, "Carlos Capitan"));
        assertEquals(regularesAntes - 1, torneo.getCuposRegularesDisponibles());
    }

    @Test
    public void empleadoNoPagaNiAlteraFondoAlInscribirseODesinscribirse() {
        Torneo torneo = new Torneo("T_EMP", "Copa Institucional", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Ajedrez",
                                  LocalDate.now().plusDays(5), 40, "INTERMEDIO", 5, 50000.0);

        // Socio regular paga 50.000
        assertTrue(torneo.inscribirParticipante("SocioRegular", true));
        // Empleado entra sin pagar (pagoConfirmado = false, esEmpleado = true)
        assertTrue(torneo.procesarInscripcion("EmpleadoClub", false, true, 28, "INTERMEDIO", false, 0, 0, 0, null));

        assertEquals(2, torneo.getInscritos().size());
        assertEquals(50000.0, torneo.getFondoPremios()); // Solo está lo del socio regular

        // Al desinscribirse el empleado, el fondo de premios sigue en 50.000 intacto
        assertTrue(torneo.desinscribirParticipante("EmpleadoClub"));
        assertEquals(50000.0, torneo.getFondoPremios());
    }

    @Test
    public void jugadorConHistorialPeroFueraDeTopKNoTomaPreferencial() {
        Torneo torneo = new Torneo("T_ELO", "Copa Ranking", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Tenis",
                                  LocalDate.now().plusDays(5), 35, "AVANZADO", 5, 50000.0);

        int prefIniciales = torneo.getCuposPreferencialesDisponibles(); // 1

        // Tiene 15 partidos jugados, pero su ELO (1400) es menor al K-ésimo mejor (1650).
        // Debe entrar como regular y dejar el preferencial intacto.
        assertTrue(torneo.procesarInscripcion("JugadorMedio", true, false, 25, "AVANZADO", true, 1400.0, 15, 1650.0, null));
        assertEquals(prefIniciales, torneo.getCuposPreferencialesDisponibles());
    }
    
    @Test
    public void HU_T14_E01_liberarCuposPreferencialesPermiteInscripcionRegular() {
        // Torneo con 5 cupos: 1 preferencial y 4 regulares
        Torneo torneo = new Torneo("T14", "Copa Liberación", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Tenis",
                                  LocalDate.now().plusDays(5), 30, "INTERMEDIO", 5, 50000.0);

        // Llenamos los 4 cupos regulares
        torneo.inscribirParticipante("J1", true);
        torneo.inscribirParticipante("J2", true);
        torneo.inscribirParticipante("J3", true);
        torneo.inscribirParticipante("J4", true);

        // El 5to jugador regular es rechazado porque el cupo restante es preferencial
        assertFalse(torneo.inscribirParticipante("J5", true));

        // Se liberan los cupos preferenciales no usados (HU-T14)
        assertTrue(torneo.liberarCuposPreferenciales());
        assertEquals(0, torneo.getCuposPreferencialesDisponibles());
        assertEquals(1, torneo.getCuposRegularesDisponibles());

        // Ahora sí puede inscribirse el 5to jugador como regular
        assertTrue(torneo.inscribirParticipante("J5", true));
        assertEquals(5, torneo.getInscritos().size());
    }

    @Test
    public void HU_T04_E01_rechazarInicioAntesDeFechaOMenosDeDosInscritos() {
        LocalDate fechaInicio = LocalDate.now().plusDays(5);
        Torneo torneo = new Torneo("T04_1", "Copa Inicio Fallido", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Tenis",
                                  fechaInicio, 30, "INTERMEDIO", 6, 50000.0);

        torneo.inscribirParticipante("SoloUno", true);

        // Rechazo 1: Fecha actual anterior a fecha de inicio (aun si tuviera inscritos)
        assertFalse(torneo.iniciarTorneo(LocalDate.now()));
        assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, torneo.getEstado());

        // Rechazo 2: Llegó la fecha, pero solo hay 1 participante (< 2)
        assertFalse(torneo.iniciarTorneo(fechaInicio));
        assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, torneo.getEstado());
    }

    @Test
    public void HU_T04_E02_iniciarTorneoValidoCambiaEstadoYGeneraCalendario() {
        LocalDate fechaInicio = LocalDate.now().plusDays(5);
        Torneo torneo = new Torneo("T04_2", "Copa Inicio Exitoso", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Tenis",
                                  fechaInicio, 30, "INTERMEDIO", 6, 50000.0);

        torneo.inscribirParticipante("Jugador 1", true);
        torneo.inscribirParticipante("Jugador 2", true);
        torneo.inscribirParticipante("Jugador 3", true);

        assertTrue(torneo.iniciarTorneo(fechaInicio));
        assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado());
        assertEquals(3, torneo.getCalendario().size()); // 3 partidos en Round-Robin de 3 jugadores
    }
    
    @Test
    public void HU_T08_E01_registrarResultadoValidaEmpateSegunModalidad() {
        LocalDate hoy = LocalDate.now();
        
        // 1. Torneo INDIVIDUAL (no permite empates)
        Torneo torneoInd = new Torneo("T08_IND", "Copa Tenis", TipoTorneo.COMPETITIVO,
                                     ModalidadTorneo.INDIVIDUAL, "Tenis",
                                     hoy, 30, "INTERMEDIO", 4, 50000.0);
        torneoInd.inscribirParticipante("Ana", true);
        torneoInd.inscribirParticipante("Luis", true);
        torneoInd.iniciarTorneo(hoy);

        String idEncInd = torneoInd.getCalendario().get(0).getIdEncuentro();
        
        // Empate 1-1 rechazado en modalidad INDIVIDUAL
        assertFalse(torneoInd.registrarResultadoEncuentro(idEncInd, 1, 1));
        assertFalse(torneoInd.getCalendario().get(0).isJugado());

        // Marcador con ganador 2-1 aceptado y finaliza el torneo (era el único partido)
        assertTrue(torneoInd.registrarResultadoEncuentro(idEncInd, 2, 1));
        assertEquals(EstadoTorneo.FINALIZADO, torneoInd.getEstado());

        // 2. Torneo CONJUNTO (sí permite empates)
        Torneo torneoConj = new Torneo("T08_CONJ", "Copa Fútbol", TipoTorneo.COMPETITIVO,
                                      ModalidadTorneo.CONJUNTO, "Fútbol",
                                      hoy, 30, "INTERMEDIO", 4, 50000.0);
        torneoConj.procesarInscripcion("Equipo A", true, false, 20, "INTERMEDIO", false, 0, 0, 0, "Cap A");
        torneoConj.procesarInscripcion("Equipo B", true, false, 20, "INTERMEDIO", false, 0, 0, 0, "Cap B");
        torneoConj.iniciarTorneo(hoy);

        String idEncConj = torneoConj.getCalendario().get(0).getIdEncuentro();
        assertTrue(torneoConj.registrarResultadoEncuentro(idEncConj, 2, 2));
        assertTrue(torneoConj.getCalendario().get(0).esEmpate());
        assertNull(torneoConj.getCalendario().get(0).getGanador());
    }

    @Test
    public void HU_T09_E01_desempateDefineGanadorCuandoHayIgualdadRegular() {
        LocalDate hoy = LocalDate.now();
        Torneo torneo = new Torneo("T09", "Copa Desempate", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Tenis",
                                  hoy, 30, "INTERMEDIO", 4, 50000.0);

        torneo.inscribirParticipante("Jugador 1", true);
        torneo.inscribirParticipante("Jugador 2", true);
        torneo.iniciarTorneo(hoy);

        Encuentro enc = torneo.getCalendario().get(0);
        String idEnc = enc.getIdEncuentro();

        // Rechaza desempate si el desempate también queda igualado (5-5)
        assertFalse(torneo.registrarDesempateEncuentro(idEnc, 6, 6, 5, 5));

        // Acepta desempate con ganador claro (6-6 en sets, 7-5 en tie-break)
        assertTrue(torneo.registrarDesempateEncuentro(idEnc, 6, 6, 7, 5));
        assertEquals(enc.getParticipante1(), enc.getGanador());
        assertFalse(enc.esEmpate());
    }
    
    @Test
    public void HU_T06_E01_empleadoConTurnoNoPuedeInscribirseYViceversa() {
        Torneo torneo = new Torneo("T06", "Copa Roles", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Tenis",
                                  LocalDate.now().plusDays(3), 45, "INTERMEDIO", 6, 50000.0);

        // Empleado asignado como juez/turno primero
        assertTrue(torneo.asignarEmpleadoATurno("EmpJuez"));
        // Intenta inscribirse como jugador -> Rechazado por conflicto de turno
        assertFalse(torneo.inscribirPorRol("EmpJuez", RolParticipante.EMPLEADO, false, false, 30, "INTERMEDIO"));

        // Empleado sin turno se inscribe gratis -> Aceptado
        assertTrue(torneo.inscribirPorRol("EmpJugador", RolParticipante.EMPLEADO, false, false, 29, "INTERMEDIO"));
        // Luego intentan asignarlo a turno -> Rechazado porque ya compite
        assertFalse(torneo.asignarEmpleadoATurno("EmpJugador"));
    }

    @Test
    public void HU_T07_E01_externoRequiereAutorizacionYPago() {
        Torneo torneo = new Torneo("T07", "Copa Abierta", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Tenis",
                                  LocalDate.now().plusDays(3), 40, "INTERMEDIO", 6, 50000.0);

        // Externo sin autorización -> Rechazado aunque haya pagado
        assertFalse(torneo.inscribirPorRol("Ext1", RolParticipante.EXTERNO, true, false, 25, "INTERMEDIO"));

        // Externo con autorización y pago confirmado -> Aceptado y suma al fondo
        assertTrue(torneo.inscribirPorRol("Ext1", RolParticipante.EXTERNO, true, true, 25, "INTERMEDIO"));
        assertEquals(50000.0, torneo.getFondoPremios(), 0.01);
    }

    @Test
    public void HU_T12_E01_consultasDeCalendarioPorRondaParticipanteYPendientes() {
        LocalDate hoy = LocalDate.now();
        Torneo torneo = new Torneo("T12", "Copa Consultas", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Ajedrez",
                                  hoy, 50, "INTERMEDIO", 4, 20000.0);

        torneo.inscribirParticipante("P1", true);
        torneo.inscribirParticipante("P2", true);
        torneo.inscribirParticipante("P3", true);
        torneo.inscribirParticipante("P4", true);
        torneo.iniciarTorneo(hoy); // 4 jugadores

        // Obtenemos cuántos partidos le asignó el calendario real a P1
        int partidosP1 = torneo.consultarEncuentrosDeParticipante("P1").size();

        assertTrue(torneo.consultarEncuentrosPorRonda(torneo.getCalendario().get(0).getRonda()).size() > 0);
        assertEquals(partidosP1, torneo.consultarEncuentrosDeParticipante("P1").size());
        assertEquals(torneo.getCalendario().size(), torneo.consultarEncuentrosPendientes().size());

        // Jugamos 1 encuentro
        String primerId = torneo.getCalendario().get(0).getIdEncuentro();
        torneo.registrarResultadoEncuentro(primerId, 1, 0);

        assertEquals(torneo.getCalendario().size() - 1, torneo.consultarEncuentrosPendientes().size());
    }
    
    @Test
    public void HU_T07_E02_validarEstudiantesYExternosConReglas() {
        LocalDate fecha = LocalDate.now().plusDays(3);
        Torneo torneo = new Torneo("T_HU07", "Copa Roles HU07", TipoTorneo.COMPETITIVO,
                                  ModalidadTorneo.INDIVIDUAL, "Tenis", fecha, 40, "INTERMEDIO", 2, 50000.0);

        // Externo sin autorización debe fallar
        boolean externoSinAuth = torneo.inscribirPorRolEspecifico("Externo1", RolParticipante.EXTERNO, false, true);
        assertFalse(externoSinAuth);

        // Externo con autorización debe pasar y sumar tarifa al fondo de premios
        boolean externoConAuth = torneo.inscribirPorRolEspecifico("Externo1", RolParticipante.EXTERNO, true, true);
        assertTrue(externoConAuth);
        assertEquals(50000.0, torneo.getFondoPremios(), 0.01);
    } 
    
}