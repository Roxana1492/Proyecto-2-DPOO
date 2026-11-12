* HU-T01: Creación de un Torneo Interno  
* Título: Creación y configuración de torneos internos por el Administrador  
* Historia: Como Administrador del club, quiero registrar y configurar un nuevo torneo interno definiendo su nombre, tipo (recreativo o competitivo), modalidad, disciplina, fecha de inicio (asumiendo que cada torneo se disputa en una sola fecha), categoría con regla de edad máxima o nivel, cupos totales y tarifa de inscripción, para dinamizar la participación y gestionar las competencias del club.  
* Dependencias: Independiente.  
* Entradas:  
  - Nombre del torneo (String).  
  - Tipo de torneo (Enum: RECREATIVO o COMPETITIVO).  
  - Modalidad (Enum: CONJUNTO, INDIVIDUAL o DOBLES).  
    * *Supuestos de capacidad y cupos:* En modalidad Conjunto, cada cupo representa 1 equipo completo (integrado solo por socios). En dobles, cada pareja consume 2 cupos físicos (exige número par de cupos totales) y el 20% preferencial se calcula sobre el número de parejas.  
  - Disciplina o deporte (String).  
  - Fecha de inicio del torneo (LocalDate / AAAA-MM-DD): Fecha única en la que se disputa el torneo (debe ser igual o posterior a la fecha actual del sistema).  
  - Categoría estructurada: Edad máxima permitida (ej. edad máxima de 14 años) o nivel requerido (Enum: PRINCIPIANTE, INTERMEDIO, AVANZADO).  
  - Cupos totales (int).  
  - Tarifa de inscripción (double: fija en 0 para recreativos; mayor estricta a 0 para competitivos).  
* Salidas / Resultados:  
  - Confirmación de creación exitosa.  
  - El torneo queda registrado en estado "Inscripciones abiertas", con el fondo de premios en \$0 y el cálculo automático de los cupos preferenciales reservados.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Tabla de ejemplos numéricos para el cálculo del 20% de cupos preferenciales en Individual y Conjunto.  
    * Dado que se configura un torneo individual o de conjunto con diferentes capacidades totales,  
    * Cuando el sistema calcula el 20% de cupos preferenciales reservados,  
    * Entonces aplica los siguientes resultados exactos: 10 cupos \= 2 preferenciales; 12 cupos \= 3 preferenciales; 5 cupos \= 1 preferencial; 1 cupo \= 1 preferencial.  
  - Escenario 2: Ejemplo numérico para el cálculo en modalidad Dobles.  
    * Dado que se configura un torneo en modalidad dobles con 6 cupos totales físicos,  
    * Cuando el sistema calcula los cupos preferenciales sobre el número de parejas (6 cupos \= 3 parejas, 20% de 3 \= 0.6, redondeado al siguiente entero superior \= 1 pareja),  
    * Entonces reserva exactamente 2 cupos físicos preferenciales.  
  - Escenario 3: Creación exitosa de un torneo recreativo.  
    * Dado que el usuario autenticado tiene rol de Administrador,  
    * Cuando ingresa los datos válidos de un torneo recreativo con fecha futura,  
    * Entonces el sistema crea el torneo en estado "Inscripciones abiertas", asigna los cupos preferenciales y fija el premio como bono de descuento.  
  - Escenario 4: Rechazo por tarifa inválida en competitivo o recreativo.  
    * Dado que el administrador configura un torneo competitivo con tarifa de \$0 o un recreativo con tarifa mayor a 0,  
    * Cuando intenta guardarlo,  
    * Entonces el sistema rechaza la operación.  
  - Escenario 5: Rechazo por número impar de cupos en modalidad dobles.  
    * Dado que el administrador intenta crear un torneo en modalidad dobles,  
    * Cuando ingresa un número impar de cupos totales (ej. 5 cupos),  
    * Entonces el sistema rechaza la creación indicando que se exige un número par de cupos.  
  - Escenario 6: Rechazo por cupos totales menores o iguales a cero.  
    * Dado que el administrador intenta crear un torneo,  
    * Cuando ingresa cupos menores o iguales a 0,  
    * Entonces el sistema rechaza la operación.  
  - Escenario 7: Rechazo por nombre vacío.  
    * Dado que el administrador intenta crear un torneo,  
    * Cuando deja el nombre vacío,  
    * Entonces el sistema rechaza la creación.  
  - Escenario 8: Rechazo por fecha de inicio pasada.  
    * Dado que el administrador intenta registrar un nuevo torneo,  
    * Cuando ingresa una fecha de inicio anterior a la fecha actual del sistema,  
    * Entonces el sistema rechaza la creación.  
  - Escenario 9: Rechazo por permisos de usuario.  
    * Dado que un usuario sin rol de Administrador intenta acceder a la creación,  
    * Cuando solicita la creación,  
    * Entonces el sistema deniega el acceso.  
