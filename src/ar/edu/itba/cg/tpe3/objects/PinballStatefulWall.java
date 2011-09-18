package ar.edu.itba.cg.tpe3.objects;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * PinballWall que al ser chocada cambia de color
 */
public class PinballStatefulWall extends PinballWall {

	private static final long serialVersionUID = 1L;
	private ColorRGBA[] colors;
	private int currentColorIndex;

	public PinballStatefulWall(String name, TriMesh mesh, ColorRGBA[] colors) {
		super(name, mesh);
		this.colors = colors;
		this.currentColorIndex = 0;
	}

	public PinballStatefulWall(TriMesh mesh, ColorRGBA[] colors) {
		this("PinballStatefullWall", mesh, colors);

	}

	public PinballStatefulWall(TriMesh mesh) {
		this(mesh, new ColorRGBA[] { ColorRGBA.black, ColorRGBA.white });
	}

	@Override
	public void doCollision(PinballBall ball, Vector3f normal) {
		setNextColor();
		super.doCollision(ball, normal);
	}

	private void setNextColor() {
		currentColorIndex = (currentColorIndex + 1) % colors.length;

		/* Pintar todos los triangulos */
		ColorRGBA[] c = new ColorRGBA[this.getVertexCount()];
		for (int i = 0; i < c.length; i++) {
			c[i] = colors[currentColorIndex];
		}
		this.setColorBuffer(BufferUtils.createFloatBuffer(c));
		this.updateRenderState();
	}

}
