package agh.ics.oop.interfaces;

import agh.ics.oop.mapElements.Animal;
import agh.ics.oop.mapElements.Grass;
import agh.ics.oop.mapElements.Vector2d;

/**
 * The interface responsible for interacting with the map of the world.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo
 *
 */
public interface IWorldMap {
  /**
   * Indicate if any object can move to the given position.
   *
   * @param position
   *            The position checked for the movement possibility.
   * @return True if the object can move to that position.
   */
  boolean canMoveTo(Vector2d position);



  boolean spawnGrass(Vector2d position);
  boolean spawnAnimal(Vector2d position);
//  boolean placeAnimal(Animal animal);
//  boolean placeGrass(Grass grass);
//
//  boolean removeAnimal(Animal animal);
//  boolean removeGrass(Grass grass);

  /**
   * Return true if given position on the map is occupied. Should not be
   * confused with canMove since there might be empty positions where the animal
   * cannot move.
   *
   * @param position
   *            Position to check.
   * @return True if the position is occupied.
   */
  boolean isOccupied(Vector2d position);

  /**
   * Return an object at a given position.
   *
   * @param position
   *            The position of the object.
   * @return Object or null if the position is not occupied.
   */
  Object objectAt(Vector2d position);
}