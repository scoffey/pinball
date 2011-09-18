package ar.edu.itba.cg.tpe3;

import java.util.logging.Level;
import java.util.logging.Logger;

import ar.edu.itba.cg.tpe3.gui.PinballGameMenu;
import ar.edu.itba.cg.tpe3.gui.PinballMainMenu;
import ar.edu.itba.cg.tpe3.gui.PinballMenuManager;

import com.jme.app.AbstractGame;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.joystick.JoystickInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.GameSettings;
import com.jme.system.JmeException;
import com.jme.system.PropertiesGameSettings;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.geom.Debugger;

public class BasePinballGame extends AbstractPinballGame {
	private static final Logger logger = Logger.getLogger(BasePinballGame.class
			.getName());

	/**
	 * The camera that we see through.
	 */
	protected Camera cam;

	/**
	 * The root of our normal scene graph.
	 */
	protected Node rootNode;

	/**
	 * Text node for showing frames per second.
	 */
	protected Text fpsNode;

	/**
	 * Handles our mouse/keyboard input in the game.
	 */
	protected InputHandler gameInputHandler;

	/**
	 * Handles our mouse/keyboard input in the menu.
	 */
	protected InputHandler menuInputHandler;

	/**
	 * High resolution timer for jME.
	 */
	protected Timer timer;

	/**
	 * Simply an easy way to get at timer.getTimePerFrame(). Also saves math
	 * cycles since you don't call getTimePerFrame more than once per frame.
	 */
	protected float tpf;

	/**
	 * True if the renderer should display normals.
	 */
	protected boolean showNormals = false;

	/**
	 * A lightstate to turn on and off for the rootNode
	 */
	protected LightState lightState;

	/**
	 * boolean for toggling the simpleUpdate and geometric update parts of the
	 * game loop on and off.
	 */
	protected boolean showMenu = true;

	protected PropertiesGameSettings settings;

	private PinballMenuManager menuManager;

	private int fpsUpdateCounter = 0;
	
	public BasePinballGame() {
		System.setProperty("jme.stats", "set");
	}

	/**
	 * Creates display, sets up camera, and binds keys. Called in
	 * BaseGame.start() directly after the dialog box.
	 * 
	 * @see AbstractGame#initSystem()
	 */
	protected void initSystem() throws JmeException {
		logger.info(getVersion());
		try {
			/**
			 * Get a DisplaySystem acording to the renderer selected in the
			 * startup box.
			 */
			display = DisplaySystem.getDisplaySystem(settings.getRenderer());

			display.setMinDepthBits(8);
			display.setMinStencilBits(0);
			display.setMinAlphaBits(0);
			display.setMinSamples(0);

			/** Create a window with the startup box's information. */
			display.createWindow(settings.getWidth(), settings.getHeight(),
					settings.getDepth(), settings.getFrequency(), settings
							.isFullscreen());

			/**
			 * Create a camera specific to the DisplaySystem that works with the
			 * display's width and height
			 */
			cam = display.getRenderer().createCamera(display.getWidth(),
					display.getHeight());

		} catch (JmeException e) {
			/**
			 * If the displaysystem can't be initialized correctly, exit
			 * instantly.
			 */
			logger.log(Level.SEVERE, "Could not create displaySystem", e);
			System.exit(1);
		}

		/** Set a black background. */
		display.getRenderer().setBackgroundColor(ColorRGBA.black.clone());

		/** Set up how our camera sees. */
		Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
		/** Move our camera to a correct place and orientation. */
		cam.setFrame(loc, left, up, dir);
		/** Signal that we've changed our camera's location/frustum. */
		cam.update();
		/** Assign the camera to this renderer. */
		display.getRenderer().setCamera(cam);

		/** Create a basic input controller. */
		gameInputHandler = new FirstPersonHandler(cam, 1, 0.5f);
		menuInputHandler = createMenuInputHandler();

		/** Get a high resolution timer for FPS updates. */
		timer = Timer.getTimer();

		/** Assign key bindings. */
		KeyBindingManager kbm = KeyBindingManager.getKeyBindingManager();
		kbm.set("toggle_pause", KeyInput.KEY_P);
		kbm.set("toggle_normals", KeyInput.KEY_N);
		kbm.set("escape", KeyInput.KEY_ESCAPE);
		kbm.set("enter", KeyInput.KEY_RETURN);
	}

