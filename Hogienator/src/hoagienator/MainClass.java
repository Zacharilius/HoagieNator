package hoagienator;

import java.applet.Applet;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainClass extends Applet implements Runnable, KeyListener{
	
	@Override
    public void init() {
        // TODO Auto-generated method stub
        super.init();
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        super.start();
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
	 * Main Game loop that updates all objects on every iteration. ie. bullet position, character position
	 */ 
	@Override
	public void run() {
		
		while (true) {
			//Removes offscreen projectiles from memory.
			//REPLACE 'SHOOTER' WITH REAL CLASS NAME.
			ArrayList<Projectile> projectiles = SHOOTER.getProjectiles();
			for(int i=0; i<projectiles.size();i++){
				Projectile p = (Projectile) projectiles.get(i);
				if(p.isVisible() == true){
					p.update();
				}else {
					projectiles.remove(i);
				}
			}
			
			//Sleep for 17 milliseconds = 60 updates per second
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
