package ar.edu.itba.cg.tpe3.objects;

import java.util.List;

import ar.edu.itba.cg.tpe3.PinballGame;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jmex.audio.AudioTrack;

public class PinballWall extends BasePinballObject {

	private static final long serialVersionUID = 1L;

	public PinballWall(TriMesh mesh) {
		this("Wall", mesh);
	}

	protected PinballWall(String name, TriMesh mesh) {
		super(name, mesh);
	}

	public void doCollision(PinballBall ball, Vector3f normal) {
		Vector3f velocity = (normal.mult(-2 * normal.dot(ball.getVelocity())))
				.add(ball.getVelocity());
		ball.setVelocity(velocity.mult(PinballGame.COLLISION_ENERGY_LOST));
		ball.stepBack();

		AudioTrack sound = getSound("collision");
		if (sound != null) {
			sound.play();
		}
	}

	@Override
	public Vector3f getNormalAt(List<Vector3f> normals, Vector3f supNormal,
			PinballBall ball) {

		Vector3f ret = new Vector3f();
		int count = 0;
		for (Vector3f normal : normals) {
			if (normal.dot(ball.getVelocity()) <= 0) {
				ret.addLocal(normal);
				count++;
			}
		}

		if (count == 0) {
			for (Vector3f normal : normals) {
				ret.addLocal(normal.mult(-1));
				count++;
			}
		}

		ret.divideLocal(count);

		Vector3f retNormal = new Vector3f(ret);
		Vector3f loQueLeRestamos = supNormal.mult(supNormal.dot(ret)
				/ supNormal.lengthSquared());
		retNormal.subtractLocal(loQueLeRestamos);
		ret = retNormal;

		return ret;

	}
}
