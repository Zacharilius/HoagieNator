package hoagienator;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 *
 */

public class Enemy {

	private int maxHealth, currentHealth, power, speedX, centerX, centerY;
	private Background bg = MainClass.getBg1();
	public Rectangle r = new Rectangle(0,0,0,0);

	// Stores the bullets that the player shoots.
	protected ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

	public void update() {
		centerX += speedX;
		speedX = bg.getSpeedX();
		r.setBounds(centerX-25, centerY-25, 50, 60);
		
		
		if(r.intersects(Hero.rect)){
			checkCollision();
		}
		
	}// end update method
	
	private void checkCollision() {
			System.out.println("collision");
			if (MainClass.heroClass.currentLife() > 0) {
				MainClass.heroClass.updateCurrentLife(MainClass.heroClass.currentLife() - 1);

			} else if (MainClass.heroClass.currentLife() == 0) {
				System.out.println("Player is dead");
				// Play a sound.
				try{
					AudioInputStream audio = AudioSystem.getAudioInputStream(new File("data/death.wav"));
		            Clip music = AudioSystem.getClip();
		            music.open(audio);
		            music.start();
		            MainClass.gameOver = true;
				}
				catch(Exception alpha)
				{
					System.out.println("Fatality could not be loaded");
				}
				MainClass.heroClass.updateCurrentLife(MainClass.heroClass.currentLife() - 1);
			}
	}

	public void die() {

	}// end die method

	public void attack() {
		Projectile p = new Projectile(centerX, centerY, false);
		projectiles.add(p);
	}// end attack method

	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}// end getMaxHealth method

	public int getCurrentHealth() {
		return currentHealth;
	}// end getCurrentHealth method

	public int getPower() {
		return power;
	}// end getPower method

	public int getSpeedX() {
		return speedX;
	}// end getSpeedX method

	public int getCenterX() {
		return centerX;
	}// end getCenterX method

	public int getCenterY() {
		return centerY;
	}// end getCenterY method

	public Background getBg() {
		return bg;
	}// end getBg method

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}// end setMaxHealth method

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}// end setCurrentHealth method

	public void setPower(int power) {
		this.power = power;
	}// end setPower method

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}// end setSpeedX method

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}// end setCenterX method

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}// end setCenterY method

	public void setBg(Background bg) {
		this.bg = bg;
	}// end setBg method


	
	
}// end class Enemy
