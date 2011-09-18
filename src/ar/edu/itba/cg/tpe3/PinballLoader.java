package ar.edu.itba.cg.tpe3;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.web3d.j3d.loaders.X3DLoader;
import org.web3d.vrml.lang.TypeConstants;
import org.web3d.vrml.lang.VRMLFieldDeclaration;
import org.web3d.vrml.lang.VRMLNode;
import org.web3d.vrml.nodes.VRMLFieldData;
import org.web3d.vrml.nodes.VRMLNodeType;
import org.web3d.vrml.renderer.j3d.nodes.J3DLightNode;
import org.web3d.vrml.renderer.j3d.nodes.J3DVRMLNode;
import org.web3d.vrml.renderer.j3d.nodes.core.J3DMetadataDouble;
import org.web3d.vrml.renderer.j3d.nodes.core.J3DMetadataFloat;
import org.web3d.vrml.renderer.j3d.nodes.core.J3DMetadataSet;
import org.web3d.vrml.renderer.j3d.nodes.core.J3DMetadataString;
import org.web3d.vrml.renderer.j3d.nodes.core.J3DWorldRoot;
import org.web3d.vrml.renderer.j3d.nodes.geom3d.J3DBox;
import org.web3d.vrml.renderer.j3d.nodes.geom3d.J3DCylinder;
import org.web3d.vrml.renderer.j3d.nodes.geom3d.J3DIndexedFaceSet;
import org.web3d.vrml.renderer.j3d.nodes.geom3d.J3DSphere;
import org.web3d.vrml.renderer.j3d.nodes.group.J3DTransform;
import org.web3d.vrml.renderer.j3d.nodes.navigation.J3DViewpoint;
import org.web3d.vrml.renderer.j3d.nodes.render.J3DCoordinate;
import org.web3d.vrml.renderer.j3d.nodes.render.J3DIndexedTriangleFanSet;
import org.web3d.vrml.renderer.j3d.nodes.render.J3DIndexedTriangleSet;
import org.web3d.vrml.renderer.j3d.nodes.render.J3DIndexedTriangleStripSet;
import org.web3d.vrml.renderer.j3d.nodes.render.J3DTriangleSet;
import org.web3d.vrml.renderer.j3d.nodes.shape.J3DAppearance;
import org.web3d.vrml.renderer.j3d.nodes.shape.J3DMaterial;
import org.web3d.vrml.renderer.j3d.nodes.shape.J3DShape;
import org.web3d.vrml.renderer.j3d.nodes.texture.J3DImageTexture;
import org.web3d.vrml.renderer.j3d.nodes.texture.J3DTextureCoordinate;

import ar.edu.itba.cg.tpe3.gui.PinballMenuManager;
import ar.edu.itba.cg.tpe3.objects.BasePinballObject;
import ar.edu.itba.cg.tpe3.objects.PinballBall;
import ar.edu.itba.cg.tpe3.objects.PinballBumper;
import ar.edu.itba.cg.tpe3.objects.PinballSpinner;
import ar.edu.itba.cg.tpe3.objects.PinballFlipper;
import ar.edu.itba.cg.tpe3.objects.PinballFloorLight;
import ar.edu.itba.cg.tpe3.objects.PinballMagnet;
import ar.edu.itba.cg.tpe3.objects.PinballObject;
import ar.edu.itba.cg.tpe3.objects.PinballObjectListener;
import ar.edu.itba.cg.tpe3.objects.PinballOneWayPath;
import ar.edu.itba.cg.tpe3.objects.PinballPlunger;
import ar.edu.itba.cg.tpe3.objects.PinballRamp;
import ar.edu.itba.cg.tpe3.objects.PinballStatefulWall;
import ar.edu.itba.cg.tpe3.objects.PinballTable;
import ar.edu.itba.cg.tpe3.objects.PinballWall;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.image.Texture.ApplyMode;
import com.jme.image.Texture.WrapMode;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.Quaternion;
import com.jme.math.Triangle;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.MaterialState.ColorMaterial;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;

