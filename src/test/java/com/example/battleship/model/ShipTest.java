package com.example.battleship.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la clase Ship.
 * Valida el correcto funcionamiento de los barcos en el juego.
 *
 * Casos de prueba cubiertos:
 * - Creación de barcos con diferentes tipos y tamaños
 * - Registro de impactos (hits)
 * - Lógica de hundimiento (sunk)
 * - Gestión de coordenadas del barco
 * - Casos límite (barco tamaño 0, múltiples hits)
 * - Serialización
 * - Validación de estados
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-10
 */
@DisplayName("Ship - Pruebas Unitarias")
class ShipTest {

    private Ship carrier;
    private Ship submarine;
    private Ship destroyer;
    private Ship frigate;

    @BeforeEach
    void setUp() {
        carrier = new Ship("Carrier", 4);
        submarine = new Ship("Submarine", 3);
        destroyer = new Ship("Destroyer", 2);
        frigate = new Ship("Frigate", 1);
    }

    @Nested
    @DisplayName("Pruebas de Creación de Barcos")
    class ShipCreationTests {

        @Test
        @DisplayName("Debe crear Carrier con tipo y tamaño correctos")
        void testCreateCarrier() {
            assertEquals("Carrier", carrier.getType(), "Tipo debe ser Carrier");
            assertEquals(4, carrier.getSize(), "Tamaño debe ser 4");
        }

        @Test
        @DisplayName("Debe crear Submarine con tipo y tamaño correctos")
        void testCreateSubmarine() {
            assertEquals("Submarine", submarine.getType(), "Tipo debe ser Submarine");
            assertEquals(3, submarine.getSize(), "Tamaño debe ser 3");
        }

        @Test
        @DisplayName("Debe crear Destroyer con tipo y tamaño correctos")
        void testCreateDestroyer() {
            assertEquals("Destroyer", destroyer.getType(), "Tipo debe ser Destroyer");
            assertEquals(2, destroyer.getSize(), "Tamaño debe ser 2");
        }

        @Test
        @DisplayName("Debe crear Frigate con tipo y tamaño correctos")
        void testCreateFrigate() {
            assertEquals("Frigate", frigate.getType(), "Tipo debe ser Frigate");
            assertEquals(1, frigate.getSize(), "Tamaño debe ser 1");
        }

        @Test
        @DisplayName("Debe crear barco personalizado con tipo y tamaño correctos")
        void testCreateCustomShip() {
            Ship custom = new Ship("Battleship", 5);
            assertEquals("Battleship", custom.getType(), "Tipo debe ser Battleship");
            assertEquals(5, custom.getSize(), "Tamaño debe ser 5");
            assertFalse(custom.isSunk(), "Barco nuevo no debe estar hundido");
        }

        @Test
        @DisplayName("Barco recién creado no debe estar hundido")
        void testNewShipNotSunk() {
            assertFalse(carrier.isSunk(), "Carrier nuevo no debe estar hundido");
            assertFalse(submarine.isSunk(), "Submarine nuevo no debe estar hundido");
            assertFalse(destroyer.isSunk(), "Destroyer nuevo no debe estar hundido");
            assertFalse(frigate.isSunk(), "Frigate nuevo no debe estar hundido");
        }

        @Test
        @DisplayName("Barco nuevo debe tener lista de coordenadas vacía")
        void testNewShipEmptyCoordinates() {
            assertTrue(carrier.getCoordinates().isEmpty(),
                    "Carrier nuevo debe tener lista de coordenadas vacía");
            assertEquals(0, carrier.getCoordinates().size(),
                    "Tamaño de lista debe ser 0");
        }

        @Test
        @DisplayName("Debe verificar estado inicial de todos los barcos")
        void testAllShipsInitialState() {
            Ship[] ships = {carrier, submarine, destroyer, frigate};

            for (Ship ship : ships) {
                assertNotNull(ship, "Barco no debe ser null");
                assertNotNull(ship.getType(), "Tipo no debe ser null");
                assertTrue(ship.getSize() > 0, "Tamaño debe ser positivo");
                assertFalse(ship.isSunk(), "Barco nuevo no debe estar hundido");
                assertTrue(ship.getCoordinates().isEmpty(), "Lista de coordenadas debe estar vacía");
            }
        }
    }

    @Nested
    @DisplayName("Pruebas de Registro de Impactos")
    class HitRegistrationTests {

