package agh.ics.oop;

import agh.ics.oop.enums.MapDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MapDirectionTest {
    @Test
    void testNext(){

        Assertions.assertEquals( MapDirection.EAST, MapDirection.NORTH.next() );
        Assertions.assertEquals( MapDirection.SOUTH, MapDirection.EAST.next() );
        Assertions.assertEquals( MapDirection.WEST, MapDirection.SOUTH.next() );
        Assertions.assertEquals( MapDirection.NORTH, MapDirection.WEST.next() );

    }

    @Test
    void testPrevious(){

        Assertions.assertEquals( MapDirection.WEST, MapDirection.NORTH.previous() );
        Assertions.assertEquals( MapDirection.NORTH, MapDirection.EAST.previous() );
        Assertions.assertEquals( MapDirection.EAST, MapDirection.SOUTH.previous() );
        Assertions.assertEquals( MapDirection.SOUTH, MapDirection.WEST.previous() );

    }
}
