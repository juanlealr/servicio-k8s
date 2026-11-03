# Servicio REST API para Monitoreo y Tolerancia a Fallas en Kubernetes

**Universidad Pedagógica y Tecnológica de Colombia (UPTC)**  
**Escuela de Ingeniería de Sistemas y Computación**  
**Asignatura:** Sistemas Distribuidos  

---

## 1. Información del Proyecto

* **Nombre del Servicio:** `servicio-info-node`
* **Tecnologías:** Java 17, Spring Boot 4.1.2, Maven, Kubernetes (Flannel + Tailscale CNI).
* **Propósito:** Proporcionar un endpoint REST para la inspección en tiempo real de métricas del contenedor (hora, identificador del Pod, consumo de memoria RAM y almacenamiento en disco) dentro de un clúster distribuido de Kubernetes, garantizando alta disponibilidad y tolerancia a fallas.

---

## 2. Decisiones de Arquitectura de Software

La aplicación fue diseñada siguiendo una variante de la **Arquitectura Limpia (Clean Architecture)** adaptada a microservicios con **Spring Boot (MVC)**. Se estructuró bajo el principio de separación de responsabilidades para garantizar mantenibilidad, prueba e independencia del framework.

```
co.edu.uptc.servicio_k8s
├── InfoApplication.java           # Punto de entrada de la aplicación Spring Boot
├── controller
│   └── InfoController.java        # Capa de Exposición (HTTP / REST)
├── service
│   └── InfoService.java           # Capa de Lógica de Negocio y Métricas del Sistema
└── model
    └── ContainerInfoResponse.java # DTO (Data Transfer Object) para serialización JSON
```

### Justificación de Componentes

1. **Capa del Controlador (`InfoController`):**
   * **Función:** Maneja las peticiones HTTP entrantes sobre la ruta `/api/v1/info`.
   * **Decisión:** Su única responsabilidad es recibir el request y retornar la respuesta estructurada en formato JSON con un código de estado HTTP `200 OK`. No contiene ninguna lógica de lectura del sistema operativo ni manipulación de datos.

2. **Capa de Servicio (`InfoService`):**
   * **Función:** Encapsula la interacción con las APIs nativas de Java (`java.io.File`, `java.lang.Runtime`, `java.net.InetAddress`) para extraer las métricas del contenedor.
   * **Decisión:** Aplica el principio **Single Responsibility Principle (SRP)** de SOLID. Si el día de mañana cambia la forma de medir la memoria o el almacenamiento, solo se modifica esta clase sin alterar la API expuesta por el controlador.

3. **Objeto de Transferencia de Datos (`ContainerInfoResponseDTO` - DTO):**
   * **Función:** Define el esquema de datos retornado al cliente.
   * **Decisión:** Desacopla las entidades internas del sistema de la representación JSON externa, evitando la exposición no deseada de atributos de la infraestructura.

---

## 3. Captura de Métricas del Contenedor en Entornos Linux/Kubernetes

La lectura de información del sistema en un contenedor virtualizado presenta particularidades debido a las capas de aislamiento (namespaces y cgroups). A continuación se documenta el funcionamiento técnico de cada métrica:

### A. Identificador del Pod (`idContenedor`)
* **Implementación:** `InetAddress.getLocalHost().getHostName()` con fallback a la variable de entorno `HOSTNAME`.
* **Mecanismo:** En Kubernetes, por defecto, el hostname del sistema operativo dentro del contenedor coincide exactamente con el nombre asignado al Pod por el `ReplicaSet` (por ejemplo: `holamundo-deployment-5494bd9595-jnnjk`). Esto permite verificar inmediatamente qué Pod y qué Nodo atendieron la petición HTTP.

### B. Almacenamiento en Disco (`almacenamientoContenedor`)
* **Implementación:** Consultas sobre el punto de montaje raíz mediante `new File("/")`.
* **Mecanismo:** Executa llamadas del sistema a la función nativa de Linux `statvfs()`. 
  * `getTotalSpace()`: Obtiene el tamaño total de la partición asignada al contenedor.
  * `getFreeSpace()`: Obtiene el espacio no utilizado.
  * **Cálculo:** 
    $$\text{Espacio Usado (GB)} = \frac{\text{TotalSpace} - \text{FreeSpace}}{1024^3}$$

### C. Memoria RAM de la JVM / Contenedor (`ramContenedor`)
* **Implementación:** `Runtime.getRuntime()`.
* **Mecanismo:** Lee los parámetros de memoria gestionados por la Máquina Virtual de Java (JVM), ajustados a los límites de *Control Groups (cgroups)* de Linux asignados al contenedor:
  * `maxMemory()`: Límite máximo de memoria asignado al proceso Java (`-Xmx`).
  * `totalMemory()`: Memoria actualmente reservada por la JVM desde el sistema operativo.
  * `freeMemory()`: Memoria no utilizada dentro de la franja reservada por la JVM.
  * **Cálculo:**
    $$\text{RAM Usada (MB)} = \frac{\text{totalMemory} - \text{freeMemory}}{1024^2}$$

---

## 4. ESPECIFICACIÓN DE LA API REST

### Endpoint Principal

* **URL:** `/api/v1/info`
* **Método HTTP:** `GET`
* **Formato de Respuesta:** `application/json`

#### Respuesta de Ejemplo (`200 OK`)

```json
{
  "horaContenedor": "2026-10-29 10:25:30",
  "integrantes": [
    "Nombre Integrante 1",
    "Nombre Integrante 2",
    "Nombre Integrante 3"
  ],
  "idContenedor": "servicio-info-deployment-78b94f568-x9z2l",
  "almacenamientoContenedor": "Usado: 10.15 GB / Total: 49.12 GB (Libre: 38.97 GB)",
  "ramContenedor": "Usada: 42.80 MB / Reservada: 128.00 MB / Máxima Permitida: 512.00 MB"
}
```

