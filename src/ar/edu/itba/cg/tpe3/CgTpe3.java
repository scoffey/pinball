package ar.edu.itba.cg.tpe3;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.input.joystick.DummyJoystickInput;
import com.jme.renderer.AbstractCamera;
import com.jme.renderer.lwjgl.LWJGLRenderer;
import com.jme.renderer.lwjgl.LWJGLTextureRenderer;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.state.lwjgl.LWJGLTextureState;
import com.jme.system.PropertiesGameSettings;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.ThrowableHandler;
import com.jmex.audio.openal.OpenALSystem;
import com.jmex.audio.util.AudioLoader;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.math.TriangulationVertex;
import com.jmex.font3d.math.Triangulator;
import com.jme.util.lwjgl.LWJGLTimer;

public class CgTpe3 {
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Missing X3D input file\n"
					+ "Syntax: java -jar pinball.jar filename");
			return;
		}
		final String filename = args[0];
		
		final PinballGame app = new PinballGame();
		customizeLoggerLevels();
		app.setThrowableHandler(new ThrowableHandler() {
			public void handle(Throwable t) {
				t.printStackTrace();
				app.finish();
				app.cleanup();
				JOptionPane.showMessageDialog(null, t.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		});
		app.setLoader(filename);
		app.setConfigShowMode(ConfigShowMode.NeverShow);
		app.start();
	}


	private static void customizeLoggerLevels() {
		final Class<?>[] classes = new Class[] { PropertiesGameSettings.class,
				AbstractPinballGame.class, Node.class, AudioLoader.class,
				TriMesh.class, AbstractCamera.class, TextureManager.class,
				LWJGLDisplaySystem.class, DummyJoystickInput.class,
				LWJGLTimer.class, LWJGLTextureRenderer.class,  LWJGLRenderer.class, BasePinballGame.class,
				OpenALSystem.class, LWJGLTextureState.class, TriangulationVertex.class, Font3D.class};
		for (Class<?> c : classes) {
			Logger.getLogger(c.getName()).setLevel(Level.SEVERE);
		}
		Logger.getLogger(Triangulator.class.getName()).setLevel(Level.OFF);
	}

	public static void raiseException(Throwable e) {
		String description = e.getMessage();
		if (description == null || description.equals("")) {
			description = "Se ha producido un error en el programa.";
		}
		// Stack trace
		StackTraceElement[] st = e.getStackTrace();
		StringBuffer s = new StringBuffer("La excepción capturada fue: "
				+ e.toString() + "\n");
		for (int i = 0; i < st.length; i++) {
			if (st[i].isNativeMethod()) {
				break;
			}
			s.append("\t\ten " + st[i].toString() + "\n");
		}
		String message = description + "\n\n" + s.toString();
		JOptionPane.showMessageDialog(null, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

}
