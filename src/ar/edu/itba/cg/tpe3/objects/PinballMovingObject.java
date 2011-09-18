package ar.edu.itba.cg.tpe3.objects;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jmex.audio.AudioTrack;

/**
 * Clase que agrupa el comportamiento de aquellos objetos que se mueven al
 * presionar una tecla, y vuelven a su estado normal al soltar la tecla. El
 * plunger y los flippers extienden de esta clase. La posición está
 * parametrizada por un único número (el ángulo en el caso de los flippers, y el
 * desplazamiento en el caso del plunger).
 */
public abstract class PinballMovingObject extends PinballWall {
	private float position;
	private float previousPosition;
	private float positionMax;
	private float positionIncreaseStep;
	private float positionDecreaseStep;
	private PinballObjectState state;

	public enum PinballObjectState {
		QUIET, INCREASING, DECREASING;
	}

	public PinballMovingObject(String name, TriMesh mesh, float positionMax,
			float positionIncreaseStep, float positionDecreaseStep) {
		super(name, mesh);
		this.positionMax = positionMax;
		this.positionIncreaseStep = positionIncreaseStep;
		this.positionDecreaseStep = positionDecreaseStep;
		this.position = 0;
		this.previousPosition = 0;
	}

	public void doCollision(PinballBall ball, Vector3f normal) {
		super.doCollision(ball, normal);
		ball.setVelocity(ball.getVelocity().add(
				normal.mult(getVelocityIncrement(ball))));
		if (state == PinballObjectState.DECREASING) {
			position = previousPosition;
		}

		// System.out.println("position: " + position + " 3previousPosition: "
		// + previousPosition);
		setPosition();

		AudioTrack soundThrow = getSound("throw");
		if (soundThrow != null && state == PinballObjectState.DECREASING) {
			soundThrow.play();
		}
	}

	public void update(boolean keyPressed, float tpf) {
		boolean refresh = false;
		AudioTrack soundUp = getSound("up");
		AudioTrack soundDown = getSound("down");

		float aux = position;
		if (keyPressed && position < positionMax) {
			if (state != PinballObjectState.INCREASING && soundUp != null) {
				soundUp.play();
			}
			state = PinballObjectState.INCREASING;
			position += positionIncreaseStep * tpf;
			refresh = true;
		} else if (!keyPressed && position > 0) {
			if (state != PinballObjectState.DECREASING && soundDown != null) {
				soundDown.play();
			}
			state = PinballObjectState.DECREASING;
			position -= positionDecreaseStep * tpf;
			refresh = true;
		} else {
			state = PinballObjectState.QUIET;
		}
		if (position < 0) {
			position = 0;
			refresh = true;
		}

		if (aux != position) {
			previousPosition = position;
		}

		if (refresh) {
			setPosition();
		}
	}

	public float getPosition() {
		return position;
	}

	public PinballObjectState getState() {
		return state;
	}

	/**
	 * Establece la ubicación del objeto a partir de la posición. Las subclases
	 * deben implementar este método, interpretando como corresponda el float
	 * position y realizando todas las transformaciones locales necesarias.
	 */
	protected abstract void setPosition();

	/**
	 * Una vez que la pelota colisiona con el objeto, y se calcula la nueva
	 * velocidad, se invoca a este método para determinar cuánto sumarle al
	 * módulo de la velocidad. Recibe la pelota porque tal vez necesite
	 * consultar la posición (por ejemplo para calcular el torque en el caso de
	 * los flippers).
	 * 
	 * @param ball
	 *            La pelota que colisionó con el objeto.
	 * @return Cuánto sumarle al módulo de la velocidad.
	 */
	protected abstract float getVelocityIncrement(PinballBall ball);
}