        @Test
        @DisplayName("Carrier debe hundirse después de 4 impactos")
        void testCarrierSinksAfter4Hits() {
            assertFalse(carrier.isSunk(), "Carrier no debe estar hundido inicialmente");

            carrier.registerHit();
            assertFalse(carrier.isSunk(), "Carrier no debe hundirse con 1 hit");

            carrier.registerHit();
            assertFalse(carrier.isSunk(), "Carrier no debe hundirse con 2 hits");

            carrier.registerHit();
            assertFalse(carrier.isSunk(), "Carrier no debe hundirse con 3 hits");

            carrier.registerHit();
            assertTrue(carrier.isSunk(), "Carrier debe hundirse con 4 hits");
        }

        @Test
        @DisplayName("Submarine debe hundirse después de 3 impactos")
        void testSubmarineSinksAfter3Hits() {
            submarine.registerHit();
            assertFalse(submarine.isSunk(), "Submarine no debe hundirse con 1 hit");

            submarine.registerHit();
            assertFalse(submarine.isSunk(), "Submarine no debe hundirse con 2 hits");

            submarine.registerHit();
            assertTrue(submarine.isSunk(), "Submarine debe hundirse con 3 hits");
        }

        @Test
        @DisplayName("Destroyer debe hundirse después de 2 impactos")
        void testDestroyerSinksAfter2Hits() {
            destroyer.registerHit();
            assertFalse(destroyer.isSunk(), "Destroyer no debe hundirse con 1 hit");

            destroyer.registerHit();
            assertTrue(destroyer.isSunk(), "Destroyer debe hundirse con 2 hits");
        }

        @Test
        @DisplayName("Frigate debe hundirse después de 1 impacto")
        void testFrigateSinksAfter1Hit() {
            assertFalse(frigate.isSunk(), "Frigate no debe estar hundido inicialmente");

            frigate.registerHit();
            assertTrue(frigate.isSunk(), "Frigate debe hundirse con 1 hit");
        }

        @Test
        @DisplayName("Barco de tamaño 1 se hunde con 1 hit")
        void testSize1ShipSinks() {
            Ship tiny = new Ship("Tiny", 1);
            assertFalse(tiny.isSunk(), "No debe estar hundido inicialmente");
            tiny.registerHit();
            assertTrue(tiny.isSunk(), "Debe hundirse con 1 hit");
        }

        @Test
        @DisplayName("Barco de tamaño 2 se hunde con 2 hits")
        void testSize2ShipSinks() {
            Ship small = new Ship("Small", 2);
            small.registerHit();
            assertFalse(small.isSunk(), "No debe hundirse con 1 hit");
            small.registerHit();
            assertTrue(small.isSunk(), "Debe hundirse con 2 hits");
        }

        @Test
        @DisplayName("Barco de tamaño 3 se hunde con 3 hits")
        void testSize3ShipSinks() {
            Ship medium = new Ship("Medium", 3);
            medium.registerHit();
            medium.registerHit();
            assertFalse(medium.isSunk(), "No debe hundirse con 2 hits");
            medium.registerHit();
            assertTrue(medium.isSunk(), "Debe hundirse con 3 hits");
        }

        @Test
        @DisplayName("Barco de tamaño 5 se hunde con 5 hits")
        void testSize5ShipSinks() {
            Ship large = new Ship("Large", 5);
            for (int i = 0; i < 4; i++) {
                large.registerHit();
                assertFalse(large.isSunk(), "No debe hundirse con " + (i+1) + " hits");
            }
            large.registerHit();
            assertTrue(large.isSunk(), "Debe hundirse con 5 hits");
        }

        @Test
        @DisplayName("Múltiples hits más allá del tamaño no deben causar errores")
        void testExcessiveHits() {
            frigate.registerHit();
            assertTrue(frigate.isSunk(), "Frigate debe estar hundido después de 1 hit");

            // Aplicar hits adicionales
            frigate.registerHit();
            frigate.registerHit();
            frigate.registerHit();

            assertTrue(frigate.isSunk(), "Frigate debe seguir hundido después de hits excesivos");
        }

