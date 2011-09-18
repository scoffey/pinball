package ar.edu.itba.cg.tpe3.gui;


import com.jme.renderer.ColorRGBA;

public class PinballGameMenu extends PinballMenu {

	private static final long serialVersionUID = 1L;

	private PinballMenuManager manager;
	
	public PinballGameMenu(PinballMenuManager manager) {
		super(manager.getRenderer(), 30, 20, 1.5f, ColorRGBA.red, ColorRGBA.yellow, 0.5f);
		
		this.manager = manager;
		
		addOption("escape", "Return to game", new PinballMenuAction() {
			public void onSelected() {
				PinballGameMenu.this.manager.getGame().hideMenu();
			}
		});

		addOption("return", "Exit to main menu", new PinballMenuAction() {
			public void onSelected() {
				PinballGameMenu.this.manager.setGameFinished();	
			}
		});
		setSelected(0);
		updateGeometricState(0.0f, true);
		updateRenderState();
	}
	
	
}
