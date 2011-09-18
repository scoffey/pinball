package ar.edu.itba.cg.tpe3.objects;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

public class PinballMagnet extends PinballWall {

	private static final long serialVersionUID = 1L;

	private static final float MIN_DISTANCE = 0.15F;

	public float charge = 1.0f;

	public PinballMagnet(TriMesh mesh, float charge) {
		this("Magnet", mesh, charge);
	}

	public PinballMagnet(String name, TriMesh mesh, float charge) {
		super(name, mesh);
		this.charge = charge;
	}

	public Vector3f getForce(PinballBall ball) {
		float distance = this.getWorldTranslation().distance(
				ball.getWorldTranslation());
		if (distance < MIN_DISTANCE) {
			return this.getWorldTranslation().subtract(
					ball.getWorldTranslation()).mult(charge / distance);
		}
		return new Vector3f();
	}
}
