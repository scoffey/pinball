package ar.edu.itba.cg.tpe3.gui;

import java.util.ArrayList;
import java.util.List;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;

public class PinballMenu extends Node {

	private static final long serialVersionUID = 1L;

	private List<PinballMenuOption> options;
	private int selected = 0;
	private Renderer renderer;

	private Quad bg;
	
	private int topMargin;
	private int optionsPadding;
	private float fontSize;
	private ColorRGBA optionColor;
	private ColorRGBA selectedOptionColor;
	
	class PinballMenuOption extends Text {
		private static final long serialVersionUID = 1L;
		public String key;
		public String value;
		public PinballMenuAction action;
		
		public PinballMenuOption(String key, String value, PinballMenuAction action) {
			super(key, value);
			this.key = key;
			this.value = value;
			this.action = action;
		}
	}

	public PinballMenu(Renderer renderer, int topMargin, int optionsPadding,
			float fontSize, ColorRGBA optionColor, ColorRGBA selectedOptionColor,
			float backgroundAlpha) {
		super("menu");
		this.renderer = renderer;
		this.topMargin = topMargin;
		this.optionsPadding = optionsPadding;
		this.fontSize = fontSize;
		this.optionColor = optionColor;
		this.selectedOptionColor = selectedOptionColor;
		
		this.options = new ArrayList<PinballMenuOption>();

		this.setCullHint(Spatial.CullHint.Never);
		this.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		
		bg = new Quad("background", renderer.getWidth(), renderer.getHeight());
		bg.setLocalTranslation(renderer.getWidth()/2f, 
					renderer.getHeight()/2f, 0);
		bg.setCullHint(Spatial.CullHint.Never);
		bg.setDefaultColor(ColorRGBA.black.clone());
		bg.getDefaultColor().a = backgroundAlpha;
		
		BlendState bs = renderer.createBlendState();
		bs.setBlendEnabled(true);
		bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		bs.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		bs.setTestEnabled(true);
		bs.setTestFunction(BlendState.TestFunction.GreaterThan);
		bs.setEnabled(true);
		bg.setRenderState(bs);

		this.attachChild(bg);
	}

	public void addOption(String key, String text, PinballMenuAction action) {
		PinballMenuOption pmo = new PinballMenuOption(key, text, action);
		this.options.add(pmo);

		pmo.setCullHint(Spatial.CullHint.Never);
		pmo.setRenderState(Text.getDefaultFontTextureState());
		pmo.setRenderState(Text.getFontBlend());
		pmo.setLocalScale(fontSize);
		pmo.setTextColor(ColorRGBA.red);
		pmo.setLocalTranslation(renderer.getWidth() / 2.0f - pmo.getWidth()
				/ 2.0f, renderer.getHeight() - pmo.getHeight() - topMargin
				- this.options.size() * (pmo.getHeight() + optionsPadding), 0);
		this.attachChild(pmo);
	}
	
	public void setSelected(int option) {
		for (PinballMenuOption o : options) {
			o.setTextColor(optionColor);
			o.updateRenderState();
		}
		this.options.get(option).setTextColor(selectedOptionColor);
		this.options.get(option).updateRenderState();
		this.selected = option;
	}

	public void selectNext() {
		if (isBlocked())
			return;
		if (this.selected < this.options.size()-1) {
			this.selected++;
			setSelected(this.selected);
		}
	}
	
	public void selectPrevious() {
		if (isBlocked()) 
			return;
		if (this.selected > 0) {
			this.selected--;
			setSelected(this.selected);
		}
	}
	
	public int getSelected() {
		return selected;
	}
	
	public void fireSelectedAction() {
		this.options.get(this.selected).action.onSelected();
	}
	
	public void fireAction(String action) {
		for (PinballMenuOption o : options) {
			if (o.key.equals(action)) {
				o.action.onSelected();
			}
		}
	}
	
	public void update() {
		
	}

	public boolean isBlocked() {
		return false;
	}

}