* Autoevaluación INVEST:  
  - Independiente: Sí, es la historia raíz.  
  - Negociable: Los parámetros son ajustables.  
  - Valiosa: Abre los espacios de competencia.  
  - Estimable: Complejidad baja.  
  - Pequeña: Limitada a la parametrización inicial.  
  - Testeable: Verificable con escenarios de éxito y rechazo.  
  - HU-T02: Inscripción de Participantes a un Torneo  
* Título: Inscripción libre de socios, estudiantes acreditados y externos a torneos con inscripciones abiertas  
* Historia: Como Socio, Estudiante acreditado (validado contra el archivo de matrículas con su código estudiantil) o Persona externa, quiero inscribirme a un torneo con inscripciones abiertas antes de que inicie, para participar en las competencias habilitadas.  
* Dependencias: No independiente: depende de HU-T01.  
* Entradas: Identificador del torneo, datos del participante y credenciales de pago confirmado (obligatorio en competitivos).  
* Salidas / Resultados: Confirmación de registro exitoso, descuento de cupos e incremento del fondo de premios en efectivo (tarifa única por inscripción).  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Inscripción exitosa de un socio con pago confirmado y actualización del fondo.  
    * Dado que un torneo competitivo tiene tarifa de \$50.000 por inscripción y el fondo está en \$0,  
    * Cuando un socio elegible se inscribe aportando su confirmación de pago,  
    * Entonces el sistema lo registra correctamente, descuenta el cupo e incrementa el fondo acumulado de premios a \$50.000.  
  - Escenario 2: Inscripción exitosa de un estudiante validado.  
    * Dado que un torneo se encuentra con "Inscripciones abiertas",  
    * Cuando un estudiante aporta su código estudiantil y el sistema lo valida exitosamente contra el archivo de estudiantes matriculados, cumpliendo además la categoría,  
    * Entonces el sistema acepta su registro de forma exitosa.  
  - Escenario 3: Inscripción de un equipo completo en modalidad de conjunto.  
    * Dado que el torneo es de disciplina de conjunto (formado por socios del club),  
    * Cuando el capitán del equipo ingresa la lista de integrantes validando el mínimo de jugadores, la edad máxima y que ninguno esté inscrito previamente en otro equipo,  
    * Entonces el sistema registra al equipo completo consumiendo 1 cupo del torneo.  
  - Escenario 4: Rechazo por rol de Administrador.  
    * Dado que un usuario con rol de Administrador intenta inscribirse a un torneo,  
    * Cuando solicita el registro,  
    * Entonces el sistema rechaza la inscripción recordando que los administradores no participan en torneos.  
  - Escenario 5: Rechazo por torneo lleno (cupos regulares agotados aunque queden preferenciales).  
    * Dado que los cupos regulares de un torneo están totalmente agotados y solo quedan libres los cupos preferenciales reservados por ranking,  
    * Cuando un socio regular intenta inscribirse,  
    * Entonces el sistema rechaza la inscripción indicando que no hay cupos regulares disponibles.  
  - Escenario 6: Rechazo por torneo totalmente lleno (incluso para jugadores de alto ranking).  
    * Dado que todos los cupos regulares y preferenciales de un torneo están ocupados,  
    * Cuando un participante intenta inscribirse,  
    * Entonces el sistema rechaza la inscripción indicando que el torneo está lleno.  
  - Escenario 7: Rechazo por inscripciones cerradas o torneo iniciado.  
    * Dado que un torneo se encuentra en estado "En curso" o "Finalizado",  
    * Cuando un participante intenta registrarse,  
    * Entonces el sistema rechaza la inscripción.  
  - Escenario 8: Rechazo por falta de cupos suficientes para dobles (ej. queda 1 cupo y entra pareja de 2).  
    * Dado que un torneo de dobles cuenta con exactamente 1 cupo libre disponible,  
    * Cuando una pareja de dos jugadores intenta inscribirse,  
    * Entonces el sistema rechaza la inscripción por requerir 2 cupos físicos.  
  - Escenario 9: Rechazo por elegibilidad (edad o nivel no válido).  
    * Dado que un participante intenta inscribirse,  
    * Cuando su edad supera el límite o su nivel técnico no cumple con los rangos definidos,  
    * Entonces el sistema rechaza la inscripción.  
  - Escenario 10: Rechazo por doble inscripción.  
    * Dado que el participante ya se encuentra registrado en el torneo,  
    * Cuando intenta inscribirse de nuevo,  
    * Entonces el sistema rechaza la operación.  