        @Test
        @DisplayName("Barco debe mantenerse hundido después de estar hundido")
        void testShipStaysSunk() {
            destroyer.registerHit();
            destroyer.registerHit();
            assertTrue(destroyer.isSunk(), "Destroyer debe estar hundido");

            assertTrue(destroyer.isSunk(), "Destroyer debe seguir hundido");
            assertTrue(destroyer.isSunk(), "Destroyer debe seguir hundido en múltiples verificaciones");
        }
    }

    @Nested
    @DisplayName("Pruebas de Gestión de Coordenadas")
    class CoordinateManagementTests {

        @Test
        @DisplayName("Debe agregar coordenadas al barco")
        void testAddCoordinate() {
            Coordinate coord1 = new Coordinate(0, 0);

            carrier.addCoordinate(coord1);

            assertEquals(1, carrier.getCoordinates().size(),
                    "Barco debe tener 1 coordenada");
            assertTrue(carrier.getCoordinates().contains(coord1),
                    "Barco debe contener la coordenada agregada");
        }

        @Test
        @DisplayName("Debe agregar múltiples coordenadas")
        void testAddMultipleCoordinates() {
            Coordinate coord1 = new Coordinate(0, 0);
            Coordinate coord2 = new Coordinate(0, 1);
            Coordinate coord3 = new Coordinate(0, 2);

            submarine.addCoordinate(coord1);
            submarine.addCoordinate(coord2);
            submarine.addCoordinate(coord3);

            assertEquals(3, submarine.getCoordinates().size(),
                    "Submarine debe tener 3 coordenadas");
            assertTrue(submarine.getCoordinates().contains(coord1), "Debe contener coord1");
            assertTrue(submarine.getCoordinates().contains(coord2), "Debe contener coord2");
            assertTrue(submarine.getCoordinates().contains(coord3), "Debe contener coord3");
        }

        @Test
        @DisplayName("Debe mantener el orden de las coordenadas")
        void testCoordinateOrder() {
            Coordinate coord1 = new Coordinate(0, 0);
            Coordinate coord2 = new Coordinate(0, 1);
            Coordinate coord3 = new Coordinate(0, 2);

            destroyer.addCoordinate(coord1);
            destroyer.addCoordinate(coord2);
            destroyer.addCoordinate(coord3);

            List<Coordinate> coords = destroyer.getCoordinates();
            assertEquals(coord1, coords.get(0), "Primera coordenada debe ser coord1");
            assertEquals(coord2, coords.get(1), "Segunda coordenada debe ser coord2");
            assertEquals(coord3, coords.get(2), "Tercera coordenada debe ser coord3");
        }

        @Test
        @DisplayName("getCoordinates() debe retornar la misma lista")
        void testGetCoordinatesReturnsSameList() {
            Coordinate coord1 = new Coordinate(5, 5);
            carrier.addCoordinate(coord1);

            List<Coordinate> list1 = carrier.getCoordinates();
            List<Coordinate> list2 = carrier.getCoordinates();

            assertSame(list1, list2,
                    "getCoordinates() debe retornar la misma instancia de lista");
        }

        @Test
        @DisplayName("Debe agregar coordenadas en posiciones horizontales")
        void testHorizontalCoordinates() {
            carrier.addCoordinate(new Coordinate(5, 0));
            carrier.addCoordinate(new Coordinate(5, 1));
            carrier.addCoordinate(new Coordinate(5, 2));
            carrier.addCoordinate(new Coordinate(5, 3));

            assertEquals(4, carrier.getCoordinates().size(),
                    "Carrier debe tener 4 coordenadas");

            for (Coordinate coord : carrier.getCoordinates()) {
                assertEquals(5, coord.getRow(),
                        "Todas las coordenadas deben estar en la fila 5");
            }
        }

        @Test
        @DisplayName("Debe agregar coordenadas en posiciones verticales")
        void testVerticalCoordinates() {
            destroyer.addCoordinate(new Coordinate(0, 7));
            destroyer.addCoordinate(new Coordinate(1, 7));

            assertEquals(2, destroyer.getCoordinates().size(),
                    "Destroyer debe tener 2 coordenadas");

            for (Coordinate coord : destroyer.getCoordinates()) {
                assertEquals(7, coord.getCol(),
                        "Todas las coordenadas deben estar en la columna 7");
            }
        }

