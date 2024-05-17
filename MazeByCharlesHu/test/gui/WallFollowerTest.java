package gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import generation.DefaultOrder;
import generation.Maze;
import generation.MazeFactory;
import generation.Order.Builder;
import gui.Robot.Direction;

/**
 * 
 * @author Charles Hu
 *
 */

class WallFollowerTest {

	/**
	 * Create a wallfollower object for testing use
	 * @param skillLevel as Integer for skill level of maze
	 * @param isPerfect as boolean for whether maze is perfect (no rooms)
	 * @param robotEnergy as float for desired energy to set for robot
	 * @return Wizard connected to a generated maze and robot
	 */
	private WallFollower createWallFollower(int skillLevel, boolean isPerfect, float robotEnergy, int meanTimeBetweenFailures, int meanTimeToRepair) {
		//Initialize maze for testing using default algorithm with deterministic settings
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(skillLevel, Builder.DFS, isPerfect, 13);
		mazeFactory.order(order);
		mazeFactory.waitTillDelivered();
		Maze maze = order.getMaze();
		
		//Set up Control object & linked StateGenerating object
		Control controller = new Control();
		controller.turnOffGraphics();
		MazePanel panel = new MazePanel();
		//Throws warning errors since panel is not properly set
		//Should be fine to ignore though as GUI is not specifically used for tests
		StateGenerating generator = new StateGenerating();
		generator.start(controller, panel);
		generator.switchFromGeneratingToPlaying(maze);
		
		//Set up robot & its sensors
		UnreliableRobot robot = new UnreliableRobot();
		robot.setController(controller);
		robot.setBatteryLevel(robotEnergy);
		UnreliableSensor sensorF = new UnreliableSensor();
		UnreliableSensor sensorB = new UnreliableSensor();
		UnreliableSensor sensorR = new UnreliableSensor();
		UnreliableSensor sensorL = new UnreliableSensor();
        robot.addDistanceSensor(sensorF, Direction.FORWARD);
        robot.addDistanceSensor(sensorB, Direction.BACKWARD);
        robot.addDistanceSensor(sensorR, Direction.RIGHT);
        robot.addDistanceSensor(sensorL, Direction.LEFT);
        robot.startFailureAndRepairProcess(Direction.FORWARD, meanTimeBetweenFailures, meanTimeToRepair);
        robot.startFailureAndRepairProcess(Direction.BACKWARD, meanTimeBetweenFailures, meanTimeToRepair);
        robot.startFailureAndRepairProcess(Direction.RIGHT, meanTimeBetweenFailures, meanTimeToRepair);
        robot.startFailureAndRepairProcess(Direction.LEFT, meanTimeBetweenFailures, meanTimeToRepair);
        
        //Set up wallfollower object
        WallFollower wallFollower = new WallFollower();
        wallFollower.setRobot(robot);
        wallFollower.setMaze(maze);
        
        return wallFollower;
	}
	
	/**
	 * Test that backup sensor alternative for left and backward sensors correctly operate for movement methods
	 * Correct behavior is the following:
	 * 	-If either the forward or leftward sensor fail, any combination of adjacent sensors can be used to replace them
	 */
	@Test
	void testSensorFailureLeftBack() {
		//Initialize maze for testing using default algorithm with deterministic settings
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(0);
		mazeFactory.order(order);
		mazeFactory.waitTillDelivered();
		Maze maze = order.getMaze();
		
		//Set up Control object & linked StateGenerating object
		Control controller = new Control();
		controller.turnOffGraphics();
		MazePanel panel = new MazePanel();
		//Throws warning errors since panel is not properly set
		//Should be fine to ignore though as GUI is not specifically used for tests
		StateGenerating generator = new StateGenerating();
		generator.start(controller, panel);
		generator.switchFromGeneratingToPlaying(maze);
		
		//Set up robot & its sensors
		UnreliableRobot robot = new UnreliableRobot();
		robot.setController(controller);
		robot.setBatteryLevel(3500);
		UnreliableSensor sensorF = new UnreliableSensor();
		UnreliableSensor sensorB = new UnreliableSensor();
		UnreliableSensor sensorR = new UnreliableSensor();
		UnreliableSensor sensorL = new UnreliableSensor();
		robot.addDistanceSensor(sensorF, Direction.FORWARD);
		robot.addDistanceSensor(sensorB, Direction.BACKWARD);
		robot.addDistanceSensor(sensorR, Direction.RIGHT);
		robot.addDistanceSensor(sensorL, Direction.LEFT);
		
		//Set up wallfollower object
        WallFollower wallFollower = new WallFollower();
        wallFollower.setRobot(robot);
        wallFollower.setMaze(maze);
        
        //Test scenario where only left and back sensors are operational, drive call should operate correctly without error thrown
        robot.startFailureAndRepairProcess(Direction.FORWARD, 0, 4000);
        robot.startFailureAndRepairProcess(Direction.BACKWARD, 4000, 0);
        robot.startFailureAndRepairProcess(Direction.RIGHT, 0, 4000);
        robot.startFailureAndRepairProcess(Direction.LEFT, 4000, 0);
        try {
			wallFollower.drive1Step2Exit();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during driving");
		}
	}
	