* Autoevaluación INVEST: No independiente (depende de HU-T01). Valiosa, estimable, pequeña y testeable.  
* HU-T03: Gestión de Cupos Preferenciales por Ranking  
* Título: Asignación de cupos preferenciales por desempeño basado en el top-K del ranking global  
* Historia: Como Jugador con alto desempeño en el ranking global de la disciplina, quiero que el sistema me otorgue prioridad ocupando los cupos preferenciales según el top-K configurable, para garantizar un espacio competitivo a los mejores posicionados. (En dobles, la evaluación se realiza calculando el promedio de ELO de la pareja; en equipos, el promedio de ELO de sus integrantes).  
* Dependencias: No independiente: depende de HU-T01 y HU-T02.  
* Entradas / Salidas: Asignación prioritaria de cupos preferenciales o desplazamiento a regular si se agotan, asignando puntaje base 0 a jugadores sin historial.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Ocupación exitosa de un cupo preferencial por un jugador, pareja o equipo dentro del top-K global.  
    * Dado que existen cupos preferenciales reservados y un jugador, pareja o equipo (evaluado por el promedio de ELO de sus integrantes) cuyo puntaje en el ranking global de la disciplina se encuentra dentro del top-K configurable solicita unirse antes de iniciar,  
    * Cuando el sistema evalúa su solicitud,  
    * Entonces le otorga un cupo preferencial de manera exitosa.  
  - Escenario 2: Desplazamiento a cupo regular por agotamiento de cupos preferenciales.  
    * Dado que un jugador con puntaje en el top-K intenta inscribirse pero los cupos preferenciales ya fueron ocupados,  
    * Cuando solicita su registro,  
    * Entonces el sistema le asigna un cupo regular disponible sin perder su derecho a participar.  
  - Escenario 3: Manejo de jugadores sin historial en el ranking.  
    * Dado que un socio sin registros previos en el ranking de la disciplina intenta inscribirse,  
    * Cuando solicita un cupo preferencial,  
    * Entonces el sistema le asigna un puntaje base de 0, impidiéndole ocupar cupos preferenciales y obligándolo a usar los cupos regulares.  
* Autoevaluación INVEST: No independiente (depende de HU-T01 y HU-T02). Valiosa, estimable, pequeña y testeable.  
* HU-T04: Cierre de Inscripciones e Inicio del Torneo por el Administrador  
* Título: Transición de estados, inicio del torneo y liberación automática de cupos preferenciales sobrantes  
* Historia: Como Administrador del club, quiero cerrar las inscripciones y dar inicio al torneo cambiando su estado a "En curso", liberando los cupos preferenciales sobrantes y disparando la generación automática del calendario, para arrancar la competencia.  
* Dependencias: No independiente: depende de HU-T01 y HU-T02.  
* Entradas: Acción del administrador para cambiar el estado a "En curso".  
* Salidas / Resultados: Cambio de estado, liberación de preferenciales y llamada a la generación del calendario (HU-T13).  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Inicio exitoso y liberación de preferenciales.  
    * Dado que un torneo se encuentra en "Inscripciones abiertas" con fecha de inicio cumplida y al menos 2 participantes inscritos,  
    * Cuando el administrador ejecuta la acción de iniciar el torneo,  
    * Entonces el sistema cambia el estado a "En curso", libera los preferenciales sobrantes como regulares y dispara la creación del calendario.  
  - Escenario 2: Rechazo por intento de iniciar antes de la fecha de inicio.  
    * Dado que un torneo se encuentra en "Inscripciones abiertas" y aún no llega su fecha programada,  
    * Cuando el administrador intenta ejecutar la acción de iniciar el torneo,  
    * Entonces el sistema rechaza la operación indicando que no se puede iniciar antes de la fecha establecida.  
  - Escenario 3: Rechazo por iniciar con menos de 2 participantes inscritos.  
    * Dado que un torneo tiene registrado solo 1 participante (o ninguno),  
    * Cuando el administrador intenta dar inicio al torneo,  
    * Entonces el sistema rechaza la acción indicando que se requieren al menos 2 participantes.  
  - Escenario 4: Rechazo por torneo ya En curso o Finalizado.  
    * Dado que el torneo ya se encuentra en estado "En curso" o "Finalizado",  
    * Cuando se intenta ejecutar nuevamente la acción de inicio,  
    * Entonces el sistema rechaza la operación.  
