package hoagienator;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainClass extends Applet implements Runnable, KeyListener {
	enum GameState {
		Running, Dead
	}

	GameState state = GameState.Running;
	
	private Image image,projectile_fork, background;
	private Graphics second;
	private URL base;
	
	
	private static Background bg1, bg2;
	public static Image tilegrassTop, tilegrassBot, tilegrassLeft,
	tilegrassRight, tiledirt;
	private ArrayList<Tile> tilearray = new ArrayList<Tile>();
	
	

	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		setSize(800, 480);
		setBackground(Color.BLACK);
		setFocusable(true);
		Frame frame = (Frame) this.getParent().getParent();
		frame.setTitle("The Hogienator");
		addKeyListener(this);

		//Define base for location of backgrounds,sprites
		try {
			base = getDocumentBase();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//Image Setups
		projectile_fork = getImage(base, "");
		
		background = getImage(base, "data/background.png");

		tiledirt = getImage(base, "data/tiledirt.png");
		tilegrassTop = getImage(base, "data/tilegrasstop.png");
		tilegrassBot = getImage(base, "data/tilegrassbot.png");
		tilegrassLeft = getImage(base, "data/tilegrassleft.png");
		tilegrassRight = getImage(base, "data/tilegrassright.png");

	}

	@Override
	public void start() {
		bg1 = new Background(0, 0);
		bg2 = new Background(1280, 0);	
		try {
			loadMap("data/map1.txt");
			System.out.println("Here");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		super.start();
		// Game Loop is on this Thread
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	/**
	 * Main Game loop that updates all objects on every iteration. ie. bullet
	 * position, character position
	 */
	@Override
	public void run() {
		while (true) {
			// Removes offscreen projectiles from memory.
			// REPLACE 'SHOOTER' WITH REAL CLASS NAME.
			// ArrayList<Projectile> projectiles = SHOOTER.getProjectiles();
			ArrayList<Projectile> projectiles = getProjectiles();
			for (int i = 0; i < projectiles.size(); i++) {
				Projectile p = (Projectile) projectiles.get(i);
				if (p.isVisible() == true) {
					p.update();
				} else {
					projectiles.remove(i);
				}
			}
			updateTiles();
			bg1.update();
			bg2.update();
			repaint();
			// Sleep for 17 milliseconds = 60 updates per second
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Double Buffering Technique to prevent image tearing and flickering.
	 */
	public void update(Graphics g) {		
		if (image == null) {
			image = createImage(this.getWidth(), this.getHeight());
			second = image.getGraphics();
		}		
		
		second.setColor(getBackground());
		second.fillRect(0, 0, getWidth(), getHeight());
		second.setColor(getForeground());
		paint(second);
		
		g.drawImage(image, 0, 0, this);
	}

	/**
	 * Draws the graphics to the screen.
	 */
	public void paint(Graphics g) {
		if (state == GameState.Running) {

			g.drawImage(background, bg1.getBgX(), bg1.getBgY(), this);
			g.drawImage(background, bg2.getBgX(), bg2.getBgY(), this);
			paintTiles(g);
			// REPLACE 'SHOOTER' WITH REAL CLASS NAME.
			// ArrayList projectiles = SHOOTER.getProjectiles();
			ArrayList projectiles = getProjectiles();
			for (int i = 0; i < projectiles.size(); i++) {
				Projectile p = (Projectile) projectiles.get(i);
				g.setColor(Color.YELLOW);
				g.fillRect(p.getX(), p.getY(), 10, 5);
			}
		}
		else if (state == GameState.Dead) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 800, 480);
			g.setColor(Color.WHITE);
			g.drawString("Dead", 360, 240);


		}
	}
	
	/**
	 * Carries out responses to key presses.
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getKeyCode()) {
		case KeyEvent.VK_UP:
			System.out.println("Move Up");
			break;

		case KeyEvent.VK_DOWN:
			System.out.println("Move Down");
			break;

		case KeyEvent.VK_LEFT:
			System.out.println("Move Left");
			break;

		case KeyEvent.VK_RIGHT:
			System.out.println("Move Right");
			break;

		case KeyEvent.VK_SPACE:
			System.out.println("Jump");
			break;

		case KeyEvent.VK_CONTROL:
			System.out.println("Shoot");
			shoot();
			break;
		}
	}

	/**
	 * Carries out responses to Key Releases.
	 */
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getKeyCode()) {
		case KeyEvent.VK_UP:
			System.out.println("Stop Moving Up");
			break;

		case KeyEvent.VK_DOWN:
			System.out.println("Stop Moving Down");
			break;

		case KeyEvent.VK_LEFT:
			System.out.println("Stop Moving Left");
			break;

		case KeyEvent.VK_RIGHT:
			System.out.println("Stop Moving Right");
			break;

		case KeyEvent.VK_SPACE:
			System.out.println("Stop Jump");
			break;
		}
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	// PLACE THESE INSIDE MAIN CHARACTER CLASS
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	/**
	 * Adds projectiles to the given location.
	 */
	public void shoot() {
		Projectile p = new Projectile(150, 357);
		projectiles.add(p);
	}
	/**
	 * 
	 * @return ArrayList of Projectile objects.
	 */
	public ArrayList getProjectiles() {
		return projectiles;
	}
	private void loadMap(String filename) throws IOException {
		ArrayList lines = new ArrayList();
		int width = 0;
		int height = 0;

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		while (true) {
			String line = reader.readLine();
			// no more lines to read
			if (line == null) {
				reader.close();
				break;
			}

			if (!line.startsWith("!")) {
				lines.add(line);
				width = Math.max(width, line.length());

			}
		}
		height = lines.size();

		for (int j = 0; j < 12; j++) {
			String line = (String) lines.get(j);
			for (int i = 0; i < width; i++) {

				if (i < line.length()) {
					char ch = line.charAt(i);
					Tile t = new Tile(i, j, Character.getNumericValue(ch));
					tilearray.add(t);
				}

			}
		}

	}
	private void updateTiles() {

		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			t.update();
		}

	}

	private void paintTiles(Graphics g) {
		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			g.drawImage(t.getTileImage(), t.getTileX(), t.getTileY(), this);
		}
	}
	public static Background getBg1() {
		return bg1;
	}

	public static Background getBg2() {
		return bg2;
	}
}
