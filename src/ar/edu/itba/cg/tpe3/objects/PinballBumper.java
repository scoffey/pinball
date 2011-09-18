package ar.edu.itba.cg.tpe3.objects;

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.MaterialState.ColorMaterial;

public class PinballBumper extends PinballWall {

	private static final long serialVersionUID = 1L;
	public float elasticConstant = 1.0f;

	public PinballBumper(TriMesh mesh, float elasticConstant) {
		this("Bumper", mesh, elasticConstant);
	}

	protected PinballBumper(String name, TriMesh mesh, float elasticConstant) {
		super(name, mesh);
		this.elasticConstant = elasticConstant;
	}

	@Override
	public void doCollision(PinballBall ball, Vector3f normal) {
		super.doCollision(ball, normal);
		ball.getVelocity().addLocal(normal.mult(elasticConstant));
	}

	@Override
	public void update(float tpf) {
		MaterialState ms = (MaterialState) ((Spatial) this)
				.getRenderState(RenderState.RS_MATERIAL);
		if (ms != null) {
			ms.setColorMaterial(ColorMaterial.AmbientAndDiffuse);
			if (ms.getSpecular().r > 0) {
				ms.getSpecular().r -= 0.1;
				ms.getSpecular().g -= 0.1;
				ms.getSpecular().b -= 0.1;
			} else {
				ms.setSpecular(null);
			}
			((Spatial) this).updateRenderState();
		}
	}

}