/**
 * Clase que dado el nombre de un archivo x3d, lo parsea y carga los objetos
 * correspondientes en el scengraph del <code>PinballGame</code>.
 * 
 * Extiende de <code>Thread</code> porque hay conflictos entre los threads de
 * OpenGL y el loader del X3D en Windows. Para utilizarla, invocar al
 * constructor con la referencia al juego y el nombre del archivo, y luego
 * lanzar el thread con el método <code>start</code>.
 * 
 * Si se desea esperar a que termine, invocar también al método
 * <code>waitUtilLoaded</code>. Si hubo errores, se guardan en el campo
 * errors, que luego puede ser consultado a través del método
 * <code>getErrors</code>.
 */
public class PinballLoader extends Thread {

	public static String basePath;

	private PinballGame game;
	private X3DLoader loader;
	private String errors;
	private String fileName;
	public boolean finished = false;

	public PinballLoader(PinballGame game, String filename) {
		this.fileName = filename;
		File f = new File(filename);
		String path = f.getParentFile().getAbsolutePath();
		this.game = game;
		this.loader = new X3DLoader(X3DLoader.LOAD_ALL);
		this.loader.setBasePath(path);
		PinballLoader.basePath = path;
	}

	@Override
	public void run() {

		/* Parsear el X3D */
		X3DLoader loader = new X3DLoader(X3DLoader.LOAD_ALL);
		try {
			loader.load(fileName);
		} catch (FileNotFoundException e) {
			errors = "File not found: " + fileName;
			return;
		} catch (Exception e) {
			errors = "Error parsing file: " + fileName;
			return;
		}

		/* Recorrer el árbol del X3D */
		VRMLNode node = loader.getVRMLScene().getRootNode();
		parseScene(node);

		finished = true;
	}

	public synchronized boolean hasFinished() {
		return finished;
	}

	public synchronized void waitUntilLoaded() {
		try {
			this.wait();
		} catch (Exception e) {
			errors = e.getMessage();
			throw new RuntimeException(e);
		}
	}

	public String getErrors() {
		return errors;
	}

	private void parseScene(VRMLNode node) {
		Node root = game.getRootNode();
		switch (node.getPrimaryType()) {
		case TypeConstants.ViewpointNodeType:
			parseViewpointNode((J3DViewpoint) node);
			break;
		case TypeConstants.LightNodeType:
			root.attachChild(parseLightNode((J3DLightNode) node));
			break;
		case TypeConstants.GroupingNodeType:
			if (node instanceof J3DTransform) {
				root.attachChild(parseTransformNode((J3DTransform) node));
			}
			break;
		case TypeConstants.WorldRootNodeType:
			for (VRMLNode v : ((J3DWorldRoot) node).getChildren()) {
				parseScene(v);
			}
		}
	}

	private void parseViewpointNode(J3DViewpoint viewpoint) {
		VRMLFieldData data;
		Vector3f position = new Vector3f(0, 0, 0);
		Quaternion rotation = new Quaternion(1, 0, 0, 0);
		Vector3f direction = new Vector3f(0, 0, -1);
		Vector3f left = new Vector3f(-1, 0, 0);
		Vector3f up = new Vector3f(0, 1, 0);

		if ((data = getField(viewpoint, "position")) != null) {
			position.set(data.floatArrayValue[0], data.floatArrayValue[1],
					data.floatArrayValue[2]);
		}
		if ((data = getField(viewpoint, "orientation")) != null) {
			rotation.fromAngleAxis(data.floatArrayValue[3], new Vector3f(
					data.floatArrayValue[0], data.floatArrayValue[1],
					data.floatArrayValue[2]));
		}

		direction = rotation.mult(direction);
		left = rotation.mult(left);
		up = direction.cross(left);

		Camera cam = game.getCamera();
		cam.setFrame(position, left, up, direction);
		float fovY = (viewpoint.getFieldOfView() * 180f) / (float) Math.PI;
		float aspect = game.getDisplay().getWidth()
				/ (float) game.getDisplay().getHeight();
		cam.setFrustumPerspective(fovY, aspect, 0.1f, 1000);
		cam.update();
	}

