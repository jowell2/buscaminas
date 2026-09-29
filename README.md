# Buscaminas

Clon completo de Minesweeper en Java Swing, con el arte dibujado desde cero en
`Graphics2D` — sin usar ni un solo componente visual por defecto.

**622 líneas** · Java SE + Swing + Graphics2D

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)

## Lo que lo hace distinto

Cada botón del tablero sobrescribe `paintComponent` y dibuja su propio rectángulo
redondeado con `fillRoundRect`, usando `RenderingHints` para el antialiasing. El color
cambia según `getModel().isPressed()` y `isRollover()`, así que tiene estados de hover
y presión propios, en lugar del estilo del sistema operativo.

El tema es oscuro, con una paleta de 8 colores y 8 colores distintos para los números
del tablero.

## Compilar y ejecutar

Requiere **JDK 8 o superior**. No tiene dependencias.

```bash
javac juego.java
java juego.juego
```

> La clase declara `package juego;`, por eso se ejecuta con el nombre completo
> `juego.juego` y no con `juego`.

## Reglas

- El **primer clic nunca explota**: las minas se colocan después de la primera
  pulsación
- **Clic izquierdo** revela una celda
- **Clic derecho** coloca o quita una bandera
- Las celdas vacías se revelan **en cascada**, arrastrando las vecinas
- El cronómetro arranca en el primer clic y se detiene al ganar o perder

## Dificultades

| Nivel | Tablero | Minas |
|---|---|---|
| Fácil | 5 × 5 | 10 |
| Medio | 15 × 15 | 15 |
| Difícil | 25 × 25 | 90 |

El tamaño de celda y el grosor de los bordes se ajustan según la dificultad para que el
tablero siempre entre en pantalla.

## Celda central bloqueada

El autor añade una mecánica no estándar: la celda central del tablero es intocable. No
se puede revelar ni marcar, queda permanentemente deshabilitada con un emoji de
obstrucción sobre fondo naranja, y `colocarMinas` la excluye de la colocación.

Como consecuencia, el tablero nunca se gana al 100%: siempre queda una celda menos por
revelar. Es una decisión deliberada, no un descuido.

## Atajos de teclado

| Tecla | Acción |
|---|---|
| `F2` | Nuevo juego |
| `ESC` | Volver al menú principal |
| `Alt` + `F4` | Salir |

## Detalles de implementación

- **`CardLayout`** para navegar entre el lobby y el tablero dentro del mismo `JFrame`,
  sin abrir y cerrar ventanas
- **Look-and-Feel cross-platform** forzado en el `main()` para que la aplicación se vea
  igual en cualquier sistema operativo
- **`javax.swing.Timer`** con evento cada 1000 ms para el cronómetro, formateado como
  `%02d:%02d`
- **`MouseAdapter.mousePressed`** en lugar de `mouseReleased`, para que la respuesta al
  clic sea inmediata
- **Colores por número** con un arreglo de 8 colores, y los estados de hover y presión
  leídos del modelo del botón

## Autor

**Jowell Acosta** — [GitHub](https://github.com/jowell2)
