package ar.edu.itba.cg.tpe3.gui;

import java.io.File;
import java.net.MalformedURLException;

import ar.edu.itba.cg.tpe3.BasePinballGame;
import ar.edu.itba.cg.tpe3.PinballLoader;

import com.jme.renderer.Renderer;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;

public class PinballMenuManager {

	private PinballMenu mainMenu;
	private PinballMenu gameMenu;
	private Renderer renderer;
	private BasePinballGame game;

	private AudioTrack introMusic;
	private AudioTrack pinballMusic;

	private boolean gameStarted = false;

	@SuppressWarnings("deprecation")
	public PinballMenuManager(Renderer r, BasePinballGame g) {
//		this.introMusic = createAudioTrack("intro.wav");
//		this.pinballMusic = createAudioTrack("pinball.wav");
		this.renderer = r;
		this.game = g;
		setGameFinished();
	}

	public PinballMenu getCurrentMenu() {
		if (gameStarted) {
			return gameMenu;
		} else {
			return mainMenu;
		}
	}

	public void setMainMenu(PinballMenu mainMenu) {
		this.mainMenu = mainMenu;
	}

	public void setGameMenu(PinballMenu gameMenu) {
		this.gameMenu = gameMenu;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public BasePinballGame getGame() {
		return game;
	}

	public void setGameStarted() {
		gameStarted = true;
//		introMusic.stop();
//		pinballMusic.play();
	}

	public void setGameFinished() {
		gameStarted = false;
//		pinballMusic.stop();
//		introMusic.play();
	}

	public PinballMenu getGameMenu() {
		return gameMenu;
	}

	private AudioTrack createAudioTrack(String trackName) {
		AudioTrack track = null;
		try {
			track = AudioSystem.getSystem().createAudioTrack(
					new File(PinballLoader.basePath + "/" + trackName).toURL(),
					true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		track.setType(TrackType.MUSIC);
		track.setMaxAudibleDistance(20000f);
		track.setVolume(0.5f);
		track.setLooping(true);
		return track;
	}
}
