import edu.utc.game.*;


import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class finalGame extends Game implements Scene {
	
	private static java.util.Random rand=new java.util.Random();

	static Texture texture=null;
	static String textFileRoute;
	static BufferedReader brTest;
	static String fullReadLine;
	static String[] strArray;
	static int array[] = {150,200,350,200,150,300,350,300,260,110};
	static int wordsArrayX[] = {150,350,150,350};
	static int wordsArrayY[] = {300,300,200,200};
	static String[] usedWordsArray = new String[20];
	static int usedWordsCounter=0;
	List<GameObject> gameObjects;
	List<GameObject> win;
	static finalGame game;
	public static void main(String[] args) throws IOException
	{
	
		// construct a DemoGame object and launch the game loop
		// DemoGame game = new DemoGame();
		// game.gameLoop();
	
		game=new finalGame();
		game.registerGlobalCallbacks();

		SimpleMenu menu = new SimpleMenu();
		menu.addItem(new SimpleMenu.SelectableText(20, 20, 20, 20, "Launch Game", 1, 0, 0, 1, 1, 1), game);
		menu.addItem(new SimpleMenu.SelectableText(20, 60, 20, 20, "Exit", 1, 0, 0, 1, 1, 1), null);
		menu.select(0);
		
		
		game.setScene(menu);
		game.gameLoop();
	}

	
	// DemoGame instance data
	static double correctCount;
	static double incorrectCount;
	int waitTime=0;
	boolean disableAllTargets=false;
	boolean youLose=false;
	boolean youWin=false;
	boolean toMenue=false;
	Text text;
	ColorChangeText ctext;
	ColorChangeText incorrectCountText;
	ColorChangeText correctCountText;
	ColorChangeText firstOption;
	ColorChangeText secondOption;
	ColorChangeText thirdOption;
	ColorChangeText fourthOption;
	ColorChangeText foreignWord;
	ColorChangeText correctTerm;
	List<GameObject> targets;
	String wordsArray[] = {"word1","word2","word3","word4"};
	Player player;
	boolean drawVariable=true;
	Sound theSound;
	Sound soundTwo;
	Sound Correct;
	int arrayCounter=0;//this will be used to load in different squares
	public finalGame()
	{
		// inherited from the Game class, this sets up the window and allows us to access
		// Game.ui
		initUI(640, 480, "Foreign Word Game");

		// screen clear is black (this could go in drawFrame if you wanted it to change
		glClearColor(.0f, .0f, .0f, .0f);
		soundTwo=new Sound("res/button_pus.wav"); //load in the sounds
		theSound=new Sound("res/Shogi.wav");
		Correct=new Sound("res/correct.wav");
		//theSound.play();
		
		gameObjects = new java.util.LinkedList<GameObject>();
		win = new java.util.LinkedList<GameObject>();
		gameObjects.add(new Duck(140, 192,"res/cardBack_blue1.png")); //load in the images
		gameObjects.add(new Duck(340, 192,"res/cardBack_blue1.png"));
		gameObjects.add(new Duck(140, 292,"res/cardBack_blue1.png"));
		gameObjects.add(new Duck(340, 292,"res/cardBack_blue1.png"));
		gameObjects.add(new Duck(250, 100,"res/cardBack_red1.png")); 
		//win.add(new Duck(250, 100,"res/youwin.png"));
		targets = new java.util.LinkedList<GameObject>(); //these targets will be used for the cards
		
		player = new Player();
		spawnTargets(4); //spawn the 4 card objects
		arrayCounter=0;
		
		
	}
	
	public void spawnTargets(int count)
	{
		float r = rand.nextFloat()*0.5f+0.25f;
		float g = rand.nextFloat()*0.5f+0.25f;
		float b = rand.nextFloat()*0.5f+0.25f;
		
		for (int i=0; i<count; i++)
		{
			targets.add(new Target(player, r, g, b));
		}
	}
	
	
	public Scene drawFrame(int delta) {
		if(waitTime>0) { //I use this wait time because if you click space, it will select the next card even if you didnt want to
			glClearColor(1f, 1f, 1f, 1f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			waitTime--;
			player.draw();
			firstOption.draw();
			secondOption.draw();
			thirdOption.draw();
			fourthOption.draw();
			foreignWord.draw();

			
			return this;
		}
		else if(youWin){ //if you won it will catch here and display the new background and texts
			glClearColor(1f, 1f, 1f, 1f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			
			for (GameObject o : win)
			{
				o.update(delta);
			}
			for (GameObject o : win)
			{
				o.draw();
			}
			correctCountText.update(delta);
			foreignWord.update(delta);
			foreignWord.draw();
			correctCountText.draw();
			if (Game.ui.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_Q) ) { //if you press q it brings you to menu
				youWin=false;
				toMenue=true;
			}
			return this;
		} 
		else if(youLose){ //if you lose, it will spawn text and background for that
			glClearColor(1f, 1f, 1f, 1f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			
			
			
			for (GameObject o : win)
			{
				o.update(delta);
			}
			for (GameObject o : win)
			{
				o.draw();
			}
			correctCountText.update(delta);
			foreignWord.update(delta);
			foreignWord.draw();
			correctCountText.draw();
			if (Game.ui.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_Q) ) { 
				youLose=false;
				toMenue=true;
			}
			return this;
		}
		else if(toMenue) {//this spawns the menu
			theSound.stop();
			glClearColor(1f, 1f, 1f, 1f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			SimpleMenu menu = new SimpleMenu();
			menu.addItem(new SimpleMenu.SelectableText(30,  30,  30,  30,  "Play the Game Again?", 1,  0,  0,  1,  1,  1), game);
			menu.addItem(new SimpleMenu.SelectableText(30,  130,  30,  30,  "Exit", 1,  0,  0,  1,  1,  1), null);
			menu.select(0);
			toMenue=false;
			reset(); //this reset resets all variables in the game so its like starting a new game
			Iterator<GameObject> it = targets.iterator();
			while (it.hasNext()) {
				GameObject o = it.next();
				if ( o.isActive())
				{
					it.remove();
					
				}
			}
			return menu;
		}
		else{
		glClearColor(1f, 1f, 1f, 1f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		player.update(delta);

		
		for (GameObject o : gameObjects)
		{
			o.draw();
		}
		
		for (GameObject o : gameObjects)
		{
			o.update(delta);
		}
		
		// update all targets and player object
		for (GameObject o : targets)
		{
			o.update(delta);
		}
		
		// check for deactivated objects
		Iterator<GameObject> it = targets.iterator();
		while (it.hasNext()) {
			GameObject o = it.next();
			if (! o.isActive())
			{
				it.remove();
				
			}
		}
		
		
		
		
		
		// if all targets have been destroyed, spawn some more
		if (targets.isEmpty()) { //every time new stuff is spawned, redo the word
			waitTime=20;
			spawnTargets(4);
			arrayCounter=0;
			try {
				game.newWords();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		// draw existing targets
		for (GameObject o : targets)
		{
			o.draw();
		}
		if(drawVariable) { //this is used to initialize the words
			try {
				game.newWords();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			drawVariable=false;
		}
		
		// draw the player last so it will appear on top of targets
		player.draw();
		
		firstOption.update(delta);
		secondOption.update(delta);
		thirdOption.update(delta);
		fourthOption.update(delta);
		foreignWord.update(delta);

		incorrectCountText.update(delta);
		correctCountText.update(delta);
		
		incorrectCountText.draw(); //this draws all of the words and texts
		correctCountText.draw();
		firstOption.draw();
		secondOption.draw();
		thirdOption.draw();
		fourthOption.draw();
		foreignWord.draw();
		
		if(incorrectCount>4) { //this is where you set the amount of words that can be missed
			youLose=true;
			foreignWord = new ColorChangeText(100,100, 30, 30, "You lose! Press Q for menu.");
			double percent=Math.floor((correctCount/(correctCount+incorrectCount))*100 * 100) / 100;
			//calculate the % correct in the game
			correctCountText = new ColorChangeText(0,30, 30, 30, "Correct Percentage: "+String.valueOf(percent)+"%");
			theSound.play(); //play the end game song
			int o=0;
			int e=0;
			int w=0;
			int h=0;
			while(e<8) { //this is how i make the card background
				while(o<8) {
					win.add(new Duck(w, h,"res/youwin.png"));
					w=w+100;
					o++;
				}
				o=0;
				e++;
				w=0;
				h=h+100;
			}
			
		}
		if(correctCount>14) { //if you get 14 right you win
			youWin=true;
			foreignWord = new ColorChangeText(100,100, 30, 30, "You win! Press Q for menu.");
			//calculate the % correct in the game
			double percent=Math.floor((correctCount/(correctCount+incorrectCount))*100 * 100) / 100;
			correctCountText = new ColorChangeText(0,30, 30, 30, "Correct Percentage: "+String.valueOf(percent)+"%");
			theSound.play(); //plays endgame sound
			int o=0;
			int e=0;
			int w=0;
			int h=0;
			while(e<8) { //spawn background
				while(o<8) {
					win.add(new Duck(w, h,"res/youwin.png"));
					w=w+100;
					o++;
				}
				o=0;
				e++;
				w=0;
				h=h+100;
			}
		}
		
		
		return this;
		}
	}
	
	public void newWords() throws IOException { //this is how i get the words from text file
		texture = new Texture("res/cardBack_blue1.png"); //load texture
		textFileRoute = "res/Vocab.txt"; //open text file
		brTest = new BufferedReader(new FileReader(textFileRoute)); //read the file
		double randomNumber = getRandomIntegerBetweenRange(0,30); //this will get a random line from the file
		int y; //used to iterate through the used words
		int copy = 0;
		
		
		while(randomNumber>0) { //it will parse through each line until we get to that random number
			
			fullReadLine = brTest.readLine();
			y=usedWordsCounter;
			randomNumber--;
			
			if(randomNumber==0) { 
				strArray = fullReadLine.split(":");
				//System.out.println(strArray[0]);
				while(y>0) {
					
					//System.out.println(strArray[0]);
					//System.out.println(usedWordsArray[y]);
					//System.out.println(" ");
					if(usedWordsArray[y].equals(strArray[0])) { //if the word it comes up with has been used
						//System.out.println("here"+usedWordsArray[y]);
						copy =1; //it is a copy
						y=1;
						
						
					}
					y--;
					
				}
			}
			if(copy==1) { //if it is a copy, search for another word
				//this has a bug where it doesnt work every time. Still haven't figured out why
				brTest.close();
				brTest = new BufferedReader(new FileReader(textFileRoute));
				randomNumber = getRandomIntegerBetweenRange(0,30);
				//System.out.println("here new random");
				copy=0;
			    
			}
			
		}
		//System.out.println(randomNumber);
		strArray = fullReadLine.split(":");
		usedWordsCounter++; //keeps track for how many words we have used for the array
		usedWordsArray[usedWordsCounter]=strArray[0];
		// Stop. text is the first line.
		//System.out.println(usedWordsArray[usedWordsCounter]);
		wordsArray[0]=strArray[1]; //I set the word options into an array
		wordsArray[1]=strArray[2]; //so that I can mix the array randomly
		wordsArray[2]=strArray[3]; //for the cards so that the position isn't what is memorized
		wordsArray[3]=strArray[4];
		shuffleArray(wordsArray); //shuffles the array
		//shuffleArray(wordsArrayY);
		disableAllTargets=false;
		firstOption = new ColorChangeText(wordsArrayX[0],wordsArrayY[0], 25, 25, wordsArray[0]); //spawns words on top of cards
		secondOption = new ColorChangeText(wordsArrayX[1],wordsArrayY[1], 25, 25, wordsArray[1]);
		thirdOption = new ColorChangeText(wordsArrayX[2],wordsArrayY[2], 25, 25, wordsArray[2]);
		fourthOption = new ColorChangeText(wordsArrayX[3],wordsArrayY[3], 25, 25, wordsArray[3]);
		foreignWord = new ColorChangeText(array[8],array[9], 30, 30, strArray[0]); //spawns foreign word in top center
		
		correctCountText = new ColorChangeText(0,30, 30, 30, "Correct: "+String.valueOf(correctCount)); //gives correct count
		incorrectCountText = new ColorChangeText(0,0, 30, 30, "Incorrect: "+String.valueOf(incorrectCount)); //gives incorrect count
		brTest.close();
	}
	
	static void shuffleArray(String[] ar) //this is the code for shuffle array. I found it on stackoverflow
	  {
	    // If running on Java 6 or older, use `new Random()` on RHS here
	    Random rnd = ThreadLocalRandom.current();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      String a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
	static void reset() { //resets the counters so that its like a new game
		incorrectCount=0;
		correctCount=0;
	}
	
	public static enum DIR { LEFT, RIGHT, UP, DOWN };
	
	private class Player extends GameObject
	{

		
		public DIR direction=DIR.LEFT;
		
		public Player()
		{
			this.hitbox.setSize(10, 10);
			this.hitbox.setLocation(Game.ui.getWidth()/2-5, Game.ui.getHeight()/2-5);
			this.setColor(1,0,0);
		}
		
		// this allows you to steer the player object
		public void update(int delta)
		{
			
			float speed=0.25f;
			if (Game.ui.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_UP) && this.hitbox.getY()>0)
			{
				this.hitbox.translate(0,  (int)(-speed*delta));
				direction=DIR.UP;
			}
			if (Game.ui.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN) && this.hitbox.getY()<470)
			{
				this.hitbox.translate(0,  (int)(speed*delta));
				direction=DIR.DOWN;
			}
			if (Game.ui.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT) && this.hitbox.getX()>0)
			{
				this.hitbox.translate((int)(-speed*delta), 0);
				direction=DIR.LEFT;
			}
			if (Game.ui.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT) && this.hitbox.getX()<630)
			{
				this.hitbox.translate((int)(speed*delta),0);
				direction=DIR.RIGHT;
			}
		}
	}
	
	private class Target extends GameObject
	{
		private Player player;
		private int size=75;
		
		
		// construct a target in a random location within the bounds of the UI
		public Target(Player p, float r, float g, float b)
		{
			this.player = p;
			this.hitbox.setSize(size+50, size);
			this.setColor(1,1,1);
			this.hitbox.setLocation(array[arrayCounter], array[arrayCounter+1]);
			arrayCounter+=2;
		}

		// if the space key is pressed, check to see if we should deactivate this target
		public void update(int delta)
		{
			if (Game.ui.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE) && 
					player.intersects(this)) {
				
				if(wordsArrayX[0]==this.hitbox.getX() && wordsArrayY[0]==this.hitbox.getY()) {
					firstOption = new ColorChangeText(wordsArrayX[0],wordsArrayY[0], 30, 30, " ");
					if(wordsArray[0]==strArray[1]) { //this checks to see if it was the correct answer
						this.deactivate();
						disableAllTargets=true;
						correctCount++;
						Correct.play();
						correctCountText = new ColorChangeText(0,30, 30, 30, "Correct: "+String.valueOf(correctCount));
					}
					else {
						this.deactivate();
						incorrectCount++;
						soundTwo.play();
						incorrectCountText = new ColorChangeText(0,0, 30, 30, "Incorrect: "+String.valueOf(incorrectCount));
						}
				}
				else if(wordsArrayX[1]==this.hitbox.getX() && wordsArrayY[1]==this.hitbox.getY()) {
					secondOption = new ColorChangeText(wordsArrayX[1],wordsArrayY[1], 30, 30, " ");
					if(wordsArray[1]==strArray[1]) {
						this.deactivate();
						disableAllTargets=true;
						correctCount++;
						Correct.play();
						correctCountText = new ColorChangeText(0,30, 30, 30, "Correct: "+String.valueOf(correctCount));
					}
					else {
						this.deactivate();
						incorrectCount++;
						soundTwo.play();
						incorrectCountText = new ColorChangeText(0,0, 30, 30, "Incorrect: "+String.valueOf(incorrectCount));
						}
				}
				else if(wordsArrayX[2]==this.hitbox.getX() && wordsArrayY[2]==this.hitbox.getY()) {
					thirdOption = new ColorChangeText(wordsArrayX[2],wordsArrayY[2], 30, 30, " ");
					if(wordsArray[2]==strArray[1]) {
						this.deactivate();
						disableAllTargets=true;
						correctCount++;
						Correct.play();
						correctCountText = new ColorChangeText(0,30, 30, 30, "Correct: "+String.valueOf(correctCount));
					}
					else {
						this.deactivate();
						incorrectCount++;
						soundTwo.play();
						incorrectCountText = new ColorChangeText(0,0, 30, 30, "Incorrect: "+String.valueOf(incorrectCount));
						}
				}
				else if(wordsArrayX[3]==this.hitbox.getX() && wordsArrayY[3]==this.hitbox.getY()) {
					fourthOption = new ColorChangeText(wordsArrayX[3],wordsArrayY[3], 30, 30, " ");
					if(wordsArray[3]==strArray[1]) {
						correctCount++;
						correctCountText = new ColorChangeText(0,30, 30, 30, "Correct: "+String.valueOf(correctCount));
						this.deactivate();
						Correct.play();
						disableAllTargets=true;
					}
					else {
						//correctTerm = new ColorChangeText(50,50, 30, 30, "Incorrect");
						this.deactivate();
						incorrectCount++;
						soundTwo.play();
						incorrectCountText = new ColorChangeText(0,0, 30, 30, "Incorrect: "+String.valueOf(incorrectCount));
						}
					
				}
				
				
				
			}
			if(disableAllTargets) {
				this.deactivate();
				}
		}
	
	}
	
	public static double getRandomIntegerBetweenRange(double min, double max){ //random integer class

	    double x = (int)(Math.random()*((max-min)+1))+min;

	    return x;

	}
	
	private enum colorStates {TO_WHITE, TO_RED, TO_GREEN, TO_BLUE};

	private class ColorChangeText extends Text
    {
        colorStates color = colorStates.TO_RED;
        private boolean reddening = false;
        public ColorChangeText(int x, int y, int w, int h, String text){
            super(x,y,w,h, text);
        }

        public void update(int delta){
        	
           
            this.r=1;
            this.g=0;
            this.b=0;
            
        }
    }
	private static class Duck extends GameObject
	{
		private static Texture texture=null;
		
		
		public Duck(int x, int y, String a)
		{
			if (texture==null)
			{
				texture = new Texture(a);
			}
			
			this.hitbox.setSize(145, 90);
			this.hitbox.setLocation(x, y);
			this.setColor(1,1,1);
		}
		
		
		public void draw() { 
			texture.draw(this);
		}
	   
		
		public void update(int delta)
		{
			if (Game.ui.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_UP))
			{
			//	this.hitbox.translate(0,  (int)(-0.5*delta));
			}
			
		}
	}

}