	private Spatial parseTransformNode(J3DTransform transformNode) {
		Node ret = new Node();

		float[] rot = transformNode.getRotation();
		ret.getLocalRotation().fromAngleAxis(rot[3],
				new Vector3f(rot[0], rot[1], rot[2]));

		float[] tras = transformNode.getTranslation();
		ret.setLocalTranslation(tras[0], tras[1], tras[2]);

		float[] scale = transformNode.getScale();
		ret.setLocalScale(new Vector3f(scale[0], scale[1], scale[2]));

		for (VRMLNode node : transformNode.getChildren()) {
			Spatial s;
			if (node instanceof J3DShape) {
				s = parseShapeNode((J3DShape) node);
				ret.attachChild(s);
			} else if (node instanceof J3DViewpoint) {
				parseViewpointNode((J3DViewpoint) node);
			} else if (node instanceof J3DTransform) {
				s = parseTransformNode((J3DTransform) node);
				ret.attachChild(s);
			}
		}

		return ret;
	}

	private Spatial parseShapeNode(J3DShape shapeNode) {
		TriMesh shape = null;
		VRMLNode geometry = shapeNode.getGeometry();

		if (geometry instanceof J3DSphere) {
			shape = parseSphere((J3DSphere) geometry);
		} else if (geometry instanceof J3DTriangleSet) {
			shape = parseTriangleSet((J3DTriangleSet) geometry);
		} else if (geometry instanceof J3DIndexedTriangleSet) {
			shape = parseTriangleSet((J3DIndexedTriangleSet) geometry);
		} else if (geometry instanceof J3DIndexedFaceSet) {
			shape = parseFaceSet((J3DIndexedFaceSet) geometry);
		} else if (geometry instanceof J3DIndexedTriangleFanSet) {
			shape = parseTriangleSet((J3DIndexedTriangleFanSet) geometry);
		} else if (geometry instanceof J3DIndexedTriangleStripSet) {
			shape = parseTriangleSet((J3DIndexedTriangleStripSet) geometry);
		} else if (geometry instanceof J3DBox) {
			shape = parseBox((J3DBox) geometry);
		} else if (geometry instanceof J3DCylinder) {
			shape = parseCylinder((J3DCylinder) geometry);
		}

		// Figura no reconocida
		if (shape == null) {
			System.out.println("Unknown shape: " + geometry.getClass());
			return null;
		}

		J3DAppearance appearance = (J3DAppearance) shapeNode.getAppearance();
		if (appearance != null) {
			J3DMaterial material = (J3DMaterial) appearance.getMaterial();
			if (material != null) {
				parseMaterial(material, shape);
			}
			J3DImageTexture texture = (J3DImageTexture) appearance.getTexture();
			if (texture != null) {
				TextureState ts = parseTexture(texture);
				shape.setRenderState(ts);
				shape.updateRenderState();
				shape.updateModelBound();
			}
		}

		shape = parsePinballObject(shape, (J3DVRMLNode) geometry);
		// listener.onObjectLoad(shape);
		return shape;
	}

	private TriMesh parseSphere(J3DSphere sphereNode) {
		VRMLFieldData data = getField(sphereNode, "radius");
		float radius = data != null ? data.floatValue : 1;
		Sphere sphere = new Sphere("sphere", new Vector3f(), 15, 15, radius);
		sphere.setModelBound(new BoundingSphere());
		sphere.updateModelBound();
		return sphere;
	}

	private TriMesh parseTriangleSet(J3DTriangleSet triangleSetNode) {
		Vector3f vertexes[] = null;
		Vector3f normals[] = null;
		int[] indexes = null;
		Vector2f texCoords[] = null;

		for (VRMLNodeType node : triangleSetNode.getComponents()) {
			if (node instanceof J3DCoordinate) {
				vertexes = getVertexArray(((J3DCoordinate) node));
				indexes = new int[vertexes.length];
				normals = new Vector3f[vertexes.length];
				for (int i = 0; i < indexes.length; i++) {
					indexes[i] = i;
				}
				for (int i = 0; i < indexes.length; i += 3) {
					Triangle t = new Triangle(vertexes[i], vertexes[i + 1],
							vertexes[i + 2]);
					Vector3f n = t.getNormal();
					normals[i] = n;
					normals[i + 1] = n;
					normals[i + 2] = n;
				}
			}
			if (node instanceof J3DTextureCoordinate) {
				J3DTextureCoordinate texCoord = (J3DTextureCoordinate) node;
				float[] points = getField(texCoord, "point").floatArrayValue;
				texCoords = new Vector2f[points.length / 2];

				int k = 0;
				for (int i = 0; i < points.length; i += 2) {
					texCoords[k++] = new Vector2f(points[i], points[i + 1]);
				}
			}
		}
		return new TriMesh("TriangleSet", BufferUtils
				.createFloatBuffer(vertexes), BufferUtils
				.createFloatBuffer(normals), null,
				TexCoords.makeNew(texCoords), BufferUtils
						.createIntBuffer(indexes));
	}

