package agh.ics.oop;

import agh.ics.oop.mapElements.Vector2d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Vector2dTest {
    @Test
    void testEquals(){
//        WARTOSCI OFFSET MUSZA BYC >= 0
        int offset_x = 1;
        int offset_y = 1;
        Vector2d vector = new Vector2d(3,4);
        Assertions.assertNotEquals(new Object(), vector);
        Assertions.assertNotEquals(vector, new Vector2d(vector.x+offset_x, vector.y));
        Assertions.assertNotEquals(vector, new Vector2d(vector.x+offset_x, vector.y+offset_y));
        Assertions.assertNotEquals(vector, new Vector2d(vector.x, vector.y+offset_y));
        Assertions.assertNotEquals(vector, new Vector2d(vector.x-offset_x, vector.y+offset_y));
        Assertions.assertNotEquals(vector, new Vector2d(vector.x-offset_x, vector.y));
        Assertions.assertNotEquals(vector, new Vector2d(vector.x-offset_x, vector.y-offset_y));
        Assertions.assertNotEquals(vector, new Vector2d(vector.x, vector.y-offset_y));
        Assertions.assertNotEquals(vector, new Vector2d(vector.x+offset_x, vector.y-offset_y));
        Assertions.assertEquals(vector, vector);
        Assertions.assertEquals(vector, new Vector2d(vector.x, vector.y));


    }

    @Test
    void testToString(){

        Assertions.assertEquals("(3, 4)", new Vector2d(3,4).toString());
        Assertions.assertEquals("(-3, 4)", new Vector2d(-3,4).toString());
        Assertions.assertEquals("(3, -4)", new Vector2d(3,-4).toString());
        Assertions.assertEquals("(-3, -4)", new Vector2d(-3,-4).toString());

    }

    @Test
    void testPrecedes(){
        Vector2d vector = new Vector2d(3, 4);
//        WARTOSCI OFFSET MUSZA BYC >= 0
        int offset_x = 1;
        int offset_y = 1;
        Assertions.assertTrue(vector.precedes(new Vector2d(vector.x, vector.y+offset_y)));
        Assertions.assertTrue(vector.precedes(new Vector2d(vector.x+offset_x, vector.y)));
        Assertions.assertTrue(vector.precedes(new Vector2d(vector.x+offset_x, vector.y+offset_y)));
        Assertions.assertTrue(vector.precedes(new Vector2d(vector.x, vector.y)));
        Assertions.assertFalse(vector.precedes(new Vector2d(vector.x, vector.y-offset_y)));
        Assertions.assertFalse(vector.precedes(new Vector2d(vector.x-offset_x, vector.y) ));
        Assertions.assertFalse(vector.precedes(new Vector2d(vector.x-offset_x, vector.y-offset_y) ));
    }

    @Test
    void testFollows(){
        Vector2d vector = new Vector2d(3, 4);
//        WARTOSCI OFFSET MUSZA BYC >= 0
        int offset_x = 1;
        int offset_y = 1;
        Assertions.assertTrue(vector.follows(new Vector2d(vector.x, vector.y-offset_y)));
        Assertions.assertTrue(vector.follows(new Vector2d(vector.x-offset_x, vector.y-offset_y)));
        Assertions.assertTrue(vector.follows(new Vector2d(vector.x-offset_x, vector.y)));
        Assertions.assertTrue(vector.follows(new Vector2d(vector.x, vector.y)));
        Assertions.assertFalse(vector.follows(new Vector2d(vector.x, vector.y+offset_y)));
        Assertions.assertFalse(vector.follows(new Vector2d(vector.x+offset_x, vector.y) ));
        Assertions.assertFalse(vector.follows(new Vector2d(vector.x+offset_x, vector.y+offset_y) ));
    }

    @Test
    void testUpperRight(){
        Vector2d vector = new Vector2d(3, 4);
//        WARTOSCI OFFSET MUSZA BYC >= 0
        int offset_x = 1;
        int offset_y = 1;
        Assertions.assertEquals(new Vector2d(vector.x+offset_x, vector.y), vector.upperRight(new Vector2d(vector.x+offset_x, vector.y)));
        Assertions.assertEquals(new Vector2d(vector.x+offset_x, vector.y+offset_y), vector.upperRight(new Vector2d(vector.x+offset_x, vector.y+offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y+offset_y), vector.upperRight(new Vector2d(vector.x, vector.y+offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y+offset_y), vector.upperRight(new Vector2d(vector.x-offset_x, vector.y+offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y), vector.upperRight(new Vector2d(vector.x-offset_x, vector.y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y), vector.upperRight(new Vector2d(vector.x-offset_x, vector.y-offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y), vector.upperRight(new Vector2d(vector.x, vector.y-offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x+offset_x, vector.y), vector.upperRight(new Vector2d(vector.x+offset_x, vector.y-offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y), vector.upperRight(new Vector2d(vector.x, vector.y)));
    }

    @Test
    void testLowerLeft(){
        Vector2d vector = new Vector2d(3, 4);
//        WARTOSCI OFFSET MUSZA BYC >= 0
        int offset_x = 1;
        int offset_y = 1;
        Assertions.assertEquals(new Vector2d(vector.x, vector.y), vector.lowerLeft(new Vector2d(vector.x+offset_x, vector.y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y), vector.lowerLeft(new Vector2d(vector.x+offset_x, vector.y+offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y), vector.lowerLeft(new Vector2d(vector.x, vector.y+offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x-offset_x, vector.y), vector.lowerLeft(new Vector2d(vector.x-offset_x, vector.y+offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x-offset_x, vector.y), vector.lowerLeft(new Vector2d(vector.x-offset_x, vector.y)));
        Assertions.assertEquals(new Vector2d(vector.x-offset_x, vector.y-offset_y), vector.lowerLeft(new Vector2d(vector.x-offset_x, vector.y-offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y-offset_y), vector.lowerLeft(new Vector2d(vector.x, vector.y-offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y-offset_y), vector.lowerLeft(new Vector2d(vector.x+offset_x, vector.y-offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x, vector.y), vector.lowerLeft(new Vector2d(vector.x, vector.y)));
    }

    @Test
    void testAdd(){
        Vector2d vector = new Vector2d(1, 1);
//        WARTOSCI OFFSET MUSZA BYC >= 0
        int offset_x = 1;
        int offset_y = 1;
        Assertions.assertEquals(new Vector2d(vector.x+offset_x, vector.y+offset_y), vector.add(new Vector2d(offset_x, offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x-offset_x, vector.y+offset_y), vector.add(new Vector2d(-offset_x, offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x+offset_x, vector.y-offset_y), vector.add(new Vector2d(offset_x, -offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x-offset_x, vector.y-offset_y), vector.add(new Vector2d(-offset_x, -offset_y)));
    }

    @Test
    void testSubtract(){
        Vector2d vector = new Vector2d(1, 1);
//        WARTOSCI OFFSET MUSZA BYC >= 0
        int offset_x = 1;
        int offset_y = 1;
        Assertions.assertEquals(new Vector2d(vector.x-offset_x, vector.y-offset_y), vector.subtract(new Vector2d(offset_x, offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x+offset_x, vector.y-offset_y), vector.subtract(new Vector2d(-offset_x, offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x-offset_x, vector.y+offset_y), vector.subtract(new Vector2d(offset_x, -offset_y)));
        Assertions.assertEquals(new Vector2d(vector.x+offset_x, vector.y+offset_y), vector.subtract(new Vector2d(-offset_x, -offset_y)));
    }

    @Test
    void testOpposite(){
//        WARTOSCI OFFSET MUSZA BYC >= 0
        int offset_x = 1;
        int offset_y = 1;
        Assertions.assertEquals(new Vector2d(offset_x, offset_y), new Vector2d(-offset_x, -offset_y).opposite());
        Assertions.assertEquals(new Vector2d(-offset_x, offset_y), new Vector2d(offset_x, -offset_y).opposite());
        Assertions.assertEquals(new Vector2d(offset_x, -offset_y), new Vector2d(-offset_x, offset_y).opposite());
        Assertions.assertEquals(new Vector2d(-offset_x, -offset_y), new Vector2d(offset_x, offset_y).opposite());
    }

}
