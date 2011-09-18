package ar.edu.itba.cg.tpe3.objects;

import java.util.List;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

public class PinballFlipper extends PinballMovingObject {

	private static final long serialVersionUID = 1L;

	private FlipperType type;
	private Vector3f rotationPoint;

	public enum FlipperType {
		LEFT(1), RIGHT(-1);

		int direction;

		FlipperType(int direction) {
			this.direction = direction;
		}
	};

	/**
	 * Crea un nuevo flipper, a partir de un trimesh, un type y un punto de
	 * rotación.
	 * 
	 * @param flipper
	 *            Objeto geométrico que representa al flipper.
	 * @param type
	 *            Tipo del flipper (izquierdo o derecho).
	 * @param rotationPoint
	 *            Punto de rotación (usualmente tiene y=0).
	 */
	public PinballFlipper(TriMesh flipper, FlipperType type,
			Vector3f rotationPoint) {
		super("flipper", flipper, 1f, 10, 10);
		this.rotationPoint = rotationPoint;
		this.type = type;
		setPosition();
	}

	public void setPosition() {
		this.getLocalRotation().fromAngleAxis(
				getPosition() * this.type.direction, new Vector3f(0, 1, 0));
		float d = (float) Math.sqrt(rotationPoint.x * rotationPoint.x
				+ rotationPoint.z * rotationPoint.z);
		this.getLocalTranslation().set(
				d * (float) Math.cos(getPosition() * this.type.direction), 0,
				-d * (float) Math.sin(getPosition() * this.type.direction));
		this.getLocalTranslation().z *= type.direction;
		this.getLocalTranslation().x *= type.direction;

		this.updateModelBound();
	}

	protected float getVelocityIncrement(PinballBall ball) {
		float factor = 0;
		Vector3f rotationPointPosta = rotationPoint.add(0, ball.getRadius(), 0);
		factor = -this.localToWorld(rotationPointPosta, null).dot(
				ball.getPosition());
		return getState() == PinballObjectState.INCREASING ? factor * 5 : 0;
	}

	@Override
	public Vector3f getNormalAt(List<Vector3f> normals, Vector3f supNormal,
			PinballBall ball) {
		Vector3f ret = supNormal.cross(Vector3f.UNIT_X);
		Quaternion rotation = this.getWorldRotation();
		return rotation.mult(ret);
	}

}
