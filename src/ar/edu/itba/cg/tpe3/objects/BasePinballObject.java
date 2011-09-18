package ar.edu.itba.cg.tpe3.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.RenderState;
import com.jmex.audio.AudioTrack;

public class BasePinballObject extends TriMesh implements PinballObject {
	private static final long serialVersionUID = 1L;
	private static int instanceCount = 0;

	private Set<PinballObjectListener> listeners;
	private Map<String, AudioTrack> sounds;

	protected BasePinballObject(TriMesh mesh) {
		this("PinballObject", mesh);
	}
	
	protected BasePinballObject(String name, TriMesh mesh) {
		super(name + (++instanceCount), mesh.getVertexBuffer(), mesh
				.getNormalBuffer(), mesh.getColorBuffer(), mesh
				.getTextureCoords(0), mesh.getIndexBuffer());
		setMeshAttributes(mesh);
		listeners = new HashSet<PinballObjectListener>();
		sounds = new HashMap<String, AudioTrack>();
	}

	private void setMeshAttributes(TriMesh mesh) {
		if (mesh.getRenderState(0) != null) {
			for (int i = 0; i < RenderState.RS_MAX_STATE; i++) {
				setRenderState(mesh.getRenderState(i));
			}
			setRenderQueueMode(mesh.getRenderQueueMode());
			updateRenderState();
		}
		setLocalRotation(mesh.getLocalRotation());
		setLocalTranslation(mesh.getLocalTranslation());
		setLocalScale(mesh.getLocalScale());
		setDefaultColor(mesh.getDefaultColor());
		setIsCollidable(mesh.isCollidable());
		setModelBound(new BoundingBox());
		updateModelBound();
	}

	public void collide(PinballBall ball, Vector3f normal) {
		for (PinballObjectListener listener : listeners) {
			listener.onCollision();
		}
		doCollision(ball, normal);
	}
	
	public void doCollision(PinballBall ball, Vector3f normal) {
		// override me
	}

	public void addListener(PinballObjectListener listener) {
		listeners.add(listener);
	}

	public void removeListener(PinballObjectListener listener) {
		listeners.remove(listener);
	}
	
	public void attachSound(String key, AudioTrack audio) {
		sounds.put(key, audio);
	}
	
	public void removeSound(String key) {
		sounds.remove(name);
	}
	
	public AudioTrack getSound(String key) {
		return sounds.get(key);
	}

	@Override
	public boolean hasCollision(Spatial scene, boolean checkTriangles) {
		return super.hasCollision(scene, checkTriangles);
	}

	public void update(float tpf) {
		// override me		
	}
	
	public Vector3f getNormalAt(List<Vector3f> normals, Vector3f supNormal, PinballBall ball){
		// Dummy impl
		return new Vector3f();
	}
}