* Autoevaluación INVEST: No independiente (depende de HU-T01 y HU-T02). Valiosa, estimable, pequeña y testeable.  
* HU-T13: Generación Automática del Calendario de Liga  
* Título: Generación del calendario de enfrentamientos todos contra todos al iniciar el torneo  
* Historia: Como Administrador del sistema, quiero que al dar inicio a un torneo se genere automáticamente el calendario de enfrentamientos bajo el formato de liga todos contra todos (manejando las rondas y contemplando los descansos cuando el número de participantes es impar), para estructurar los encuentros de la competencia.  
* Dependencias: No independiente: depende de HU-T04.  
* Entradas: Lista de participantes inscritos al iniciar el torneo y parámetro de la modalidad.  
* Salidas / Resultados: Creación y almacenamiento del calendario de encuentros ordenado por rondas.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Ejemplo numérico de calendario con número par de participantes (4 participantes).  
    * Dado que un torneo pasa a estado "En curso" con 4 participantes inscritos (número par),  
    * Cuando el sistema procesa el listado,  
    * Entonces genera exactamente 3 rondas con 2 encuentros por ronda (todos contra todos sin descansos).  
  - Escenario 2: Ejemplo numérico de calendario con número impar de participantes (5 participantes).  
    * Dado que un torneo pasa a estado "En curso" con 5 participantes inscritos (número impar),  
    * Cuando el sistema procesa el listado,  
    * Entonces genera exactamente 5 rondas con 2 encuentros y 1 participante en descanso por cada ronda.  
* Autoevaluación INVEST: No independiente (depende de HU-T04). Valiosa, estimable, pequeña y testeable.  
* HU-T05: Desinscripción de un Torneo antes de su Inicio  
* Título: Salida voluntaria de participantes y liberación de cupos antes del inicio  
* Historia: Como Participante inscrito, quiero poder desinscribirme libremente de un torneo mientras esté en "Inscripciones abiertas", para liberar mi espacio.  
* Dependencias: No independiente: depende de HU-T02.  
* Entradas / Salidas: Eliminación de inscripción, liberación de cupos y ajuste del fondo financiero.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Desinscripción exitosa individual y conservación de cupo preferencial.  
    * Dado que un socio ocupa un cupo preferencial en un torneo competitivo con "Inscripciones abiertas" cuya tarifa de \$50.000 sumó al fondo,  
    * Cuando solicita desinscribirse,  
    * Entonces el sistema lo retira, devuelve 1 cupo preferencial al torneo y resta \$50.000 del fondo acumulado.  
  - Escenario 2: Desinscripción de un empleado (sin afectar el fondo).  
    * Dado que un empleado está inscrito gratuitamente en un torneo competitivo individual con "Inscripciones abiertas",  
    * Cuando solicita desinscribirse,  
    * Entonces el sistema libera su cupo regular y mantiene intacto el saldo del fondo acumulado de premios.  
  - Escenario 3: Desinscripción de una pareja en dobles y ajuste del fondo.  
    * Dado que una pareja (socio \+ empleado que no pagó) está inscrita en un torneo de dobles competitivo con tarifa de \$40.000 pagada por el socio,  
    * Cuando solicitan desinscribirse,  
    * Entonces el sistema retira a la pareja, libera 2 cupos y resta la tarifa completa de \$40.000 del fondo.  
  - Escenario 4: Desinscripción de un equipo por su capitán y rechazo por miembro no capitán.  
    * Dado que un equipo está inscrito en un torneo de conjunto,  
    * Cuando el capitán solicita la desinscripción, el sistema lo retira y libera 1 cupo; si lo intenta un miembro regular, el sistema rechaza la operación por falta de privilegios.  
  - Escenario 5: Rechazo por persona o equipo no inscrito o torneo ya iniciado.  
    * Dado que un usuario intenta desinscribirse de un torneo donde no está registrado o cuyo estado ya es "En curso" o "Finalizado",  
    * Cuando ejecuta la acción,  
    * Entonces el sistema rechaza la solicitud.  
