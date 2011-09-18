package ar.edu.itba.cg.tpe3;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.AbstractGame;
import com.jme.input.InputSystem;
import com.jme.util.ThrowableHandler;
import com.jme.system.GameSettings;

/**
 * <code>BaseGame</code> provides the simplest possible implementation of a
 * main game loop. Both logic and graphics are updated as quickly as possible,
 * with no interpolation to account for shifting frame rates. It is suggested
 * that a more complex variant of AbstractGame be used in almost all cases.
 * 
 * @author Mark Powell, Eric Woroshow
 * @version $Id: BaseGame.java,v 1.16 2007/08/02 21:36:19 nca Exp $
 */
public abstract class AbstractPinballGame extends AbstractGame {
	private static final Logger logger = Logger
			.getLogger(AbstractPinballGame.class.getName());
	protected ThrowableHandler throwableHandler;

	/**
	 * The simplest main game loop possible: render and update as fast as
	 * possible.
	 */
	public final void start() {
		logger.info("Application started.");
		try {
			getAttributes();
			
			if (!finished) {
				initSystem();

				assertDisplayCreated();

				initGame();

				// main loop
				while (!finished && !display.isClosing()) {
					// handle input events prior to updating the scene
					// - some applications may want to put this into update of
					// the game state
					InputSystem.update();

					// update game state, do not use interpolation parameter
					update(-1.0f);

					// render, do not use interpolation parameter
					render(-1.0f);

					// swap buffers
					display.getRenderer().displayBackBuffer();

					Thread.yield();
				}
			}
		} catch (Throwable t) {
			logger.logp(Level.SEVERE, this.getClass().toString(), "start()",
					"Exception in game loop", t);
			if (throwableHandler != null) {
				throwableHandler.handle(t);
			}
		}

		try {
			cleanup();
			logger.info("Application ending.");

			if (display != null)
				display.reset();
			quit();
		} catch (Throwable t) {
			logger.logp(Level.SEVERE, this.getClass().toString(), "start()",
					"Exception at game exit", t);
			if (throwableHandler != null) {
				throwableHandler.handle(t);
			}
		}
	}

	/**
	 * Closes the display
	 * 
	 * @see AbstractGame#quit()
	 */
	protected void quit() {
		if (display != null)
			display.close();
	}

	/**
	 * 
	 * @return
	 */
	protected ThrowableHandler getThrowableHandler() {
		return throwableHandler;
	}

	/**
	 * 
	 * @param throwableHandler
	 */
	protected void setThrowableHandler(ThrowableHandler throwableHandler) {
		this.throwableHandler = throwableHandler;
	}

	/**
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#update(float interpolation)
	 */
	protected abstract void update(float interpolation);

	/**
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#render(float interpolation)
	 */
	protected abstract void render(float interpolation);

	/**
	 * @see AbstractGame#initSystem()
	 */
	protected abstract void initSystem();

	/**
	 * @see AbstractGame#initGame()
	 */
	protected abstract void initGame();

	/**
	 * @see AbstractGame#reinit()
	 */
	protected abstract void reinit();

	/**
	 * @see AbstractGame#cleanup()
	 */
	protected abstract void cleanup();

	/**
	 * @see AbstractGame#getNewSettings()
	 */
	protected abstract GameSettings getNewSettings();

}
