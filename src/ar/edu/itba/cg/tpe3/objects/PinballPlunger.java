package ar.edu.itba.cg.tpe3.objects;

import java.util.List;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

public class PinballPlunger extends PinballMovingObject {

	private static final long serialVersionUID = 1L;

	public PinballPlunger(TriMesh mesh) {
		super("Plunger", mesh, 10f, 20, 40);
		setPosition();
	}

	public void setPosition() {
		this.getLocalTranslation().z = getPosition();
		this.updateModelBound();
	}

	protected float getVelocityIncrement(PinballBall ball) {
		return getState() == PinballObjectState.DECREASING ? getPosition() / 1f
				: 0;
	}

	@Override
	public Vector3f getNormalAt(List<Vector3f> normals, Vector3f supNormal, PinballBall ball) {
		return supNormal.cross(Vector3f.UNIT_X);
	}

}