	private Vector3f[] getVertexArray(J3DCoordinate coords) {
		float[] points = coords.getPointRef();
		Vector3f[] vertexes = new Vector3f[points.length / 3];
		for (int i = 0; i < points.length / 3; i++) {
			vertexes[i] = new Vector3f(points[i * 3], points[i * 3 + 1],
					points[i * 3 + 2]);
		}
		return vertexes;
	}

	private TriMesh parseTriangleSet(J3DIndexedTriangleSet setNode) {
		VRMLNode[] components = setNode.getComponents();
		Vector3f[] vertexes = getVertexArray((J3DCoordinate) components[0]);
		int[] indexes = setNode.getFieldValue(setNode.getFieldIndex("index")).intArrayValue;

		return new TriMesh("IndexedTriangleSet", BufferUtils
				.createFloatBuffer(vertexes), null, null, null, BufferUtils
				.createIntBuffer(indexes));
	}

	private TriMesh parseFaceSet(J3DIndexedFaceSet setNode) {
		VRMLNode[] components = setNode.getComponents();
		Vector3f[] vertexes = getVertexArray((J3DCoordinate) components[0]);
		int k, first, tri;

		int[] faceIndexes = setNode.getFieldValue(setNode
				.getFieldIndex("coordIndex")).intArrayValue;
		int[] indexes = new int[faceIndexes.length * 3 - 2];

		k = 0;
		first = 0;
		tri = 0;
		for (int i = 0; i < faceIndexes.length; i++) {
			if (faceIndexes[i] == -1) {
				first = i + 1;
				tri = 0;
			}
			if (tri < 3) {
				indexes[k++] = faceIndexes[i];
				tri++;
			} else {
				indexes[k++] = faceIndexes[i];
				indexes[k++] = faceIndexes[first];
				indexes[k++] = faceIndexes[first + 1];
			}
		}

		return new TriMesh("IndexedFaceSet", BufferUtils
				.createFloatBuffer(vertexes), null, null, null, BufferUtils
				.createIntBuffer(indexes));
	}

	private TriMesh parseTriangleSet(J3DIndexedTriangleFanSet setNode) {
		VRMLNode[] components = setNode.getComponents();
		Vector3f[] vertexes = getVertexArray((J3DCoordinate) components[0]);
		int k;

		int[] fanIndexes = setNode
				.getFieldValue(setNode.getFieldIndex("index")).intArrayValue;
		int[] indexes = new int[fanIndexes.length * 3 - 2];

		indexes[0] = fanIndexes[0];
		indexes[1] = fanIndexes[1];

		k = 2;
		for (int i = 2; i < fanIndexes.length; i++) {
			indexes[k++] = fanIndexes[i];
			indexes[k++] = fanIndexes[i - 1];
			indexes[k++] = 0;
		}

		return new TriMesh("IndexedTriangleFanSet", BufferUtils
				.createFloatBuffer(vertexes), null, null, null, BufferUtils
				.createIntBuffer(indexes));
	}

	private TriMesh parseTriangleSet(J3DIndexedTriangleStripSet setNode) {
		VRMLNode[] components = setNode.getComponents();
		Vector3f[] vertexes = getVertexArray((J3DCoordinate) components[0]);
		int k;

		// Puntos
		int[] stripIndexes = setNode.getFieldValue(setNode
				.getFieldIndex("index")).intArrayValue;
		int[] indexes = new int[stripIndexes.length * 3 - 1];

		indexes[0] = stripIndexes[0];
		indexes[1] = stripIndexes[1];

		k = 2;
		for (int i = 2; i < stripIndexes.length; i++) {
			indexes[k++] = stripIndexes[i];
			indexes[k++] = stripIndexes[i - 1];
			indexes[k++] = stripIndexes[i - 2];
		}

		return new TriMesh("IndexedTriangleStripSet", BufferUtils
				.createFloatBuffer(vertexes), null, null, null, BufferUtils
				.createIntBuffer(indexes));
	}

