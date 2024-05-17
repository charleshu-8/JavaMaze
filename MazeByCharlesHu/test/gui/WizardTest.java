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

class WizardTest {
	/**
	 * Create a wizard object for testing use
	 * @param skillLevel as Integer for skill level of maze
	 * @param isPerfect as boolean for whether maze is perfect (no rooms)
	 * @param robotEnergy as float for desired energy to set for robot
	 * @return Wizard connected to a generated maze and robot
	 */
	private Wizard createWizard(int skillLevel, boolean isPerfect, float robotEnergy) {
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
		ReliableRobot robot = new ReliableRobot();
		robot.setController(controller);
		robot.setBatteryLevel(robotEnergy);
		ReliableSensor sensorF = new ReliableSensor();
        ReliableSensor sensorB = new ReliableSensor();
        ReliableSensor sensorR = new ReliableSensor();
        ReliableSensor sensorL = new ReliableSensor();
        robot.addDistanceSensor(sensorF, Direction.FORWARD);
        robot.addDistanceSensor(sensorB, Direction.BACKWARD);
        robot.addDistanceSensor(sensorR, Direction.RIGHT);
        robot.addDistanceSensor(sensorL, Direction.LEFT);
        
        //Set up wizard object
        Wizard wizard = new Wizard();
        wizard.setRobot(robot);
        wizard.setMaze(maze);
        
        return wizard;
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
		
		//Test if the wizard driver can traverse a maze, iteratively increase the maze skill level value from 0 to 3 and continue checking
		
		//Call the drive2Exit function to test; should return true to indicate success
		//Use try/catch to check if any drive attempt ends in failure
		//Should not occur reasonably given that each have enough energy to move about (3500 / 6) = 583 cells and have optimized path to exit
		//This test also encompasses drive1Step2Exit function since drive2Exit fully utilizes it during operation
		Wizard wizard0 = createWizard(0, true, 3500);
		try {
			assertTrue(wizard0.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Wizard wizard1 = createWizard(1, true, 3500);
		try {
			assertTrue(wizard1.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Wizard wizard2 = createWizard(2, true, 3500);
		try {
			assertTrue(wizard2.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Rectangle maze, shouldn't effect outcome but good to test anyways
		Wizard wizard3 = createWizard(3, true, 3500);
		try {
			assertTrue(wizard3.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Now repeat the same test on non-perfect mazes (have rooms)
		Wizard wizard0Rooms = createWizard(0, false, 3500);
		try {
			assertTrue(wizard0Rooms.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Wizard wizard1Rooms = createWizard(1, false, 3500);
		try {
			assertTrue(wizard1Rooms.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Wizard wizard2Rooms = createWizard(2, false, 3500);
		try {
			assertTrue(wizard2Rooms.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Rectangle maze, shouldn't effect outcome but good to test anyways
		Wizard wizard3Rooms = createWizard(3, false, 3500);
		try {
			assertTrue(wizard3Rooms.drive2Exit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Now test if the robot throws an error when it runs out of energy and become inoperable
		//Don't need to test for crashes since operating wizard driver cannot crash reasonably
		Wizard wizard0NoEnergy = createWizard(0, true, 1);
		assertThrows(Exception.class, () -> wizard0NoEnergy.drive2Exit());
		Wizard wizard1NoEnergy = createWizard(1, true, 1);
		assertThrows(Exception.class, () -> wizard1NoEnergy.drive2Exit());
		Wizard wizard2NoEnergy = createWizard(2, true, 1);
		assertThrows(Exception.class, () -> wizard2NoEnergy.drive2Exit());
		//Rectangle maze, shouldn't effect outcome but good to test anyways
		Wizard wizard3NoEnergy = createWizard(3, true, 1);
		assertThrows(Exception.class, () -> wizard3NoEnergy.drive2Exit());
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
		Wizard wizard = createWizard(3, true, 3500);
		
		//Initial check should show 0 on both meters as hasn't been used yet
		assertEquals(0, wizard.getEnergyConsumption());
		assertEquals(0, wizard.getPathLength());
		
		//Call on the drive1Step2Exit function so we can iteratively check on the meters to ensure that they correctly operate
		//Will use a variable to track the values of the meters at each point and compare them to the next point
		int distanceTraveled = 0;
		float energyUsed = 0;
		for (int i = 0; i< 5; i++) {
			try {
				wizard.drive1Step2Exit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Make sure that the meters at this current point are greater than the meters at the previous point in time
			//Odometer and energy used should increase as the robot moves
			assertTrue(wizard.getEnergyConsumption() > energyUsed);
			assertTrue(wizard.getPathLength() > distanceTraveled);
			energyUsed = wizard.getEnergyConsumption();
			distanceTraveled = wizard.getPathLength();
		}
	}
}
