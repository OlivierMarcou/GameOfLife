package net.arkaine.game.thread;

import net.arkaine.game.component.GameCanvas;

public class GameThread  extends Thread  {

    public GameCanvas getGameCanvas() {
        return gameCanvas;
    }


    public void setGameCanvas(GameCanvas gameCanvas) {
        this.gameCanvas = gameCanvas;
    }

    public boolean isDontStop() {
        return dontStop;
    }

    public void setDontStop(boolean dontStop) {
        this.dontStop = dontStop;
    }

    private boolean dontStop = true;
    private GameCanvas gameCanvas;

    public void run()
    {
        while (dontStop)
        {
            try
            {
                // thread to sleep for 500 milliseconds
                gameCanvas.updateGame();
                sleep(1000);
                //System.out.println(Thread.currentThread().getName());
            }catch(InterruptedException e){System.out.println(e);}
        }
    }
}
