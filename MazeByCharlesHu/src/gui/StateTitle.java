package gui;

import java.util.Random;
import java.util.logging.Logger;

import generation.Order;
import generation.Order.Builder;
import gui.Constants.UserInput;

/**
 * Class handles the user interaction
 * while the game is in the first stage
 * where the user can select the skill-level.
 * This class is part of a state pattern for the
 * Control class. It is a ConcreteState.
 * 
 * It implements a state-dependent behavior
 * that controls the display and reacts to
 * key board input from a user. 
 * At this point user keyboard input is first dealt
 * with a key listener in Control 
 * and then handed over with a call to handleUserInput.
 * 
 * Responsibilities:
 * Show the title screen,
 * Accept input for skill level,  
 * Switch to the generating screen,
 * If given a filename, pass it on to the generating screen
 * 
 * Warning: Any instance is intended for one time use. 
 * Internal settings are not reset after use. 
 * Reusing the same instance over multiple rounds
 * would require adjustments such as resetting internal
 * fields to meaningful values.
 *
 * This code contains refactored code from Maze.java by Paul Falstad,
 * www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code
 * for teaching purposes.
 * Refactored by Peter Kemper
 */
public class StateTitle implements State {
	/**
	 * The logger is used to track execution and report issues.
	 * Collaborators are the UI and the MazeFactory.
	 * Level INFO: logs mitigated issues such as unexpected user input
	 * Level FINE: logs information flow in and out of its fields.
	 */
	private static final Logger LOGGER = Logger.getLogger(StateTitle.class.getName());

	/**
	 * The view determines what is seen on the screen.
	 */
    SimpleScreens view;
    
    /**
     * The panel is the capability to draw on the screen.
     */
    MazePanel panel;
    
    /**
     * Control is the context class of the State pattern.
     * The reference is needed to pull some pieces of information
     * plus switch control to the next state, which 
     * is the maze generating state.
     */
    Control control;
    
    /**
     * The seed to be used for the random number generator
     * during maze generation.
     */
    int seed;
    
    /** 
     * The filename for a file that stores a maze.
     * If filename is null: generate a maze. 
     * If filename is set to a name of an xml file with a maze: load maze from file
     */
    String filename; 
    
    /**
     * The builder algorithm to use for generating a maze.
     */
    Order.Builder builder;
    
    /** 
     * Started is used to enforce ordering constraint on method calls.
     * start() must be called before keyDown()
     * to make sure control variable has been set.
     * initial setting: false, start sets it to true.
     */
    boolean started;  
    
    /**
     * Default constructor with internal constraint that
     * method {@link #start(Control, MazePanel) start}
     * is called before any calls to 
     * {@link #handleUserInput(UserInput, int) handleUserInput} for handling
     * user input.
     * The seed is set to some fixed value which is
     * effective if the game operates in a deterministic
     * mode and will recompute the same maze over and 
     * over again if other parameters stay the same as well.
     * This behavior is useful during development.
     */
    public StateTitle() {
    	// initialization of some fields is delayed and done in start method
    	view = null; // initialized in start method
    	panel = null; // provided by start method
    	control = null; // provided by start method
    	started = false; // method start has not been called yet
    	
    	
    	filename = null; // no information yet, provided by set method if at all
    	
    	builder = Builder.DFS; //default generation method
    	
    	// The specific value used here does not matter.
    	// It is independent from others.
    	// Using the same value in repeated rounds of
    	// the game allows for playing the exact same
    	// maze again.
    	seed = 86422; //13; // default
    }
    /**
     * Sets the filename.
     * Calling with null does not hurt
     * but is pointless as maze will be then
     * generated randomly, which is the default
     * behavior anyway.
     * The filename must be set before 
     * {@link #start(Control, MazePanel) start} is
     * called or it will have no effect for this round
     * of the game.
     * @param filename provides the name of a file to
     * load a previously generated and stored maze from.
     * Can be null.
     */
    public void setFileName(String filename) {
    	// fail if control flow is wrong
    	// the filename must be set before start method is called
    	assert !started: "filename handed too late to be effective."; 
    	
        this.filename = filename;  
    }

