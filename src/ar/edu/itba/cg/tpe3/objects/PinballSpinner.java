package ar.edu.itba.cg.tpe3.objects;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

public class PinballSpinner extends PinballWall {
	
	private static final long serialVersionUID = 1L;
	private float angle;
	private float angleIncrement;

	public PinballSpinner(String name, TriMesh mesh, float angleIncrement) {
		super(name, mesh);
		this.angle = 0;
		this.angleIncrement = angleIncrement;
	}

	public PinballSpinner(TriMesh mesh, float angleIncrement) {
		this("PinballCarousell", mesh, angleIncrement);
	}

	@Override
	public void update(float tpf) {
		this.getLocalRotation().fromAngleAxis(angle, Vector3f.UNIT_Y);
		angle += angleIncrement;
	}

}
