package deportivo;

import fachada.OperacionRechazadaException;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GestorTorneosTest {

    @Test
    public void testCrearYBuscarTorneoExitoso() {
        GestorTorneos gestor = new GestorTorneos();
        LocalDate fechaFutura = LocalDate.now().plusDays(10);

        // Probamos crear un torneo a través del gestor
        Torneo torneo = gestor.crearTorneo("T_FACADE_1", "Copa Fachada", TipoTorneo.COMPETITIVO,
                                            ModalidadTorneo.INDIVIDUAL, "Tenis", fechaFutura,
                                            8, "INTERMEDIO", 2, 40000.0);

        assertNotNull(torneo);
        
        // Probamos que el gestor lo encuentre por ID correctamente
        Torneo encontrado = gestor.buscarTorneo("T_FACADE_1");
        assertEquals("T_FACADE_1", encontrado.getIdTorneo());
    }

    @Test
    public void testBuscarTorneoInexistenteLanzaExcepcion() {
        GestorTorneos gestor = new GestorTorneos();

        // Verificamos que al buscar un ID que no existe, lance la OperacionRechazadaException con mensaje
        OperacionRechazadaException ex = assertThrows(OperacionRechazadaException.class, () -> {
            gestor.buscarTorneo("ID_FALSO");
        });

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    public void testCrearTorneoConDatosInvalidosLanzaExcepcion() {
        GestorTorneos gestor = new GestorTorneos();
        LocalDate fechaPasada = LocalDate.now().minusDays(5);

        // Validamos que las restricciones que pusimos en el constructor viajen a través del gestor
        assertThrows(OperacionRechazadaException.class, () -> {
            gestor.crearTorneo("T_MALO", "", TipoTorneo.COMPETITIVO,
                                ModalidadTorneo.INDIVIDUAL, "Tenis", fechaPasada,
                                8, "INTERMEDIO", 2, 40000.0);
        });
    }
    
}