	private TriMesh parseBox(J3DBox boxNode) {
		Box box = new Box("box");
		VRMLFieldData data = getField(boxNode, "size");
		if (data != null) {
			box.setData(new Vector3f(), data.floatArrayValue[0] / 2,
					data.floatArrayValue[1] / 2, data.floatArrayValue[2] / 2);
		}
		box.setModelBound(new BoundingBox());
		box.updateModelBound();

		return box;
	}

	private TriMesh parseCylinder(J3DCylinder cylinderNode) {
		float radius = 5, height = 10;
		VRMLFieldData data = getField(cylinderNode, "radius");
		if (data != null) {
			radius = data.floatValue;
		}
		data = getField(cylinderNode, "height");
		if (data != null) {
			height = data.floatValue;
		}
		TriMesh cylinder = new Cylinder("cylinder", 10, 10, radius, height,
				true);

		// Por default aparece "acostado" alrededor del eje Z
		cylinder.getLocalRotation().fromAngleAxis((float) (Math.PI / 2.0),
				Vector3f.UNIT_X);
		cylinder.setModelBound(new BoundingBox());
		cylinder.updateModelBound();

		return cylinder;
	}

	private void parseMaterial(J3DMaterial material, Spatial shape) {
		Geometry geo = null;
		ColorRGBA color = new ColorRGBA(material.getDiffuseColor()[0], material
				.getDiffuseColor()[1], material.getDiffuseColor()[2],
				1.0f - material.getTransparency());

		if (shape.getClass().equals(TriMesh.class)) {
			TriMesh triMesh = (TriMesh) shape;
			ColorRGBA[] colors = new ColorRGBA[triMesh.getVertexCount()];
			for (int i = 0; i < colors.length; i++) {
				colors[i] = color;
			}
			triMesh.setColorBuffer(BufferUtils.createFloatBuffer(colors));
			triMesh.updateRenderState();
			geo = (TriMesh) shape;
		} else if (shape instanceof Geometry) {
			geo = (Geometry) shape;
		} else if (shape instanceof PinballFlipper) {
			geo = (TriMesh) shape;
		}

		if (!shape.getClass().equals(TriMesh.class)) {
			geo.setDefaultColor(color);
		}

		if (material.getTransparency() != 0) {
			MaterialState materialState = game.getDisplay().getRenderer()
					.createMaterialState();
			materialState.setDiffuse(color);
			materialState.setEnabled(true);
			materialState
					.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
			geo.setRenderState(materialState);

			final BlendState alphaState = game.getDisplay().getRenderer()
					.createBlendState();
			alphaState.setBlendEnabled(true);
			geo.setRenderState(alphaState);
			geo.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		}

	}

