package agh.ics.oop.enums;

import agh.ics.oop.mapElements.Vector2d;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    public String toString(){
        return switch (this){
            case NORTH -> "Północ";
            case NORTHEAST -> "Północny wschód";
            case NORTHWEST -> "Północny zachód";
            case SOUTH -> "Południe";
            case SOUTHEAST -> "Południowy wschód";
            case SOUTHWEST -> "Południowy zachód";
            case WEST -> "Zachód";
            case EAST -> "Wschód";
        };
    }

    public MapDirection next(){
        return switch (this){
            case NORTH -> NORTHEAST;
            case NORTHEAST -> EAST;
            case EAST -> SOUTHEAST;
            case SOUTHEAST -> SOUTH;
            case SOUTH -> SOUTHWEST;
            case SOUTHWEST -> WEST;
            case WEST -> NORTHWEST;
            case NORTHWEST -> NORTH;
        };
    }

    public MapDirection previous(){
        return switch (this){
            case NORTH -> NORTHWEST;
            case NORTHWEST -> WEST;
            case WEST -> SOUTHWEST;
            case SOUTHWEST -> SOUTH;
            case SOUTH -> SOUTHEAST;
            case SOUTHEAST -> EAST;
            case EAST -> NORTHEAST;
            case NORTHEAST -> NORTH;
        };
    }

    public Vector2d toUnitVector(){
        return switch (this){
            case NORTH -> new Vector2d(0,1);
            case NORTHEAST -> new Vector2d(1,1);
            case EAST -> new Vector2d(1,0);
            case SOUTHEAST -> new Vector2d(1,-1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTHWEST -> new Vector2d(-1,-1);
            case WEST -> new Vector2d(-1, 0);
            case NORTHWEST -> new Vector2d(-1,1);
        };
    }

    private MapDirection repeatNext(MapDirection direction, int howManyTimes){
        for (int i=0; i<howManyTimes; i++)
            direction = direction.next();
        return direction;
    }

    public MapDirection rotate(MoveDirection direction){
        return switch (direction){
            case FORWARD, BACKWARD -> this;
            case TURN45DEG -> repeatNext(this, 1);
            case TURN90DEG -> repeatNext(this, 2);
            case TURN135DEG -> repeatNext(this, 3);
            case TURN225DEG -> repeatNext(this, 5);
            case TURN270DEG -> repeatNext(this, 6);
            case TURN315DEG -> repeatNext(this, 7);
        };
    }
}