package gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import generation.CardinalDirection;
import gui.Robot.Direction;
import gui.Robot.Turn;

/**
 * 
 * @author Charles Hu
 *
 */

class ReliableRobotTest extends RobotTest {
	
	/**
	 * Test if bad controller assignment is caught
	 * Correct behavior is the following: Method throws IllegalArgumentException when receiving a null parameter
	 */
	@Test
	void testBadControl() {
		ReliableRobot robot = new ReliableRobot();
		//Test if method catches null input
		assertThrows(IllegalArgumentException.class, () -> robot.setController(null));
	}
	
	/**
	 * Test to see if locational methods correctly operate (position & direction)
	 * Correct behavior is the following:
	 * 	-Setter and getter for position correctly update and report the robot's position
	 * 	-Setter and getter for direction correctly update and report the robot's direction
	 * 	-Illegal (not in maze) positions are caught with thrown exception
	 */
	@Test
	void testLocation() {
		//Initialize object instance
		Control controller = createController(0);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		
		//Test if position and direction of robot and controller line up
		controller.setCurrentDirection(CardinalDirection.East);
		int[] coordinates = {2, 2};
		controller.setCurrentPosition(coordinates[0], coordinates[1]);
		//Direction should line up with set direction
		assertEquals(CardinalDirection.East, robot.getCurrentDirection());
		try {
			//Position should line up with set position
			assertEquals(coordinates[0], robot.getCurrentPosition()[0]);
			assertEquals(coordinates[1], robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test that illegal position is caught with exception thrown
		int[] badCoordinates = {20, 2};
		controller.setCurrentPosition(badCoordinates[0], badCoordinates[1]);
		assertThrows(Exception.class, () -> robot.getCurrentPosition());
	}
	
	/**
	 * Test to see if battery getter & setter work
	 * Correct behavior is the following:
	 * 	-Setter and getter should correctly update and report the value of the battery attribute
	 * 	-Setter recognizes a 0 value input as resulting in a stopped robot and updating the hasStopped attribute to indicate so
	 */
	@Test
	void testBattery() {
		//PSEUDOCODE
		//Initialize object instance
		//Set & get battery a number of times
		//Check to see if these values line up
		//**********
		
		//Initialize object instance
		Control controller = createController(0);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		
		//Test if setter & getter work
		assertEquals(3500, robot.getBatteryLevel());
		robot.setBatteryLevel(1000);
		assertEquals(1000, robot.getBatteryLevel());
		robot.setBatteryLevel(0);
		//Test to see if 0 energy triggers hasStopped attribute
		assertEquals(0, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
	}
	
	/**
	 * Test if movement & sensory methods correctly consume energy
	 * Correct behavior is the following:
	 * 	-Each 90 degree rotation consumes 3 energy
	 * 	-Each 1 cell movement consumes 6 energy
	 * 	-Each jump consumes 40 energy
	 * 	-Movement methods cannot operate without the necessary energy as listed above
	 */
	@Test
	void testEnergyConsumption() {
		//PSEUDOCODE
		//Call energy consumption methods
		//Check to see if they line up with expected energy consumption rates
		//**********
		
		//Initialize object instance
		Control controller = createController(1);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		//Set up position & direction
		controller.setCurrentPosition(0, 2);
		controller.setCurrentDirection(CardinalDirection.North);
		
		//Check rotation energy use
		//2 3 energy rotations & 1 6 energy rotation, should consume 12 overall
		robot.setBatteryLevel(12);
		assertEquals(12, robot.getBatteryLevel());
		robot.rotate(Turn.LEFT);
		robot.rotate(Turn.RIGHT);
		robot.rotate(Turn.AROUND);
		assertEquals(CardinalDirection.South, robot.getCurrentDirection());
		assertEquals(0, robot.getBatteryLevel());
		
		//Check that low energy prevents rotation
		//Energy should not be consumed & direction should not be changed
		robot.setBatteryLevel(2);
		robot.resetHasStopped();
		assertEquals(2, robot.getBatteryLevel());
		assertFalse(robot.hasStopped());
		
		robot.rotate(Turn.LEFT);
		assertEquals(2, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
		assertEquals(CardinalDirection.South, robot.getCurrentDirection());
		robot.resetHasStopped();
		
		robot.rotate(Turn.RIGHT);
		assertEquals(2, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
		assertEquals(CardinalDirection.South, robot.getCurrentDirection());
		robot.resetHasStopped();
		
		robot.rotate(Turn.AROUND);
		assertEquals(2, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
		assertEquals(CardinalDirection.South, robot.getCurrentDirection());
		robot.resetHasStopped();
		
		//Check movement energy use
		// 2 single movements for 6 energy each & 1 double movement for 12 energy, 24 energy overall
		controller.getMaze().getFloorplan().markAreaAsRoom(3, 3, 1, 1, 3, 3);
		robot.setBatteryLevel(24);
		controller.setCurrentDirection(CardinalDirection.East);
		controller.setCurrentPosition(1, 1);
		robot.move(1);
		robot.move(1);
		controller.setCurrentPosition(1, 1);
		robot.move(2);
		assertEquals(0, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
		robot.resetHasStopped();
		
		//Check that low energy prevents movement
		//Energy should not be consumed & position should not be changed
		controller.setCurrentPosition(1, 1);
		robot.setBatteryLevel(5);
		robot.move(1);
		assertEquals(5, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
		try {
			assertEquals(1, robot.getCurrentPosition()[0]);
			assertEquals(1, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		robot.resetHasStopped();
		
		//Check that low energy prevents movement during walk cycle
		//Energy should only be consumed for the first move but will stop moving on the second walk
		robot.setBatteryLevel(7);
		robot.move(2);
		assertEquals(1, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
		try {
			assertEquals(2, robot.getCurrentPosition()[0]);
			assertEquals(1, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		robot.resetHasStopped();
		
		//Check jump energy use
		//2 jumps for 40 energy each, 80 energy overall
		controller.setCurrentPosition(1, 1);
		controller.setCurrentDirection(CardinalDirection.East);
		robot.setBatteryLevel(80);
		robot.jump();
		robot.jump();
		assertEquals(0, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
		robot.resetHasStopped();
		
		//Check that low energy prevents jumps
		//Energy should not be consumed & position should not be changed
		robot.setBatteryLevel(39);
		controller.setCurrentPosition(0, 2);
		robot.jump();
		assertEquals(39, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
		try {
			assertEquals(0, robot.getCurrentPosition()[0]);
			assertEquals(2, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		robot.resetHasStopped();
		
		//Check distance sensing energy use
		// 4 wall check and 4 exit checks, 1 energy each so 8 energy overall
		ReliableSensor sensorF = new ReliableSensor();
		ReliableSensor sensorB = new ReliableSensor();
		ReliableSensor sensorR = new ReliableSensor();
		ReliableSensor sensorL = new ReliableSensor();
		robot.addDistanceSensor(sensorF, Direction.FORWARD);
		robot.addDistanceSensor(sensorB, Direction.BACKWARD);
		robot.addDistanceSensor(sensorR, Direction.RIGHT);
		robot.addDistanceSensor(sensorL, Direction.LEFT);
		robot.setBatteryLevel(8);
		robot.distanceToObstacle(Direction.FORWARD);
		robot.distanceToObstacle(Direction.BACKWARD);
		robot.distanceToObstacle(Direction.RIGHT);
		robot.distanceToObstacle(Direction.LEFT);
		robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD);
		robot.canSeeThroughTheExitIntoEternity(Direction.BACKWARD);
		robot.canSeeThroughTheExitIntoEternity(Direction.RIGHT);
		robot.canSeeThroughTheExitIntoEternity(Direction.LEFT);
		assertEquals(0, robot.getBatteryLevel());
		assertTrue(robot.hasStopped());
		
		//Check that low energy prevents scans
		//Should return -1, which is arbitrary value set for error checking if sensing operation cannot occur
		robot.resetHasStopped();
		assertFalse(robot.hasStopped());
		assertEquals(-1, robot.distanceToObstacle(Direction.FORWARD));
		assertEquals(-1, robot.distanceToObstacle(Direction.BACKWARD));
		assertEquals(-1, robot.distanceToObstacle(Direction.RIGHT));
		assertEquals(-1, robot.distanceToObstacle(Direction.LEFT));
	}
	
	/**
	 * Tests if energy calls correctly report value for energy consumed
	 * Correct behavior is the following: Calls on energy consumption reporting methods should correctly report energy used for such operations; full rotation is 12, single step is 6
	 */
	@Test
	void testEnergyCall() {
		//Initialize object instance
		Control controller = createController(0);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		
		//Full rotation is 3 (for 1 rotation) * 4 = 12 energy; single step is 6 energy
		assertEquals(12, robot.getEnergyForFullRotation());
		assertEquals(6, robot.getEnergyForStepForward());
	}
	
	/**
	 * Test odometer to see if associated methods correctly operate
	 * Correct behavior is the following:
	 * 	-Odometer can return current amount of cells walked
	 * 	-Odometer can be reset to 0
	 */
	@Test
	void testOdometer() {
		//PSEUDOCODE
		//Initialize object instance
		//Manipulate odometer value
		//Check if getter correctly returns travel value
		//Use reset method, check to see if reset to 0
		//**********
		
		//Initialize object instance
		Control controller = createController(0);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		
		//Check default value, should be 0
		assertEquals(0, robot.getOdometerReading());
		
		//Check odometer during a 2 cell walk
		controller.setCurrentPosition(0, 2);
		controller.setCurrentDirection(CardinalDirection.North);
		for (int i = 1; i < 3; i++) {
		robot.move(1);
		assertEquals(i, robot.getOdometerReading());
		}
		
		//Check a reset
		robot.resetOdometer();
		assertEquals(0, robot.getOdometerReading());
	}
	
	/**
	 * Test rotate method to ensure correct & expected operation
	 * Correct behavior is the following:
	 * 	-Robot's direction will be updated to reflect new direction per turn
	 * 	-Turn will not occur if the robot is inoperable
	 */
	@Test
	void testRotate() {
		//PSEUDOCODE
		//Initialize object instance
		//Turn robot to left, right, and around
		//Check if direction correctly reflects such movement
		//**********
		
		//Initialize object instance
		Control controller = createController(0);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		//Set up position & direction
		controller.setCurrentPosition(0, 2);
		controller.setCurrentDirection(CardinalDirection.North);
		
		//Check around turn
		robot.rotate(Turn.AROUND);
		assertEquals(CardinalDirection.South, robot.getCurrentDirection());
		//Check left turn
		robot.rotate(Turn.LEFT);
		assertEquals(CardinalDirection.East, robot.getCurrentDirection());
		//Check right turn
		robot.rotate(Turn.RIGHT);
		assertEquals(CardinalDirection.South, robot.getCurrentDirection());
		
		//Check if it correctly catches stopped status
		robot.setBatteryLevel(0);
		robot.rotate(Turn.AROUND);
		assertEquals(CardinalDirection.South, robot.getCurrentDirection());
	}
	
	/**
	 * Test movement method to ensure correct & expected operation
	 * Correct behavior is the following:
	 * 	-Robot's position will be updated to reflect the number of cells moved
	 * 	-Robot will be set to inoperable status if it moves into a wall
	 * 	-Robot will not accept illegal inputs (< 1)
	 * 	-Robot will not move when inoperable
	 */
	@Test
	void testMovement() {
		//PSEUDOCODE
		//Initialize object instance
		//Test forward movement in legal, unobstructed directions
		//Test forward movement into obstacle, should stop robot
		//Test illegal forward movement, should throw error
		//**********
		
		//Initialize object instance
		Control controller = createController(1);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		//Set up position & direction
		controller.getMaze().getFloorplan().markAreaAsRoom(3, 3, 1, 1, 3, 3);
		controller.setCurrentPosition(1, 1);
		controller.setCurrentDirection(CardinalDirection.East);
		
		//Check legal forward movement
		robot.move(1);
		try {
			assertEquals(2, robot.getCurrentPosition()[0]);
			assertEquals(1, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		robot.move(1);
		try {
			assertEquals(3, robot.getCurrentPosition()[0]);
			assertEquals(1, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		controller.setCurrentPosition(1, 1);
		robot.move(2);
		try {
			assertEquals(3, robot.getCurrentPosition()[0]);
			assertEquals(1, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Check illegal forward movement
		assertThrows(IllegalArgumentException.class, () -> robot.move(0));
		assertThrows(IllegalArgumentException.class, () -> robot.move(-1));
		
		//Check movement into wall, should be stopped
		controller.setCurrentPosition(1, 1);
		robot.move(3);
		try {
			assertEquals(3, robot.getCurrentPosition()[0]);
			assertEquals(1, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(robot.hasStopped());
		
		//Check if it correctly catches stopped status
		controller.setCurrentPosition(1, 1);
		robot.move(1);
		try {
			assertEquals(1, robot.getCurrentPosition()[0]);
			assertEquals(1, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test jump method to ensure correct & expected operation
	 * Correct behavior is the following:
	 * 	-Robot can jump over walls and cells without walls, position will be changed to reflect that
	 * 	-Robot can only jump to cells within the maze, will be set to inoperable otherwise
	 * 	-Robot will not move while inoperable
	 */
	@Test
	void testJump() {
		//PSEUDOCODE
		//Initialize object instance
		//Test jumping in legal areas
		//Test jumping in illegal areas, should not execute
		//**********
		
		//Initialize object instance
		Control controller = createController(0);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		//Set up position & direction
		controller.setCurrentPosition(0, 2);
		controller.setCurrentDirection(CardinalDirection.North);
		
		//Test jump over no walls
		robot.jump();
		try {
			assertEquals(0, robot.getCurrentPosition()[0]);
			assertEquals(1, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test jump over wall
		controller.setCurrentPosition(2, 2);
		controller.setCurrentDirection(CardinalDirection.East);
		robot.jump();
		try {
			assertEquals(3, robot.getCurrentPosition()[0]);
			assertEquals(2, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test illegal jump over border, should be set to inoperable
		controller.setCurrentPosition(0, 0);
		controller.setCurrentDirection(CardinalDirection.North);
		robot.jump();
		try {
			assertEquals(0, robot.getCurrentPosition()[0]);
			assertEquals(0, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(robot.hasStopped());
		
		//Check if it correctly catches stopped status
		controller.setCurrentPosition(0, 2);
		robot.jump();
		try {
			assertEquals(0, robot.getCurrentPosition()[0]);
			assertEquals(2, robot.getCurrentPosition()[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests to see if checks on current positional information (room, exit) correctly reflect current cell state
	 * Correct behavior is the following: Exit and room reporting methods will correctly identify when they are at an exit or room
	 */
	@Test
	void testPositionalInfo() {
		//PSEUDOCODE
		//Initialize object instance
		//Move robot around, test to see if it correctly reflects status on being near/in exit or room
		//**********
		
		//Initialize object instance
		Control controller = createController(0);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		//Set up position & direction
		controller.setCurrentPosition(0, 0);
		
		//Test exit check
		//Force set an exit
		controller.getMaze().getFloorplan().setExitPosition(0, 0);
		assertTrue(robot.isAtExit());
		//Test room check
		//Force set a room
		controller.getMaze().getFloorplan().markAreaAsRoom(1, 1, 2, 2, 2, 2);
		controller.setCurrentPosition(2, 2);
		assertTrue(robot.isInsideRoom());
	}
	
	/**
	 * Test to see if distance sensors for walls/exits can correctly detect and report distances for such obstacles
	 * Correct behavior is the following:
	 * 	-Robot will throw an error if a sensor operation is called on a direction without a sensor
	 * 	-Legally set sensors will correctly report the distance between them and a wall
	 * 	-Legally set sensors will report if it can see an exit
	 *  -Legally set sensors will report if it cannot see an exit
	 *  -Sensors will not operate without power or when the robot is inoperable
	 */
	@Test
	void testDistanceSensors() {
		//PSEUDOCODE
		//Initialize object instance
		//Run varied tests on distance check on walls
		//Run check on distance check to exit
		//**********
		
		//Initialize object instance
		Control controller = createController(1);
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);

		//Test illegal call on non-existent sensors
		assertThrows(UnsupportedOperationException.class, () -> robot.distanceToObstacle(Direction.FORWARD));
		assertThrows(UnsupportedOperationException.class, () -> robot.distanceToObstacle(Direction.BACKWARD));
		assertThrows(UnsupportedOperationException.class, () -> robot.distanceToObstacle(Direction.RIGHT));
		assertThrows(UnsupportedOperationException.class, () -> robot.distanceToObstacle(Direction.LEFT));
		
		//Initialize some sensor objects
		ReliableSensor sensorF = new ReliableSensor();
		ReliableSensor sensorB = new ReliableSensor();
		ReliableSensor sensorR = new ReliableSensor();
		ReliableSensor sensorL = new ReliableSensor();
		robot.addDistanceSensor(sensorF, Direction.FORWARD);
		robot.addDistanceSensor(sensorB, Direction.BACKWARD);
		robot.addDistanceSensor(sensorR, Direction.RIGHT);
		robot.addDistanceSensor(sensorL, Direction.LEFT);
		
		//Test legal distance call on all sensors
		controller.getMaze().getFloorplan().markAreaAsRoom(2, 2, 2, 2, 3, 3);
		controller.setCurrentPosition(2, 2);
		controller.setCurrentDirection(CardinalDirection.North);
		assertEquals(0, robot.distanceToObstacle(Direction.FORWARD));
		assertEquals(1, robot.distanceToObstacle(Direction.BACKWARD));
		assertEquals(0, robot.distanceToObstacle(Direction.RIGHT));
		assertEquals(1, robot.distanceToObstacle(Direction.LEFT));
		
		robot.rotate(Turn.LEFT);
		
		//Translating the distances by 90 degrees
		assertEquals(0, robot.distanceToObstacle(Direction.FORWARD));
		assertEquals(1, robot.distanceToObstacle(Direction.BACKWARD));
		assertEquals(1, robot.distanceToObstacle(Direction.RIGHT));
		assertEquals(0, robot.distanceToObstacle(Direction.LEFT));
		
		//Test distance sensing on exit
		//Should report Integer.MAX_VALUE for normal sensing and true for canSeeThroughTheExitIntoEternity calls
		controller.setCurrentPosition(0, 0);
		controller.getMaze().getFloorplan().setExitPosition(0, 0);
		assertEquals(Integer.MAX_VALUE, robot.distanceToObstacle(Direction.FORWARD));
		assertEquals(true, robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD));
		robot.rotate(Turn.LEFT);
		assertEquals(Integer.MAX_VALUE, robot.distanceToObstacle(Direction.LEFT));
		assertEquals(true, robot.canSeeThroughTheExitIntoEternity(Direction.LEFT));
		robot.rotate(Turn.LEFT);
		assertEquals(Integer.MAX_VALUE, robot.distanceToObstacle(Direction.BACKWARD));
		assertEquals(true, robot.canSeeThroughTheExitIntoEternity(Direction.BACKWARD));
		robot.rotate(Turn.LEFT);
		assertEquals(Integer.MAX_VALUE, robot.distanceToObstacle(Direction.RIGHT));
		assertEquals(true, robot.canSeeThroughTheExitIntoEternity(Direction.RIGHT));
		
		//Check exit check on non-exit, should return false
		controller.setCurrentPosition(0, 2);
		assertEquals(false, robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD));
		
		//Check if it correctly catches stopped status
		robot.setBatteryLevel(0);
		assertTrue(robot.hasStopped());
		assertThrows(UnsupportedOperationException.class, () -> robot.distanceToObstacle(Direction.FORWARD));
		assertThrows(UnsupportedOperationException.class, () -> robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD));
	}
}
