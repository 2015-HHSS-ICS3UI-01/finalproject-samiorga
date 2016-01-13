
import java.awt.image.BufferedImage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lamonta
 */
public class Animation {
    
    private BufferedImage[] frames;
    private int frame = 0;
    private float rate = 0;
    
    private long lastUpdate = 0;
    
    private long elapsed = 0;
    
    private boolean loop = true;
    private boolean finished = false;
    private boolean play = false;
    
    
    public Animation(float rate, BufferedImage[] frames)
    {
        this.frames = frames;
        this.rate = 1000/rate;
    }
    
    private void update(long delta)
    {
        if(play)
        {
            elapsed += delta;
            while(elapsed >= rate)
            {   frame ++;
                elapsed -= rate;
            }
            if(frame >= frames.length )
            {
                if(loop)
                {
                    frame %= frames.length;
                }else
                {
                    frame = 0;
                    play = false;
                }
                finished = true;
            }
        }
    }
    
    public void play()
    {
        if(!play)
        {
            play = true;
            finished = false;
            lastUpdate = System.currentTimeMillis();
        }
    }
    
    public void stop()
    {
        play = false;
        frame = 0;
        lastUpdate = 0;
        finished = false;
    }
    
    public boolean hasFinished()
    {
        return this.finished;
    }
    
    public boolean isPlaying()
    {
        return play;
    }
    
    public void setLoop(boolean set)
    {
        loop = set;
    }
    
    public BufferedImage getFrame()
    {
        long current = System.currentTimeMillis();
        long delta = current - lastUpdate;
        lastUpdate = current;
        update(delta);
        
        return frames[frame];
    }
    

}
