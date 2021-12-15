package agh.ics.oop;

import agh.ics.oop.interfaces.IWorldMap;
import agh.ics.oop.mapElements.Animal;
import agh.ics.oop.mapElements.Vector2d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RectangularMapTest {
//    Animal animal1;
//    Animal animal2;
//    IWorldMap map;
//
//    @Test
//    void objectAt(){
//        Assertions.assertEquals(animal1, map.objectAt(new Vector2d(1, 1)));
//        Assertions.assertEquals(animal2, map.objectAt(animal2.getPosition()));
//        Assertions.assertNull(map.objectAt(new Vector2d(3,3)));
//    }
//
//    @Test
//    void isOccupied(){
//        Assertions.assertTrue(map.isOccupied(new Vector2d(2,2)));
//        Assertions.assertFalse(map.isOccupied(new Vector2d(3, 2)));
//        Assertions.assertTrue(map.isOccupied(new Vector2d(1, 1)));
//        Assertions.assertFalse(map.isOccupied(new Vector2d(0, 5)));
//    }
//
//    @Test
//    void place(){
//        Assertions.assertTrue(map.placeAnimal(new Animal(map, new Vector2d(0,0))));
//        Assertions.assertThrows(IllegalArgumentException.class ,() -> map.placeAnimal(new Animal(map, new Vector2d(0,0))));
//        Assertions.assertThrows(IllegalArgumentException.class ,() -> map.placeAnimal(new Animal(map, new Vector2d(15, 5))));
//        Assertions.assertThrows(IllegalArgumentException.class ,() -> map.placeAnimal(new Animal(map, new Vector2d(14, 6))));
//        Assertions.assertThrows(IllegalArgumentException.class ,() -> map.placeAnimal(new Animal(map, new Vector2d(-1, 1))));
//        Assertions.assertThrows(IllegalArgumentException.class ,() -> map.placeAnimal(new Animal(map, new Vector2d(-1, -1))));
//        Assertions.assertThrows(IllegalArgumentException.class ,() -> map.placeAnimal(new Animal(map, new Vector2d(1, -1))));
//    }
//
//    @Test
//    void canMoveTo(){
//        Assertions.assertTrue(map.canMoveTo(new Vector2d(2,3)));
//        Assertions.assertTrue(map.canMoveTo(new Vector2d(14,1)));
//        Assertions.assertTrue(map.canMoveTo(new Vector2d(14,5)));
//        Assertions.assertTrue(map.canMoveTo(new Vector2d(0,0)));
//        Assertions.assertFalse(map.canMoveTo(new Vector2d(2,2)));
//        Assertions.assertFalse(map.canMoveTo(new Vector2d(1,1)));
//        Assertions.assertFalse(map.canMoveTo(new Vector2d(-1,-1)));
//        Assertions.assertFalse(map.canMoveTo(new Vector2d(-1,1)));
//        Assertions.assertFalse(map.canMoveTo(new Vector2d(1,-1)));
//        Assertions.assertFalse(map.canMoveTo(new Vector2d(2,2)));
//        Assertions.assertFalse(map.canMoveTo(new Vector2d(16,1)));
//        Assertions.assertFalse(map.canMoveTo(new Vector2d(1,6)));
//    }
//
//
//    @BeforeEach
//    void setUp(){
//        map = new RectangularMap(15, 6);
//        animal1 = new Animal(map, new Vector2d(1, 1));
//        animal2 = new Animal(map, new Vector2d(2, 2));
//        map.placeAnimal(animal1);
//        map.placeAnimal(animal2);
//    }
}