package ar.edu.itba.cg.tpe3.objects;

import ar.edu.itba.cg.tpe3.PinballGame;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

public class PinballRamp extends PinballSurface {

	private static final long serialVersionUID = 1L;

	// protected Vector3f center;
	// protected float width;
	// protected float height;
	// protected float depth;

	public PinballRamp(TriMesh mesh) {
		super("Ramp", mesh);
	}

	@Override
	public void doCollision(PinballBall ball, Vector3f normal) {
		normal = getNormal();
		/*
		 * Proyectamos la velocidad actual en la superficie, corrigiendo la
		 * componente y, tal que el producto escalar entre la velocidad y la
		 * normal de 0 exacto.
		 */
		Vector3f velocity = ball.getVelocity();
		velocity.setY((-normal.x * velocity.x - normal.z * velocity.z)
				/ normal.y);
		ball.setVelocity(velocity);

		ball.setPosition(ball.getPosition().add(
				normal.normalize().mult(0.00001f)));

		/* Acumulamos la fuerza normal. */
		ball.addForce(normal.mult(PinballBall.MASS * PinballGame.GRAVITY));
	}

}
