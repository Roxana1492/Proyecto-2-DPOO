package deportivo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

import autenticacion.Socio;
import fachada.OperacionRechazadaException;

/** Pruebas del canje de bonos (HU-T11). */
public class BonoDescuentoTest {

    private Socio socio = new Socio("s1", "1234", "Carlos", new Date());

    private BonoDescuento bono() {
        return new BonoDescuento("B-T1-1", 20, socio, 1, new Jugador(socio));
    }

    @Test
    public void testCanjeDescuentaVeintePorCiento() {
        BonoDescuento b = bono();
        assertEquals(10000.0, b.canjear("s1", 50000, false), 0.01);
        assertTrue(b.isUsado());
    }

    @Test
    public void testBonoUsadoSeRechaza() {
        BonoDescuento b = bono();
        b.canjear("s1", 50000, false);
        assertThrows(OperacionRechazadaException.class, () -> b.canjear("s1", 50000, false));
    }

    @Test
    public void testNoSeAcumulaConOtroDescuento() {
        BonoDescuento b = bono();
        assertThrows(OperacionRechazadaException.class, () -> b.canjear("s1", 50000, true));
        assertFalse(b.isUsado());
    }

    @Test
    public void testSoloLoUsaElTitular() {
        assertThrows(OperacionRechazadaException.class, () -> bono().canjear("otro", 50000, false));
    }
}
