# Proyecto 2 – DPOO (ISIS-1226)

Torneos internos del club, construido sobre el Proyecto 1 (Entrega 2).

Todo lo de la entrega está en `Proyecto 2/Entrega única/`:

- `Proyecto2/`: proyecto Eclipse (Java 21, JUnit 5).
- `diagramas/`: fuentes `.drawio` e imágenes de los diagramas.
- `documentos/`: historias de usuario y documento de diseño.

## Cómo abrir el proyecto

1. En Eclipse: **File → Import → Existing Projects into Workspace**.
2. Seleccione la carpeta `Proyecto 2/Entrega única/Proyecto2`.

## Cómo ejecutar las consolas

Cada consola tiene su propio `main` (clic derecho → **Run As → Java Application**):

| Consola | Clase | Usuarios de prueba |
|---|---|---|
| Administrador | `consola.ConsolaAdministrador` | `admin` / `1234` |
| Socios, estudiantes y externos | `consola.ConsolaParticipante` | `socio1` / `1234` (o crear una cuenta nueva; los códigos estudiantiles válidos están en `datos/matriculas.txt`) |
| Empleados | `consola.ConsolaEmpleado` | `entrenador1` / `1234`, `fisio1` / `1234` |

Los datos se cargan y se guardan en la carpeta `datos/` del proyecto: `usuarios.txt`, `matriculas.txt`, `torneos.txt`, `inscripciones.txt`, `encuentros.txt`, `premios.txt`, `bonos.txt` y los archivos de comercio del Proyecto 1.

## Pruebas

Las pruebas JUnit 5 están en la carpeta `test`: `TorneoTest`, `TablaTorneoTest`, `BonoDescuentoTest`, `GestorTorneosTest` (paquete `deportivo`) y `ClubFachadaTest` (paquete `fachada`). Clic derecho en la carpeta `test` → **Run As → JUnit Test**.
