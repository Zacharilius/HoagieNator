package hoagienator;

public class Projectile {
	private int x, y;
	private int speedX; // Speed of projectile
	private boolean visible;
	private int screenBorder;
	private boolean facingRight = true;

	/**
	 * Default Constructor.
	 * 
	 * @param startX
	 *            - starting X coordinate of projectile.
	 * @param startY
	 *            - starting Y coordinate of projectile.
	 */
	public Projectile(int startX, int startY) {
		x = startX;
		y = startY;
		speedX = 6;
		screenBorder = 800;
		visible = true;
	}

	/**
	 * Constructor with option to change bullet direction.
	 * 
	 * @param startX
	 *            - starting X coordinate of projectile.
	 * @param startY
	 *            - starting Y coordinate of projectile.
	 * @param facingRight
	 *            - true:bullet flies right, false:bullet flies left.
	 */
	public Projectile(int startX, int startY, boolean facingRight) {
		x = startX;
		y = startY;
		speedX = 6;
		screenBorder = 800;
		visible = true;
		this.facingRight = facingRight;
	}

	/**
	 * Called to update the position of the projectile(x) and make offscreen
	 * projectiles invisible.
	 */
	public void update() {
		if (facingRight == true) {
			x += speedX;
			if (x > screenBorder) {
				visible = false;
			}
		} else {
			x -= speedX;
			if (x < 0) {
				visible = false;
			}
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getSpeedX() {
		return speedX;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setScreenBorder(int screenBorder) {
		this.screenBorder = screenBorder;
	}
	
	public void isFacingRight(boolean facingRight){
		this.facingRight = facingRight;
	}
}
