/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.SwingUtilities;

/**
 *
 * @author Lamonta
 */
public class Sound {
    
    private Clip clip;
    private boolean looping = false;
    
    public Sound(String fileName)
    {
        File file = new File(fileName);
        try{
        clip = AudioSystem.getClip();
        // getAudioInputStream() also accepts a File or InputStream
        AudioInputStream ais = AudioSystem.getAudioInputStream( file );
        clip.open(ais);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        clip.loop(0);
    }
    
    public void play()
    {
        clip.setMicrosecondPosition(0);
        clip.start();
    }
    
    public void stop()
    {
        clip.stop();
        clip.setMicrosecondPosition(0);
    }
    
    public void setLoop(boolean shouldLoop)
    {
        looping = shouldLoop;
        if(shouldLoop)
        {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }else
        {
            clip.loop(0);
        }
    }
    
    public boolean isPlaying()
    {
        return clip.isActive();        
    }
    
}
