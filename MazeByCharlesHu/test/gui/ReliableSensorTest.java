package gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import generation.CardinalDirection;
import generation.DefaultOrder;
import generation.Maze;
import generation.MazeFactory;
import gui.Robot.Direction;

/**
 * 
 * @author Charles Hu
 *
 */

class ReliableSensorTest {

	/**
	 * Tests if distanceToObstacle can reliably detect walls, borders, & exit any number of tiles away
	 * Correct behavior is the following:
	 * 	-Distance sensor can correctly identify walls any number of cells away from them, will return the number of cells such wall is away from it
	 * 	-Sensor can identify an exit and return Integer.MAX_VALUE to indicate that such is an exit
	 */
	@Test
	void testDistanceCheck() {
		//PSEUDOCODE
		//Initialize instance of object with an associated maze
		//Test if sensor can detect wall on same tile
		//Test for wall 1 tile away
		//Test for wall 3 tiles away
		//Test for border wall
		//Test for exit
		//**********
		
		//Initialize maze for testing using default algorithm with deterministic settings
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(0);
		mazeFactory.order(order);
		mazeFactory.waitTillDelivered();
		Maze maze = order.getMaze();
		
		//Initialize sensor instance
		ReliableSensor sensor = new ReliableSensor();
		sensor.setMaze(maze);
		float[] power = {1};
		
		//Test if sensor can detect wall on same tile, should return 0 distance
		int[] coordinates0 = {0,2};
		try {
			assertEquals(0, sensor.distanceToObstacle(coordinates0, CardinalDirection.North, power));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test for wall 1 cell away
		try {
			assertEquals(1, sensor.distanceToObstacle(coordinates0, CardinalDirection.South, power));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test for wall 3 tiles away
		try {
			assertEquals(3, sensor.distanceToObstacle(coordinates0, CardinalDirection.East, power));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test for border wall detection, should return 0
		//Since border walls aren't read the same as standard walls, need special casing to ensure that sensor can catch them
		int[] coordinates1 = {0,3};
		try {
			assertEquals(0, sensor.distanceToObstacle(coordinates1, CardinalDirection.South, power));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test for exit, should return Integer.MAX_VALUE
		int[] coordinates2 = {2,0};
		try {
			assertEquals(Integer.MAX_VALUE, sensor.distanceToObstacle(coordinates2, CardinalDirection.North, power));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test that errors throwing is operational and correct
	 * Correct behavior is the following:
	 * 	-An error is thrown if any inputs to the sensor operation are null
	 * 	-An error is thrown if any positional inputs are not within the maze
	 * 	-An error is thrown if the power supply array is not of size 1
	 * 	-An error is thrown if the sensor has no power to operate on
	 */
	@Test
	void testBadDistanceCall() {
		//Initialize maze for testing using default algorithm with deterministic settings
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(0);
		mazeFactory.order(order);
		mazeFactory.waitTillDelivered();
		Maze maze = order.getMaze();
		
		//Initialize sensor instance
		ReliableSensor sensor = new ReliableSensor();
		sensor.setMaze(maze);
		int[] goodCoordinates = {0,0};
		int[] badCoordinates1 = {12,0};
		int[] badCoordinates2 = {2,-1};
		float[] goodPower = {1};
		float[] badPower1 = {1, 0};
		float[] badPower2 = {0};
		
		//Test null parameters, should throw IllegalArgumentException
		assertThrows(IllegalArgumentException.class, () -> sensor.distanceToObstacle(null, null, null));
		assertThrows(IllegalArgumentException.class, () -> sensor.distanceToObstacle(goodCoordinates, null, null));
		assertThrows(IllegalArgumentException.class, () -> sensor.distanceToObstacle(null, CardinalDirection.East, null));
		assertThrows(IllegalArgumentException.class, () -> sensor.distanceToObstacle(null, null, goodPower));
		
		//Test illegal parameters (coordinates not in the maze), should throw IllegalArgumentException
		assertThrows(IllegalArgumentException.class, () -> sensor.distanceToObstacle(badCoordinates1, CardinalDirection.East, goodPower));
		assertThrows(IllegalArgumentException.class, () -> sensor.distanceToObstacle(badCoordinates2, CardinalDirection.East, goodPower));
		
		//Test out of range power supply, should throw IndexOutOfBoundsException
		assertThrows(IndexOutOfBoundsException.class, () -> sensor.distanceToObstacle(goodCoordinates, CardinalDirection.East, badPower1));
		
		//Test no power supply, should throw Exception
		assertThrows(Exception.class, () -> sensor.distanceToObstacle(goodCoordinates, CardinalDirection.East, badPower2));
	}
	
	/**
	 * Test getter and setter for sensor direction
	 * Correct behavior is the following: A sensor should handle and track the direction it is set as and be able to report it
	 */
	@Test
	void testSensorDirection() {
		//Initialize sensor instance
		ReliableSensor sensor = new ReliableSensor();
		
		//Check to see if set and get have same value
		sensor.setSensorDirection(Direction.FORWARD);
		assertEquals(Direction.FORWARD, sensor.getSensorDirection());
	}
	
	/**
	 * Test if energy consumption value is correctly returned
	 * Correct behavior is the following: A sensor should report that it consumes 1 unit of energy per use
	 */
	@Test
	void testEnergyConsumption() {
		//PSEUDOCODE
		//Initialize instance of object
		//Check if energy consumption call returns correct value of 1
		//**********
		
		//Initialize arbitrary instance of object (energy consumption should be independent)
		ReliableSensor sensor = new ReliableSensor();
		//Check if energy consumption call returns correct value of 1
		assertEquals(1, sensor.getEnergyConsumptionForSensing());
	}
}
