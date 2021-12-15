package agh.ics.oop.enums;

public enum MoveDirection{
    FORWARD(0),
    TURN45DEG(1),
    TURN90DEG(2),
    TURN135DEG(3),
    BACKWARD(4),
    TURN225DEG(5),
    TURN270DEG(6),
    TURN315DEG(7);

    public final int value;
    MoveDirection(int i) {
        value = i;
    }
}