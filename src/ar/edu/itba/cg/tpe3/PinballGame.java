package ar.edu.itba.cg.tpe3;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.itba.cg.tpe3.objects.BasePinballObject;
import ar.edu.itba.cg.tpe3.objects.PinballBall;
import ar.edu.itba.cg.tpe3.objects.PinballFlipper;
import ar.edu.itba.cg.tpe3.objects.PinballMagnet;
import ar.edu.itba.cg.tpe3.objects.PinballObject;
import ar.edu.itba.cg.tpe3.objects.PinballPlunger;
import ar.edu.itba.cg.tpe3.objects.PinballSurface;

import com.jme.input.ChaseCamera;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.intersection.TriangleCollisionResults;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.MaterialState.ColorMaterial;
import com.jme.system.DisplaySystem;
import com.jme.system.PropertiesGameSettings;
import com.jmex.audio.AudioSystem;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.Text3D;

public class PinballGame extends BasePinballGame {

	public static final float GRAVITY = 9.8f;
	public static final float COLLISION_ENERGY_LOST = 0.5f;
	/*
	 * Máxima cantidad de impulso ruidoso que se le agrega a la pelota cuando se
	 * genera un tilt
	 */
	private static final float TILT_STRENGHT = 5F;
	/* Cuanto el tilt mueve la mesa */
	private static final float TILT_PINBALLTABLE_MOVEMENT_STRENGHT = 0.01F;

	/* Cantidad de movimientos de la mesa que provoca un tilt */
	private static final int HITS_PER_TILT = 4;

	/* Cantidad máxima de tilts que se pueden hacer sin "romper" la mesa */
	private static final int MAX_TILTS_COUNT = 10;
	private static final String INITIAL_LIVES = "4";

	private PinballBall ball;
	private Set<PinballFlipper> leftFlippers;
	private Set<PinballFlipper> rightFlippers;
	private PinballPlunger plunger;
	private List<PinballObject> pinballObjects;
	private int tiltMovementCount;
	private int tiltsCount;
	private TriMesh scoreMesh;
	private Text3D scoreText;
	private Text3D livesText;
	private ChaseCamera ballChaserCamera;
	private boolean firstTime = true;
	private List<PinballSurface> pinballSurfaces;

	private enum CameraType {
		THIRD_PERSON_CAMERA, NICE_CAMERA, CHASE_CAMERA
	};

	private CameraType cameraType;

	/* por un Bug en JME, debe definirse como variable de clase */
	// private static TrianglePickResults tpr = new TrianglePickResults();
	/*
	 * Cilindro que se utiliza para ver si entre 2 cuadros hubo alguna
	 * intersección
	 */
	// private Cylinder cylinder;
	// private Sphere sphere;
	private Vector3f tiltTraslation = null;

	private PinballLoader loader;

	public PinballGame() {
		super();
		this.pinballObjects = new ArrayList<PinballObject>();
		this.pinballSurfaces = new ArrayList<PinballSurface>();
		this.leftFlippers = new HashSet<PinballFlipper>();
		this.rightFlippers = new HashSet<PinballFlipper>();
		this.cameraType = CameraType.THIRD_PERSON_CAMERA;
	}