* Autoevaluación INVEST: No independiente (depende de HU-T02). Valiosa, estimable, pequeña y testeable.  
* HU-T06: Participación de Empleados  
* Título: Inscripción gratuita de empleados en disciplinas individuales condicionada al día de turno  
* Historia: Como Empleado del club, quiero inscribirme gratuitamente a torneos individuales (incluyendo dobles) si no tengo turno el día del torneo, para participar sin afectar mis labores.  
* Dependencias: No independiente: depende de HU-T01 y HU-T02.  
* Entradas: Identificador del torneo (con fecha de inicio), datos del empleado y su agenda laboral en Turno.  
* Salidas / Resultados: Registro gratuito en cupo regular, exento de premio en efectivo.  
* Supuesto clave: Cada torneo se disputa en una sola fecha, por lo que basta con validar el día de la semana de dicha fecha contra el turno registrado.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Inscripción exitosa de un empleado libre de turno en torneo competitivo individual (1 vs 1).  
    * Dado que un torneo competitivo individual se realiza un domingo, el empleado no tiene turno asignado los domingos y cumple la edad/nivel de la categoría,  
    * Cuando solicita su inscripción,  
    * Entonces el sistema lo registra en 1 cupo regular sin cobrar tarifa, deja el fondo sin cambios y lo marca exento de premio en efectivo.  
  - Escenario 2: Inscripción de un empleado en dobles junto a un socio.  
    * Dado que un empleado libre de turno se inscribe en dobles con un socio elegible,  
    * Cuando el socio aporta el pago de la pareja (\$40.000),  
    * Entonces el sistema ocupa 2 cupos regulares, suma los \$40.000 al fondo y marca al empleado exento de efectivo.  
  - Escenario 3: Rechazo por conflicto con turno laboral.  
    * Dado que el empleado tiene turno asignado el día del torneo,  
    * Cuando intenta inscribirse,  
    * Entonces el sistema bloquea la operación.  
  - Escenario 4: Rechazo por intento de inscripción en deporte de conjunto.  
    * Dado que el torneo corresponde a un deporte de conjunto,  
    * Cuando un empleado intenta inscribirse,  
    * Entonces el sistema deniega la solicitud.  
* Autoevaluación INVEST: No independiente (depende de HU-T01 y HU-T02). Valiosa, estimable, pequeña y testeable.  
* HU-T07: Registro y Creación de Cuenta  
* Título: Registro de credenciales de acceso para estudiantes y externos  
* Historia: Como Estudiante matriculado o Externo, quiero registrarme en el sistema creando usuario, contraseña segura (mín. 8 caracteres, letra y número), código estudiantil de 9 dígitos y fecha de nacimiento válida, para autenticarme.  
* Dependencias: Independiente.  
* Entradas: Nombre, documento, fecha de nacimiento, nivel técnico, login, contraseña, tipo de usuario y código estudiantil (si aplica).  
* Salidas / Resultados: Creación de cuenta habilitada para iniciar sesión.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Registro exitoso de un estudiante y de un externo.  
    * Dado que un usuario ingresa datos válidos, fecha pasada, nivel, contraseña segura (Club2026) y código de 9 dígitos verificado en matrículas (o externo sin código),  
    * Cuando confirma el registro,  
    * Entonces el sistema crea su cuenta habilitada para iniciar sesión.  
  - Escenario 2: Rechazo por login repetido.  
    * Dado que ya existe una cuenta con el mismo login,  
    * Cuando un nuevo usuario intenta registrarse usándolo,  
    * Entonces el sistema rechaza el registro.  
  - Escenario 3: Rechazo por código estudiantil no matriculado.  
    * Dado que se intenta registrar como estudiante con un código que no está en el archivo de matrículas,  
    * Cuando confirma,  
    * Entonces el sistema rechaza el registro.  
  - Escenario 4: Rechazo por datos inválidos (campos vacíos, contraseña débil, código incorrecto o fecha futura).  
    * Dado que el usuario ingresa datos incorrectos en el formulario,  
    * Cuando intenta registrarse,  
    * Entonces el sistema rechaza la operación mostrando el error específico.  
  - Escenario 5: Rechazo por documento de identidad duplicado.  
    * Dado que el documento ya pertenece a un usuario existente,  
    * Cuando se intenta un nuevo registro,  
    * Entonces el sistema bloquea la creación.  
