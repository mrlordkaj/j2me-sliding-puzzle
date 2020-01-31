/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.microedition.lcdui.game.GameCanvas;

/**
 *
 * @author Thinh
 */
public abstract class GamePage extends GameCanvas implements Runnable {
    public short schedule;
    protected boolean isLoading = true, pageLooping = true;
    
    public GamePage() {
        super(false);
        setFullScreenMode(true);
    }
    
    public void run() {
        while(pageLooping) {
            update();
            repaint();
            try {
                Thread.sleep(schedule);
            } catch (InterruptedException ex) {}
        }
    }
    
    protected abstract void update();
    
    public abstract void dispose();
}
