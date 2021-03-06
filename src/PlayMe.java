/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
//import sun.audio.AudioData;
//import sun.audio.AudioPlayer;
//import sun.audio.AudioStream;
//import sun.audio.ContinuousAudioDataStream;

/**
 *
 * @author iorgs3184
 */
// make sure you rename this class if you are doing a copy/paste
public class PlayMe extends JComponent implements KeyListener {

    // Height and Width of our game
    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    // sets the framerate and delay for our game
    // you just need to select an approproate framerate
    long desiredFPS = 60;
    long desiredTime = (1000) / desiredFPS;

    //playing sound during game
    Sound mainSound = new Sound("runningsound.wav");
    Sound gameOver = new Sound("Dead.wav");

    //new array for platforms, lava and enemy
    ArrayList<Rectangle> smallPlat = new ArrayList();
    ArrayList<Rectangle> mediumPlat = new ArrayList();
    ArrayList<Rectangle> longPlat = new ArrayList();
    ArrayList<Rectangle> lavas = new ArrayList();
    ArrayList<Rectangle> enemys = new ArrayList();

    //images for backgrounds, character, platforms, lava, enemy, start and game over screen
    BufferedImage startScreen = ImageHelper.loadImage("startscreen.png");
    BufferedImage ShrtPlatForm = ImageHelper.loadImage("SmallPlatForm.png");
    BufferedImage LngPlatForm = ImageHelper.loadImage("LongPlatForm.png");
    BufferedImage lava = ImageHelper.loadImage("lava.png");
    BufferedImage enemy = ImageHelper.loadImage("ENEMY.png");
    BufferedImage cave = ImageHelper.loadImage("cave_with_lava.png");
    BufferedImage playerOrig = ImageHelper.loadImage("player.orig.png");
    BufferedImage gameOverScreen = ImageHelper.loadImage("gameoverScreen.png");

    //animations
    //Animation runLeft;
    Animation running;

    //VARIABLES FOR GAMEPLAY
    //player score
    Font scoreFont = new Font("Arial", Font.BOLD, 40);
    Font scoreDisplay = new Font("Arial", Font.ITALIC, 72);
    int scoreCount = 0;

    //level for different screens
    int level = 0;

    //player controls
    boolean jump = false;
    boolean jumping = false;

    //player gravity and jump value
    int gravity = 2;
    int playerJump = -30;

    //player camera
    int playerCam = 0;

    //generate game world
    public void createWorld() {
     
        //character animation (did not finish)
        BufferedImage[] player = new BufferedImage[2];
        for (int i = 0; i < 1; i++) {
            player[i] = ImageHelper.loadImage("player.orig.png");
        }
        //create runner animation
        running = new Animation(3, player);
    }
    //player variable
    int dy = 0;
    //random number variable for enemy generation
    int randomHL = 0;
    Random random = new Random();
    int randomNumber = (int) (Math.random() * 150) + 1;
    
