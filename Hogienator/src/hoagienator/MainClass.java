package hoagienator;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;

public class MainClass extends Applet implements Runnable, KeyListener {
	private Image image,projectile_fork;
	private Graphics second;
	private URL base;

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
	}

	@Override
	public void start() {
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
		// REPLACE 'SHOOTER' WITH REAL CLASS NAME.
		// ArrayList projectiles = SHOOTER.getProjectiles();
		ArrayList projectiles = getProjectiles();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = (Projectile) projectiles.get(i);
			g.setColor(Color.YELLOW);
			g.fillRect(p.getX(), p.getY(), 10, 5);
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
}
