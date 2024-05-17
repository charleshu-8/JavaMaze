package gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import generation.CardinalDirection;
import generation.DefaultOrder;
import generation.Maze;
import generation.MazeFactory;

/**
 * 
 * @author Charles Hu
 *
 */

class UnreliableSensorTest {

	/**
	 * Test if sensor's fail and repair process correctly operates
	 * Correct behavior is the following:
	 * 	-Fail and repair process exists in an independent thread and is initialized via startFailureAndRepairProcess()
	 * 	-Operating status will be set to failed after time of meanTimeBetweenFailures milliseconds
	 * 	-Operating status will be set to operable after time of meanTimeToRepair milliseconds
	 * 	-Thread is killed via stopFailureAndRepairProcess()
	 * 	-Thread killing sets the operating status to operable
	 * 	-Premature thread killing throws an error
	 */
	@Test
	void testFailureAndRepairProcess() {
		//PSEUDOCODE
		//Test if process can start
		//Test if it correctly sets sensor to failed state
		//Test if it restores the state to operable after repair
		//Test if process can stop
		//Test if error is thrown for attempt to end a non-existent process
		//**********
		
		//Set up sensor
		UnreliableSensor sensor = new UnreliableSensor();
		
		//Perform premature thread kill, should throw error
		assertThrows(UnsupportedOperationException.class, () -> sensor.stopFailureAndRepairProcess());
		
		//Give 500 milliseconds of operation and 500 milliseconds of failure
		sensor.startFailureAndRepairProcess(500, 500);
		
		//Wait for 600 milliseconds and check if thread successfully changed operating status to failed
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(sensor.getIsOperating());
		
		//Wait for 500 milliseconds and check if thread successfully repaired the sensor
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(sensor.getIsOperating());
		
		//Wait for 500 milliseconds and check if thread successfully changed operating status back to failed
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(sensor.getIsOperating());
		
		//Kill thread
		sensor.stopFailureAndRepairProcess();
		//Check if kill sets operating status back to operable
		assertTrue(sensor.getIsOperating());
	}
	
	/**
	 * Test if operating state correctly affects distance sensing method calls
	 * Correct behavior is the following:
	 * 	-A valid call while sensor is operable should result in a valid distance sensing
	 * 	-An invalid call while sensor is inoperable should result in an error
	 */
	@Test
	void testOperatingStatusDistanceSensing() {
		//Initialize maze for testing using default algorithm with deterministic settings
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(0);
		mazeFactory.order(order);
		mazeFactory.waitTillDelivered();
		Maze maze = order.getMaze();
		
		//Set up sensor for testing
		UnreliableSensor sensor = new UnreliableSensor();
		sensor.setMaze(maze);
		float[] power = {1};
		int[] coordinates = {0,2};
		
		//Boot up thread; give it 3 seconds of operation, 4 seconds of failure
		sensor.startFailureAndRepairProcess(3000, 4000);
		
		//Test if sensor works during operating status, should work and return 0 distance
		try {
			assertEquals(0, sensor.distanceToObstacle(coordinates, CardinalDirection.North, power));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Wait 3 seconds to ensure we enter failure status
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Test sensor again, since it is in failure state it should return an error a sensing attempt is made
		assertThrows(Exception.class, () -> sensor.distanceToObstacle(coordinates, CardinalDirection.North, power));
		
		//Kill thread
		sensor.stopFailureAndRepairProcess();
	}
	
	/**
	 * Following tests are pulled from ReliableSensorTest and are adjusted for testing UnreliableSensor instances instead
	 * Mainly to ensure that distanceToObstable method in the UnreliableSensor class is on par with the one in ReliableSensor feature-wise
	 */
	
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
		UnreliableSensor sensor = new UnreliableSensor();
		sensor.setMaze(maze);
		float[] power = {1};
		
		//Boot up thread; give it a long operating time for testing purposes since we don't care about failure state here
		sensor.startFailureAndRepairProcess(60000, 0);
		
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
		
		//Kill thread
		sensor.stopFailureAndRepairProcess();
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
		UnreliableSensor sensor = new UnreliableSensor();
		sensor.setMaze(maze);
		int[] goodCoordinates = {0,0};
		int[] badCoordinates1 = {12,0};
		int[] badCoordinates2 = {2,-1};
		float[] goodPower = {1};
		float[] badPower1 = {1, 0};
		float[] badPower2 = {0};
		
		//Boot up thread; give it a long operating time for testing purposes since we don't care about failure state here
		sensor.startFailureAndRepairProcess(60000, 0);
		
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
		
		//Kill thread
		sensor.stopFailureAndRepairProcess();
	}
}
