import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.geom.Rectangle2D; 
import java.awt.geom.Ellipse2D; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class MainGame extends PApplet {

float frame;
long coinTime;
float time;
Player player;
ArrayList<Wall> objects;
ArrayList[] walls;
Coin[] coins;
boolean gameOver = false;
boolean gameLoop, keyPressed, isJumping, isGrounded, keyA, keyW, keyD;
PImage level;
PVector previous, spawnPoint;
Collidable[][] levelObjects;
float gravity = 0.5f;


public void setup(){
  frameRate(60);
  
  noStroke();

  println(width + " - " + height);

  coins = new Coin[(int) random(20)];
  frame = 0;

  level = loadImage("assets/levels/Level1.png");

//Creates level from image
  this.createLevel();
  this.populateWalls();

//Creates a new player in the bottom left of the screen
  player = new Player(spawnPoint.x, spawnPoint.y);
  isJumping = false;
  isGrounded = false;
}

public void draw(){
//Init
	background(200);
  //image(level, 0, 0); //Creates the image behind the level *DEBUG HELPER
// Draws the Level
  this.drawLevel();
//Player
  player.draw();
  previous = player.getPosition().copy();
//Timer create
  frame++;
  time = millis();
  this.createTimer();
//Game Logic

//Gravity
  this.updateGravity();
//KeStroke handeling
  this.keyStrokes();
//Wall Collision Check
	this.playerCollisions();
}

//Updates the gravity
public void updateGravity(){
  player.vel.y += gravity;
  player.pos.y += player.vel.y;
}

//Draws the level
public void drawLevel() {
  for(int y = 0; y < levelObjects[0].length; y++){
    for(int x = 0; x < levelObjects.length; x++){
      if(levelObjects[x][y] != null) levelObjects[x][y].draw();
        //stroke(0);
    }
  }
}

//Creates the level from the file
public void createLevel(){
  levelObjects = new Collidable[this.width / 16][this.height / 16];

  for(int y = 0; y < levelObjects[0].length; y++){
    for(int x = 0; x < levelObjects.length; x++){
      PImage c = level.get(x * 16, y * 16, 16, 16);
      int pixel = c.get(8, 8);

      float r = red(pixel);
      float g = green(pixel);
      float b = blue(pixel);

      int wall = color(35, 31, 32);
      int coin = color(255, 242, 0); // 248.0, 239.0, 36.0 //255, 222, 23
      int door = color(0, 174, 239); // 40.0, 172.0, 226.0
      int gate = color(0, 166, 81);
      int spawn = color(46.0f, 49.0f, 146.0f); //41.0, 60.0, 146.0
      int lava = color(236, 0, 140);
      int movingPlatform = color(139.0f, 94.0f, 60.0f); //139.0, 94.0, 59.0
      int gateKey = color(247.0f, 148.0f, 30.0f); //247.0, 148.0, 30.0

      // if((r != 0.0 || g != 0.0 || b != 0.0 ) && (r != 35.0 || g != 31.0 || b != 32.0) && (r != 0.0 || g != 174.0 || b != 239.0)
      // && (r != 236.0 || g != 0 || b != 140 ) && (r != 248.0 || g != 239 || b != 36 ) && (r != 139.0 || g != 94 || b != 59 )
      // && (r != 40.0 || g != 172 || b != 226 ) && (r != 41.0 || g != 60 || b != 146 ))
      if((r != 0.0f || g != 0.0f || b != 0.0f ))
      println(r + ", " + g + ", " + b);

      if(pixel == wall) levelObjects[x][y] = new Wall(x * 16, y * 16, 16, 16);
      else if(pixel == gate) levelObjects[x][y] = new Goal(x * 16, y * 16);
      else if(pixel == coin) levelObjects[x][y] = new Coin(x * 16, y * 16);
      else if(pixel == spawn) spawnPoint = new PVector(x * 16, y * 16);
      else if(pixel == lava) levelObjects[x][y] = new Lava(x * 16, y * 16);
      else if(pixel == movingPlatform) levelObjects[x][y] = new Platform(x * 16, y * 16);


    }
  }
}

public void populateWalls(){
  walls = new ArrayList[4];
  for(int i = 0; i < walls.length; i++){
    walls[i] = new ArrayList<Wall>();
  }

  for(int y = 1; y < levelObjects[0].length - 1; y++){
    for(int x = 1; x < levelObjects.length - 1; x++){
      Collidable current = levelObjects[x][y];
    //Detect wether each wall is the floor, left, right or top walls
      if(current != null && current.getType().equals("wall")){
        if(levelObjects[x][y - 1] == null) walls[0].add(current); //Floor
        else if(levelObjects[x][y + 1] == null) walls[1].add(current); //Top
        else if(levelObjects[x + 1][y] == null) walls[2].add(current); //Left
        else if(levelObjects[x - 1][y] == null) walls[3].add(current); //Right
      }

    }
  }
}

//Handels the collisions of the player to the walls and coins
public void playerCollisions(){

  for(int y = levelObjects[0].length - 1; y > 1; y--){
  	for(int x = 0; x < levelObjects.length; x++){
  		Collidable tile = levelObjects[x][y];

  		if(tile != null && player.isColliding(tile)){

        //Coin Collisions
  			if(tile.getType().equals("coin")){
  			 	coinTime += 10;
  			 	levelObjects[x][y] = null;
  			}

  			//Wall collisions
  			if(tile.getType().equals("wall")){
  				this.wallCollision(tile);
  			}

  		}

  	}
  }

}

//Vector projection for collisions which is added to player on collision
public PVector projection(PVector pos, Collidable wall, String axis){
  PVector wallPos = wall.getPosition();
  PVector proj = new PVector();
  if(axis.equals("y")){
    if(pos.y < wallPos.y){
      float y = wallPos.y - pos.y + player.getHeight();
      println(y);
      proj = new PVector(0, y);
    }else if(pos.y > wallPos.y){
      float y = wallPos.y + wall.getHeight() - pos.y;
      println(y);
      proj = new PVector(0, y);
    }
  }
  return proj.mult(-1);
}

public void wallCollision(Collidable tile){
  PVector tilePos = tile.getPosition();
  PVector pos = player.getPosition();

  //Floor
  if(walls[0].contains(tile)){
    isGrounded = true;
    player.vel.y = 0;
    player.pos.y = tilePos.y - player.getHeight();

  }

  //Ceiling
  if(walls[1].contains(tile)){
    player.pos = previous;
  }

  if(walls[2].contains(tile)){
    player.pos = previous;
  }

  if(walls[3].contains(tile)){
    player.pos = previous;
  }

}

//Checks if a key has been pressed then executes the corresponding action
public void keyStrokes(){
  if(keyA) player.moveLeft();
  if(keyD) player.moveRight();
  if(keyW) {
    isJumping = true;
    if(isGrounded){
      player.jump();
      isGrounded = false;
    }
    isJumping = false;
  }
}

//Sets the keyPressed Boolean to true;
public void keyPressed() {
  keyPressed = true;
  switch(key){
    case 'd':
      keyD = true;
      break;

    case 'a':
      keyA = true;
      break;

    case 'w':
      keyW = true;
      break;
  }
}

//Sets the keyPressed Boolean to false
public void keyReleased(){
  keyPressed = false;
  switch(key){
    case 'd':
      keyD = false;
      break;

    case 'a':
      keyA = false;
      break;

    case 'w':
      keyW = false;
      break;
  }
}

//Creates the timer bar
public void createTimer(){
  float length = (float) (width - 40 * 2) - time / 100 + coinTime;
  if(length > width - 40 * 2) length = width - 40 * 2;

  PFont f = loadFont("Carlito-14.vlw");
  createFont("Century Gothic",14);

  if(length > 0){
    fill(0xff466CB7);
    rect(40, 40 / 2 - 10, length, 40 / 2);

    textFont(f,14);
    fill(255);
    text(round(length) , length, 40 / 3 + 12);

  }else if(!gameOver){
    gameOver = true;
    text("Game Over", 250, 250);
    gameLoop = false;
    stop();
  }

}
class Coin extends Collidable{
  String type = "coin";
  float widthC, heightC, addTime, timer, delay;
  int numFrames, currentFrame;
  PImage[] images;
  PVector pos;

  Coin(float x, float y){
    pos = new PVector(x, y);
    this.widthC = 16;
    this.heightC = 16;
    this.addTime = 10;

    //Sprite animation
    timer = 0;
    delay = 100000000;
    numFrames = 9;  // The number of frames in the animation, # of images
    currentFrame = 0;
    images = new PImage[numFrames];
    for(int i = 0; i < numFrames; i++){ //Populates the array with the images
      images[i] = loadImage("assets/coin/coin" + (i + 1) + ".png");
    }

  }

  public void draw(){
    timer++;
      currentFrame = (currentFrame + 1) % numFrames;  // Use % to cycle through frames
  if(timer % delay == 1){
    //for (int x = 0; x < width; x += images[0].width + 10) {
      image(images[(currentFrame) % numFrames], pos.x - images[0].width / 2, pos.y - images[0].height / 2);
      // fill(255, 0, 0);
      // rect(pos.x, pos.y, widthC, heightC);
    //}
  }else
    image(images[(currentFrame) % numFrames], pos.x - images[0].width / 2, pos.y - images[0].height / 2);
  }

  //Returns the width, the height, a position vector and the amount of time that is added on pickups

  public float getWidth(){
      return this.widthC;
    }

  public float getHeight(){
      return this.heightC;
    }

  public String getType(){
      return this.type;
    }

  public float getTimeAddition(){
      return this.addTime;
    }

  public PVector getPosition(){
      return pos;
    }

}



abstract class Collidable{

  public abstract float getWidth();
  public abstract float getHeight();
  public abstract PVector getPosition();
  public abstract String getType();
  public abstract void draw();
//  abstract String type;

  //Gets the bounds off the current Object
  public Rectangle2D getBounds() {
    return new Rectangle2D.Float(getPosition().x, getPosition().y, getWidth(), getHeight());
  }

  public Ellipse2D getBoundsEllipse() {
    return new Ellipse2D.Float(getPosition().x, getPosition().y, getWidth(), getHeight());
  }

//Returns true if it is colliding with the other object
  public boolean isColliding(Collidable other) {
    return (getBounds().intersects(other.getBounds()));
  }

  public boolean isCollidingEllipse(Collidable other) {
    return (getBoundsEllipse().intersects(other.getBounds()));
  }


}
class Gate extends Collidable{
  PVector pos;
  int widthG, heightG;
  String type = "gate";
  
  Gate(){
    
  }
  
  public void draw(){
    fill(0, 255, 0);
    rect(pos.x, pos.y, widthG, heightG);
  }

  //Returns the width, the height and a position vector

  public float getWidth(){
      return this.widthG;
    }

  public float getHeight(){
      return this.heightG;
    }

  public PVector getPosition(){
      return pos;
    }

    public String getType(){
        return this.type;
      }

}
class Goal extends Collidable{
  String type = "goal";
  float widthG, heightG;
  PVector pos;

  Goal(float x, float y){
    pos = new PVector(x, y);
    this.widthG = 16;
    this.heightG = 16;
  }

  public void draw(){
    fill(0, 255, 0);
    rect(pos.x, pos.y, widthG, heightG);
  }

  //Returns the width, the height and a position vector

  public float getWidth(){
      return this.widthG;
    }

  public float getHeight(){
      return this.heightG;
    }

  public PVector getPosition(){
      return pos;
    }

    public String getType(){
        return this.type;
      }

}
class Key extends Collidable{
  PVector pos;
  int widthK, heightK;

  Key(int x, int y){
    pos = new PVector(x, y);
    widthK = 16;
    heightK = 16;
  }

  public void draw(){

  }

//Returns the width, the height, a position vector and the amount of time that is added on pickups
  public float getWidth(){
      return this.widthK;
  }

  public float getHeight(){
      return this.heightK;
  }

  public String getType(){
      return this.type;
  }

  public float getTimeAddition(){
      return this.addTime;
  }

  public PVector getPosition(){
      return pos;
  }

}
class Lava extends Collidable{
  String type = "lava";
  float widthL, heightL;
  PVector pos;
  
  Lava(int x, int y){
    this.pos = new PVector(x, y);
    widthL = 16;
    heightL = 16;
  }
  public void draw(){
    fill(236, 0, 140);
    rect(pos.x, pos.y, widthL, heightL);
  }

  //Returns the width, the height, a position vector and type
  public float getWidth(){
      return this.widthL;
  }

  public float getHeight(){
      return this.heightL;
  }

  public PVector getPosition(){
      return pos;
  }

  public String getType(){
      return this.type;
  }
  
}
class Platform extends Collidable{
  PVector pos;
  int widthP, heightP;
  String type = "platform";
  
  Platform(int x, int y){
    this.pos = new PVector(x, y);
    widthP = 16;
    widthP = 16;
  }
  
  public void draw(){
    fill(0xffA5A5A5);
    rect(pos.x, pos.y, widthP, heightP);
  }

  //Returns the width, the height and a position vector

  public float getWidth(){
      return this.widthP;
    }

  public float getHeight(){
      return this.heightP;
    }

  public PVector getPosition(){
      return pos;
    }

    public String getType(){
        return this.type;
      }
}
class Player extends Collidable{
  public PVector pos, vel, jump;
  String type = "player";
  float widthP, heightP, accel, grav;
  int jumpSpeed, walkSpeed, left, right;
	boolean isJumping = false;

  Player(float x, float y){
    this.pos = new PVector(x, y);
    this.vel = new PVector(4, 0);
    this.grav = 0.5f;
		this.walkSpeed = 6;
    this.jump = new PVector(0, -20);
    this.jumpSpeed = 8;
    this.widthP = 16;
    this.heightP = 16;
  }

  public void draw(){
    fill(0xff466CB7);
    rect(pos.x, pos.y, widthP, heightP);
  }


  public void update(boolean isGrounded, boolean right, boolean left, boolean jump){
    if(!isGrounded)  vel.y += grav;
    else vel.y = 0;

    if(right) moveRight();
    if(left) moveLeft();
    if(jump) {
			isJumping = true;
	    if(isGrounded){
	      this.jump();
	      isGrounded = false;
	    }
	    isJumping = false;
		}

		vel.x = walkSpeed * (this.left + this.right);
		pos.add(vel);
  }

//Adds vel to posplacement to move right
  public void moveRight(){
    pos.add(vel);
  }

//Adds negative x vel to posplacement to move Left
  public void moveLeft(){
    PVector temp = new PVector();
    temp.x = vel.x * -1;
    temp.y = vel.y;
    pos.add(temp);
  }

//Handels Jumping
  public void jump(){
    this.vel.y = -jumpSpeed;
    this.pos.y -= 1;
  }


//Returns the width, the height and a position vector

  public float getWidth(){
    return this.widthP;
  }

  public float getHeight(){
    return this.heightP;
  }

  public PVector getPosition(){
    return pos;
  }

  public String getType(){
    return this.type;
  }




}
class Wall extends Collidable{
  String type = "wall";
  float x, y;
  float widthW, heightW;

  Wall(float x, float y, float widthW, float heightW){
    this.x = x;
    this.y = y;
    this.widthW = widthW;
    this.heightW = heightW;
  }

  public void draw(){
    fill(0xffA5A5A5);
    rect(x, y, widthW, heightW);
  }

  //Returns the width, the height and a position vector

  public float getWidth(){
    return this.widthW;
  }

  public float getHeight(){
    return this.heightW;
  }

  public PVector getPosition(){
    return new PVector(x, y);
  }

  public String getType(){
      return this.type;
    }




}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "MainGame" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