	private InputHandler createMenuInputHandler() {
		InputHandler ih = new InputHandler();
		KeyInputAction up = new KeyInputAction() {
			public void performAction(InputActionEvent arg0) {
				BasePinballGame.this.menuManager.getCurrentMenu()
						.selectPrevious();
			}
		};
		KeyInputAction down = new KeyInputAction() {
			public void performAction(InputActionEvent arg0) {
				BasePinballGame.this.menuManager.getCurrentMenu().selectNext();
			}
		};
		KeyInputAction enter = new KeyInputAction() {
			public void performAction(InputActionEvent arg0) {
				BasePinballGame.this.menuManager.getCurrentMenu()
						.fireSelectedAction();
			}
		};
		KeyInputAction escape = new KeyInputAction() {
			public void performAction(InputActionEvent arg0) {
				BasePinballGame.this.menuManager.getCurrentMenu().fireAction(
						"escape");
				BasePinballGame.this.showMenu = false;
			}
		};

		ih.addAction(up, "lookUp", false);
		ih.addAction(down, "lookDown", false);
		ih.addAction(escape, "escape", false);
		ih.addAction(enter, "enter", false);

		return ih;
	}

	/**
	 * Creates rootNode, lighting, statistic text, and other basic render
	 * states. Called in BaseGame.start() after initSystem().
	 * 
	 * @see AbstractGame#initGame()
	 */
	protected void initGame() {
		/** Create rootNode */
		rootNode = new Node("rootNode");

		/**
		 * Create a ZBuffer to display pixels closest to the camera above
		 * farther ones.
		 */
		ZBufferState buf = display.getRenderer().createZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		rootNode.setRenderState(buf);

		/** Set up a basic, default light. */
		PointLight light = new PointLight();
		light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
		light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		light.setLocation(new Vector3f(100, 100, 100));
		light.setEnabled(true);

		/** Attach the light to a lightState and the lightState to rootNode. */
		lightState = display.getRenderer().createLightState();
		lightState.setEnabled(true);
		lightState.attach(light);
		rootNode.setRenderState(lightState);

		/** Create fps node. */
		fpsNode = new Text("fps", "FPS");
		fpsNode.setCullHint(Spatial.CullHint.Never);
		fpsNode.setRenderState(Text.getDefaultFontTextureState());
		fpsNode.setRenderState(Text.getFontBlend());
		fpsNode.setLocalScale(.8f);
		fpsNode.setTextColor(ColorRGBA.gray);
		fpsNode.setLocalTranslation(display.getRenderer().getWidth()
				- fpsNode.getWidth() - 50, display.getRenderer().getHeight()
				- fpsNode.getHeight() - 10, 0);
		rootNode.attachChild(fpsNode);

		/**
		 * Create menu system.
		 */
		menuManager = new PinballMenuManager(display.getRenderer(), this);
		menuManager.setMainMenu(new PinballMainMenu(menuManager));
		menuManager.setGameMenu(new PinballGameMenu(menuManager));

		timer.reset();

		/**
		 * Update geometric and rendering information for the rootNode.
		 */
		rootNode.updateGeometricState(0.0f, true);
		rootNode.updateRenderState();

		timer.reset();

	}

	/**
	 * Updates the timer, sets tpf, updates the input and updates the fps
	 * string. Also checks keys for toggling pause, bounds, normals, lights,
	 * wire etc.
	 * 
	 * @param interpolation
	 *            unused in this implementation
	 * @see AbstractGame#update(float interpolation)
	 */
	protected void update(float interpolation) {
		/** Recalculate the framerate. */
		timer.update();

		/** Update tpf to time per frame according to the Timer. */
		tpf = timer.getTimePerFrame();

		/** Check for key/mouse updates. */
		if (showMenu) {
			menuManager.getCurrentMenu().update();
			menuInputHandler.update(tpf);
		} else {
			gameInputHandler.update(tpf);
		}

		/** Execute updateQueue item. */
		GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE)
				.execute();

