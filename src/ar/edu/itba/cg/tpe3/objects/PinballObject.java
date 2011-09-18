package ar.edu.itba.cg.tpe3.objects;

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

public interface PinballObject {

	/**
	 * Realiza la colisión entre la pelota y el objeto actual. El objeto
	 * es el encargado de modificar el estado de la pelota de acuerdo con
	 * sus parámetros. Este método debe ser invocado cuando ya se detectó
	 * una colisión, y se dispone de la normal en el punto de colisión.
	 * 
	 * @param ball Pelota del juego que colisionó cno este objeto.
	 * @param normal Normal en el punto de colisión.
	 */
	public void collide(PinballBall ball, Vector3f normal);

	/**
	 * Actualiza el estado del objeto en cada instante si es necesario.
	 * @param tpf Tiempo del cuadro actual
	 */
	public void update(float tpf);

	/**
	 * Indica si hay colisión del objeto con un Spatial.
	 * 
	 * @param world
	 * @param checkTriangles
	 * @return
	 */
	public boolean hasCollision(Spatial world, boolean checkTriangles);

}
