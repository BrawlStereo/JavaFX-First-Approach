# JavaFX Invoice (módulo de facturación)

Pequeña aplicación de escritorio para crear facturas usando JavaFX, FXML y un patrón simple MVC.

Contenido
- Código fuente Java en `src/main/java`.
- Vistas FXML en `src/main/resources`.
- Datos de ejemplo en `DataStore` (memoria).

Resumen

Esta aplicación muestra un módulo de facturación con:
- Lista de clientes y productos de ejemplo.
- Generación de folios (INV-YYYY-XXXX).
- Selección de productos y edición de cantidades.
- Totales reactivos (subtotal, IVA 16%, total).
- Historial en memoria de facturas (no persistente en disco por defecto).

Requisitos

- JDK 17 (se probó con OpenJDK 17).
- Maven 3.6+
- Conexión a internet la primera vez para descargar dependencias (OpenJFX).

Cómo clonar

Abre PowerShell y ejecuta:

```powershell
git clone https://github.com/BrawlStereo/JavaFX-First-Approach.git
cd "JavaFX"
``` 

(Reemplaza la URL por la de tu repositorio.)

Compilar y ejecutar (modo recomendado)

La forma más sencilla es usar el plugin de JavaFX para Maven que ya está configurado en el proyecto. Desde PowerShell en la raíz del proyecto:

```powershell
mvn -DskipTests javafx:run
```

Esto compilará el proyecto y lanzará la aplicación JavaFX.

Notas sobre ejecutar el JAR

El empaquetado tradicional `java -jar target/your-app.jar` puede requerir opciones adicionales para el módulo JavaFX o el uso de `jlink` para generar un runtime que incluya JavaFX. Para evitar complicaciones, recomendamos usar `mvn javafx:run`.

Empaquetar

Para compilar y empaquetar el proyecto (sin ejecutar):

```powershell
mvn -DskipTests package
```

Esto generará un JAR en `target/` (dependiendo de la configuración, puede requerir pasos extra para ejecutar como jar autónomo).