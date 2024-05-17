package generation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import generation.Order.Builder;

class MazeBuilderBoruvkaTest extends MazeFactoryTest {
	/**
	 *Creates a maze using the Burovka algorithm by queuing a build order via mazeFactory
	 * @param skillLevel as an integer to indicate desired maze size
	 * @return Generated maze based on the desired maze size using the Boruvka maze generation algorithm
	 */
	@Override
	protected Maze createNewMaze(int skillLevel) {
		MazeFactory mazeFactory = new MazeFactory();
		DefaultOrder order = new DefaultOrder(skillLevel, Builder.Boruvka, true, 13);
		mazeFactory.order(order);
		mazeFactory.waitTillDelivered();
		return order.getMaze();
	}
	
	/**
	 * Goal: Check if weights are persistent
	 * Test fixture: Will need instance of MazeBuilderBoruvka that has generated a maze
	 * Functionality: Will test if getEdgeWeights consistently returns the same weight for the same input edge
	 * Check: Give some edge, calling getEdgeWeights should return the same value
	 */
	@Test
	void wallWeightsPersistent() {
		//Generate square (0) and rectangle (3) maze
		MazeBuilderBoruvka mazeSize0 = new MazeBuilderBoruvka();
		mazeSize0.buildOrder(new DefaultOrder(0, Builder.Boruvka, true, 13));
		mazeSize0.generate();
		MazeBuilderBoruvka mazeSize3 = new MazeBuilderBoruvka();
		mazeSize3.buildOrder(new DefaultOrder(3, Builder.Boruvka, true, 13));
		mazeSize3.generate();
		
		//Test square maze
		int edge0Call1 = mazeSize0.getEdgeWeight(0, 1, CardinalDirection.North);
		int edge0Call2 = mazeSize0.getEdgeWeight(0, 1, CardinalDirection.North);
		int edge0Call3 = mazeSize0.getEdgeWeight(3, 3, CardinalDirection.West);
		int edge0Call4 = mazeSize0.getEdgeWeight(3, 3, CardinalDirection.West);
		//Repeated calls on same edge should result in same weight
		assertEquals(edge0Call1, edge0Call2);
		assertEquals(edge0Call3, edge0Call4);
		
		//Test rectangle maze
		int edge3Call1 = mazeSize3.getEdgeWeight(0, 0, CardinalDirection.East);
		int edge3Call2 = mazeSize3.getEdgeWeight(0, 0, CardinalDirection.East);
		int edge3Call3 = mazeSize3.getEdgeWeight(4, 3, CardinalDirection.South);
		int edge3Call4 = mazeSize3.getEdgeWeight(4, 3, CardinalDirection.South);
		//Repeated calls on same edge should result in same weight
		assertEquals(edge3Call1, edge3Call2);
		assertEquals(edge3Call3, edge3Call4);
	}
	
	/**
	 * Goal: Check if walls share weights
	 * Test fixture: Will need instance of MazeBuilderBoruvka that has generated a maze
	 * Functionality: Will test if the weights of an edge is recognized and shared by both sides of the wall (i.e., neighboring cells share weights)
	 * Check: Given some wall with some weight, the cells on both sides of the wall should recognize the wall as an edge with the same weight value
	 */
	@Test
	void wallWeightsShared() {
		//Generate square (0) and rectangle (3) maze
		MazeBuilderBoruvka mazeSize0 = new MazeBuilderBoruvka();
		mazeSize0.buildOrder(new DefaultOrder(0, Builder.Boruvka, true, 13));
		mazeSize0.generate();
		MazeBuilderBoruvka mazeSize3 = new MazeBuilderBoruvka();
		mazeSize3.buildOrder(new DefaultOrder(3, Builder.Boruvka, true, 13));
		mazeSize3.generate();
		
		//Test square maze
		//Known edges with weights, should return some equal positive integer
		assertEquals(mazeSize0.getEdgeWeight(0, 1, CardinalDirection.North), mazeSize0.getEdgeWeight(0, 0, CardinalDirection.South));
		assertEquals(mazeSize0.getEdgeWeight(3, 3, CardinalDirection.West), mazeSize0.getEdgeWeight(2, 3, CardinalDirection.East));
		assertEquals(mazeSize0.getEdgeWeight(3, 0, CardinalDirection.South), mazeSize0.getEdgeWeight(3, 1, CardinalDirection.North));
		assertEquals(mazeSize0.getEdgeWeight(0, 2, CardinalDirection.East), mazeSize0.getEdgeWeight(1, 2, CardinalDirection.West));
		//Known edges without weights, should return 0
		assertEquals(mazeSize0.getEdgeWeight(0, 0, CardinalDirection.East), mazeSize0.getEdgeWeight(1, 0, CardinalDirection.West));
		
		//Test rectangle maze
		//Known edge weights, should return some equal positive integer
		assertEquals(mazeSize3.getEdgeWeight(0, 0, CardinalDirection.East), mazeSize3.getEdgeWeight(1, 0, CardinalDirection.West));
		assertEquals(mazeSize3.getEdgeWeight(4, 3, CardinalDirection.South), mazeSize3.getEdgeWeight(4, 4, CardinalDirection.North));
		assertEquals(mazeSize3.getEdgeWeight(6, 13, CardinalDirection.West), mazeSize3.getEdgeWeight(5, 13, CardinalDirection.East));
		assertEquals(mazeSize3.getEdgeWeight(5, 6, CardinalDirection.North), mazeSize3.getEdgeWeight(5, 5, CardinalDirection.South));
		//Known edges without weights, should return 0
		assertEquals(mazeSize3.getEdgeWeight(0, 1, CardinalDirection.North), mazeSize3.getEdgeWeight(0, 0, CardinalDirection.South));
	}
	
	/**
	 * Goal: Check if getEdgeWeight can handle invalid input
	 * Test fixture: Will need instance of MazeBuilderBoruvka that has generated a maze
	 * Functionality: Will test if method getEdgeWeight correctly identifies incorrect inputs and returns an invalid edge response (value 0)
	 * Check: Given invalid input, getEdgeWeight should return 0
	 */
	@Test
	void invalidWallWeights() {
		//Generate square (0) and rectangle (3) maze
		MazeBuilderBoruvka mazeSize0 = new MazeBuilderBoruvka();
		mazeSize0.buildOrder(new DefaultOrder(0, Builder.Boruvka, true, 13));
		mazeSize0.generate();
		MazeBuilderBoruvka mazeSize3 = new MazeBuilderBoruvka();
		mazeSize3.buildOrder(new DefaultOrder(3, Builder.Boruvka, true, 13));
		mazeSize3.generate();
		
		//Test square maze
		//Invalid edges, should return 0
		assertEquals(0, mazeSize0.getEdgeWeight(6, 6, CardinalDirection.North));
		assertEquals(0, mazeSize0.getEdgeWeight(0, 0, CardinalDirection.North));
		
		//Test rectangle maze
		//Invalid edges, should return 0
		assertEquals(0, mazeSize3.getEdgeWeight(30, 30, CardinalDirection.North));
		assertEquals(0, mazeSize3.getEdgeWeight(0, 0, CardinalDirection.North));
	}
}
