package ar.edu.itba.cg.tpe3.objects;

import ar.edu.itba.cg.tpe3.PinballGame;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;

public class PinballBall extends Sphere {

	private static final long serialVersionUID = 1L;
	public static final float MASS = 1;
	private static final float VELOCITY_LIMIT = 1.5F;

	private Vector3f initialPosition;
	private Vector3f previousPosition;
	private Vector3f position;
	private Vector3f velocity;
	private Vector3f forces;

	public PinballBall(Sphere sphere) {
		super("Ball", new Vector3f(), 15, 15, sphere.getRadius());
		this.setLocalRotation(sphere.getLocalRotation());
		this.setLocalTranslation(sphere.getLocalTranslation());
		this.setLocalScale(sphere.getLocalScale());
		this.setDefaultColor(sphere.getDefaultColor());
		setModelBound(new BoundingSphere());
		// updateModelBound();
		// updateWorldBound();
		// updateWorldVectors();
		// this.setLocalTranslation(this.getLocalTranslation().add(-0.1F, 0,
		// -0.15F));
		this.position = new Vector3f(getLocalTranslation());
		this.previousPosition = this.position;
		this.initialPosition = this.position;
		this.velocity = new Vector3f(0, 0, 0);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

	public void reset() {
		this.previousPosition = initialPosition;
		this.position = initialPosition;
		this.velocity = new Vector3f();
	}

	/**
	 * Actualiza el estado de la pelota, integrando con Euler. Recibe el
	 * intervalo de tiempo entre el cuadro anterior y este.
	 * 
	 * @param h
	 *            Paso de integración.
	 */
	public void update(float h) {
		previousPosition = position;
		if (velocity.length() > VELOCITY_LIMIT) {
			velocity = velocity.normalize().mult(VELOCITY_LIMIT);
		}
		position = velocity.mult(h).add(position);
		velocity = forces.mult(h / MASS).add(velocity);
		this.setLocalTranslation(position);
		this.updateModelBound();
	}

	/**
	 * Establece la posición de la pelota en donde se encontraba en el frame
	 * anterior.
	 */
	public void stepBack() {
		position = previousPosition;
	}

	public void addForce(Vector3f force) {
		forces = forces.add(force);
	}

	public void clearForce() {
		forces = new Vector3f(0, -MASS * PinballGame.GRAVITY, 0);
	}

	public Vector3f getPreviousPosition() {
		return previousPosition;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setPreviousPosition(Vector3f previousPosition) {
		this.previousPosition = previousPosition;
	}

	public void setInitialPosition(Vector3f initialPosition) {
		this.initialPosition = initialPosition;
	}
}