* Autoevaluación INVEST: Independiente. Valiosa, estimable, pequeña y testeable.  
* Tanda B — Ciclo del Torneo y Consultas  
* HU-T08: Registro de Resultados de Encuentros  
* Título: Registro de marcadores y ganadores por el Administrador en torneos en curso  
* Historia: Como Administrador del club, quiero registrar los marcadores de los encuentros programados en el calendario de un torneo en curso (aplicando formato por modalidad y reglas de empate 3-1-0 solo en conjunto según el deporte), para actualizar la tabla de posiciones.  
* Dependencias: No independiente: depende de HU-T04 y HU-T13.  
* Entradas: Identificador del torneo ("En curso"), identificador del encuentro y marcadores del partido.  
* Salidas / Resultados: Actualización de la tabla de posiciones y del marcador del encuentro.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Registro exitoso de resultado con victoria en disciplina individual o dobles (3 puntos).  
    * Dado que un torneo individual o de dobles está "En curso" y existe el encuentro en el calendario,  
    * Cuando el administrador registra un marcador definitivo con ganador,  
    * Entonces el sistema actualiza la tabla de posiciones otorgando 3 puntos al ganador y 0 al perdedor.  
  - Escenario 2: Registro exitoso de resultado en deporte de conjunto permitido con empates (3-1-0).  
    * Dado que un torneo de conjunto (en un deporte que permite empates, ej. fútbol) está "En curso" y existe el encuentro en el calendario,  
    * Cuando el administrador registra un marcador válido de empate (ej. 2 a 2),  
    * Entonces el sistema actualiza la tabla de posiciones otorgando 1 punto a cada equipo.  
  - Escenario 3: Rechazo por empate en deporte de conjunto que no los permite (ej. baloncesto o voleibol).  
    * Dado que un torneo de conjunto de un deporte sin empates (como baloncesto o voleibol) está "En curso",  
    * Cuando el administrador intenta registrar un empate en marcador,  
    * Entonces el sistema rechaza la operación indicando que este deporte no permite empates.  
  - Escenario 4: Rechazo por empate en disciplina individual o dobles.  
    * Dado que un torneo individual o de dobles está "En curso",  
    * Cuando el administrador intenta registrar un empate en marcador,  
    * Entonces el sistema rechaza la operación indicando que esta modalidad no permite empates.  
  - Escenario 5: Rechazo por marcador negativo.  
    * Dado que el administrador intenta registrar un resultado,  
    * Cuando ingresa marcadores negativos,  
    * Entonces el sistema rechaza la operación.  
  - Escenario 6: Rechazo por participantes ajenos al torneo.  
    * Dado que se intenta registrar un encuentro con participantes que no pertenecen al torneo,  
    * Cuando se ejecuta la acción,  
    * Entonces el sistema rechaza el registro.  
  - Escenario 7: Rechazo por encuentro ya registrado previamente.  
    * Dado que el encuentro ya tiene un resultado ingresado,  
    * Cuando se intenta registrar de nuevo,  
    * Entonces el sistema bloquea la duplicación.  
  - Escenario 8: Rechazo por usuario sin rol de Administrador.  
    * Dado que un usuario regular intenta registrar un resultado,  
    * Cuando ejecuta la acción,  
    * Entonces el sistema deniega el acceso.  
* Autoevaluación INVEST: No independiente (depende de HU-T04 y HU-T13). Valiosa, estimable, pequeña y testeable.  
* HU-T09: Finalización del Torneo por el Administrador  
* Título: Cierre del torneo, desempate y ejecución automática de premios  
* Historia: Como Administrador del club, quiero dar por finalizado un torneo cuando todos los encuentros del calendario tengan resultado, aplicando los criterios de desempate (puntos, diferencia de marcador, marcador a favor y orden de inscripción), para fijar las posiciones definitivas y disparar el reparto automático de premios.  
* Dependencias: No independiente: depende de HU-T04 y HU-T08.  
* Entradas: Acción del administrador para cambiar el estado a "Finalizado".  
* Salidas / Resultados: Posiciones congeladas y ejecución automática de la premiación (HU-T10).  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Finalización exitosa y reparto automático de premios.  
    * Dado que un torneo está "En curso" y todos los encuentros del calendario tienen resultado registrado,  
    * Cuando el administrador ejecuta la finalización,  
    * Entonces el sistema fija las posiciones definitivas (aplicando desempate por puntos, diferencia de marcador, marcador a favor y orden de inscripción), cambia el estado a "Finalizado" y ejecuta la entrega de premios de forma automática.  
  - Escenario 2: Rechazo por encuentros pendientes.  
    * Dado que un torneo tiene encuentros del calendario sin resultado registrado,  
    * Cuando el administrador intenta finalizarlo,  
    * Entonces el sistema rechaza la acción indicando que faltan resultados por ingresar.  
  - Escenario 3: Rechazo por usuario sin rol de Administrador.  
    * Dado que un usuario regular intenta finalizar un torneo,  
    * Cuando ejecuta la acción,  
    * Entonces el sistema deniega el acceso.  
