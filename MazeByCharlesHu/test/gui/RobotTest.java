package gui;

import generation.DefaultOrder;
import generation.Maze;
import generation.MazeFactory;

/**
 * Parent class for shared methods between ReliableRobotTest and UnreliableRobotTest
 * 
 * @author Charles Hu
 *
 */

class RobotTest {

	/**
	 * Create a controller object for testing use
	 * @param skillLevel as Integer for skill level of maze
	 * @return Control object connected to a maze of skill level size
	 */
	protected Control createController(int skillLevel) {
		//Initialize maze for testing using default algorithm with deterministic settings
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(skillLevel);
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
		
		return controller;
	}
}
