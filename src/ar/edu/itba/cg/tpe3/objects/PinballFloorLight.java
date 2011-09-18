package ar.edu.itba.cg.tpe3.objects;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

public class PinballFloorLight extends BasePinballObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float passedTime;
	private float period;
	private int phase;
	private Vector3f initialColor;
	private Vector3f finalColor;
	private Vector3f direction;
	private float activePassedTime;
	private int framesCounter;

	public PinballFloorLight(float time, int phase, ColorRGBA initialColor,
			ColorRGBA finalColor, TriMesh mesh) {
		this(time, phase, initialColor, finalColor, "PinballFloorLight", mesh);
	}

	public PinballFloorLight(float time, int phase, ColorRGBA initialColor,
			ColorRGBA finalColor, String name, TriMesh mesh) {
		super(name, mesh);
		this.passedTime = 0;
		this.period = time;
		this.phase = phase;
		this.initialColor = new Vector3f(initialColor.r, initialColor.g,
				initialColor.b);
		this.finalColor = new Vector3f(finalColor.r, finalColor.g, finalColor.b);
		this.direction = this.finalColor.subtract(this.initialColor);
		this.activePassedTime = 0;
		this.setIsCollidable(false);
		this.framesCounter = 0;
		paint(initialColor);
	}

	@Override
	public void update(float tpf) {
		if (passedTime < phase) {
			passedTime += tpf;
		} else {
			if (framesCounter % 10 == 0) {
				Vector3f current = initialColor.add(direction
						.mult(activePassedTime / period));
				ColorRGBA currentColor = new ColorRGBA(current.x, current.y,
						current.z, 1);
				paint(currentColor);
				activePassedTime += tpf;
				if (activePassedTime >= period) {
					activePassedTime = 0;
					Vector3f aux = initialColor;
					initialColor = finalColor;
					finalColor = aux;
					direction = finalColor.subtract(initialColor);
				}
			}
			framesCounter++;
		}

	}

	private void paint(ColorRGBA currentColor) {
		ColorRGBA[] c = new ColorRGBA[this.getVertexCount()];
		for (int i = 0; i < c.length; i++) {
			c[i] = currentColor;
		}
		setColorBuffer(BufferUtils.createFloatBuffer(c));
		updateRenderState();
	}

	@Override
	public void collide(PinballBall ball, Vector3f normal) {
	}

	@Override
	public void doCollision(PinballBall ball, Vector3f normal) {
	}
}