* Autoevaluación INVEST: No independiente (depende de HU-T04 y HU-T08). Valiosa, estimable, pequeña y testeable.  
* HU-T10: Gestión y Reparto Automático de Premios  
* Título: Distribución de bonos recreativos y premios en efectivo competitivos (fondo acumulado repartido 60%-30%-10%)  
* Historia: Como Ganador de un torneo, quiero recibir automáticamente mi premio al finalizar la competencia (bonos de descuento en recreativos, o el fondo acumulado en efectivo para competitivos repartido 60%-30%-10%, reordenando la clasificación si el ganador es empleado y excluyendo a parejas mixtas con empleado del efectivo, o entregando a capitanes en equipos), para obtener el reconocimiento por mi desempeño.  
* Dependencias: No independiente: depende de HU-T09.  
* Entradas: Posiciones finales y fondo acumulado.  
* Salidas / Resultados: Registro del monto adeudado en efectivo (por estar los pagos tercerizados) o generación de bonos de descuento en el perfil del socio.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Reparto en torneo competitivo con reordenamiento por empleado o pareja mixta ganadora.  
    * Dado que un torneo competitivo finaliza con un fondo acumulado de \$100.000 (repartido 60% \= \$60.000 al 1.º, 30% \= \$30.000 al 2.º y 10% \= \$10.000 al 3.º), y el competidor que ocupaba el 1.er puesto es un empleado (o una pareja de dobles donde al menos un integrante es empleado),  
    * Cuando el sistema procesa la premiación aplicando la regla de exclusión de efectivo y reordenamiento,  
    * Entonces el empleado/pareja mixta queda excluido del efectivo y los siguientes participantes elegibles suben de puesto (el antiguo 2.º recibe los \$60.000, el 3.º recibe los \$30.000, y el 4.º recibe los \$10.000).  
  - Escenario 2: Reparto de premios con fondo en \$0.  
    * Dado que un torneo competitivo finaliza con un fondo acumulado en \$0 (porque todos los inscritos fueron empleados exentos),  
    * Cuando se procesa la premiación,  
    * Entonces el sistema registra que no hay fondo a repartir y emite un aviso informativo.  
  - Escenario 3: Premiación con exactamente 2 participantes (mínimo para iniciar).  
    * Dado que un torneo competitivo finaliza con solo 2 participantes (1.º y 2.º puesto),  
    * Cuando se reparte el fondo (60% al primero, 30% al segundo),  
    * Entonces el 10% restante del tercer puesto no asignado se retiene y queda guardado en el fondo general del club.  
  - Escenario 4: Premiación del equipo ganador.  
    * Dado que un equipo gana un torneo competitivo o recreativo,  
    * Cuando se distribuyen los premios,  
    * Entonces el premio en efectivo queda a nombre del equipo para ser entregado al capitán, y se otorga un bono por cada integrante si es recreativo.  
  - Escenario 5: Rechazo por duplicidad de entrega.  
    * Dado que los premios de un torneo ya fueron entregados,  
    * Cuando se intenta procesar la premiación de nuevo,  
    * Entonces el sistema evita duplicar los registros.  
