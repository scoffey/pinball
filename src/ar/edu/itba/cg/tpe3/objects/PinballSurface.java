package ar.edu.itba.cg.tpe3.objects;

import ar.edu.itba.cg.tpe3.PinballGame;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

public class PinballSurface extends BasePinballObject {

	private static final long serialVersionUID = 1L;

	public PinballSurface(String name, TriMesh mesh) {
		super(name, mesh);
	}

	public void doCollision(PinballBall ball, Vector3f normal) {

		if (Math.abs(ball.getVelocity().dot(normal)) < 1) {
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
		} else {
			Vector3f velocity = (normal.mult(-2
					* normal.dot(ball.getVelocity()))).add(ball.getVelocity());
			ball.setVelocity(velocity.mult(PinballGame.COLLISION_ENERGY_LOST));
			ball.stepBack();
		}
	}

	public Vector3f getNormal() {
		return getWorldRotation().mult(Vector3f.UNIT_Y);
	}

}
