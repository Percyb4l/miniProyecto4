# ⚓ Batalla Naval - JavaFX Battleship Game

![Java](https://img.shields.io/badge/Java-17-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.14-blue)
![Maven](https://img.shields.io/badge/Maven-3.8.5-red)
![JUnit](https://img.shields.io/badge/JUnit-5.12.1-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Instalación](#-instalación)
- [Uso](#-uso)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Patrones de Diseño](#-patrones-de-diseño)
- [Persistencia de Datos](#-persistencia-de-datos)
- [Pruebas Unitarias](#-pruebas-unitarias)
- [Javadoc](#-javadoc)
- [Contribuidores](#-contribuidores)
- [Licencia](#-licencia)

---

## 🎮 Descripción

**Batalla Naval** es una implementación moderna del clásico juego de estrategia naval, desarrollada en Java con JavaFX. El juego permite enfrentarse a una **Inteligencia Artificial** con tres niveles de dificultad, ofreciendo una experiencia visual rica con **gráficos 2D detallados** y una interfaz de usuario intuitiva.

### 🎯 Objetivo del Proyecto

Desarrollar un juego completo aplicando:
- ✅ Arquitectura **MVC** (Modelo-Vista-Controlador)
- ✅ **Patrones de diseño** (Singleton, Observer, Strategy, Factory)
- ✅ **Principios SOLID**
- ✅ **Programación orientada a eventos**
- ✅ **Persistencia de datos** (serialización y archivos planos)
- ✅ **Concurrencia** con hilos
- ✅ **Figuras 2D** personalizadas
- ✅ **Pruebas unitarias** con JUnit 5

---

## ✨ Características

### 🎨 Interfaz Gráfica
- **Diseño moderno** con gradientes y efectos visuales
- **Preview de barcos** antes de colocarlos
- **Atajos de teclado**: `R` (rotar), `Space` (iniciar), `P` (pausa), `ESC` (pausa)
- **Animaciones suaves** para disparos y hundimientos
- **Log de eventos** en tiempo real

### 🤖 Inteligencia Artificial
- **Easy**: Disparos aleatorios
- **Medium**: Ataca celdas adyacentes después de un impacto
- **Hard**: Estrategia Hunt & Target con patrón checkerboard

### 🎯 Mecánicas de Juego
- **10 barcos por jugador**:
  - 1 Portaaviones (4 celdas)
  - 2 Submarinos (3 celdas cada uno)
  - 3 Destructores (2 celdas cada uno)
  - 4 Fragatas (1 celda cada una)
- **Colocación inteligente** con validación de límites y superposiciones
- **Sistema de turnos** automático
- **Guardado automático** después de cada jugada

### 💾 Persistencia
- **Archivos serializables** para guardar/cargar partidas
- **Archivos planos** para estadísticas del jugador
- **Continuar partida** desde donde se dejó

### 🔍 Modo Verificación
- **Visualización del tablero enemigo** para profesores (HU-3)
- Activable mediante checkbox en la interfaz

---

## 🛠 Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 17+ | Lenguaje de programación |
| **JavaFX** | 17.0.14 | Framework de interfaz gráfica |
| **Maven** | 3.8.5 | Gestión de dependencias |
| **JUnit 5** | 5.12.1 | Pruebas unitarias |
| **Scene Builder** | 21.0+ | Diseño de interfaces FXML |
| **IntelliJ IDEA** | 2024+ | IDE recomendado |

---

## 🏗 Arquitectura

### Patrón MVC

```
┌─────────────────────────────────────────┐
│              VISTA (View)               │
│  - MenuView.fxml                        │
│  - GameView.fxml                        │
│  - GameOverView.fxml                    │
│  - CellRenderer.java (Figuras 2D)       │
└─────────────────┬───────────────────────┘
                  │
                  │ Eventos
                  ▼
┌─────────────────────────────────────────┐
│         CONTROLADOR (Controller)        │
│  - GameController.java                  │
│  - ViewController.java                  │
│  - MenuController.java                  │
│  - NavigationController.java            │
│  - GameSession.java (Singleton)         │
└─────────────────┬───────────────────────┘
                  │
                  │ Lógica
                  ▼
┌─────────────────────────────────────────┐
│            MODELO (Model)               │
│  - Board.java                           │
│  - Ship.java                            │
│  - Coordinate.java                      │
└─────────────────────────────────────────┘
```

---

## 📦 Instalación

### Requisitos Previos

- **JDK 17** o superior ([Descargar](https://adoptium.net/))
- **Maven 3.8+** ([Descargar](https://maven.apache.org/download.cgi))
- **Git** ([Descargar](https://git-scm.com/downloads))

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/batalla-naval.git
cd batalla-naval
```

2. **Compilar el proyecto**
```bash
mvn clean compile
```

3. **Ejecutar las pruebas**
```bash
mvn test
```

4. **Ejecutar la aplicación**
```bash
mvn javafx:run
```

### Instalación Alternativa (IntelliJ IDEA)

1. Abrir IntelliJ IDEA
2. `File` → `Open` → Seleccionar carpeta del proyecto
3. Esperar a que Maven descargue las dependencias
4. Click derecho en `Main.java` → `Run 'Main.main()'`

---

## 🎮 Uso

### Iniciar una Nueva Partida

1. **Menú Principal**: Click en `NEW GAME`
2. **Ingresar Nickname**: Escribir nombre del comandante
3. **Seleccionar Dificultad**: Click en `DIFFICULTY` para cambiar entre Easy/Medium/Hard
4. **Colocar Barcos**:
   - Click en el tablero para colocar barcos
   - Presionar `R` para rotar entre horizontal/vertical
   - Usar el preview para ver el barco actual
5. **Iniciar Batalla**: Click en `START GAME` o presionar `Space`
6. **Disparar**: Click en las celdas del tablero enemigo

### Atajos de Teclado

| Tecla | Acción |
|-------|--------|
| `R` | Rotar barco |
| `Space` | Iniciar juego |
| `P` | Pausar |
| `ESC` | Pausar |

### Continuar Partida Guardada

1. Desde el menú principal, click en `CONTINUE GAME`
2. La partida se cargará automáticamente

---

## 📁 Estructura del Proyecto

```
batallaNaval/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/battleship/
│   │   │       ├── controller/           # Controladores MVC
│   │   │       │   ├── GameController.java
│   │   │       │   ├── ViewController.java
│   │   │       │   ├── MenuController.java
│   │   │       │   ├── GameOverController.java
│   │   │       │   ├── NavigationController.java
│   │   │       │   └── GameSession.java
│   │   │       ├── model/                # Modelos de datos
│   │   │       │   ├── Board.java
│   │   │       │   ├── Ship.java
│   │   │       │   ├── Coordinate.java
│   │   │       │   └── GameState.java
│   │   │       ├── view/                 # Renderizado visual
│   │   │       │   ├── CellRenderer.java
│   │   │       │   └── ShipPreviewPanel.java
│   │   │       ├── patterns/             # Patrones de diseño
│   │   │       │   ├── GameObserver.java
│   │   │       │   ├── ShootingStrategy.java
│   │   │       │   ├── RandomStrategy.java
│   │   │       │   ├── SmartStrategy.java
│   │   │       │   └── HuntTargetStrategy.java
│   │   │       ├── exceptions/           # Excepciones personalizadas
│   │   │       │   └── InvalidShipPlacementException.java
│   │   │       ├── interfaces/           # Interfaces del sistema
│   │   │       │   └── IBattleShipGame.java
│   │   │       ├── util/                 # Utilidades
│   │   │       │   ├── ArchivoUtil.java
│   │   │       │   └── ShipFactory.java
│   │   │       └── Main.java             # Punto de entrada
│   │   └── resources/
│   │       └── com/example/battleship/
│   │           ├── MenuView.fxml
│   │           ├── GameView.fxml
│   │           └── GameOverView.fxml
│   └── test/
│       └── java/
│           └── com/example/battleship/
│               └── model/
│                   ├── BoardTest.java
│                   ├── ShipTest.java
│                   ├── CoordinateTest.java
│                   └── GameSessionTest.java
├── battleship_data/                     # Datos persistentes
│   ├── game.ser                         # Partida guardada
│   └── score.txt                        # Estadísticas
├── pom.xml                              # Configuración Maven
└── README.md                            # Este archivo
```

---

## 🎨 Patrones de Diseño

### 1. **Singleton Pattern**

**Ubicación**: `GameSession.java`, `NavigationController.java`

```java
public class GameSession {
    private static GameSession instance;
    
    private GameSession() {}
    
    public static GameSession getInstance() {
        if (instance == null) {
            synchronized (GameSession.class) {
                if (instance == null) {
                    instance = new GameSession();
                }
            }
        }
        return instance;
    }
}
```

**Propósito**: Garantizar una única instancia de la sesión de juego y navegación.

---

### 2. **Observer Pattern**

**Ubicación**: `GameObserver.java`, `GameController.java`

```java
public interface GameObserver {
    void onBoardChanged(boolean isPlayerBoard);
    void onShotFired(boolean isHit, boolean isSunk);
    void onGameOver(boolean playerWon);
    void onTurnChanged(boolean isPlayerTurn);
}
```

**Propósito**: Desacoplar la lógica del juego de la interfaz de usuario mediante notificaciones.

---

### 3. **Strategy Pattern**

**Ubicación**: `ShootingStrategy.java` + implementaciones

```java
public interface ShootingStrategy {
    Coordinate getNextShot(Board board);
}

// Implementaciones:
// - RandomStrategy (Easy)
// - SmartStrategy (Medium)
// - HuntTargetStrategy (Hard)
```

**Propósito**: Permitir cambiar el algoritmo de IA dinámicamente según la dificultad.

---

### 4. **Factory Pattern**

**Ubicación**: `ShipFactory.java`

```java
public class ShipFactory {
    public static Ship createShip(String type) {
        switch (type.toUpperCase()) {
            case "CARRIER": return new Ship("Carrier", 4);
            case "SUBMARINE": return new Ship("Submarine", 3);
            // ...
        }
    }
}
```

**Propósito**: Centralizar la creación de barcos con tipos específicos.

---

## 💾 Persistencia de Datos

### Archivos Serializables

**Archivo**: `battleship_data/game.ser`

**Contenido**:
- Tablero del jugador (`Board` serializado)
- Tablero de la máquina (`Board` serializado)
- Turno actual (`boolean`)

**Implementación**: `ArchivoUtil.java` → `saveGame()`, `loadGame()`

### Archivos Planos

**Archivo**: `battleship_data/score.txt`

**Formato**:
```
Nickname: Admiral
Barcos Hundidos: 10
Fecha: 2025-12-09T20:02:04.548157900
```

**Implementación**: `ArchivoUtil.java` → `saveScore()`

---

## 🧪 Pruebas Unitarias

### Ejecución de Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar con cobertura (opcional)
mvn test jacoco:report
```

### Clases de Prueba

| Archivo | Cobertura | Pruebas |
|---------|-----------|---------|
| `BoardTest.java` | Validación de tableros | 30+ tests |
| `ShipTest.java` | Lógica de barcos | 25+ tests |
| `CoordinateTest.java` | Coordenadas y hashing | 35+ tests |
| `GameSessionTest.java` | Singleton y sesión | 30+ tests |

### Ejemplo de Prueba

```java
@Test
@DisplayName("Should sink carrier after 4 hits")
void testSinkCarrier() {
    Ship carrier = new Ship("Carrier", 4);
    
    carrier.registerHit();
    carrier.registerHit();
    carrier.registerHit();
    assertFalse(carrier.isSunk());
    
    carrier.registerHit();
    assertTrue(carrier.isSunk());
}
```

---

## 📚 Javadoc

### Generar Documentación

```bash
# Generar Javadoc HTML
mvn javadoc:javadoc

# Abrir en navegador
# Windows:
start target/site/apidocs/index.html

# macOS:
open target/site/apidocs/index.html

# Linux:
xdg-open target/site/apidocs/index.html
```

### Ubicación

La documentación se generará en: `target/site/apidocs/`

### Ejemplo de Documentación

```java
/**
 * Main controller for the Battleship game.
 * Handles game logic, AI turns, persistence, and observer notifications.
 * Implements MVC architecture and Observer/Strategy patterns.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-07
 */
public class GameController implements IBattleShipGame {
    // ...
}
```

---

## 🎨 Figuras 2D

### Barcos Renderizados

El juego utiliza **130+ figuras 2D** para renderizar barcos realistas con efecto 3D:

#### Figuras JavaFX Utilizadas:
- **Rectangle**: Cascos, cubiertas, torres
- **Polygon**: Proas, aviones de combate, alas
- **Ellipse**: Submarinos, hélices, fragatas
- **Circle**: Radares, explosiones, torpedos
- **Line**: Cañones, cruces (hundido/agua)
- **Path**: Olas curvas del océano

#### Efectos Visuales:
- **LinearGradient**: Degradados del océano
- **RadialGradient**: Explosiones, luces
- **DropShadow**: Sombras proyectadas (efecto 3D)
- **InnerShadow**: Profundidad acuática

### Ejemplo de Código

```java
// Portaaviones con efecto 3D
Polygon hullBottom = new Polygon(-15, 2, -15, 11, 15, 11, 15, 2);
hullBottom.setFill(HULL_SHADOW);

Polygon deckTop = new Polygon(-15, -11, 15, -11, 15, 2, -15, 2);
deckTop.setFill(HULL_TOP);

DropShadow shadow = new DropShadow();
shadow.setRadius(3);
deckTop.setEffect(shadow);
```

---

## 🚀 Características Técnicas

### Principios SOLID

✅ **Single Responsibility**: Cada clase tiene una única responsabilidad  
✅ **Open/Closed**: Estrategias extensibles sin modificar código existente  
✅ **Liskov Substitution**: Cualquier estrategia es intercambiable  
✅ **Interface Segregation**: Interfaces específicas y mínimas  
✅ **Dependency Inversion**: Depende de abstracciones, no implementaciones

### Estructuras de Datos

- **HashMap**: Almacenamiento de estado del tablero (O(1))
- **ArrayList**: Coordenadas de barcos, observadores
- **LinkedList (Queue)**: Cola de barcos a colocar (FIFO)
- **HashSet**: Validación de unicidad

### Concurrencia

- **Hilo principal (JavaFX)**: Renderizado de UI
- **Hilo secundario**: Turno de la máquina con pausas
- **Platform.runLater()**: Sincronización con UI thread

---

## 👥 Contribuidores

- **Battleship Team** - Desarrollo completo
- **Profesor/Instructor** - Supervisión y requisitos

---

## 📄 Licencia

Este proyecto fue desarrollado como parte de un proyecto académico.

**MIT License** - Siéntete libre de usar este código para propósitos educativos.

---

## 🐛 Reporte de Bugs

Si encuentras un bug, por favor crea un issue en GitHub con:

1. Descripción del problema
2. Pasos para reproducirlo
3. Comportamiento esperado vs. actual
4. Screenshots (opcional)

---

## 📞 Contacto

Para preguntas o sugerencias:
- **GitHub Issues**: [Crear issue](https://github.com/tu-usuario/batalla-naval/issues)
- **Email**: tu-email@ejemplo.com

---

## 🎯 Roadmap Futuro

- [ ] Modo multijugador en red
- [ ] Más niveles de dificultad
- [ ] Efectos de sonido
- [ ] Tablas de clasificación global
- [ ] Personalización de flotas
- [ ] Modo campaña con misiones

---

## 🌟 Agradecimientos

Gracias a:
- **OpenJFX Team** por JavaFX
- **JUnit Team** por el framework de testing
- **Maven Community** por la gestión de dependencias

---

<div align="center">

**⚓ Hecho con ❤️ por el Battleship Team ⚓**

[⬆ Volver arriba](#-batalla-naval---javafx-battleship-game)

</div>
