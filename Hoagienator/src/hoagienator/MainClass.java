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

	// test enemies
	protected static Heliboy testHeliboy, testHeliboy2;
	
	//time for respawning enemies
	protected boolean spawnTimerOn = false;
	protected int spawnTimer = 0;
	
	// Array to hold all hero images.
	protected BufferedImage[] hero = new BufferedImage[16];
	
	// Array to hold all enemy images.
	protected BufferedImage[] enemy = new BufferedImage[5];

	// Array to hold all the guns.
	protected BufferedImage[] guns = new BufferedImage[1];

	// Array to hold ammunition types?

	// Array to hold all drops.
	protected BufferedImage[] drops = new BufferedImage[5];

	// Arrays to hold all current drops which should be currently shown on
	// screen.
	protected BufferedImage[] currentDropImages = new BufferedImage[9];
	protected int[] currentDropX = new int[9];
	protected int[] currentDropY = new int[9];
	protected int[] currentDropTimer = new int[9];
	protected int[] interior = new int[9];

	// Array to hold keys pressed so key combinations can be made (Ex - Shift +
	// Move = Run)
	protected boolean[] keysPressed = new boolean[25];

	// Self-explanatory.
	// protected KeyListener alpha;
	protected static Hero heroClass = new Hero();

	protected static ItemDrops ItemClass = new ItemDrops();
	// protected Image image;
	// protected Graphics second;

	// Array with the different life hearts.
	protected BufferedImage[] lifeHearts = new BufferedImage[heroClass
			.maxLives()];
	// Current score. 
	protected int score = 0;
		
	// Landing X coordinate (If one wanted the character to land at the bottom of the screen, one would put around 350).
	protected int ground = 355;
	
	// Jump speed.
	protected int jumpSpeed = 0;
		
	// Fall speed.
	protected int fallSpeed = 1;
		
	// Used to determine if the jumpSpeed should be altered.
	protected int changeJump = 0;
		
	// Used to determine if the fallSpeed should be altered.
	protected int changeFall = 0;
		
	// Used to see when the game is over.
	protected static boolean gameOver = false;
		
	GameState state = GameState.Running;

	private Image image, projectile_fork, background, testEnemy;
	private Image heliboy, heliboy2, heliboy3, heliboy4, heliboy5;
	private Graphics second;
	private URL base;

	private static Background bg1, bg2;
	public static Image tilegrassTop, tilegrassBot, tilegrassLeft,
			tilegrassRight, tiledirt;
	private ArrayList<Tile> tilearray = new ArrayList<Tile>();
	
	private int rightBorderX = 800;
	private int upperBorderY = 480;
	private int maxNumberProjectilesOnScreen = 6;

	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		setSize(rightBorderX, upperBorderY);
		setBackground(Color.BLACK);
		setFocusable(true);
		Frame frame = (Frame) this.getParent().getParent();
		frame.setTitle("The Hoagienator");
		addKeyListener(this);

		// Define base for location of backgrounds, sprites
		try {
			base = getDocumentBase();
		} catch (Exception e) {
			// TODO: handle exception
		}

		// Image Setups
		projectile_fork = getImage(base, "data/bullet_fork2.png");
		
		background = getImage(base, "data/background.png");

		tiledirt = getImage(base, "data/tiledirt.png");
		tilegrassTop = getImage(base, "data/tilegrasstop.png");
		tilegrassBot = getImage(base, "data/tilegrassbot.png");
		tilegrassLeft = getImage(base, "data/tilegrassleft.png");
		tilegrassRight = getImage(base, "data/tilegrassright.png");
		
		//test enemy
		testEnemy = getImage(base, "data/heliboy.png");

		// Try to load Hero sprites, guns, enemy sprites, and hearts.
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
			drops[0] = ImageIO.read(new File("data/dropheart.png"));
			drops[1] = ImageIO.read(new File("data/salami.png"));
			drops[2] = ImageIO.read(new File("data/ham.png"));
			drops[3] = ImageIO.read(new File("data/bacon.png"));
			drops[4] = ImageIO.read(new File("data/bigheart.png"));
			
			// Load enemy sprites.
			enemy[0] = ImageIO.read(new File("data/heliboy.png"));
			enemy[1] = ImageIO.read(new File("data/heliboy2.png"));
			enemy[2] = ImageIO.read(new File("data/heliboy3.png"));
			enemy[3] = ImageIO.read(new File("data/heliboy4.png"));
			enemy[4] = ImageIO.read(new File("data/heliboy5.png"));
		} catch (Exception imageNotFound) {
			System.out.println("Images could not be loaded. Closing game.");
			System.exit(0);
		}

	}
	public void updateDrop(int itemNumber)
	{
		int i = 0;
		while(currentDropImages[i] != null)
		{
			// The current slot is full, increment i.
			i++;
		}
		if(i == 8)
		{
			// If i is 8, it is beyond the arrays size. Reduce i to the slot with the lowest timer.
			int timer = 999;
			for(int j = 0; j < 8; j++)
			{
				if(currentDropTimer[j] < timer)
				{
					timer = currentDropTimer[j];
					i = j;
				}
			}
		}
		// Propagate the arrays with the appropriate information.
		currentDropImages[i] = drops[itemNumber];
		currentDropX[i]= ItemClass.deadX();
		currentDropY[i]= ItemClass.deadY();
		interior[i] = itemNumber;
		// Drops will disappear in 10 seconds.
		currentDropTimer[i]= 600;
	}
	@Override
	public void start() {
		bg1 = new Background(0, 0);
		bg2 = new Background(1280, 0);
		testHeliboy = new Heliboy(360, 360);
		//testHeliboy2 = new Heliboy(800, 360);
		try {
			loadMap("data/map1.txt");
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
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(new File("data/song.wav"));
            Clip music = AudioSystem.getClip();
            
            music.open(audio);
            music.loop(Clip.LOOP_CONTINUOUSLY);

            music.start();
        }
		catch(Exception alpha){}
		while (true) {
			if(gameOver) {
				try {
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
				}
				catch(Exception alpha){}
				repaint();
				break;
			}
			testHeliboy.update();
			//handle respawning enemies
			if (spawnTimerOn){
				if (spawnTimer < 250 && heroClass.movingRight()){
					spawnTimer++;
				}
				else if (spawnTimer < 250){
					
				}
				else {
					spawnTimer = 0;
					spawnTimerOn = false;
					Random randy = new Random();
					testHeliboy = new Heliboy(799, randy.nextInt(60) + 340);
				}
			}
			// Check to see if hero is jumping
			if(jumpSpeed > 0)
			{
				// Change jump speed every 8 cycles.
				if(changeJump == 8)
				{
					changeJump = 0;
					jumpSpeed--;
				}
				else
				{
					changeJump++;
				}
			}
			// If character is above the ground, make the character fall to the ground.
			else if(jumpSpeed == 0 && heroClass.heroY() < ground-1)
			{
				// Change fall speed every 8 cycles.
				if(changeFall == 8) {
					fallSpeed++;
					changeFall = 0;
				}
				else {
					changeFall++;
				}
			}
			else
			{
				// Reset fallSpeed.
				fallSpeed = 1;
			}
			// Removes offscreen projectiles from memory.
			ArrayList<Projectile> heroProjectiles = heroClass.getProjectiles();
			//REPLACE 'enemy' WITH ACTUAL ENEMY VARIABLE NAME
			//ArrayList<Projectile> enemyProjectiles = enemy.getProjectiles();
			for (int i = 0; i < heroProjectiles.size(); i++) {
				Projectile p = (Projectile) heroProjectiles.get(i);
				p.setScreenBorder(rightBorderX);
				if (p.isVisible() == true) {
					p.update();
				} else {
					heroProjectiles.remove(i);
				}
			}
			try{
			// Check to see if more hearts need to be loaded. The number of
			// hearts cannot decrease.
			if (heroClass.lastCheckedLife() != heroClass.totalLife()) {
				heroClass
						.updateLastCheckedLife(heroClass.lastCheckedLife() + 1);
				lifeHearts[heroClass.lastCheckedLife()] = ImageIO
						.read(new File("data/heart.png"));
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
			// If any items went off-screen, delete them.
			for(int i = 0; i < 8; i++)
			{
				if(currentDropX[i] < 0)
				{
					currentDropTimer[i] = 0;
					currentDropImages[i] = null;
					currentDropX[i] = -1;
					currentDropY[i] = -1;
				}
			}
			// 
			// Check to see if any enemy died to respawn and spawn items.
			if (ItemClass.isDead() == true) {
				if (testHeliboy.getCenterX() > 0){
					score += 10;
				}
				spawnTimerOn = true;
				ItemClass.updateIsDead(false);
				// Get a number from ItemDrops.
				int randomNumber = ItemClass.randomDrop();
				// Place the appropriate information in the currentDrops array.
				if(randomNumber == 7)
				{
					updateDrop(0);
				}
				if(randomNumber == 9)
				{
					// Spawn ammo.
				}
				if(randomNumber > 9 && randomNumber < 20)
				{
					// Spawn points/money.
					if(randomNumber < 15)
					{
						// Spawn 100 pt salami.
						updateDrop(1);
					}
					else if(randomNumber < 18)
					{
						// Spawn 300 pt ham.
						updateDrop(2);
					}
					else
					{
						// Spawn 1000 pt bacon.
						updateDrop(3);
					}
				}
				if(randomNumber == 20)
				{
					// Spawn increase health.
					updateDrop(4);
				}
			}

			// Check to see if the hero has picked up a heart.
			for (int i = 0; i < 8; i++) {

				if (heroClass.heroX() <= currentDropX[i] + 25
						&& heroClass.heroX() >= currentDropX[i] - 25
						&& heroClass.heroY() <= currentDropY[i] + 23
						&& heroClass.heroY() >= currentDropY[i] - 96) {
					if(interior[i] == 0)
					{
						// The hero has touched a heart!
						// Remove that heart.
						currentDropTimer[i] = 0;
						currentDropImages[i] = null;
						currentDropX[i] = -1;
						currentDropY[i] = -1;
						// Increase the hero's health.
						if(heroClass.currentLife() != heroClass.lastCheckedLife())
						{
							heroClass.updateCurrentLife(heroClass.currentLife + 1);
						}
						// Play a sound.
						AudioInputStream audio = AudioSystem.getAudioInputStream(new File("data/powerup.wav"));

						//System.out.println("A");
			            Clip music = AudioSystem.getClip();
			            
			            music.open(audio);
			            music.start();
					}
					if(interior[i] == 1)
					{
						currentDropTimer[i] = 0;
						currentDropImages[i] = null;
						currentDropX[i] = -1;
						currentDropY[i] = -1;
						score += 100;
					}
					if(interior[i] == 2)
					{
						currentDropTimer[i] = 0;
						currentDropImages[i] = null;
						currentDropX[i] = -1;
						currentDropY[i] = -1;
						score += 300;
					}
					if(interior[i] == 3)
					{
						currentDropTimer[i] = 0;
						currentDropImages[i] = null;
						currentDropX[i] = -1;
						currentDropY[i] = -1;
						score += 1000;
					}
					if(interior[i] > 0 && interior[i] != 4)
					{
						// Play a sound.
						AudioInputStream audio = AudioSystem.getAudioInputStream(new File("data/bite.wav"));
			            Clip music = AudioSystem.getClip();
			            
			            music.open(audio);
			            music.start();
					}
				}
				else if(heroClass.heroX() <= currentDropX[i]+75 && heroClass.heroX() >= currentDropX[i] -35 && heroClass.heroY() <= currentDropY[i]+38 && heroClass.heroY() >= currentDropY[i] -106)
				{
					if(interior[i] == 4)
					{
						// Increase total life.
						if(heroClass.totalLife() < heroClass.maxLives() - 1)
						{
							heroClass.updateTotalLife(heroClass.totalLife() + 1);
						}
						
						// Play a sound.
						AudioInputStream audio = AudioSystem.getAudioInputStream(new File("data/ohyea.wav"));
			            Clip music = AudioSystem.getClip();
			            
			            music.open(audio);
			            music.start();
			            currentDropTimer[i] = 0;
						currentDropImages[i] = null;
						currentDropX[i] = -1;
						currentDropY[i] = -1;
					}
				}
				}
			
			}
			catch(Exception everything) {}

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
		if (gameOver == false) {
			// Modify the vertical component.
			// Jumping.
			if(jumpSpeed > 0)
			{
				heroClass.updatePictureChange(0);
				heroClass.updateHeroY(heroClass.heroY()-jumpSpeed);
			}
			// Falling.
			else if(heroClass.heroY() < ground-1)
			{
				heroClass.updatePictureChange(0);
				heroClass.updateHeroY(heroClass.heroY()+fallSpeed);
			}
			if(heroClass.heroY() >= ground)
			{
				heroClass.updateHeroY(ground-1);
			}
			g.drawImage(background, bg1.getBgX(), bg1.getBgY(), this);
			g.drawImage(background, bg2.getBgX(), bg2.getBgY(), this);

			//paint test enemy
			g.drawImage(testEnemy, testHeliboy.getCenterX() - 48, testHeliboy.getCenterY() - 48, this);
			//g.drawRect((int)testHeliboy.r.getX(), (int)testHeliboy.r.getY(), (int)testHeliboy.r.getWidth(), (int)testHeliboy.r.getHeight());
			
			paintTiles(g);
			ArrayList projectiles = heroClass.getProjectiles();
			for (int i = 0; i < projectiles.size(); i++) {
				Projectile p = (Projectile) projectiles.get(i);
				// g.setColor(Color.YELLOW);
				g.drawImage(projectile_fork, p.getX(), p.getY(), this);
				// g.fillRect(p.getX(), p.getY(), 10, 5);
			}
		

		// Draw hero bounding box
		//g.drawRect((int)heroClass.rect.getX(), (int)heroClass.rect.getY(), (int)heroClass.rect.getWidth(), (int)heroClass.rect.getHeight());
		
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
		// Create the current score.
		g.drawString(Integer.toString(score), 717, 90);

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
		
		//Update Bounding Box position after movement
		heroClass.updateBoundingBox(heroClass.heroX,heroClass.heroY);
		
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
			// If the hero is moving right, move all drops and enemies left.
						if(heroClass.movingRight() == true)
						{
							testHeliboy.setCenterX(testHeliboy.getCenterX() - 2);
							for(int k = 0; k < 8; k++)
							{
								if(currentDropX[k] != -1 && keysPressed[2] == true)
								{
									currentDropX[k] = currentDropX[k] - 8; 
								}
								else
								{
									currentDropX[k] = currentDropX[k] - 4; 
								}
							}
						}
			//Added code to control background
			if (heroClass.movingRight){
				bg1.setBgX(bg1.getBgX() - 4);
				bg2.setBgX(bg2.getBgX() - 4);
			}
		}
		// Ensure that the hero cannot pass the left of the screen.
				if(heroClass.heroX() <= 10) {
					heroClass.updateHeroX(10);
				}
				
				// If the game is over.
				if(gameOver == true) {
					try {
						BufferedImage im = ImageIO.read(new File("data/blood.png"));
						g.drawImage(im, 0, 0, this);
					}
					catch(Exception alpha) {
						System.out.println("Game could not display blood");
					}
				}
		}
		 else {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, 800, 480);
				g.setColor(Color.WHITE);
				g.drawString("Dead", 360, 240);
				g.drawString(Integer.toString(score), 360, 253);
				try
				{
					BufferedImage im = ImageIO.read(new File("data/blood.png"));
					g.drawImage(im, 0, 0, this);
				}
				catch(Exception alpha)
				{
					System.out.println("Game could not display blood");
				}
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
			// If character is on the ground.
			if(heroClass.heroY() == ground-1) {
				// Update jumpSpeed to indicate character is jumping.
				jumpSpeed = 5;
			}
			break;

		case KeyEvent.VK_DOWN:
			//System.out.println("Move Down");
			break;

		case KeyEvent.VK_LEFT:
			//System.out.println("Move Left");
			heroClass.updateMovingLeft(true);
			keysPressed[0] = true;
			break;

		case KeyEvent.VK_RIGHT:
			//System.out.println("Move Right");
			heroClass.updateMovingRight(true);
			keysPressed[1] = true;
			break;

		case KeyEvent.VK_SPACE:
			//keypress works only if max number of projectiles aren't on screen already.
			if(heroClass.getProjectiles().size() < maxNumberProjectilesOnScreen){
				//System.out.println("Shoot");
				heroClass.shoot();
			}
			break;

		case KeyEvent.VK_CONTROL:
			//TEMPORARY
			{
				ground = 250;
			}
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
				//System.out.println("Player is dead");
				// Play a sound.
				try{
					AudioInputStream audio = AudioSystem.getAudioInputStream(new File("data/death.wav"));
		            Clip music = AudioSystem.getClip();
		            music.open(audio);
		            music.start();
		            gameOver = true;
				}
				catch(Exception alpha) {
					System.out.println("Fatality could not be loaded");
				}
				heroClass.updateCurrentLife(heroClass.currentLife() - 1);
			}
		}
	}//end keyPressed method

	/**
	 * Carries out responses to Key Releases.
	 */
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getKeyCode()) {
		case KeyEvent.VK_UP:
			//System.out.println("Stop Moving Up");
			break;

		case KeyEvent.VK_DOWN:
			//System.out.println("Stop Moving Down");
			break;

		case KeyEvent.VK_LEFT:
			//System.out.println("Stop Moving Left");
			heroClass.updateMovingLeft(false);
			keysPressed[0] = false;
			break;

		case KeyEvent.VK_RIGHT:
			//System.out.println("Stop Moving Right");
			heroClass.updateMovingRight(false);
			keysPressed[1] = false;
			break;

		case KeyEvent.VK_SPACE:
			//System.out.println("Stop Jump");
			break;

		case KeyEvent.VK_SHIFT:
			keysPressed[2] = false;
			ItemClass.updateIsDead(false);
			ItemClass.updateDeadX(0);
			ItemClass.updateDeadY(0);
		}
	}//end keyReleased method

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}//end keyTyped method

	//Loads the level data from file
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

	}//end loadMap method

	//Updates the tile array
	private void updateTiles() {
		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			t.update();
		}
	}//end updateTiles method

	//Paints the level tiles to the screen
	private void paintTiles(Graphics g) {
		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			g.drawImage(t.getTileImage(), t.getTileX(), t.getTileY(), this);
		}
	}//end paintTiles method

	public static Background getBg1() {
		return bg1;
	}//end getBg1 method

	public static Background getBg2() {
		return bg2;
	}//end getBg2 method
	
}//end MainClass