        @Test
        @DisplayName("Debe poder agregar coordenadas en esquinas del tablero")
        void testCornerCoordinates() {
            Ship testShip = new Ship("Test", 4);
            testShip.addCoordinate(new Coordinate(0, 0));
            testShip.addCoordinate(new Coordinate(0, 9));
            testShip.addCoordinate(new Coordinate(9, 0));
            testShip.addCoordinate(new Coordinate(9, 9));

            assertEquals(4, testShip.getCoordinates().size(), "Debe tener 4 coordenadas");
        }
    }

    @Nested
    @DisplayName("Pruebas de Casos Límite")
    class BoundaryTests {

        @Test
        @DisplayName("Barco con tamaño 0 debe estar hundido inmediatamente")
        void testZeroSizeShip() {
            Ship tinyShip = new Ship("Tiny", 0);

            assertEquals(0, tinyShip.getSize(), "Tamaño debe ser 0");
            assertTrue(tinyShip.isSunk(),
                    "Barco de tamaño 0 debe estar hundido inmediatamente");
        }

        @Test
        @DisplayName("Barco con tamaño muy grande debe funcionar correctamente")
        void testLargeSizeShip() {
            Ship megaShip = new Ship("Mega", 10);

            assertEquals(10, megaShip.getSize(), "Tamaño debe ser 10");
            assertFalse(megaShip.isSunk(), "No debe estar hundido inicialmente");

            for (int i = 0; i < 9; i++) {
                megaShip.registerHit();
                assertFalse(megaShip.isSunk(),
                        "No debe hundirse con " + (i + 1) + " hits");
            }

            megaShip.registerHit();
            assertTrue(megaShip.isSunk(), "Debe hundirse con 10 hits");
        }

        @Test
        @DisplayName("Barco con tipo vacío debe funcionar")
        void testEmptyTypeShip() {
            Ship ship = new Ship("", 2);
            assertEquals("", ship.getType(), "Tipo debe ser cadena vacía");
            assertEquals(2, ship.getSize(), "Tamaño debe ser 2");
        }

        @Test
        @DisplayName("Barco con tipo especial debe funcionar")
        void testSpecialCharacterType() {
            Ship ship = new Ship("SuperShip-2000!", 3);
            assertEquals("SuperShip-2000!", ship.getType(),
                    "Tipo con caracteres especiales debe preservarse");
        }

        @Test
        @DisplayName("Barco con tamaño 1 debe funcionar correctamente")
        void testMinimalShip() {
            Ship minimal = new Ship("Minimal", 1);
            assertEquals(1, minimal.getSize(), "Tamaño debe ser 1");
            assertFalse(minimal.isSunk(), "No debe estar hundido inicialmente");
            minimal.registerHit();
            assertTrue(minimal.isSunk(), "Debe hundirse con 1 hit");
        }
    }

    @Nested
    @DisplayName("Pruebas de Lógica de Hundimiento")
    class SinkingLogicTests {

        @Test
        @DisplayName("isSunk() debe retornar false antes de alcanzar el tamaño")
        void testNotSunkBeforeSize() {
            carrier.registerHit();
            carrier.registerHit();
            carrier.registerHit();

            assertFalse(carrier.isSunk(),
                    "Carrier no debe estar hundido con 3 hits de 4");
        }

        @Test
        @DisplayName("isSunk() debe retornar true al alcanzar exactamente el tamaño")
        void testSunkAtExactSize() {
            for (int i = 0; i < destroyer.getSize(); i++) {
                destroyer.registerHit();
            }

            assertTrue(destroyer.isSunk(),
                    "Destroyer debe estar hundido al recibir exactamente 2 hits");
        }

        @Test
        @DisplayName("isSunk() debe retornar true después de exceder el tamaño")
        void testSunkAfterExceedingSize() {
            for (int i = 0; i < frigate.getSize() + 5; i++) {
                frigate.registerHit();
            }

            assertTrue(frigate.isSunk(),
                    "Frigate debe estar hundido después de exceder los hits necesarios");
        }

        @Test
        @DisplayName("Barco sin hits no debe estar hundido (excepto tamaño 0)")
        void testNoHitsNotSunk() {
            Ship ship = new Ship("Normal", 3);
            assertFalse(ship.isSunk(),
                    "Barco sin hits no debe estar hundido");
        }