		/** If toggle_pause is a valid command (via key p), change pause. */
		if (KeyBindingManager.getKeyBindingManager().isValidCommand(
				"toggle_pause", false)) {
			showMenu = !showMenu;
		}

		/** Actualizar los cuadros por segundo. */
		if (fpsUpdateCounter == 0) {
			fpsNode.getText().delete(0, fpsNode.getText().length());
			fpsNode.getText().append("FPS " + (int) timer.getFrameRate());
			fpsUpdateCounter = 20;
		} else {
			fpsUpdateCounter--;
		}
		
		if (KeyBindingManager.getKeyBindingManager().isValidCommand(
				"toggle_normals", false)) {
			showNormals = !showNormals;
		}

		/** Handle escape command. */
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("escape",
				false)) {
			showMenu = !showMenu;
		}

		if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit",
				false)) {
			finish();
		}

		if (!showMenu) {
			/** Call simpleUpdate in any derived classes of SimpleGame. */
			simpleUpdate();

			/** Update controllers/render states/transforms/bounds for rootNode. */
			rootNode.updateGeometricState(tpf, true);
		}

	}

	/**
	 * Clears stats, the buffers and renders bounds and normals if on.
	 * 
	 * @param interpolation
	 *            unused in this implementation
	 * @see AbstractGame#render(float interpolation)
	 */
	protected void render(float interpolation) {
		Renderer r = display.getRenderer();
		/** Clears the previously rendered information. */
		r.clearBuffers();

		// Execute renderQueue item
		GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
				.execute();

		/** Draw the rootNode and all its children. */
		r.draw(rootNode);

		/** Call simpleRender() in any derived classes. */
		simpleRender();

		/** Draw the stats node to show our stat charts. */
		if (showMenu) {
			r.draw(menuManager.getCurrentMenu());
		}

		if (showNormals) {
			Debugger.drawNormals(rootNode, r);
		}
	}

	public void startGame() {
		logger.log(Level.INFO, "Loading scene...");
		simpleInitGame();
		logger.log(Level.INFO, "Scene loaded!");
		rootNode.updateGeometricState(0.0f, true);
		rootNode.updateRenderState();
		timer.reset();
	}

	/**
	 * Called near end of initGame(). Must be defined by derived classes.
	 */
	protected void simpleInitGame() {
		// do nothing
	}

	/**
	 * Can be defined in derived classes for custom updating. Called every frame
	 * in update.
	 */
	protected void simpleUpdate() {
		// do nothing
	}

	/**
	 * Can be defined in derived classes for custom rendering. Called every
	 * frame in render.
	 */
	protected void simpleRender() {
		// do nothing
	}

	/**
	 * unused
	 * 
	 * @see AbstractGame#reinit()
	 */
	protected void reinit() {
		// do nothing
	}

	/**
	 * Cleans up the keyboard.
	 * 
	 * @see AbstractGame#cleanup()
	 */
	protected void cleanup() {
		TextureManager.doTextureCleanup();
		if (display != null && display.getRenderer() != null)
			display.getRenderer().cleanup();
		KeyInput.destroyIfInitalized();
		MouseInput.destroyIfInitalized();
		JoystickInput.destroyIfInitalized();
	}

	/**
	 * Calls the quit of BaseGame to clean up the display and then closes the
	 * JVM.
	 */
	protected void quit() {
		super.quit();
		System.exit(0);
	}

	@Override
	protected GameSettings getNewSettings() {
		return settings;
	}

	public void hideMenu() {
		this.showMenu = false;
	}

	public PinballMenuManager getMenuManager() {
		return menuManager;
	}
}
