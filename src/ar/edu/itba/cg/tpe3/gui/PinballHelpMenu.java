package ar.edu.itba.cg.tpe3.gui;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.Text;

public class PinballHelpMenu extends PinballMenu {

	private static final long serialVersionUID = 1L;
	private PinballMenuManager manager;
	private PinballMenu mainMenu;
	
	public PinballHelpMenu(PinballMenuManager manager, PinballMenu mainMenu) {
		super(manager.getRenderer(), 300, 20, 1.5f, ColorRGBA.red,
				ColorRGBA.yellow, 1);
		
		this.manager = manager;
		this.mainMenu = mainMenu;
		
		addOption("escape", "Back", new PinballMenuAction() {
			public void onSelected() {
				PinballHelpMenu.this.manager.setMainMenu(PinballHelpMenu.this.mainMenu);
			}
		});

		setSelected(0);
		
		addLine("Left flipper: . (dot)", 1);
		addLine("Right flipper: , (comma)", 2);
		addLine("Plunger: space", 3);
		addLine("Tilt: tab", 4);
		addLine("Menu: escape", 5);
		addLine("Move camera: q,w,a,s,d,z", 6);	
		addLine("Change Camera: 8", 7);
		addLine("Toggle sound: o", 8);
		addLine("Quick restart: 0", 9);
	}
	
	
	private void addLine(String text, int line) {
		Text t = new Text(text, text);
		t.setCullHint(Spatial.CullHint.Never);
		t.setRenderState(Text.getDefaultFontTextureState());
		t.setRenderState(Text.getFontBlend());
		t.setLocalScale(1.2f);
		t.setTextColor(ColorRGBA.red);
		t.setLocalTranslation(30, manager.getRenderer().getHeight() - t.getHeight() - 40
				- line * 30, 0);
		this.attachChild(t);
		
	}
}
