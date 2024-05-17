package generation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MazeFactoryTest {
	/**
	 *Creates a maze using the default algorithm by queuing a build order via mazeFactory
	 * @param skillLevel as an integer to indicate desired maze size
	 * @return Generated maze based on the desired maze size using the default maze generation algorithm
	 */
	protected Maze createNewMaze(int skillLevel) {
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(skillLevel);
		mazeFactory.order(order);
		mazeFactory.waitTillDelivered();
		return order.getMaze();
	}
	
	/**
	 * Goal: Test will check if maze correctly generates with a single exit
	 * Test fixture: Will need a fully generated instance of the maze provided by MazeBuilder, queued using MazeFactory
	 * Functionality: Will check along the borders of the maze to find an exit; will also count the amount of exits encountered to ensure only 1 exists
	 * Check: Given some maze, test will return true if exit exists on border, else fails
	 */
	@Test
	void testHasExit() {
		int exitCounter = 0;
		//Generate a perfect square maze (0) and a rectangle maze (3)
		Maze mazeSize0 = createNewMaze(0);
		Maze mazeSize3 = createNewMaze(3);
		int[][] mazeDistances0 = mazeSize0.getMazedists().getAllDistanceValues();
		int[][] mazeDistances3 = mazeSize3.getMazedists().getAllDistanceValues();
		//A single maze exit will be indicated by a cell having mazedists of 1
		//In order to ensure that there is only one exit, check that only 1 cell out of all cells in a maze have a value of 1
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (mazeDistances0[i][j] == 1) {
					exitCounter++;
				}
			}
		}
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 15; j++) {
				if (mazeDistances3[i][j] == 1) {
					exitCounter++;
				}
			}
		}
		//The counter should have a value of 2; 1 for a single exit for maze of skillLevel 0 and 1 for a single exit for maze of skillLevel 3
		assertTrue(exitCounter == 2);
	}
	
	/**
	 * Goal: Check if each cell in maze can reach the exit
	 * Test fixture: Will need a fully generated instance of the maze provided by MazeBuilder, queued using MazeFactory; accessible instance of MazeContainer
	 * Functionality: Will check mazedists from MazeContainer to see if all values are valid
	 * Check: Given mazedists from MazeContainer for some maze, values in mazedist should be valid
	 */
	@Test
	void testHasReachableExit() {	
		//Generate 2 mazes that are perfect squares (0, 5), 2 that are rectangles (3, 6) to test
		Maze mazeSize0 = createNewMaze(0);
		Maze mazeSize3 = createNewMaze(3);
		Maze mazeSize5 = createNewMaze(5);
		Maze mazeSize6 = createNewMaze(6);
		int[][] mazeDistances0 = mazeSize0.getMazedists().getAllDistanceValues();
		int[][] mazeDistances3 = mazeSize3.getMazedists().getAllDistanceValues();
		int[][] mazeDistances5 = mazeSize5.getMazedists().getAllDistanceValues();
		int[][] mazeDistances6 = mazeSize6.getMazedists().getAllDistanceValues();
		//Have to iterate through every cell of maze to ensure that all cells can reach exit (has a distance to exit greater than 0)
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				assertTrue(mazeDistances0[i][j] > 0);
			}
		}
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 15; j++) {
				assertTrue(mazeDistances3[i][j] > 0);
			}
		}
		for (int i = 0; i < 25; i++) {
			for (int j = 0; j < 25; j++) {
				assertTrue(mazeDistances5[i][j] > 0);
			}
		}
		for (int i = 0; i < 35; i++) {
			for (int j = 0; j < 25; j++) {
				assertTrue(mazeDistances6[i][j] > 0);
			}
		}
	}

	/**
	 * Goal: Check if wall count of maze without rooms has correct amount of walls to make it perfect
	 * Test fixture: Will need a fully generated instance of the maze provided by MazeBuilder, queued using MazeFactory
	 * Functionality: For perfect maze of w width and h height, interior wall count should be equal to (w-1)(h-1)
	 * Check: If wall count matches expected, pass, else fail
	 */
	@Test
	void testPerfectMazeWallCount() {	
		int wallCounter = 0;
		//Generate a perfect square maze (0) and a rectangle maze (3)
		Maze mazeSize0 = createNewMaze(0);
		Maze mazeSize3 = createNewMaze(3);
		//Need to find amount of walls that exist for maze of skillLevel = 0
		//First check existence of east and south walls; iterate from (0,0) to (2,2) to avoid counting border walls
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (mazeSize0.hasWall(i, j, CardinalDirection.East)) {
					wallCounter++;
				}
				if (mazeSize0.hasWall(i, j, CardinalDirection.South)) {
					wallCounter++;
				}
			}
		}
		//Check along farthest right side of maze; only check for southern wall to avoid border walls
		for (int i = 0; i < 3; i++) {
			if (mazeSize0.hasWall(3, i, CardinalDirection.South)) {
				wallCounter++;
			}
		}
		//Check along farthest lower side of maze; only check for eastern wall to avoid border walls
		for (int i = 0; i < 3; i++) {
			if (mazeSize0.hasWall(i, 3, CardinalDirection.East)) {
				wallCounter++;
			}
		}
		//Walls should amount to (w-1)(h-1)=(4-1)(4-1)=9
		assertTrue(wallCounter == 9);
		//Do same check with maze of skillLevel = 3
		//Reset wall counter
		wallCounter = 0;
		//Check cells from (0,0) to (18, 13)
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 14; j++) {
				if (mazeSize3.hasWall(i, j, CardinalDirection.East)) {
					wallCounter++;
				}
				if (mazeSize3.hasWall(i, j, CardinalDirection.South)) {
					wallCounter++;
				}
			}
		}
		//Check cells to farthest right side
		for (int i = 0; i < 14; i++) {
			if (mazeSize3.hasWall(19, i, CardinalDirection.South)) {
				wallCounter++;
			}
		}
		//Check cells to farthest lower side
		for (int i = 0; i < 19; i++) {
			if (mazeSize3.hasWall(i, 14, CardinalDirection.East)) {
				wallCounter++;
			}
		}
		//Walls should amount to (w-1)(h-1)=(20-1)(15-1)=266
		assertTrue(wallCounter == 266);
	}
	
	/**
	 * Goal: Check if size of generated maze matches inputed level size
	 * Test fixture: Will need a fully generated instance of the maze provided by MazeBuilder, queued using MazeFactory
	 * Functionality: Check maze height and width to see if they match height and width of given level size
	 * Check: For given maze, height and width should match preset height and width for some level
	 */
	@Test
	void testCorrectLevelRoomSize() {	
		//Generate a perfect square maze (0) and a rectangle maze (3)
		Maze mazeSize0 = createNewMaze(0);
		Maze mazeSize3 = createNewMaze(3);
		//Check widths and heights correct per values set in Constants class
		assertTrue(mazeSize0.getWidth() == 4);
		assertTrue(mazeSize0.getHeight() == 4);
		assertTrue(mazeSize3.getWidth() == 20);
		assertTrue(mazeSize3.getHeight() == 15);
	}
}
