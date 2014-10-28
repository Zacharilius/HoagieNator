package hoagienator;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MainClass extends Applet implements Runnable, KeyListener {
	enum GameState {
		Running, Dead
	}

	// Array to hold all hero images.
	protected BufferedImage[] hero = new BufferedImage[16];

	// Array to hold all the guns.
	protected BufferedImage[] guns = new BufferedImage[1];

	// Array to hold ammunition types?

	// Array to hold all drops.
	protected BufferedImage[] drops = new BufferedImage[1];

	// Arrays to hold all current drops which should be currently shown on
	// screen.
	protected BufferedImage[] currentDropImages = new BufferedImage[9];
	protected int[] currentDropX = new int[9];
	protected int[] currentDropY = new int[9];
	protected int[] currentDropTimer = new int[9];

	// Array to hold keys pressed so key combinations can be made (Ex - Shift +
	// Move = Run)
	protected boolean[] keysPressed = new boolean[25];

	// Self-explanatory.
	// protected KeyListener alpha;
	protected Hero heroClass = new Hero();
	protected ItemDrops ItemClass = new ItemDrops();
	// protected Image image;
	// protected Graphics second;

	// Array with the different life hearts.
	protected BufferedImage[] lifeHearts = new BufferedImage[heroClass
			.maxLives()];

	GameState state = GameState.Running;

	private Image image, projectile_fork, background;
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

		// Define base for location of backgrounds,sprites
		try {
			base = getDocumentBase();
		} catch (Exception e) {
			// TODO: handle exception
		}

		// Image Setups
		projectile_fork = getImage(base, "data/bullet_fork.png");

		background = getImage(base, "data/background.png");

		tiledirt = getImage(base, "data/tiledirt.png");
		tilegrassTop = getImage(base, "data/tilegrasstop.png");
		tilegrassBot = getImage(base, "data/tilegrassbot.png");
		tilegrassLeft = getImage(base, "data/tilegrassleft.png");
		tilegrassRight = getImage(base, "data/tilegrassright.png");

		// Try to load Hero sprites, guns, and hearts.
		try {
			int i = 0;
			while (i < 16) {
				String j = "data/" + Integer.toString(i + 1) + ".png";
				hero[i] = ImageIO.read(new File(j));
				i++;
			}
			i = 0;
			while (i < 3) {
				lifeHearts[i] = ImageIO.read(new File("data/heart.png"));
				i++;
			}
			i = 0;
			while (i < 1) {
				guns[i] = ImageIO.read(new File("data/pistol.png"));
				i++;
			}

			// Load drop images.
			i = 0;
			while (i < 1) {
				drops[i] = ImageIO.read(new File("data/dropheart.png"));
				i++;
			}
		} catch (Exception imageNotFound) {
			System.out.println("Images could not be loaded. Closing game.");
			System.exit(0);
		}

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
			try{
			// Check to see if more hearts need to be loaded. The number of
			// hearts cannot decrease.
			if (heroClass.lastCheckedLife() != heroClass.totalLife()) {
				heroClass
						.updateLastCheckedLife(heroClass.lastCheckedLife() + 1);
				lifeHearts[heroClass.lastCheckedLife()] = ImageIO
						.read(new File("heart.png"));
				heroClass.updateCurrentLife(heroClass.currentLife() + 1);
			}

			// Check to see if player has lost a heart.
			if (heroClass.currentLife() < heroClass.livesPresent()) {
				int i = 0;
				// Fill in remaining hearts that the player has.
				while (i <= heroClass.currentLife()) {
					lifeHearts[i] = ImageIO.read(new File("data/heart.png"));
					i++;
				}
				// Fill in empty hearts.
				while (i <= heroClass.lastCheckedLife()) {
					lifeHearts[i] = ImageIO.read(new File("data/heartblack.png"));
					i++;
				}
				heroClass.updateLivesPresent(heroClass.currentLife());
			}
			if (heroClass.currentLife() > heroClass.livesPresent()) {
				int i = 0;
				// Fill in the hearts that the player has.
				while (i <= heroClass.currentLife()) {
					lifeHearts[i] = ImageIO.read(new File("data/heart.png"));
					i++;
				}
				// Fill in empty hearts.
				while (i <= heroClass.lastCheckedLife()) {
					lifeHearts[i] = ImageIO.read(new File("data/heartblack.png"));
					i++;
				}
				heroClass.updateLivesPresent(heroClass.currentLife());
			}

			// Update all drop timers.
			for (int i = 0; i < 8; i++) {
				if (currentDropImages[i] != null) {
					// Update the timers for each drop currently present.
					currentDropTimer[i] = currentDropTimer[i] - 1;

					// If any timer is at, or below 0, remove that drop.
					if (currentDropTimer[i] <= 0) {
						currentDropTimer[i] = 0;
						currentDropImages[i] = null;
						currentDropX[i] = -1;
						currentDropY[i] = -1;
					}
				}
			}

			// Check to see if any enemy died to spawn items.
			if (ItemClass.isDead() == true) {
				// Get a number from ItemDrops.
				int randomNumber = ItemClass.randomDrop();

				// Place the appropriate information in the currentDrops array.
				if (randomNumber == 8) {
					// Put health into drops array.
					int i = 0;
					while (currentDropImages[i] != null) {
						// The current slot is full, increment i.
						i++;
					}
					if (i == 8) {
						// If i is 8, it is beyond the arrays size. Reduce i to
						// 0.
						i = 0;
					}
					// Propagate the arrays with the appropriate information.
					currentDropImages[i] = drops[0];
					currentDropX[i] = ItemClass.deadX();
					currentDropY[i] = ItemClass.deadY();
					// Drops will disappear in 10 seconds.
					currentDropTimer[i] = 600;
				}
				if (randomNumber == 8) {
					// Spawn ammo.
				}
				if (randomNumber == 8) {
					// Spawn points/money.
				}
			}

			// Check to see if the hero has picked up a heart.
			for (int i = 0; i < 8; i++) {

				if (heroClass.heroX() <= currentDropX[i] + 25
						&& heroClass.heroX() >= currentDropX[i] - 25
						&& heroClass.heroY() <= currentDropY[i] + 23
						&& heroClass.heroY() >= currentDropY[i] - 96) {
					// The hero has touched a heart!
					// Remove that heart.
					currentDropTimer[i] = 0;
					currentDropImages[i] = null;
					currentDropX[i] = -1;
					currentDropY[i] = -1;
					// Increase the hero's health.
					if (heroClass.currentLife() != heroClass.lastCheckedLife()) {
						heroClass.updateCurrentLife(heroClass.currentLife + 1);
					}
					// Play a sound.
					AudioInputStream audio = AudioSystem
							.getAudioInputStream(new File("data/powerup.wav"));
					Clip music = AudioSystem.getClip();

					music.open(audio);
					music.start();
				}
			}
			}
			catch(Exception everything)
			{
				
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
				// g.setColor(Color.YELLOW);
				g.drawImage(projectile_fork, p.getX(), p.getY(), this);
				// g.fillRect(p.getX(), p.getY(), 10, 5);
			}
		} else if (state == GameState.Dead) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 800, 480);
			g.setColor(Color.WHITE);
			g.drawString("Dead", 360, 240);

		}

		// Draw hero in current location.
		g.drawImage(hero[heroClass.heroPic()], heroClass.heroX(),
				heroClass.heroY(), this);

		// Create the overlay (hearts, ammo, types of guns);
		// Create the life hearts.
		int i = 0;
		int j = 750;
		while (i < heroClass.maxLives() && lifeHearts[i] != null) {
			g.drawImage(lifeHearts[i], j, 15, this);
			j = j - 55;
			i++;
		}
		// Create the guns.
		i = 0;
		j = 745;
		while (i < heroClass.numberGuns() && guns[i] != null) {
			g.drawImage(guns[i], j, 70, this);
			j = j - 55;
			i++;
		}

		// Drop all drops.
		i = 0;
		for (i = 0; i < 8; i++) {
			if (currentDropImages[i] != null) {
				// Draw the drop.
				g.drawImage(currentDropImages[i], currentDropX[i],
						currentDropY[i], this);
			}
		}

		// Check to see how the hero is moving (left or right and running or
		// walking
		if (keysPressed[1] == true && keysPressed[2] == true) {
			// Update the hero's X coordinates.
			heroClass.updateHeroX(heroClass.heroX + 8);
			// Check pictureChange. If it is 2, change the picture.
			if (heroClass.pictureChange() >= 2) {
				// Update the hero's picture.
				heroClass.updateHeroPic(heroClass.heroPic() + 1);
				heroClass.updatePictureChange(0);
			} else {
				heroClass.updatePictureChange(heroClass.pictureChange() + 1);
			}
		} else if (keysPressed[1] == true) {
			heroClass.updateHeroX(heroClass.heroX + 4);
			if (heroClass.pictureChange() >= 4) {
				heroClass.updateHeroPic(heroClass.heroPic() + 1);
				heroClass.updatePictureChange(0);
			} else {
				heroClass.updatePictureChange(heroClass.pictureChange() + 1);
			}

		} else if (keysPressed[0] == true && keysPressed[2] == true) {
			heroClass.updateHeroX(heroClass.heroX - 8);
			if (heroClass.pictureChange() >= 2) {
				heroClass.updateHeroPic(heroClass.heroPic() - 1);
				heroClass.updatePictureChange(0);
			} else {
				heroClass.updatePictureChange(heroClass.pictureChange() + 1);
			}
		} else if (keysPressed[0] == true) {
			heroClass.updateHeroX(heroClass.heroX - 4);
			if (heroClass.pictureChange() >= 4) {
				heroClass.updateHeroPic(heroClass.heroPic() - 1);
				heroClass.updatePictureChange(0);
			} else {
				heroClass.updatePictureChange(heroClass.pictureChange() + 1);
			}
		}
		// Make sure that the array of hero images never passes 16 or goes below
		// 0.
		if (heroClass.heroPic() == 16) {
			heroClass.updateHeroPic(0);
		} else if (heroClass.heroPic() == -1) {
			heroClass.updateHeroPic(15);
		}
		// Ensure that the hero cannot pass the center of the screen.
		if (heroClass.heroX() >= 400) {
			heroClass.updateHeroX(400);
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
			heroClass.updateMovingLeft(true);
			keysPressed[0] = true;
			break;

		case KeyEvent.VK_RIGHT:
			System.out.println("Move Right");
			heroClass.updateMovingRight(true);
			keysPressed[1] = true;
			break;

		case KeyEvent.VK_SPACE:
			System.out.println("Jump");
			break;

		case KeyEvent.VK_CONTROL:
			System.out.println("Shoot");
			shoot();
			break;

		case KeyEvent.VK_SHIFT:
			keysPressed[2] = true;

			// TEMPORARY - Randomly spawn health drops.
			Random random = new Random();
			int number = random.nextInt(800);
			ItemClass.updateIsDead(true);
			ItemClass.updateDeadX(number);
			number = random.nextInt(480);
			ItemClass.updateDeadY(number);
			break;

		// TEMPORARY - This removes health from the hero (but not a heart).
		case KeyEvent.VK_BACK_SPACE:
			if (heroClass.currentLife() > 0) {
				heroClass.updateCurrentLife(heroClass.currentLife() - 1);

			} else if (heroClass.currentLife() == 0) {
				System.out.println("Player is dead");
				heroClass.updateCurrentLife(heroClass.currentLife() - 1);
			}
			break;

		// TEMPORARY - This increases the hero's total health.
		case KeyEvent.VK_ALT:
			if (heroClass.totalLife() < heroClass.maxLives() - 1) {
				heroClass.updateTotalLife(heroClass.totalLife() + 1);
			}
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
			heroClass.updateMovingLeft(false);
			keysPressed[0] = false;
			break;

		case KeyEvent.VK_RIGHT:
			System.out.println("Stop Moving Right");
			heroClass.updateMovingRight(false);
			keysPressed[1] = false;
			break;

		case KeyEvent.VK_SPACE:
			System.out.println("Stop Jump");
			break;

		case KeyEvent.VK_SHIFT:
			keysPressed[2] = false;
			ItemClass.updateIsDead(false);
			ItemClass.updateDeadX(0);
			ItemClass.updateDeadY(0);
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

	// //
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