	private TextureState parseTexture(J3DImageTexture texture) {
		try {
			Texture t = TextureManager
					.loadTexture(new URL(texture.getUrl()[0]));
			TextureState ts = game.getDisplay().getRenderer()
					.createTextureState();
			t.setApply(ApplyMode.Replace);
			t.setWrap(WrapMode.Repeat);
			ts.setTexture(t);
			ts.setEnabled(true);
			return ts;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Spatial parseLightNode(J3DLightNode node) {
		PointLight light = new PointLight();
		VRMLFieldData data = getField(node, "location");
		light.setDiffuse(new ColorRGBA(node.getColor()[0], node.getColor()[1],
				node.getColor()[2], 1));
		if (data != null) {
			light.setLocation(new Vector3f(data.floatArrayValue[0],
					data.floatArrayValue[1], data.floatArrayValue[2]));
		}
		LightNode lightNode = new LightNode("light");
		lightNode.setLight(light);
		return lightNode;
	}

	private VRMLFieldData getField(J3DVRMLNode node, String field) {
		for (int i = 0; i < node.getNumFields(); i++) {
			VRMLFieldDeclaration declaration = node.getFieldDeclaration(i);
			if (declaration != null && declaration.getName().equals(field)) {
				return node.getFieldValue(i);
			}
		}
		return null;
	}

	private Map<String, Object> getMetadata(J3DVRMLNode node) {
		J3DMetadataSet metadata = (J3DMetadataSet) node.getMetadataObject();
		if (metadata == null) {
			return null;
		}

		VRMLFieldData data = metadata.getFieldValue(metadata
				.getFieldIndex("value"));
		VRMLNode[] metadataNodes = data.nodeArrayValue;
		HashMap<String, Object> metadataHash = new HashMap<String, Object>();

		for (int i = 0; i < metadataNodes.length; i++) {
			if (metadataNodes[i] instanceof J3DMetadataString) {
				J3DMetadataString f = (J3DMetadataString) metadataNodes[i];
				VRMLFieldData d = f.getFieldValue(f.getFieldIndex("value"));
				metadataHash.put(f.getName(), d.stringArrayValue[0]);
			} else if (metadataNodes[i] instanceof J3DMetadataFloat) {
				J3DMetadataFloat f = (J3DMetadataFloat) metadataNodes[i];
				VRMLFieldData d = f.getFieldValue(f.getFieldIndex("value"));
				if (d.floatArrayValue != null) {
					if (d.floatArrayValue.length > 1) {
						metadataHash.put(f.getName(), d.floatArrayValue);
					} else {
						metadataHash.put(f.getName(), new Float(
								d.floatArrayValue[0]));
					}
				} else {
					metadataHash.put(f.getName(), new Float(d.floatValue));
				}
			} else if (metadataNodes[i] instanceof J3DMetadataDouble) {
				J3DMetadataDouble f = (J3DMetadataDouble) metadataNodes[i];
				VRMLFieldData d = f.getFieldValue(f.getFieldIndex("value"));
				metadataHash.put(f.getName(), d.doubleValue);
			} else {
				throw new RuntimeException("TODO");
			}
		}
		return metadataHash;
	}

	@SuppressWarnings("deprecation")
	private TriMesh parsePinballObject(TriMesh mesh, J3DVRMLNode node) {
		Map<String, Object> metadata = getMetadata(node);
		if (metadata == null || !metadata.containsKey("type")) {
			mesh.setIsCollidable(false);
			return mesh;
		}

		if (metadata.get("type").equals("carousell")) {
			mesh = new PinballSpinner(mesh, (Float) metadata.get("speed"));
			game.addPinballObject((PinballObject) mesh);
		} else if (metadata.get("type").equals("floorlight")) {
			float[] initialColor = (float[]) metadata.get("initialColor");
			if (initialColor == null) {
				initialColor = new float[] { 0, 0, 0 };
			}
			float[] finalColor = (float[]) metadata.get("finalColor");
			if (finalColor == null) {
				finalColor = new float[] { 1, 1, 1 };
			}

			Float time = (Float) metadata.get("period");
			Float phase = (Float) metadata.get("phase");

			mesh = new PinballFloorLight(time, phase.intValue(), new ColorRGBA(
					initialColor[0], initialColor[1], initialColor[2], 1),
					new ColorRGBA(finalColor[0], finalColor[1], finalColor[2],
							1), mesh);
			game.addPinballObject((PinballObject) mesh);

		} else if (metadata.get("type").equals("bumper")) {
			mesh = new PinballBumper(mesh, (Float) metadata.get("intensity"));
			final Float score = ((Float) metadata.get("score"));
			final PinballBumper po = (PinballBumper) mesh;
			((PinballBumper) mesh).addListener(new PinballObjectListener() {
				public void onCollision() {
					if (score != null) {
						PinballLoader.this.game.addToScore(score.intValue());
					}
					MaterialState ms = (MaterialState) po
							.getRenderState(RenderState.RS_MATERIAL);
					if (ms == null) {
						ms = PinballLoader.this.game.getDisplay().getRenderer()
								.createMaterialState();
						ms.setColorMaterial(ColorMaterial.AmbientAndDiffuse);
						po.setRenderState(ms);
					}
					ms.setSpecular(ColorRGBA.white.clone());
					po.updateRenderState();
				}
			});
			game.addPinballObject((PinballObject) mesh);
		} else if (metadata.get("type").equals("magnet")) {
			mesh = new PinballMagnet(mesh, (Float) metadata.get("charge"));
			game.addPinballObject((PinballObject) mesh);
		} else if (metadata.get("type").equals("ball")) {
			mesh = new PinballBall((Sphere) mesh);
			game.setBall((PinballBall) mesh);
		} else if (metadata.get("type").equals("table")) {
			mesh = new PinballTable(mesh);
			game.addPinballObject((PinballObject) mesh);
			// game.setPinballTableRotation()
		} else if (metadata.get("type").equals("lflipper")) {
			float[] center = (float[]) metadata.get("center");
			Vector3f vec = new Vector3f(center[0], 0, center[1]);
			PinballFlipper flipper = new PinballFlipper(mesh,
					PinballFlipper.FlipperType.LEFT, vec);
			game.addLeftFlipper(flipper);
			mesh = flipper;
		} else if (metadata.get("type").equals("rflipper")) {
			float[] center = (float[]) metadata.get("center");
			Vector3f vec = new Vector3f(center[0], 0, center[1]);
			PinballFlipper flipper = new PinballFlipper(mesh,
					PinballFlipper.FlipperType.RIGHT, vec);
			game.addRightFlipper(flipper);
			mesh = flipper;
		} else if (metadata.get("type").equals("plunger")) {
			PinballPlunger plunger = new PinballPlunger(mesh);
			game.setPlunger(plunger);
			mesh = plunger;
		} else if (metadata.get("type").equals("wall")) {
			PinballWall wall = null;
			if (metadata.containsKey("oneWayPath")) {
				wall = new PinballOneWayPath(mesh);
				final Float score = ((Float) metadata.get("score"));
				((PinballOneWayPath) wall)
						.addListener(new PinballObjectListener() {
							public void onCollision() {
								if (score != null) {
									PinballLoader.this.game.addToScore(score
											.intValue());
								}
							}
						});
				game.addPinballObject(wall);
			} else if (metadata.containsKey("statefull")) {
				wall = new PinballStatefulWall(mesh, new ColorRGBA[] {
						ColorRGBA.white, new ColorRGBA(0.3f, 0.3f, 1f, 1f) });
				final Float score = ((Float) metadata.get("score"));
				((PinballStatefulWall) wall)
						.addListener(new PinballObjectListener() {
							public void onCollision() {
								if (score != null) {
									PinballLoader.this.game.addToScore(score
											.intValue());
								}
							}
						});
				game.addPinballObject(wall);
			} else {
				wall = new PinballWall(mesh);
				game.addPinballObject(wall);
				if (metadata.containsKey("gameover")) {
					wall.addListener(new PinballObjectListener() {
						public void onCollision() {
							if (game.getLivesLeft() == 0) {
								System.out.println("GAME OVER!!!");
								PinballMenuManager menuManager = game
										.getMenuManager();
								menuManager.setGameFinished();
								game.showMenu = true;
								game.reinit();
							} else {
								PinballLoader.this.game.substractLife();
								PinballLoader.this.game.replay();
							}
						}
					});
				}
			}
			mesh = wall;
		} else if (metadata.get("type").equals("ramp")) {
			PinballRamp ramp = new PinballRamp(mesh);
			game.addPinballObject(ramp);
			mesh = ramp;
		} else if (metadata.get("type").equals("score")) {
			game.setScore(mesh);
		}

		String path = loader.getBasePath();
		for (String key : metadata.keySet()) {
			if (key.startsWith("sound-")) {
				String s[] = key.split("-");
				URL url;
				try {
					url = new File(path + "/" + metadata.get(key)).toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
					continue;
				}
				AudioTrack track = AudioSystem.getSystem().createAudioTrack(
						url, false);
				track.setMaxAudibleDistance(2000);
				track.setVolume(1f);
				((BasePinballObject) mesh).attachSound(s[1], track);
			}
		}
		return mesh;
	}

}