	/**
	 * Test that backup sensor alternatives for right and backward sensors correctly operate for movement methods
	 * Correct behavior is the following:
	 * 	-If either the forward or leftward sensor fail, any combination of adjacent sensors can be used to replace them
	 */
	@Test
	void testSensorFailureRightBack() {
		//Initialize maze for testing using default algorithm with deterministic settings
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(0);
		mazeFactory.order(order);
		mazeFactory.waitTillDelivered();
		Maze maze = order.getMaze();
		
		//Set up Control object & linked StateGenerating object
		Control controller = new Control();
		controller.turnOffGraphics();
		MazePanel panel = new MazePanel();
		//Throws warning errors since panel is not properly set
		//Should be fine to ignore though as GUI is not specifically used for tests
		StateGenerating generator = new StateGenerating();
		generator.start(controller, panel);
		generator.switchFromGeneratingToPlaying(maze);
		
		//Set up robot & its sensors
		UnreliableRobot robot = new UnreliableRobot();
		robot.setController(controller);
		robot.setBatteryLevel(3500);
		UnreliableSensor sensorF = new UnreliableSensor();
		UnreliableSensor sensorB = new UnreliableSensor();
		UnreliableSensor sensorR = new UnreliableSensor();
		UnreliableSensor sensorL = new UnreliableSensor();
		robot.addDistanceSensor(sensorF, Direction.FORWARD);
		robot.addDistanceSensor(sensorB, Direction.BACKWARD);
		robot.addDistanceSensor(sensorR, Direction.RIGHT);
		robot.addDistanceSensor(sensorL, Direction.LEFT);
		
		//Set up wallfollower object
        WallFollower wallFollower = new WallFollower();
        wallFollower.setRobot(robot);
        wallFollower.setMaze(maze);
        
        //Test scenario where only right and back sensors are operational, drive call should operate correctly without error thrown
        //robot.startFailureAndRepairProcess(Direction.FORWARD, 0, 4000);
        robot.startFailureAndRepairProcess(Direction.BACKWARD, 4000, 0);
        robot.startFailureAndRepairProcess(Direction.RIGHT, 4000, 0);
        //robot.startFailureAndRepairProcess(Direction.LEFT, 0, 4000);
        try {
			wallFollower.drive1Step2Exit();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during driving");
		}
	}
	
	/**
	 * Test that backup sensor alternatives for right and forward sensors correctly operate for movement methods
	 * Correct behavior is the following:
	 * 	-If either the forward or leftward sensor fail, any combination of adjacent sensors can be used to replace them
	 */
	@Test
	void testSensorFailureRightFront() {
		//Initialize maze for testing using default algorithm with deterministic settings
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(0);
		mazeFactory.order(order);
		mazeFactory.waitTillDelivered();
		Maze maze = order.getMaze();
		
		//Set up Control object & linked StateGenerating object
		Control controller = new Control();
		controller.turnOffGraphics();
		MazePanel panel = new MazePanel();
		//Throws warning errors since panel is not properly set
		//Should be fine to ignore though as GUI is not specifically used for tests
		StateGenerating generator = new StateGenerating();
		generator.start(controller, panel);
		generator.switchFromGeneratingToPlaying(maze);
		
		//Set up robot & its sensors
		UnreliableRobot robot = new UnreliableRobot();
		robot.setController(controller);
		robot.setBatteryLevel(3500);
		UnreliableSensor sensorF = new UnreliableSensor();
		UnreliableSensor sensorB = new UnreliableSensor();
		UnreliableSensor sensorR = new UnreliableSensor();
		UnreliableSensor sensorL = new UnreliableSensor();
		robot.addDistanceSensor(sensorF, Direction.FORWARD);
		robot.addDistanceSensor(sensorB, Direction.BACKWARD);
		robot.addDistanceSensor(sensorR, Direction.RIGHT);
		robot.addDistanceSensor(sensorL, Direction.LEFT);
		
		//Set up wallfollower object
        WallFollower wallFollower = new WallFollower();
        wallFollower.setRobot(robot);
        wallFollower.setMaze(maze);
        
        //Test scenario where only right and front sensors are operational, drive call should operate correctly without error thrown
        robot.startFailureAndRepairProcess(Direction.FORWARD, 4000, 0);
        robot.startFailureAndRepairProcess(Direction.BACKWARD, 0, 4000);
        robot.startFailureAndRepairProcess(Direction.RIGHT, 4000, 0);
        robot.startFailureAndRepairProcess(Direction.LEFT, 0, 4000);
        try {
			wallFollower.drive1Step2Exit();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during driving");
		}
	}
	
