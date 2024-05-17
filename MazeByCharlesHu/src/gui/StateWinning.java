package gui;

import java.util.logging.Logger;

import gui.Constants.UserInput;


/**
 * Class handles the user interaction
 * while the game is in the final stage
 * where the user sees the final screen
 * and can restart the game.
 * This class is part of a state pattern for the
 * Controller class. It is a ConcreteState.
 * 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt with a key listener in Control
 * and then handed over with the handleUserInput method.
 * 
 * Responsibilities
 * Show the final screen
 * Accept input of any kind to return to title screen  
 *
 * This code contains refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class StateWinning implements State {
	/**
	 * The logger is used to track execution and report issues.
	 * Collaborators are the UI and the MazeFactory.
	 * Level INFO: logs mitigated issues such as unexpected user input
	 * Level FINE: logs information flow in and out of its fields.
	 */
	private static final Logger LOGGER = Logger.getLogger(StateWinning.class.getName());

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
     * Started is used to enforce ordering constraint on method calls.
     * start() must be called before keyDown()
     * to make sure control variable has been set.
     * initial setting: false, start sets it to true.
     */
    boolean started;
    
    /**
     * Describes the path length, the number of steps, the player did
     * to get to the exit.
     */
    int pathLength;
    
    /**
     * Default constructor with internal constraint that
     * method {@link #start(Controller, MazePanel) start}
     * is called before any calls to 
     * {@link #handleUserInput(UserInput, int) keydown} for handling
     * user input.
     */
    public StateWinning() {
       	// initialization of some fields is delayed and done in start method
    	view = null; // initialized in start method
    	panel = null; // provided by start method
    	control = null; // provided by start method
    	started = false; // method start has not been called yet
 
        pathLength = 0;
        
    }
    
    /**
     * Start the game by showing the final screen with a winning message.
     * @param controller needed to be able to switch states, must be not null
     * @param panel is the UI entity to produce the screen on 
     */
    public void start(Control control, MazePanel panel) {
        started = true;
        // keep the reference to the controller to be able to call method to switch the state
        this.control = control;
        // keep the reference to the panel for drawing
        this.panel = panel;
        // create view to show content
        view = new SimpleScreens();

        if (panel == null) {
    		LOGGER.config("Warning: no panel, dry-run game without graphics!");
    		return;
    	}
        // otherwise show finish screen with winning message
        // draw content on panel
        view.redrawFinish(panel,0, 0, 0);
        // update screen with panel content
        panel.update();

    }
    /**
     * Start the game by showing the final screen with a winning message. Overloaded for automated driver end game states.
     * @param controller needed to be able to switch states, must be not null
     * @param panel is the UI entity to produce the screen on 
     * @param winState for the type of win screen desired; 1 automated win, 2 automated loss, 3 automated crash
     */
    public void start(Control control, MazePanel panel, int winState) {
        started = true;
        // keep the reference to the controller to be able to call method to switch the state
        this.control = control;
        // keep the reference to the panel for drawing
        this.panel = panel;
        // create view to show content
        view = new SimpleScreens();

        if (panel == null) {
    		LOGGER.config("Warning: no panel, dry-run game without graphics!");
    		return;
    	}
        // otherwise show finish screen with winning message
        // draw content on panel
        view.redrawFinish(panel, winState, this.control.getDriver().getPathLength(), this.control.getDriver().getEnergyConsumption());
        // update screen with panel content
        panel.update();

    }
    /**
     * The method provides an appropriate response to user keyboard input. 
     * The control calls this method to communicate input and delegate its handling.
     * Method requires {@link #start(Control, MazePanel) start} to be
     * called before.
     * Regardless of input, the response is to switch to the title screen.
     * @param userInput is not used in this state, exists only for consistency across State classes
     * @param value is not used in this state, exists only for consistency across State classes
     * @return true if handled, false if input comes before start was called
     */
    public boolean handleUserInput(UserInput userInput, int value) {
    	// user input too early, not sure how this could happen
        if (!started) {
        	LOGGER.info("Premature keyboard input:" + userInput + "with value " + value + ", ignored for mitigation");
            return false;
        }
        // for any keyboard input switch to title screen
        switchToTitle();    
        return true;
    }

    /**
     * Switches the controller to the initial screen.
     */
    public void switchToTitle() {
    	// need to instantiate and configure the title state
        StateTitle currentState = new StateTitle();
        
        LOGGER.fine("Control switches from winning to title screen.");
        
        // update the context class with the new state
        // and hand over control to the new state

        control.setState(currentState);
        currentState.start(control, panel);
    }
    
    /**
     * Sets the path length.
     * @param pathLength the number of steps the player did from starting to exit position
     */
    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }
}