    //player dimentions
    Rectangle player = new Rectangle(60, 490, 48, 110);

    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)
    @Override
    public void paintComponent(Graphics g) {
        // always clear the screen first!
        //score font 
        g.setFont(scoreFont);
        g.clearRect(0, 0, WIDTH, HEIGHT);

        // GAME DRAWING GOES HERE
        //first level is on the start screen
        if (level == 0) {
            //drawing title screen
            g.drawImage(startScreen, 0, 0, this);
        }

        //seconf level is the main game
        if (level == 1) {
            //drawing the cave
            g.drawImage(cave, 0, 0, this);

            //draw small platforms     
            for (int i = 0; i < smallPlat.size(); i++) {
                g.drawImage(ShrtPlatForm, smallPlat.get(i).x - playerCam, smallPlat.get(i).y,
                        smallPlat.get(i).width, smallPlat.get(i).height, null);
            }

            //draw medium platforms     
            for (int i = 0; i < mediumPlat.size(); i++) {
                g.drawImage(ShrtPlatForm, mediumPlat.get(i).x - playerCam, mediumPlat.get(i).y,
                        mediumPlat.get(i).width, mediumPlat.get(i).height, null);
            }

            //draw long platforms    
            for (int i = 0; i < longPlat.size(); i++) {
                g.drawImage(LngPlatForm, longPlat.get(i).x - playerCam, longPlat.get(i).y,
                        longPlat.get(i).width, longPlat.get(i).height, null);
            }

            //draw lava
            for (int i = 0; i < lavas.size(); i++) {
                g.drawImage(lava, lavas.get(i).x - playerCam, lavas.get(i).y,
                        lavas.get(i).width, lavas.get(i).height, null);
            }
            //draw lava ball enemy
            for (int i = 0; i < enemys.size(); i++) {
                g.drawImage(enemy, enemys.get(i).x - playerCam, enemys.get(i).y,
                        enemys.get(i).width, enemys.get(i).height, null);
            }

            //player        
            g.drawImage(playerOrig, player.x - playerCam, player.y, null);

            //player score
            g.setColor(Color.white);
            g.fillOval(645, 16, 100, 100);
            g.setColor(Color.black);
            g.drawString("" + scoreCount, 680, 75);
        }

        //thirsd level is game over screen
        if (level == 2) {
            //show player their score
            g.setFont(scoreDisplay);
            //draws game over screen
            g.drawImage(gameOverScreen, 0, 0, null);
            //displays players score
            if (scoreCount < 10) {
                g.drawString("" + scoreCount, 125, 340);
            } else {//moves score over if double digits
                g.drawString("" + scoreCount, 105, 340);
            }
        }
    }
    // GAME DRAWING ENDS HERE

    // The main game loop
    // In here is where all the logic for my game will go
    public void run() {
        // Used to keep track of time used to draw and update the game
        // This is used to limit the framerate later on
        long startTime;
        long deltaTime;

        // the main game loop section
        // game will end if you set done = false;
        boolean done = false;
        createWorld();
        //time, lava and platfrom time generator
        //small platfom generating time
        Timer smallGen = new Timer(6000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                smallPlat.add(new Rectangle(player.x + 700, 125, 116, 50));
            }
        });
        //set first delay
        smallGen.setInitialDelay(4400);

        //medium platfom generating time
        Timer mediumGen = new Timer(6000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediumPlat.add(new Rectangle(player.x + 600, 275, 175, 50));
            }
        });
        //set first delay
        mediumGen.setInitialDelay(3950);

        //long platfom generating time
        Timer longGen = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                longPlat.add(new Rectangle(player.x + 700, 425, 246, 50));
            }
        });
        //set first delay
        longGen.setInitialDelay(3000);

        //lava generating time
        Timer lavaGen = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lavas.add(new Rectangle(player.x + 900, 550, 114, 50));
            }
        });
        //set first delay
        lavaGen.setInitialDelay(2000);
        
        //lava generating time
        Timer enemyGen = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //random generation time for lava balls
                int High = 250;
                int Low = randomNumber;
                randomHL = random.nextInt((High - Low) + Low);
                enemys.add(new Rectangle(player.x + 900, randomHL, 48, 16));
                enemys.add(new Rectangle(player.x + 900, 500, 48, 16));
            }
        });
        //set first delay
        enemyGen.setInitialDelay(3000);

        //player score
        Timer scoreIncrease = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scoreCount = scoreCount + 1;
            }
        });
        //set first delay
        scoreIncrease.setInitialDelay(0000);

        //main theme plays the whole time
        mainSound.setLoop(true);

        while (!done) {
            // determines when we started so we can keep a framerate
            startTime = System.currentTimeMillis();

            // all your game rules and move is done in here
            // GAME LOGIC STARTS HERE 
            //title
            if (level == 0) {
                running.play(); //title screen idle animation
                
                //do not play game over at start of game
                if (gameOver.isPlaying()) {
                    gameOver.stop();
                }
            }
            //main game is playing
            if (level == 1) {

                //play music
                if (!mainSound.isPlaying()) {
                    mainSound.play();
                }

                //start small platform timer
                if (!smallGen.isRunning()) {
                    smallGen.start();
                }

                //start medium platform timer
                if (!mediumGen.isRunning()) {
                    mediumGen.start();
                }

                //start long platform timer
                if (!longGen.isRunning()) {
                    longGen.start();
                }

                //start lava timer
                if (!lavaGen.isRunning()) {
                    lavaGen.start();
                }
                //start enemy timer
                if (!enemyGen.isRunning()) {
                    enemyGen.start();
                }
                //start score
                if (!scoreIncrease.isRunning()) {
                    scoreIncrease.start();
                }

                //move the player
                player.x = player.x + 10;
                player.y = player.y + dy;

                //player not jumping
                if (!jumping && jump) {
                    jumping = true;
                    dy = playerJump;
                }

                //add gravity to player y coordinates
                dy = dy + gravity;

                //prevent player from falling
                if (player.y + player.height > HEIGHT) {
                    dy = 0;
                    player.y = HEIGHT - player.height;
                    jumping = false;
                }

                //collision with lava
                //if collides go to game over screen
                for (int i = 0; i < lavas.size(); i++) {
                    Rectangle collision = player.intersection(lavas.get(i));

                    if (player.intersects(lavas.get(i))) {
                        if (collision.width < collision.height) {
                            if (player.x < lavas.get(i).x) {
                                level = 2;
                            }
                            if (player.y < lavas.get(i).y) {
                                level = 2;
                            } else {
                                player.y = player.y + collision.height;
                            }
                        }
                    }
                }
                //collision with enemy lava ball enemy
                //if collides go to game over screen
                for (int i = 0; i < enemys.size(); i++) {
                    Rectangle collision = player.intersection(enemys.get(i));

                    if (player.intersects(enemys.get(i))) {
                        if (collision.width < collision.height) {
                            if (player.x < enemys.get(i).x) {
                                level = 2;
                            }
                            if (player.y < enemys.get(i).y) {
                                level = 2;
                            } else {
                                player.y = player.y + collision.height;
                            }
                        }
                    }
                }
                
                //collision with first long platform
                for (int i = 0; i < longPlat.size(); i++) {
                    if (player.intersects(longPlat.get(i))) {
                        Rectangle collision = player.intersection(longPlat.get(i));
                        if (collision.width < collision.height) {
                            if (player.x < longPlat.get(i).x) {
                                player.x = player.x - collision.width;
                            } else {
                                player.x = player.x + collision.width;
                            }
                        } else {
                            if (player.y < longPlat.get(i).y) {
                                player.y = player.y - collision.height;
                                jumping = false;
                                dy = 0;
                            } else {
                                player.y = player.y + collision.height;
                                jumping = true;
                                dy = 0;
                            }
                        }
                    }
                }
                //collision with second medium platform
                for (int i = 0; i < mediumPlat.size(); i++) {
                    if (player.intersects(mediumPlat.get(i))) {
                        Rectangle collision = player.intersection(mediumPlat.get(i));

                        if (collision.width < collision.height) {
                            if (player.x < mediumPlat.get(i).x) {
                                player.x = player.x - collision.width;
                            } else {
                                player.x = player.x + collision.width;
                            }
                        } else {
                            if (player.y < mediumPlat.get(i).y) {
                                player.y = player.y - collision.height;
                                jumping = false;
                                dy = 0;
                            } else {
                                player.y = player.y + collision.height;
                                jumping = true;
                                dy = 0;
                            }
                        }
                    }
                }
                //collision with last small platform
                for (int i = 0; i < smallPlat.size(); i++) {
                    if (player.intersects(smallPlat.get(i))) {
                        Rectangle collision = player.intersection(smallPlat.get(i));

                        if (collision.width < collision.height) {
                            if (player.x < smallPlat.get(i).x) {
                                player.x = player.x - collision.width;
                            } else {
                                player.x = player.x + collision.width;
                            }
                        } else {
                            if (player.y < smallPlat.get(i).y) {
                                player.y = player.y - collision.height;
                                jumping = false;
                                dy = 0;
                            } else {
                                player.y = player.y + collision.height;
                                jumping = true;
                                dy = 0;
                            }
                        }
                    }
                }
                //camera follows player
                if (player.x - playerCam > 150) {
                    playerCam = playerCam + 10;
                }
                if (playerCam < 0) {
                    playerCam = 0;
                }

                //game over screen when charcter dies
                if (level == 2) {
                    //stop main music from playing
                    if (mainSound.isPlaying()) {
                        mainSound.stop();
                    }
                    //play game over theme
                    //start to play game over screen
                    if (!gameOver.isPlaying()) {
                        gameOver.play();
                    }
                    //player measurements
                    player = new Rectangle(50, 450, 48, 110);
                    playerCam = 0;
                    
                    //stop score
                    if (scoreIncrease.isRunning()) {
                        scoreIncrease.stop();
                    }

                    //stop generater timers
                    smallGen.stop();
                    mediumGen.stop();
                    longGen.stop();
                    lavaGen.stop();
                    enemyGen.stop();

                    //clear collision arrays
                    lavas.clear();
                    enemys.clear();
                    smallPlat.clear();
                    mediumPlat.clear();
                    longPlat.clear();
                }
            }

            // GAME LOGIC ENDS HERE 
            // update the drawing (calls paintComponent)
            repaint();

            // SLOWS DOWN THE GAME BASED ON THE FRAMERATE ABOVE
            // USING SOME SIMPLE MATH
            deltaTime = System.currentTimeMillis() - startTime;
            if (deltaTime > desiredTime) {
                //took too much time, don't wait
            } else {
                try {
                    Thread.sleep(desiredTime - deltaTime);
                } catch (Exception e) {
                };
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creates a windows to show my game
        JFrame frame = new JFrame("Volcano Run");

        // creates an instance of my game
        PlayMe game = new PlayMe();
        // sets the size of my game
        game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // adds the game to the window
        frame.add(game);

        // sets some options and size of the window automatically
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // shows the window to the user
        frame.setVisible(true);
        frame.addKeyListener(game);

        // starts my game loop
        game.run();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {
            jump = true;
        }

        if (key == KeyEvent.VK_ENTER) {
            level = 1;
            scoreCount = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {
            jump = false;
        }

        if (key == KeyEvent.VK_ENTER) {
            level = 1;
        }
    }
}
