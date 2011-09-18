package ar.edu.itba.cg.tpe3.gui;

import java.io.File;

import ar.edu.itba.cg.tpe3.PinballLoader;

import com.jme.image.Texture;
import com.jme.image.Texture.ApplyMode;
import com.jme.image.Texture.WrapMode;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class PinballMainMenu extends PinballMenu {

	private static final long serialVersionUID = 1L;

	private PinballMenuManager manager;
	private Quad logo;
	private Text loading;
	private boolean showLoading = false;

	@SuppressWarnings("deprecation")
	public PinballMainMenu(PinballMenuManager manager) {
		super(manager.getRenderer(), 270, 20, 1.5f, ColorRGBA.red,
				ColorRGBA.yellow, 1);

		this.manager = manager;

		/* Crear nodo para el logo del juego. */
		this.logo = new Quad("logo", 512, 256);
		this.logo.setLocalTranslation(manager.getRenderer().getWidth() / 2,
				manager.getRenderer().getHeight() - 200, 0.1f);
		this.logo.setCullHint(Spatial.CullHint.Never);
		this.logo.setDefaultColor(ColorRGBA.green.clone());
		try {
			String logoFile = PinballLoader.basePath + "/logo.jpg";

			Texture t = TextureManager.loadTexture(new File(logoFile).toURL());
			TextureState ts = manager.getRenderer().createTextureState();
			t.setApply(ApplyMode.Replace);
			t.setWrap(WrapMode.Repeat);
			ts.setTexture(t);
			ts.setEnabled(true);
			this.logo.setRenderState(ts);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.attachChild(logo);

		/* Crear nodo para el cartel de Loading... */
		this.loading = new Text("loading", "Loading...");
		this.loading.setCullHint(Spatial.CullHint.Never);
		this.loading.setRenderState(Text.getDefaultFontTextureState());
		this.loading.setRenderState(Text.getFontBlend());
		this.loading.setLocalScale(0.9f);
		this.loading.setTextColor(ColorRGBA.green);
		this.loading.getTextColor().a = 0;
		this.loading.setLocalTranslation(manager.getRenderer().getWidth()
				/ 2.0f - this.loading.getWidth() / 2.0f, this.loading
				.getHeight(), 0);
		BlendState bs = manager.getRenderer().createBlendState();
		bs.setBlendEnabled(true);
		bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		bs
				.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		bs.setTestEnabled(true);
		bs.setTestFunction(BlendState.TestFunction.GreaterThan);
		bs.setEnabled(true);
		this.loading.setRenderState(bs);
		this.attachChild(this.loading);

		addOption("return", "Start game", new PinballMenuAction() {
			public void onSelected() {
				PinballMainMenu.this.showLoading = true;
				new Thread() {
					public void run() {
						PinballMainMenu.this.manager.getGame().startGame();
						PinballMainMenu.this.manager.setGameStarted();
						PinballMainMenu.this.manager.getGame().hideMenu();
						PinballMainMenu.this.showLoading = false;
					}
				}.start();
			}
		});
		addOption("help", "Help", new PinballMenuAction() {
			public void onSelected() {
				PinballMenu helpMenu = new PinballHelpMenu(PinballMainMenu.this.manager, 
						PinballMainMenu.this);
				PinballMainMenu.this.manager.setMainMenu(helpMenu);
				helpMenu.updateGeometricState(0, true);
				helpMenu.updateRenderState();
			}
		});
		addOption("escape", "Exit", new PinballMenuAction() {
			public void onSelected() {
				PinballMainMenu.this.manager.getGame().finish();
				System.exit(0);
			}
		});
		setSelected(0);
		updateGeometricState(0.0f, true);
		updateRenderState();
	}

	@Override
	public void update() {
		if (showLoading) {
			this.loading.getTextColor().a = 1;
		} else {
			this.loading.getTextColor().a = 0;
		}
		this.loading.updateRenderState();
	}

	@Override
	public boolean isBlocked() {
		return showLoading;
	}
}