        @Test
        @DisplayName("Debe verificar hundimiento progresivo")
        void testProgressiveSinking() {
            Ship testShip = new Ship("Test", 4);

            assertFalse(testShip.isSunk(), "0 hits - no hundido");
            testShip.registerHit();
            assertFalse(testShip.isSunk(), "1 hit - no hundido");
            testShip.registerHit();
            assertFalse(testShip.isSunk(), "2 hits - no hundido");
            testShip.registerHit();
            assertFalse(testShip.isSunk(), "3 hits - no hundido");
            testShip.registerHit();
            assertTrue(testShip.isSunk(), "4 hits - hundido");
        }
    }

    @Nested
    @DisplayName("Pruebas de Serialización")
    class SerializationTests {

        @Test
        @DisplayName("Ship debe ser Serializable")
        void testSerializable() {
            assertTrue(carrier instanceof java.io.Serializable,
                    "Ship debe implementar Serializable");
        }

        @Test
        @DisplayName("Debe mantener el estado después de simular serialización")
        void testStatePreservation() {
            carrier.registerHit();
            carrier.registerHit();
            carrier.addCoordinate(new Coordinate(0, 0));
            carrier.addCoordinate(new Coordinate(0, 1));

            assertFalse(carrier.isSunk(), "Estado de hundimiento debe mantenerse");
            assertEquals(2, carrier.getCoordinates().size(), "Coordenadas deben mantenerse");
            assertEquals("Carrier", carrier.getType(), "Tipo debe mantenerse");
            assertEquals(4, carrier.getSize(), "Tamaño debe mantenerse");
        }
    }

    @Nested
    @DisplayName("Pruebas de Integración Barco-Coordenadas")
    class ShipCoordinateIntegrationTests {

        @Test
        @DisplayName("Barco completamente configurado debe tener todas las propiedades correctas")
        void testFullyConfiguredShip() {
            Ship ship = new Ship("TestShip", 3);
            ship.addCoordinate(new Coordinate(0, 0));
            ship.addCoordinate(new Coordinate(0, 1));
            ship.addCoordinate(new Coordinate(0, 2));

            assertEquals("TestShip", ship.getType(), "Tipo correcto");
            assertEquals(3, ship.getSize(), "Tamaño correcto");
            assertEquals(3, ship.getCoordinates().size(), "Número de coordenadas correcto");
            assertFalse(ship.isSunk(), "No debe estar hundido");

            ship.registerHit();
            ship.registerHit();
            ship.registerHit();

            assertTrue(ship.isSunk(), "Debe estar hundido después de 3 hits");
            assertEquals(3, ship.getCoordinates().size(),
                    "Coordenadas deben mantenerse después de hundirse");
        }

        @Test
        @DisplayName("Barco horizontal debe tener coordenadas en la misma fila")
        void testHorizontalShipCoordinates() {
            destroyer.addCoordinate(new Coordinate(3, 2));
            destroyer.addCoordinate(new Coordinate(3, 3));

            List<Coordinate> coords = destroyer.getCoordinates();
            assertEquals(coords.get(0).getRow(), coords.get(1).getRow(),
                    "Coordenadas horizontales deben estar en la misma fila");
        }

        @Test
        @DisplayName("Barco vertical debe tener coordenadas en la misma columna")
        void testVerticalShipCoordinates() {
            submarine.addCoordinate(new Coordinate(5, 7));
            submarine.addCoordinate(new Coordinate(6, 7));
            submarine.addCoordinate(new Coordinate(7, 7));

            List<Coordinate> coords = submarine.getCoordinates();
            assertEquals(coords.get(0).getCol(), coords.get(1).getCol(),
                    "Coordenadas verticales deben estar en la misma columna");
            assertEquals(coords.get(1).getCol(), coords.get(2).getCol(),
                    "Todas las coordenadas verticales deben estar en la misma columna");
        }

        @Test
        @DisplayName("Barco con coordenadas puede ser hundido correctamente")
        void testShipWithCoordinatesCanSink() {
            Ship ship = new Ship("Test", 2);
            ship.addCoordinate(new Coordinate(1, 1));
            ship.addCoordinate(new Coordinate(1, 2));

            assertEquals(2, ship.getCoordinates().size(), "Debe tener 2 coordenadas");
            assertFalse(ship.isSunk(), "No debe estar hundido");

            ship.registerHit();
            ship.registerHit();

            assertTrue(ship.isSunk(), "Debe estar hundido");
            assertEquals(2, ship.getCoordinates().size(),
                    "Coordenadas deben permanecer después de hundirse");
        }
    }
}