package ar.edu.itba.cg.tpe3.objects;

import java.util.List;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

public class PinballTable extends PinballSurface {

	private static final long serialVersionUID = 1L;

	public PinballTable(TriMesh mesh) {
		super("PinballTable", mesh);
	}

	
	@Override
	public Vector3f getNormalAt(List<Vector3f> normals, Vector3f supNormal,
			PinballBall ball) {
		return supNormal;
	}
	
	@Override
	public Vector3f getNormal() {
		return getWorldRotation().mult(Vector3f.UNIT_Y);
	}

}