	/**
	 * Test that driving is operational and correctly throws errors in correct scenarios
	 * Correct behavior is the following:
	 * 	-Given enough energy, the wizard will drive the robot to and orient itself towards the exit; this is denoted by drive2Exit returning true
	 * 	-Given not enough energy, the wizard will fail to drive the robot fully to the exit and will return an exception noting the inoperable state of the vehicle
	 */
	@Test
	void testDriving() {
		//PSEUDOCODE
		//Set up tests where robot can drive to exit
		//Set up tests where it cannot (e.g., runs out of battery)
		//**********
		
		//Test if the wallfollower driver can traverse a maze, iteratively increase the maze skill level value from 0 to 3 and continue checking
		
		//Call the drive2Exit function to test; should return true to indicate success
		//Use try/catch to check if any drive attempt ends in failure
		//Should not occur reasonably given that each have enough energy to move about (3500 / 6) = 583 cells and have optimized path to exit
		//This test also encompasses drive1Step2Exit function since drive2Exit fully utilizes it during operation
		WallFollower wallFollower0 = createWallFollower(0, true, 5000, 4000, 2000);
		try {
			assertTrue(wallFollower0.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during driving");
		}
		WallFollower wallFollower1 = createWallFollower(1, true, 5000, 4000, 2000);
		try {
			assertTrue(wallFollower1.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during driving");
		}
		WallFollower wallFollower2 = createWallFollower(2, true, 5000, 4000, 2000);
		try {
			assertTrue(wallFollower2.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during driving");
		}
				
		//Now repeat the same test on non-perfect mazes (have rooms)
		WallFollower wallFollower0Rooms = createWallFollower(0, false, 5000, 4000, 2000);
		try {
			assertTrue(wallFollower0Rooms.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during driving");
		}
		WallFollower wallFollower1Rooms = createWallFollower(1, false, 5000, 4000, 2000);
		try {
			assertTrue(wallFollower1Rooms.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during driving");
		}
		WallFollower wallFollower2Rooms = createWallFollower(2, false, 5000, 4000, 2000);
		try {
			assertTrue(wallFollower2Rooms.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during driving");
		}
				
		//Now test if the robot throws an error when it runs out of energy and become inoperable
		WallFollower wallFollower0NoEnergy = createWallFollower(0, true, 1, 4000, 2000);
		assertThrows(Exception.class, () -> wallFollower0NoEnergy.drive2Exit());
		WallFollower wallFollower1NoEnergy = createWallFollower(1, true, 1, 4000, 2000);
		assertThrows(Exception.class, () -> wallFollower1NoEnergy.drive2Exit());
		WallFollower wallFollower2NoEnergy = createWallFollower(2, true, 1, 4000, 2000);
		assertThrows(Exception.class, () -> wallFollower2NoEnergy.drive2Exit());
	}

	/**
	 * Ensure that distance and energy consumption metrics are correctly tracked
	 * Correct behavior is the following: The meters will track the energy consumption and distance traveled at the beginning (both 0), and will iteratively increase as the robot moves along
	 */
	@Test
	void testMeters() {
		//PSEUDOCODE
		//Set up and check if odometer and energy consumption are correctly reported for various values
		//**********
		
		//Test by tracking that the value of the odometer and energy consumption increase as the robot goes along
		
		//Generate a wizard for testing use
		WallFollower wallFollower = createWallFollower(3, true, 3500, 5000, 5000);
				
		//Initial check should show 0 on both meters as hasn't been used yet
		assertEquals(0, wallFollower.getEnergyConsumption());
		assertEquals(0, wallFollower.getPathLength());
				
		//Call on the drive1Step2Exit function so we can iteratively check on the meters to ensure that they correctly operate
		//Will use a variable to track the values of the meters at each point and compare them to the next point
		int distanceTraveled = 0;
		float energyUsed = 0;
		for (int i = 0; i< 5; i++) {
			try {
				wallFollower.drive1Step2Exit();
			} catch (Exception e) {
				e.printStackTrace();
				fail("Error during driving");
			}
			//Make sure that the meters at this current point are greater than the meters at the previous point in time
			//Odometer and energy used should increase as the robot moves
			assertTrue(wallFollower.getEnergyConsumption() > energyUsed);
			assertTrue(wallFollower.getPathLength() > distanceTraveled);
			energyUsed = wallFollower.getEnergyConsumption();
			distanceTraveled = wallFollower.getPathLength();
		}
	}
}
