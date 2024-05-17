package gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gui.Robot.Direction;

/**
 * 
 * @author Charles Hu
 *
 */

class UnreliableRobotTest extends RobotTest {

	/**
	 * Test if robot can fail and repair process for a sensor can correctly be called
	 * Correct behavior is the following:
	 * 	-Fail and repair process exists in an independent thread and is initialized via startFailureAndRepairProcess() on sensor
	 * 	-Operating status will be set to failed after time of meanTimeBetweenFailures milliseconds
	 * 	-Operating status will be set to operable after time of meanTimeToRepair milliseconds
	 * 	-Thread is killed via stopFailureAndRepairProcess() on sensor
	 * 	-Thread killing sets the operating status to operable
	 * 	-Premature thread killing throws an error
	 */
	@Test
	void testFailAndRepair() {
		//PSEUDOCODE
		//Test start call
		//Test stop call
		//Test premature stop call
		//**********
		
		//Set up controller and robot
		Control controller = createController(0);
		UnreliableRobot robot = new UnreliableRobot();
		robot.setController(controller);
		
		//Set up UnreliableSensor for robot
		UnreliableSensor sensorF = new UnreliableSensor();
		UnreliableSensor sensorB = new UnreliableSensor();
		UnreliableSensor sensorR = new UnreliableSensor();
		UnreliableSensor sensorL = new UnreliableSensor();	
		robot.addDistanceSensor(sensorF, Direction.FORWARD);
		robot.addDistanceSensor(sensorB, Direction.BACKWARD);
		robot.addDistanceSensor(sensorR, Direction.RIGHT);
		robot.addDistanceSensor(sensorL, Direction.LEFT);
		
		//Test premature kill call on sensors, should throw back an error
		assertThrows(UnsupportedOperationException.class, () -> robot.stopFailureAndRepairProcess(Direction.FORWARD));
		assertThrows(UnsupportedOperationException.class, () -> robot.stopFailureAndRepairProcess(Direction.BACKWARD));
		assertThrows(UnsupportedOperationException.class, () -> robot.stopFailureAndRepairProcess(Direction.RIGHT));
		assertThrows(UnsupportedOperationException.class, () -> robot.stopFailureAndRepairProcess(Direction.LEFT));
		
		//Start up fail and repair process on sensors
		robot.startFailureAndRepairProcess(Direction.FORWARD, 500, 500);
		robot.startFailureAndRepairProcess(Direction.BACKWARD, 500, 500);
		robot.startFailureAndRepairProcess(Direction.RIGHT, 500, 500);
		robot.startFailureAndRepairProcess(Direction.LEFT, 500, 500);
		
		//Sleep until sensors enter failure mode
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//In failure mode, sensors should return -1 value to indicate that they are inoperable
		//Thus when called in this state, we should always receive -1
		assertEquals(-1, robot.distanceToObstacle(Direction.FORWARD));
		assertEquals(-1, robot.distanceToObstacle(Direction.BACKWARD));
		assertEquals(-1, robot.distanceToObstacle(Direction.RIGHT));
		assertEquals(-1, robot.distanceToObstacle(Direction.LEFT));
		
		//Sleep until repairs have finished and sensors are back online
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Test to see if sensors are operable, should return some integer value >=0
		assertEquals(0, robot.distanceToObstacle(Direction.FORWARD));
		assertEquals(0, robot.distanceToObstacle(Direction.BACKWARD));
		assertEquals(1, robot.distanceToObstacle(Direction.RIGHT));
		assertEquals(0, robot.distanceToObstacle(Direction.LEFT));
		
		//Sleep until next failure mode cycles around
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Kill the threads for the fail and repair process
		robot.stopFailureAndRepairProcess(Direction.FORWARD);
		robot.stopFailureAndRepairProcess(Direction.BACKWARD);
		robot.stopFailureAndRepairProcess(Direction.RIGHT);
		robot.stopFailureAndRepairProcess(Direction.LEFT);
		
		//Since we killed the threads during a failure state, the sensors should return to an operable state
		//Thus when we call the sensors again, they should properly return actual distances to closest walls in that direction
		assertEquals(0, robot.distanceToObstacle(Direction.FORWARD));
		assertEquals(0, robot.distanceToObstacle(Direction.BACKWARD));
		assertEquals(1, robot.distanceToObstacle(Direction.RIGHT));
		assertEquals(0, robot.distanceToObstacle(Direction.LEFT));
	}
}
