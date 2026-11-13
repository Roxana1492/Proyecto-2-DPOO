package deportivo;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FachadaTorneoTest {

    @Test
    public void testCrearYBuscarTorneoExitoso() {
        FachadaTorneos fachada = new FachadaTorneos();
        LocalDate fechaFutura = LocalDate.now().plusDays(10);

        // Probamos crear un torneo a través de la fachada
        Torneo torneo = fachada.crearTorneo("T_FACADE_1", "Copa Fachada", TipoTorneo.COMPETITIVO,
                                            ModalidadTorneo.INDIVIDUAL, "Tenis", fechaFutura,
                                            8, "INTERMEDIO", 2, 40000.0);

        assertNotNull(torneo);
        
        // Probamos que la fachada lo encuentre por ID correctamente
        Torneo encontrado = fachada.buscarTorneo("T_FACADE_1");
        assertEquals("T_FACADE_1", encontrado.getIdTorneo());
    }

    @Test
    public void testBuscarTorneoInexistenteLanzaExcepcion() {
        FachadaTorneos fachada = new FachadaTorneos();

        // Verificamos que al buscar un ID que no existe, lance la OperacionRechazadaException con mensaje
        OperacionRechazadaException ex = assertThrows(OperacionRechazadaException.class, () -> {
            fachada.buscarTorneo("ID_FALSO");
        });

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    public void testCrearTorneoConDatosInvalidosLanzaExcepcion() {
        FachadaTorneos fachada = new FachadaTorneos();
        LocalDate fechaPasada = LocalDate.now().minusDays(5);

        // Validamos que las restricciones que pusimos en el constructor viajen a través de la fachada
        assertThrows(OperacionRechazadaException.class, () -> {
            fachada.crearTorneo("T_MALO", "", TipoTorneo.COMPETITIVO,
                                ModalidadTorneo.INDIVIDUAL, "Tenis", fechaPasada,
                                8, "INTERMEDIO", 2, 40000.0);
        });
    }
    
}