	public void setLoader(String filename) {
		this.settings = new PropertiesGameSettings("pinball.properties");
		this.settings.load();
		try {
			this.settings.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.loader = new PinballLoader(this, filename);
	}

	@Override
	protected void simpleInitGame() {

		/*
		 * La carga se hace la primera vez, despues es solo reiniciar
		 * parametros.
		 */
		if (firstTime) {
			firstTime = false;

			loader.start();
			try {
				while (!loader.hasFinished())
					Thread.sleep(100);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			// La invocacion a createScoreLabels se movio a simpleUpdate con un
			// if para que se ejecute 1 sola vez.

			// Assign key bindings
			KeyBindingManager kbm = KeyBindingManager.getKeyBindingManager();
			kbm.set("LEFT_FLIPPER", KeyInput.KEY_COMMA);
			kbm.set("RIGHT_FLIPPER", KeyInput.KEY_PERIOD);
			kbm.set("PLUNGER", KeyInput.KEY_SPACE);
			kbm.set("TILT", KeyInput.KEY_TAB);
			kbm.set("CHANGE_CAMERA", KeyInput.KEY_8);
			kbm.set("RESET_CAMERA", KeyInput.KEY_9);
			kbm.set("RESET", KeyInput.KEY_0);
			kbm.set("SOUND", KeyInput.KEY_O);

			// Make the object default colors shine through
			MaterialState ms = display.getRenderer().createMaterialState();
			ms.setColorMaterial(ColorMaterial.AmbientAndDiffuse);
			rootNode.setRenderState(ms);
			cam.update();

			HashMap<String, Object> props = new HashMap<String, Object>();
			props.put(ThirdPersonMouseLook.PROP_MAXROLLOUT, "6");
			props.put(ThirdPersonMouseLook.PROP_MINROLLOUT, "3");
			props.put(ThirdPersonMouseLook.PROP_MAXASCENT, "" + 90
					* FastMath.DEG_TO_RAD);
			props.put(ChaseCamera.PROP_INITIALSPHERECOORDS, new Vector3f(5, 0,
					30 * FastMath.DEG_TO_RAD));
			props.put(ChaseCamera.PROP_DAMPINGK, "4");
			props.put(ChaseCamera.PROP_SPRINGK, "9");

			ballChaserCamera = new ChaseCamera(cam, ball/* , props */);
			ballChaserCamera.setMaxDistance(0.5F);
			ballChaserCamera.setMinDistance(0.1F);
		}
		
		cameraType = CameraType.NICE_CAMERA;
		Vector3f position = new Vector3f(0.018705372F, 1.6280912F,
				0.33572504F);
		Quaternion rotation = new Quaternion(1, 0, 0, 0);
		float inclination = -0.48F;
		setCamera(position, rotation, inclination);
	}

	protected void simpleUpdate() {

		if (scoreMesh != null && scoreText == null) {
			createLabels();
		}

		if (KeyBindingManager.getKeyBindingManager().isValidCommand(
				"CHANGE_CAMERA", false)) {
			if (cameraType.equals(CameraType.THIRD_PERSON_CAMERA)) {
				cameraType = CameraType.NICE_CAMERA;
				Vector3f position = new Vector3f(0.018705372F, 1.6280912F,
						0.33572504F);
				Quaternion rotation = new Quaternion(1, 0, 0, 0);
				float inclination = -0.48F;
				setCamera(position, rotation, inclination);
			} else if (cameraType.equals(CameraType.NICE_CAMERA)) {
				cameraType = CameraType.CHASE_CAMERA;
			} else {
				cameraType = CameraType.THIRD_PERSON_CAMERA;
				Vector3f position = new Vector3f(0.020092139F, 1.9646039F,
						1.2954552F);
				Quaternion rotation = new Quaternion(1, 0, 0, 0);
				float inclination = -0.36F;
				setCamera(position, rotation, inclination);
			}
			cam.update();
		}

		if (KeyBindingManager.getKeyBindingManager().isValidCommand("RESET",
				false)) {
			reinit();
		}

		if (KeyBindingManager.getKeyBindingManager().isValidCommand("SOUND",
				false)) {
			if (AudioSystem.getSystem().isMuted()) {
				AudioSystem.getSystem().unmute();
			} else {
				AudioSystem.getSystem().mute();
			}
		}

		/* Actualizar flippers derechos. */
		for (PinballFlipper f : rightFlippers) {
			f.update(tiltsCount < MAX_TILTS_COUNT
					&& KeyBindingManager.getKeyBindingManager().isValidCommand(
							"RIGHT_FLIPPER", true), this.tpf);
		}

		/* Actualizar flippers izquierdos. */
		for (PinballFlipper f : leftFlippers) {
			f.update(tiltsCount < MAX_TILTS_COUNT
					&& KeyBindingManager.getKeyBindingManager().isValidCommand(
							"LEFT_FLIPPER", true), this.tpf);
		}

		/* Actualizar tilt. */
		if (tiltsCount < MAX_TILTS_COUNT
				&& KeyBindingManager.getKeyBindingManager().isValidCommand(
						"TILT", false)) {
			initTilt();
		}

		/* Agregar impulso generado por el Tilt */
		updateTiltMovement();

		/* Chequear colisiones */
		// Vector3f tableNormal = new Vector3f(0, 1, 0);
		// for (PinballObject po : pinballObjects) {
		// if (po instanceof PinballTable) {
		// tableNormal = ((Spatial) po).getWorldRotation().mult(
		// tableNormal);
		// }
		// }
		/*
		 * Le decimos a la pelota que por ahora la única fuerza que actúa sobre
		 * ella es la gravedad. Eventualmente, si hay colisiones cada objeto se
		 * encargará de llamar a addForce para agregarle cualquier otra fuerza.
		 */
		if (ball != null) {
			ball.clearForce();
		}

		/*
		 * Promediar las normales de las superficies con las que la pelota esta
		 * en contacto (suelo y rampa)
		 */
		Vector3f surfaceNormal = new Vector3f();
		int count = 0;
		for (PinballSurface ps : pinballSurfaces) {
			if (ball.hasCollision((Spatial) ps, false)) {
				surfaceNormal.addLocal(ps.getNormal());
				count++;
			}
		}
		surfaceNormal.divideLocal(count);

		for (PinballObject po : pinballObjects) {
			po.update(this.tpf);
			/* Si es un Magnet, agregarle la fuerza correspondiente */
			if (po instanceof PinballMagnet) {
				ball.addForce(((PinballMagnet) po).getForce(ball));
			}

			/* Fijarse si hubo colisión. */
			if (po.hasCollision(ball, false)) {
				TriMesh mesh = (TriMesh) po;
				TriangleCollisionResults tcr = new TriangleCollisionResults();
				ball.findCollisions((Spatial) po, tcr);
				/*
				 * Si hubo por lo menos una colisión en por lo menos un
				 * triángulo...
				 */
				if (tcr.getNumber() > 0
						&& tcr.getCollisionData(0).getTargetTris().size() > 0) {
					/*
					 * Calculamos la normal en el punto de interseccion como el
					 * promedio de las normales en todos los triángulos.
					 */
					List<Vector3f> normals = new ArrayList<Vector3f>();
					Vector3f normal = new Vector3f(0, 0, 0);
					for (Integer t : tcr.getCollisionData(0).getTargetTris()) {
						Vector3f[] vertex = new Vector3f[3];
						mesh.getTriangle(t, vertex);
						vertex[0] = mesh.localToWorld(vertex[0], null);
						vertex[1] = mesh.localToWorld(vertex[1], null);
						vertex[2] = mesh.localToWorld(vertex[2], null);
						Vector3f currentNormal = new Triangle(vertex[0],
								vertex[1], vertex[2]).getNormal();
						normals.add(currentNormal);
						normal = normal.add(currentNormal);
					}
					normal = normal.divide(tcr.getCollisionData(0)
							.getTargetTris().size());

					/*
					 * Si no es la mesa, entonces proyectamos la normal del
					 * objeto colisionado de manera tal de que quede
					 * perfectamente perpendicular a la normal de la mesa.
					 */
					if (!(po instanceof PinballSurface)) {
						normal = proyectVector(normal, surfaceNormal);
					}
					/*
					 * Si la pelota está más de un 10% incrustada, la sacamos
					 * para afuera
					 */
					float ballPercentageCollision = (float) tcr
							.getCollisionData(0).getSourceTris().size()
							/ ball.getTriangleCount();
					if (ballPercentageCollision > 0.1) {
						ball.setPosition(ball.getPosition().add(
								normal.mult(0.001f)));
					}

					/* El objeto colisionado se encarga de actualizar la pelota. */
					/* Parche para que funcione bien el plunger. */
					normal = ((BasePinballObject) po).getNormalAt(normals,
							surfaceNormal, ball);
					po.collide(ball, normal.normalize());
				}
			}
		}

		/* Actualizar plunger. */
		plunger.update(tiltsCount < MAX_TILTS_COUNT
				&& KeyBindingManager.getKeyBindingManager().isValidCommand(
						"PLUNGER", true), tpf);

		if (cameraType.equals(CameraType.CHASE_CAMERA)) {
			ballChaserCamera.update(this.tpf);
		}
		ball.update(tpf);
		cam.update();
		AudioSystem.getSystem().update();
	}

	private Vector3f proyectVector(Vector3f vector, Vector3f direction) {
		Vector3f vectorCopy = new Vector3f(vector);
		Vector3f loQueLeRestamos = direction.mult(direction.dot(vector)
				/ direction.lengthSquared());
		vectorCopy.subtractLocal(loQueLeRestamos);
		return vectorCopy;
	}

	private void setCamera(Vector3f position, Quaternion rotation,
			float inclination) {
		Vector3f direction = new Vector3f(0, 0, -1);
		Vector3f left = new Vector3f(-1, 0, 0);
		Vector3f up = new Vector3f(0, 1, 0);
		rotation.fromAngleAxis(inclination, new Vector3f(1, 0, 0));
		direction = rotation.mult(direction);
		left = rotation.mult(left);
		up = direction.cross(left);
		cam.setFrame(position, left, up, direction);
	}

	private void updateTiltMovement() {
		if (tiltMovementCount > 0) {
			Node transformNode = (Node) rootNode.getChild(3);
			Vector3f movement = null;
			movement = new Vector3f(TILT_PINBALLTABLE_MOVEMENT_STRENGHT
					* ((tiltMovementCount % 2) * 2 - 1), 0, 0);
			tiltMovementCount++;
			if (tiltTraslation == null) {
				tiltTraslation = transformNode.getLocalTranslation();
			}
			transformNode.setLocalTranslation(tiltTraslation.add(movement));
			transformNode.updateModelBound();
			ball.addForce(new Vector3f((float) Math.random() * TILT_STRENGHT,
					(float) Math.random() * TILT_STRENGHT, (float) Math
							.random()
							* TILT_STRENGHT));

			if (tiltMovementCount > HITS_PER_TILT) {
				restorePinballTable();
			}
		}
	}

	private void restorePinballTable() {
		tiltMovementCount = 0;
		Node transformNode = (Node) rootNode.getChild(3);
		transformNode.setLocalTranslation(tiltTraslation);
		tiltTraslation = null;
		transformNode.updateModelBound();
	}

	private void initTilt() {
		if (tiltMovementCount == 0) {
			tiltMovementCount = 1;
			tiltsCount++;
		}
	}

	/**
	 * Se encarga de resetear todo lo que sea necesario en el juego para volver
	 * a jugar como la primera vez. (Nuevo juego, con todas las vidas, puntaje
	 * cero, etc.)
	 */
	public void reinit() {
		replay();
		tiltsCount = 0;
		setScore(0);
		setInitialLivesLeft();
	}

	/**
	 * Se encarga de lo que haya que hacer cuando se "pierde una vida" en el
	 * pinball. (Reubicar la pelota para volver a lanzar, restar una vida, etc.)
	 */
	public void replay() {
		ball.reset();
	}

	// public void resetCamera() {
	// if (initialCamera != null) {
	// copyCamera(initialCamera, cam);
	// cam.update();
	// ballChaserCamera = new ChaseCamera(cam, ball/* , props */);
	// ballChaserCamera.setMaxDistance(0.5F);
	// ballChaserCamera.setMinDistance(0.1F);
	// this.cameraType = CameraType.THIRD_PERSON_CAMERA;
	// }
	// }
	//
	// private void copyCamera(Camera original, Camera copy) {
	// copy.setFrame(new Vector3f(original.getLocation()), new Vector3f(
	// original.getLeft()), new Vector3f(original.getUp()),
	// new Vector3f(original.getDirection()));
	// }

	public Node getRootNode() {
		return rootNode;
	}

	public void setBall(PinballBall ball) {
		this.ball = ball;
	}

	public void addLeftFlipper(PinballFlipper f) {
		this.leftFlippers.add(f);
		addPinballObject(f);
	}

	public void addRightFlipper(PinballFlipper f) {
		this.rightFlippers.add(f);
		addPinballObject(f);
	}

	public void addPinballObject(PinballObject po) {
		pinballObjects.add(po);
		if (po instanceof PinballSurface) {
			pinballSurfaces.add((PinballSurface) po);
		}
	}

	public Camera getCamera() {
		return cam;
	}

	public DisplaySystem getDisplay() {
		return display;
	}

	public void setPlunger(PinballPlunger plunger) {
		this.plunger = plunger;
		this.addPinballObject(plunger);
	}

	/*
	 * Por problemas con los threads, no se pueden crear los Text3d desde el
	 * loader :(
	 */
	private void createLabels() {
		Font3D font = new Font3D(new Font("Arial", Font.PLAIN, 20), 0.001f,
				true, true, true);
		// Font3DGradient gradient = new Font3DGradient(new Vector3f(0.5f, 1,
		// 0),
		// ColorRGBA.white, ColorRGBA.white);
		// gradient.applyEffect(font);
		Node parent = scoreMesh.getParent();

		Vector3f scaleFactor = new Vector3f(scoreMesh.getLocalScale().mult(
				1 / (float) 15));
		scaleFactor = new Vector3f(scaleFactor.x, scaleFactor.y, 0.001F);
		scoreText = font.createText("0000000", 50.0f, 1);
		livesText = font.createText(INITIAL_LIVES, 50.0f, 1);
		scoreText.setLocalScale(scaleFactor);
		scoreText.setFontColor(ColorRGBA.white);
		scoreText.setLocalTranslation(new Vector3f(-0.02F, -0.22F,
				((Box) scoreMesh).zExtent * 1.1F));
		livesText.setLocalScale(scaleFactor);
		livesText.setLocalTranslation(new Vector3f(-0.23F, -0.22F,
				((Box) scoreMesh).zExtent * 1.1F));
		livesText.setFontColor(ColorRGBA.white);

		Font3D fontLabels = new Font3D(new Font("Arial", Font.PLAIN, 20),
				0.001f, true, true, true);
		// gradient.applyEffect(fontLabels);
		Text3D scoreLabel = fontLabels.createText("Score", 50.0f, 0);
		scoreLabel.setLocalScale(scaleFactor.mult(0.6f));
		scoreLabel.setLocalTranslation(new Vector3f(-0.02F, -0.15F,
				((Box) scoreMesh).zExtent * 1.1F));
		scoreLabel.updateRenderState();
		scoreLabel.updateModelBound();
		scoreLabel.updateGeometricState(0, true);

		Text3D livesLabel = fontLabels.createText("Lives", 50.0f, 0);
		livesLabel.setLocalScale(scaleFactor.mult(0.6f));
		livesLabel.setLocalTranslation(new Vector3f(-0.23F, -0.15F,
				((Box) scoreMesh).zExtent * 1.1F));
		livesLabel.updateRenderState();
		livesLabel.updateModelBound();
		livesLabel.updateGeometricState(0, true);

		parent.attachChild(scoreText);
		parent.attachChild(livesText);
		parent.attachChild(scoreLabel);
		parent.attachChild(livesLabel);
	}

	public void setScore(TriMesh mesh) {
		scoreMesh = mesh;
	}

	public int getScore() {
		return Integer.parseInt(scoreText.getText().toString());
	}

	public void setScore(int newScore) {
		scoreText.setText(scoreToString(newScore, 7));
		scoreText.updateRenderState();
		scoreText.updateGeometricState(0, true);
	}

	public void addToScore(int count) {
		setScore(getScore() + count);
	}

	private String scoreToString(Integer score, int minDigits) {
		String ret = score.toString();
		int len = minDigits - ret.length();
		for (int i = 0; i < len; i++) {
			ret = "0" + ret;
		}
		return ret;
	}

	public void setInitialLivesLeft() {
		livesText.setText(INITIAL_LIVES);
	}

	public void substractLife() {
		livesText.setText(new Integer(getLivesLeft() - 1).toString());
	}

	public int getLivesLeft() {
		return Integer.parseInt(livesText.getText().toString());
	}

}
