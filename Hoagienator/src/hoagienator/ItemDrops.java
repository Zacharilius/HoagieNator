package hoagienator;
import java.util.Random;


public class ItemDrops {
	// Boolean value to indicate something has died.
	protected boolean isDead = false;
	
	// Coordinates of dead enemy if one exists.
	protected int deadX = 0;
	protected int deadY = 0;
	
	// Return methods to display the global variables.
	public boolean isDead()
	{
		return isDead;
	}
	public int deadX()
	{
		return deadX;
	}
	public int deadY()
	{
		return deadY;
	}
	
	// Update methods.
	public void updateIsDead(boolean x)
	{
		isDead = x;
	}
	public void updateDeadX(int x)
	{
		deadX = x;
	}
	public void updateDeadY(int x)
	{
		deadY = x;
	}
	
	// Method to determine if something should be dropped.
	public int randomDrop()
	{
		// Randomly get a number between 0-10.
				Random random = new Random();
				int number = random.nextInt(10);
				if(number == 9)
				{
					number = random.nextInt(10);
					if(number == 9)
					{
						// Go for the big score! (999)
						number = random.nextInt(10);
						if(number == 9)
						{
							
							return 20;
						}
					}
					return number + 10;
				}
				else
				{
					// Return that number (0-6 - no drop, 7 - health, 8 - ammo (removed)).
					return number;
				}
	}
// Random number generator used to determine which item is dropped.
}