    public void setBuilder(Builder builder) {
        this.builder = builder; 
    }
    /**
     * The method provides an appropriate response to user keyboard input. 
     * The control calls this method to communicate input and delegate its handling.
     * Method requires {@link #start(Controller, MazePanel) start} to be
     * called before.
     * @param userInput provides the feature the user selected, in this only start is supported
     * @param value is only used to carry the skill level, the size of the maze.
     * Range: {@literal {0,1,...,15} }, with 0 being the smallest maze.
     * @return true if handled, false if input comes before start was called
     */
    public boolean handleUserInput(UserInput userInput, int value) {
    	// user input too early, not sure how this could happen
        if (!started) {
        	LOGGER.info("Premature keyboard input:" + userInput + "with value " + value + ", ignored for mitigation");
            return false;
        }

        // keys describe level of expertise
        // create a maze according to the user's selected level
        // check ranges of values, use 0 as default
        if (value < 0 || value > 15) {
           	LOGGER.warning("Range violation:" + value + "outside range {0,...,15}, set to 0 for mitigation");
            value = 0;
        }
        // user types wrong key, just use 0 as a possible default value
        if (userInput == UserInput.START) {
            switchFromTitleToGenerating(value);
        }
        else {
        	LOGGER.info("Unexpected command:" + userInput + "with value " + value + ", ignored for mitigation");
        }    
        return true;
    }
    
    /**
     * Switches the controller to the generating screen.
     * Assumes that builder and perfect fields are already set
     * with set methods if default settings are not acceptable.
     * A maze is generated.
     * @param skillLevel, {@code 0 <= skillLevel}, size of maze to be generated
     */
    protected void switchFromTitleToGenerating(int skillLevel) {
    	// need to instantiate and configure the generating state
        StateGenerating nextState = new StateGenerating();

    	// The generating state needs
    	// 1) the builder algorithm to use
    	// 2) whether the maze needs to be perfect (no rooms) or can have rooms
    	// 3) the size of the maze or skill level
    	// 4) which seed to use for the random number generation
        nextState.setBuilder(builder); 
        nextState.setPerfect(control.isPerfect());
        nextState.setSkillLevel(skillLevel);
        if (!control.isDeterministic()) {
        	//LOGGER.severe("Assignment: implement code such that a repeated generation creates different mazes! Program stops!");
			//System.exit(0) ;
        	// TODO: implement code that makes sure we generate different random mazes each time
			// once done, comment out the System.exit call and the Logger.severe warning
        	// HINT: check http://download.oracle.com/javase/6/docs/api/java/util/Random.html
        	Random random = new Random();
        	seed = random.nextInt();
        }
        nextState.setSeed(seed);
        
        LOGGER.fine("Control switches from title to generating screen, maze will be newly generated for size " + skillLevel);
        
        // update the context class with the new state
        // and hand over control to the new state
        control.setState(nextState);
        nextState.start(control, panel);
    }
    /**
     * Switches the controller to the generating screen and
     * loads maze from file.
     * @param filename gives file to load maze from
     */
    public void switchFromTitleToGenerating(String filename) {
    	// need to instantiate and configure the generating state
    	StateGenerating nextState = new StateGenerating();
    	
    	// The generating state needs just the filename
    	// for the file to load a complete maze from.
    	nextState.setFileName(filename);

        LOGGER.fine("Control switches from title to generating screen, maze will be loaded from file " + filename);
    	
        // update the context class with the new state
        // and hand over control to the new state
    	control.setState(nextState);
        nextState.start(control, panel);
    }
    /**
     * Start the game by showing the title screen.
     * @param controller needed to be able to switch states, not null
     * @param panel is the UI entity to produce the title screen on 
     */
	@Override
	public void start(Control control, MazePanel panel) {
        started = true;
        // keep the reference to the controller to be able to call method to switch the state
        this.control = control;
        // keep the reference to the panel for drawing
        this.panel = panel;
        
        // make content show on the screen
        view = new SimpleScreens();
        // if given a filename, show a message and move to the loading screen
        // otherwise, show message that we wait for the skill level for input
        view.redrawTitle(panel,filename);
        panel.update(); // as drawing is complete, make screen update happen
        
        if (filename != null) {
            // wait 3 sec to give user
        	// a chance to read the title screen message
        	// and then move to the generating screen to
        	// actually load the maze from file
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switchFromTitleToGenerating(filename);
        }
        // one could reset internal fields at this point
        // to prepare for reuse of this object
        // eg reset filename such that in next round, 
        // user can select skill-level
        // filename = null;
		
	}

}
