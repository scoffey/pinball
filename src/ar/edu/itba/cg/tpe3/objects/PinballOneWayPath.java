package ar.edu.itba.cg.tpe3.objects;

import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jmex.audio.AudioTrack;

public class PinballOneWayPath extends PinballWall {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector3f canCrossWallNormal;

	public PinballOneWayPath(String name, TriMesh mesh) {
		super(name, mesh);

		Vector3f[] vertex = new Vector3f[3];
		getTriangle(0, vertex);
		Triangle t = new Triangle(vertex[0], vertex[1], vertex[2]);
		this.canCrossWallNormal = t.getNormal().normalize();
//		System.out
//				.println("PinballOneWayPath: la normal que puede atravezar es: "
//						+ canCrossWallNormal);
	}

	public PinballOneWayPath(TriMesh mesh) {
		this("PinballOneWayPath", mesh);
	}

	@Override
	public void doCollision(PinballBall ball, Vector3f normal) {
		Vector3f worldNormal = new Vector3f();
		this.localToWorld(canCrossWallNormal, worldNormal);
		if (ball.getVelocity().normalize().dot(canCrossWallNormal) < 0) {
			Vector3f velocity = (normal.mult(-2
					* normal.dot(ball.getVelocity()))).add(ball.getVelocity());
			ball.setVelocity(velocity.mult(1.5F));
//			ball.stepBack();

			AudioTrack sound = getSound("collision");
			if (sound != null) {
				sound.play();
			}

		}
	}

}
