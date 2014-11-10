package hoagienator;

import java.util.ArrayList;

public class Hero {
	// Hero X and Y coordinates.
	protected int heroX = 50;
	protected int heroY = 350;

	// Shows which way the hero is moving.
	protected boolean movingLeft = false;
	protected boolean movingRight = false;

	// Shows which picture the hero is currently portrayed as.
	protected int heroPic = 0;

	// Shows when to change the picture.
	protected int pictureChange = 0;

	// Shows the maximum lives that the hero can have.
	protected int maxLives = 6;

	// How many hearts the hero should start with. The desired value should be
	// one less due to 0.
	protected int startingHearts = 2;

	// totalLife is the max number of hearts the player has.
	protected int totalLife = startingHearts;

	// lastCheckedLife is the total number of hearts last printed on the
	// screen..
	protected int lastCheckedLife = startingHearts;

	// currentLife is the current number of filled hearts the player is on.
	protected int currentLife = startingHearts;

	// lifesPresent is the current number of filled hearts last printed on the
	// screen.
	protected int livesPresent = startingHearts;

	// This is the current number of guns the player has - Not the types of guns
	// the player has.
	protected int numberGuns = 1;
	
	// Stores the bullets that the player shoots.
	protected ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

	// Return methods to display the global variables.
	public int heroX() {
		return heroX;
	}

	public int heroY() {
		return heroY;
	}

	public boolean movingLeft() {
		return movingLeft;
	}

	public boolean movingRight() {
		return movingRight;
	}

	public int heroPic() {
		return heroPic;
	}

	public int startingHearts() {
		return startingHearts;
	}

	public int totalLife() {
		return totalLife;
	}

	public int lastCheckedLife() {
		return lastCheckedLife;
	}

	public int currentLife() {
		return currentLife;
	}

	public int livesPresent() {
		return livesPresent;
	}

	public int maxLives() {
		return maxLives;
	}

	public int numberGuns() {
		return numberGuns;
	}

	public int pictureChange() {
		return pictureChange;
	}

	// Update methods.
	public void updateHeroX(int x) {
		heroX = x;
	}

	public void updateHeroY(int x) {
		heroY = x;
	}

	public void updateMovingLeft(boolean x) {
		movingLeft = x;
	}

	public void updateMovingRight(boolean x) {
		movingRight = x;
	}

	public void updateHeroPic(int x) {
		heroPic = x;
	}

	public void updateStartingHearts(int x) {
		startingHearts = startingHearts - x;
	}

	public void updateTotalLife(int x) {
		totalLife = x;
	}

	public void updateLastCheckedLife(int x) {
		lastCheckedLife = x;
	}

	public void updateCurrentLife(int x) {
		currentLife = x;
	}

	public void updateLivesPresent(int x) {
		livesPresent = x;
	}

	public void updateMaxLives(int x) {
		maxLives = x;
	}

	public void updateNumberGuns(int x) {
		numberGuns = x;
	}

	public void updatePictureChange(int x) {
		pictureChange = x;
	}

	

	/**
	 * Adds projectiles to the given location.
	 */
	public void shoot() {
		Projectile p = new Projectile(heroX+70, heroY+15);
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