* Autoevaluación INVEST: No independiente (depende de HU-T09). Valiosa, estimable, pequeña y testeable.  
* HU-T11: Almacenamiento, Consulta y Canje de Bonos de Descuento  
* Título: Consulta, guardado y redención de bonos de descuento (20% sobre subtotal, un solo uso y no acumulables)  
* Historia: Como Socio ganador de un torneo recreativo, quiero consultar los bonos guardados en mi perfil y aplicarlos en una compra futura en la tienda o cafetería (20% sobre el subtotal antes de impuestos, sin modificar propinas ni consumo, de un solo uso y no acumulable con puntos o códigos de empleado), para disfrutar de mi beneficio.  
* Dependencias: No independiente: depende de HU-T10.  
* Entradas: Identificador del socio, bonos guardados en su perfil y tiquete de compra en tienda/cafetería.  
* Salidas / Resultados: Aplicación del 20% de descuento sobre el subtotal e invalidación del bono consumido.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Consulta exitosa de bonos propios.  
    * Dado que un socio tiene bonos ganados guardados en su perfil,  
    * Cuando solicita consultar sus bonos vigentes,  
    * Entonces el sistema muestra la lista de sus bonos disponibles con su valor correspondiente.  
  - Escenario 2: Canje exitoso de un bono en cafetería.  
    * Dado que un socio realiza una compra en la cafetería con subtotal de \$50.000 y presenta su bono del 20%,  
    * Cuando aplica el bono al pagar,  
    * Entonces el sistema descuenta \$10.000 (20% del subtotal, sin alterar propinas ni impuestos al consumo) y marca el bono cómo usado.  
  - Escenario 3: Rechazo por bono ya usado.  
    * Dado que el socio intenta usar un bono que ya fue redimido en una compra anterior,  
    * Cuando intenta aplicarlo de nuevo,  
    * Entonces el sistema rechaza la operación indicando que el bono ya no está disponible.  
  - Escenario 4: Rechazo por intento de acumulación con código de empleado o puntos.  
    * Dado que el socio intenta aplicar su bono combinándolo de forma simultánea con el canje de puntos del P1 o un código de descuento de empleado,  
    * Cuando procesa el pago,  
    * Entonces el sistema rechaza la combinación por no ser acumulable.  
  - Escenario 5: Rechazo por intento de usar un bono ajeno (intransferible).  
    * Dado que un socio intenta pagar utilizando un bono ganado por otra persona,  
    * Cuando intenta canjearlo,  
    * Entonces el sistema rechaza el bono por pertenecer a otro usuario.  
* Autoevaluación INVEST: No independiente (depende de HU-T10). Valiosa, estimable, pequeña y testeable.  
* HU-T12: Consulta de Estado e Historial Detallado de Torneos  
* Título: Listado general y visualización detallada del historial de torneos para cualquier usuario autenticado  
* Historia: Como Usuario autenticado (Administrador, Socio, Estudiante, Externo o Empleado), quiero listar todos los torneos del club con su estado y consultar el historial detallado de cualquiera (incluyendo inscritos con solo nombre, encuentros, posiciones, premios entregados y cupos disponibles en inscripciones abiertas), para conocer la actividad deportiva sin importar el estado del torneo.  
* Dependencias: No independiente: depende de HU-T01 y HU-T08.  
* Entradas: Identificador o selección del torneo a consultar.  
* Salidas / Resultados: Visualización en pantalla del estado, cupos, lista de inscritos, encuentros, posiciones y premios entregados.  
* Criterios de Aceptación (Dado-Cuando-Entonces):  
  - Escenario 1: Listado general de torneos.  
    * Dado que un usuario autenticado accede a la opción de torneos,  
    * Cuando solicita ver el listado general,  
    * Entonces el sistema muestra todos los torneos registrados con su estado actual ("Inscripciones abiertas", "En curso" o "Finalizado").  
  - Escenario 2: Consulta detallada del torneo en estado "Inscripciones abiertas".  
    * Dado que el usuario consulta un torneo abierto,  
    * Cuando accede al detalle,  
    * Entonces el sistema muestra los cupos disponibles (regulares y preferenciales) y la lista de inscritos (mostrando únicamente nombres, sin documentos ni logins).  
  - Escenario 3: Consulta del historial de torneo en curso o finalizado.  
    * Dado que el torneo está "En curso" o "Finalizado",  
    * Cuando se solicita el detalle,  
    * Entonces el sistema muestra los encuentros disputados, posiciones, podio y los premios entregados (si ya finalizó).  
  - Escenario 4: Mensaje informativo por torneo sin encuentros o inexistente.  
    * Dado que el usuario busca un torneo que no existe o que aún no tiene encuentros generados,  
    * Cuando realiza la consulta,  
    * Entonces el sistema devuelve un mensaje informativo claro y amigable sin arrojar errores de sistema.  
* Autoevaluación INVEST: No independiente (depende de HU-T01 y HU-T08). Valiosa, estimable, pequeña y testeable.