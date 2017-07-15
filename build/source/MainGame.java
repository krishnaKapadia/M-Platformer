import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class MainGame extends PApplet {

float frame, time;
long coinTime;
Player player;
ArrayList<Wall> objects;
ArrayList[] walls;
ArrayList<BlueBot> blueBots = new ArrayList<BlueBot>();
ArrayList<Turret> turrets = new ArrayList<Turret>();
Coin[] coins;
Shard[] explosion;
boolean gameOver = false;
boolean gameLoop, keyPressed, isGrounded, isJumping, keyA, keyW, keyD, dead, collided, gameWon, menu = true;
PImage level, menuImage, background;
boolean menuActive = true;
PVector previous, spawnPoint;
Collidable[][] levelObjects;
float gravity = 0.8f;
Gate gate;
Key gateKey;
int currentLevel = 1;
int currentFrame;
public PImage wallSprite;

public void setup(){
  frameRate(60);
  
  //Sort out issues when using P2D such as often frame skips/pauses
  //fullScreen();
  noStroke();
  println(width + " - " + height);
  frame = 0;
  gameWon = false;
  gameOver = false;
  level = loadImage("assets/levels/Level" + currentLevel + ".png");
  wallSprite  = loadImage("assets/levels/floorTile.png");
  menuImage = loadImage("assets/menu.png");
  this.restart();
  background = loadImage("assets/levels/Background.png");
}

public void draw(){
   //Init
	background(200);
   // Draws the Level and menu if menu is active
   if(menu){
      image(menuImage, 0, 0);
   }else
   if(gameWon) this.displayWinScreen();
   else if(gameOver) {
      this.displayGameOverScreen();
   }
   else{
      image(background, 0, 0);
   //Draws the level
      this.drawLevel();
   //Player
      player.draw();
      previous = player.getPosition().copy();
   //Timer create
      this.createTimer();
   //Gravity
      this.updateGravity();
   //KeStroke handeling
      this.keyStrokes();
   //Collision handeling
      this.collisions();
   //Enemy Movements and protocols
      this.enemyMovements();
   //Handels particle effects
      this.particleEffects();
     }
   //frameRate counter
   text(frameRate, width - 50, 50);
}

//Displays the game over screen
public void displayGameOverScreen(){
   PImage lose = loadImage("assets/levels/GameOver.png");
   image(lose, 0, 0);
}
//Displays the win screen
public void displayWinScreen(){
  PImage win = loadImage("assets/levels/gameWon.png");
  image(win, 0, 0);
}

//Handels all game particle effects
public void particleEffects(){
  if(dead) {
    //Draws them
    for(int i = 0; i < explosion.length; i++){
      if(explosion[i] != null){
        explosion[i].draw();
        PVector sPos = explosion[i].getPosition();
        if(sPos.x > width || sPos.y > height) explosion[i] = null;
      }
    }
  }
  if(this.noShards()) this.restart();
}

//Returns if no shards are on screen
public boolean noShards(){
  for(int i = 0; i < explosion.length; i++){
      if(explosion[i] == null) return true;
  }
  return false;
}

//Handels all enemy Ai protocols
public void enemyMovements(){
   //Blue bot movement
   this.moveBot();

   //Turret
   for(Turret t : turrets){
      if(dist(player.pos.x, player.pos.y, t.getPosition().x, t.getPosition().y) < 250) t.fire();
   }
}

//Moves the bots
public void moveBot(){
   for(int y = 0; y < levelObjects[0].length; y++){
     for(int x = 0; x < levelObjects.length; x++){
        Collidable tile = levelObjects[x][y];
        if(tile != null && tile.getType().equals("blueBot")){
           BlueBot current = (BlueBot) levelObjects[x][y];
           if(levelObjects[x][y + 2] != null){
              current.move("left");
           }
        }
     }
  }
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
      //stroke(0);
      if(levelObjects[x][y] != null){
        if(levelObjects[x][y].getType().equals("goal")) this.sample32(x, y, "goal");
        else if(levelObjects[x][y].getType().equals("blueBot")) this.sample32(x, y, "blueBot");
        else if(levelObjects[x][y].getType().equals("key")) { this.gateKey = (Key) levelObjects[x][y]; levelObjects[x][y].draw(); }
        else levelObjects[x][y].draw();
      }
    }
  }

  //Draw other objects, generally these are objects larger than 16 x 16 that are not stored in the levelObjects array
  if(gate != null) gate.draw();
}

//Creates a partical explosion
public void explode(){
  dead = true;
}

//Redraws all game objects
public void restart(){
  //Clears pervious level
  if(levelObjects != null){
     for(int y = 0; y < levelObjects[0].length; y++){
      for(int x = 0; x < levelObjects.length; x++){
        levelObjects[x][y] = null;
      }
     }
  }
  gate = null;
  gateKey = null;

  turrets = new ArrayList<Turret>();
  blueBots = new ArrayList<BlueBot>();
   //Creates level from image
  this.createLevel();
  this.populateWalls();

  //Creates a new player in the bottom left of the screen
  player = new Player(spawnPoint.x, spawnPoint.y);

  //Resets the bullet arrays
  for(Turret t : turrets){
    t.bullets = new ArrayList<Bullet>();
  }

  //Resets the death particles
  explosion = new Shard[10];

  //populates the array
  for(int i = 0; i < explosion.length; i++){
    explosion[i] = new Shard(color(203, 8, 20), new PVector(player.pos.x, player.pos.y));
  }

  isGrounded = false;
  isJumping = false;
  collided = false;
  dead = false;
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
      int blueBot = color(0, 104, 56);
      int turret = color(158, 31, 99);

      // if((r != 0.0 || g != 0.0 || b != 0.0 ) &&  (r != 0.0 || g != 174.0 || b != 239.0)
      // && (r != 236.0 || g != 0 || b != 140 ) && (r != 248.0 || g != 239 || b != 36 ) && (r != 139.0 || g != 94 || b != 59 )
      // && (r != 40.0 || g != 172 || b != 226 ) && (r != 41.0 || g != 60 || b != 146 ))
      // if((r != 0.0 || g != 0.0 || b != 0.0 ))
      // println(r + ", " + g + ", " + b);

      if(pixel == wall) levelObjects[x][y]                      = new Wall(x * 16, y * 16, 16, 16);
      else if(pixel == door) levelObjects[x][y]                 = new Goal(x * 16, y * 16);
      else if(pixel == coin) levelObjects[x][y]                 = new Coin(x * 16, y * 16);
      else if(pixel == spawn) spawnPoint                        = new PVector(x * 16, y * 16);
      else if(pixel == lava) levelObjects[x][y]                 = new Lava(x * 16, y * 16);
      else if(pixel == movingPlatform) levelObjects[x][y]       = new Platform(x * 16, y * 16);
      else if(pixel == gateKey) levelObjects[x][y]              = new Key(x * 16, y * 16);
      else if(pixel == blueBot) levelObjects[x][y]              = new BlueBot(x * 16, y * 16);
      else if(pixel == gate){ if(this.gate == null) {this.gate  = new Gate(x * 16, y * 16);}}
      else if(pixel == turret) {
         levelObjects[x][y] = new Turret(x * 16, y * 16);
         turrets.add((Turret) levelObjects[x][y]);
      }
    }
  }
}

//Takes a given postition, draws the object in a 32 x 32 box
public void sample32(int x, int y, String type){
  if(levelObjects[x + 1][y] != null && levelObjects[x][y + 1] != null && levelObjects[x + 1][y + 1] != null){
    if(levelObjects[x + 1][y].getType().equals(type) && levelObjects[x][y + 1].getType().equals(type)
    && levelObjects[x + 1][y + 1].getType().equals(type)){
      levelObjects[x][y].draw();
      if(levelObjects[x][y].getType().equals("gate")) gate = (Gate) levelObjects[x][y];
    }
  }
}

//Populates the walls array with the position of walls
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
        Wall wall = (Wall) current;
        if(levelObjects[x][y - 1] == null || levelObjects[x][y - 1].getType() == "coin" || levelObjects[x][y - 1].getType() == "key" || levelObjects[x][y - 1].getType() == "lava" || levelObjects[x][y - 1].getType() == "goal") {
          wall.sprite = loadImage("assets/levels/floorTile2.png");
          walls[0].add(current); //Floor
        } else if(levelObjects[x][y + 1] == null) walls[1].add(current); //Top
        else if(levelObjects[x + 1][y] == null) walls[2].add(current); //Left
        else if(levelObjects[x - 1][y] == null) walls[3].add(current); //Right
      }

    }
  }
}

//Handels the collisions of the player to the walls and coins, also bullet collisions
public void collisions(){
  int checkBoundX = (int) Math.floor(player.pos.x / 16.0f);
  int checkBoundY = (int) Math.floor(player.pos.y / 16.0f);

  for(int y = levelObjects[0].length - 2; y > 1; y--){
  	for(int x = 1; x < levelObjects.length ; x++){
  		Collidable tile = levelObjects[x][y];

      //Handeles bullet collisions on walls and the player
      if(tile != null && tile.getType().equals("wall")){
        for(Turret t : turrets){
          for(int i = 0; i < t.bullets.size(); i++){
                //collides with wall
            if(t.bullets.get(i).isColliding(tile)){
              t.bullets.remove(i);
            }else{
               //Collides with player
                if(t.bullets.get(i).isColliding(player)){
                  for(Shard s : explosion){
                     s.updatePos(new PVector(player.pos.x, player.pos.y));
                   }

                   player.dead();
                   t.bullets.get(i).explode();
                   t.bullets.remove(i);
                   if(!collided) dead = true;
                   collided = true;
                   break;
                 }
             }
          }
        }
      }

      //Player Collisions
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

        //Gate Key Collision
        if(tile.getType().equals("key")){
          gate = null;
          gateKey.notActive();
        }

        //Mine collision
        if(tile.getType().equals("lava")) {
          for(Shard s : explosion){
            s.updatePos(new PVector(player.pos.x, player.pos.y));
          }
          player.dead();
          if(!collided) dead = true;
          collided = true;
        }

        //Goal Collision
        if(tile.getType().equals("goal")) {
          if(currentLevel != 9){
            currentLevel++;
            this.setup();
          }else{
           gameWon = true;
         }
        }

  		}
  	}
  }

  //Gate Collision Checks
  if(gate != null && player.isColliding(gate)){
     player.pos = previous;
  }

}

//Handels all the wall collisions
public void wallCollision(Collidable tile){
  PVector tilePos = tile.getPosition();
  PVector pos = player.getPosition();

  //Floor
  if(walls[0].contains(tile)){
    isGrounded = true;
    isJumping = false;
    player.vel.y = 0;
    player.pos.y = tilePos.y - player.getHeight();
    player.resetJump();
  }

  //Ceiling
  if(walls[1].contains(tile)){
    player.vel.y = 0;
    if(player.pos.x + player.getWidth() > tilePos.x && player.pos.x < tilePos.x && player.pos.y < tilePos.y && player.pos.y + player.getHeight() > tilePos.y){
      player.pos.x -= pos.x + player.getWidth() - tilePos.x;
      keyD = false;
    }
   //  else if(player.pos.x < tilePos.x + tile.getWidth() && player.pos.x + player.getWidth() > tilePos.x + tile.getWidth() && player.pos.y < tilePos.y + tile.getHeight()
   //  && player.pos.y + player.getHeight() > tilePos.y + tile.getHeight() && keyA){
   //    player.pos.x += tilePos.x + tile.getWidth() - player.pos.x;
   //    keyA = false;
   //  }
    else player.pos.y = tilePos.y + tile.getHeight();
  }

  //Left Wall
  if(walls[2].contains(tile)){
    player.pos.x = tilePos.x + tile.getWidth();
    keyA = false;
  }else

  //Right Wall
  if(walls[3].contains(tile)){
    player.pos.x = tilePos.x - player.getWidth();
    keyD = false;
  }
}

//Checks if a key has been pressed then executes the corresponding action
public void keyStrokes(){
  if(keyA) player.moveLeft();
  if(keyD) player.moveRight();

  if(keyW && isJumping && player.jumpNum == 1){
    if(player.jumpNum < 2) player.jump();
  }

  if(keyW) {
    if(isGrounded){
      isJumping = true;
      player.jump();
      isGrounded = false;
    }
    keyW = false;
  }
}

//Sets the keyPressed Boolean to true;
public void keyPressed() {
  keyPressed = true;
  switch(key){
      case 'd':
         keyD = true;
         player.activeRight();
         break;

      case 'a':
         keyA = true;
         player.activeLeft();
         break;

      case ' ':
         keyW = true;
         break;

      case 'z':
         keyW = true;
         break;

      case 'p':
        if(gameWon){
          currentLevel = 1;
          gameWon = false;
          this.setup();
        }else if(menu){
          currentLevel = 1;
          menu = false;
          this.setup();
        }
      //   else if(gameOver){
      //     currentLevel = 1;
      //     gameOver = false;
      //     this.setup();
      //   }
        break;
  }

  switch(keyCode){
      case RIGHT:
         keyD = true;
         player.activeRight();
         break;

      case LEFT:
         keyA = true;
         player.activeLeft();
         break;

      case UP:
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
         player.deactivateLeft();
         break;

      case 'a':
         keyA = false;
         player.deactivateRight();
         break;

      case ' ':
         keyW = false;
         break;

      case 'z':
        keyW = false;
        break;
  }

  switch(keyCode){
      case RIGHT:
         keyD = false;
         player.deactivateLeft();
         break;

      case LEFT:
         keyA = false;
         player.deactivateRight();
         break;

      case UP:
         keyW = false;
         break;
   }
}

//Creates the timer bar
public void createTimer(){
   time = millis();
   frame++;
   //float length = (float) (width - 40 * 2);
   float length = (float) (width - 40 * 2) - time / 100 + coinTime;
   if(length > width - 40 * 2) length = width - 40 * 2;

   PFont f = loadFont("Carlito-14.vlw");

   if(length > 0){
       fill(0xff466CB7);
       rect(40, 40 / 2 - 10, length, 40 / 2);

       textFont(f,14);
       fill(255);
       text(round(length) , length, 40 / 3 + 12);

   }else if(!gameOver){
    gameOver = true;
   }
}
class BlueBot extends Collidable{
   PVector pos, vel;
   int widthB, heightB, speed;
   PImage sprite;
   String type = "blueBot";
   String dir = "left";

   BlueBot(int x, int y){
      this.pos = new PVector(x, y);
      this.vel = new PVector(0, 0);
      this.speed = 1;
      this.widthB = 32;
      this.heightB = 32;
      sprite = loadImage("assets/enemies/blueBot/blueBot.png");
   }

   public void draw(){
      //rect(pos.x, pos.y, 16, 16);
      if(dir.equals("left")){
         pushMatrix();
         translate(pos.x, pos.y);
         rotate(radians(90));
         image(sprite, pos.x, pos.y);
         popMatrix();
      }else {
         image(sprite, pos.x, pos.y);
      }

   }

   //Handels AI movement
   public void move(String dir){
      this.dir = dir;
      int temp = speed * -1;
      switch(dir){
         case "left":
            this.vel.x = temp;
            this.vel.y = 0;
            pushMatrix();
            rotate(radians(90));
            popMatrix();
            break;

         case "down":
            this.vel.y = speed;
            this.vel.x = 0;
            break;

         case "right":
            this.vel.x = speed;
            this.vel.y = 0;
            pushMatrix();
            rotate(radians(90));
            popMatrix();
            break;

         case "up":
            this.vel.y = temp;
            this.vel.x = 0;
            break;
      }
      this.pos.add(vel);
   }


//Returns the width, the height, a position vector and the amount of time that is added on pickups
  public float getWidth(){
      return this.widthB;
    }

  public float getHeight(){
      return this.heightB;
    }

  public String getType(){
      return this.type;
    }

  public PVector getPosition(){
      return pos;
    }

}
class Bullet extends Collidable{
   PVector pos;
   int widthB, heightB;
   String type = "bullet";
   int speed = 2;
   //PImage sprite = loadImage("/assets/Missle.png");
   boolean dead = false;
   Shard[] explosion = new Shard[10];

   Bullet(PVector spawn){
      pos = new PVector(spawn.x, spawn.y);
      widthB = 8;
      heightB = 8;

      for(int i = 0; i < explosion.length; i++){
        explosion[i] = new Shard(color(29, 175, 236), new PVector(player.pos.x, player.pos.y));
      }

   }

   public void draw(){
      if(!dead){
         float angle = atan2((player.getPosition().y + player.getHeight() / 2) - pos.y + heightB, (player.getPosition().x + player.getWidth() / 2) - pos.x + widthB);
         float newX = cos(angle) * speed + pos.x;
         float newY = sin(angle) * speed + pos.y;
         pos.set(newX, newY);
         fill(88, 89, 91);
         rect(pos.x, pos.y, widthB, heightB);
      }else{
        for(Shard s: explosion){
          s.updatePos(this.pos);
        }
         this.explode();
      }
   }

   public void explode(){
     dead = true;
   }

   //Returns the width, the height, a position vector and the type of the object
   public float getWidth(){
       return this.widthB;
     }

   public float getHeight(){
       return this.heightB;
     }

   public String getType(){
       return this.type;
     }

   public PVector getPosition(){
       return pos;
     }
}
class Coin extends Collidable{
  String type = "coin";
  float widthC, heightC, addTime, delay;
  int numFrames, currentFrame, lastTime;
  PImage[] images;
  PVector pos;

  Coin(float x, float y){
    pos = new PVector(x, y);
    this.widthC = 16;
    this.heightC = 16;
    this.addTime = 10;

    //Sprite animation
    delay = 90;
    numFrames = 10;  // The number of frames in the animation, # of images
    currentFrame = 0;

    images = new PImage[numFrames];
    for(int i = 0; i < numFrames; i++){ //Populates the array with the images
      images[i] = loadImage("assets/coin/coin" + (i) + ".png");
    }

    lastTime = millis();

  }

  public void draw(){

   image(images[(currentFrame) % numFrames], pos.x - images[0].width / 2, pos.y - images[0].height / 2);

   if(millis() - lastTime >= delay){
      currentFrame = (currentFrame + 1) % numFrames;  // Use % to cycle through frames
      lastTime = millis();
    }
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

  //Returns wether an object is colliding with another
   public boolean isColliding(Collidable other) {
    if (this.getPosition().x + getWidth() > other.getPosition().x && this.getPosition().x < other.getPosition().x + other.getWidth()
    && this.getPosition().y + getHeight() > other.getPosition().y && this.getPosition().y < other.getPosition().y + other.getHeight())
    return true;
    return false;
  }

  //Calculates projection vector to reset position after collision
  public PVector calcProjection(Collidable other){
     PVector pos = this.getPosition();
     PVector otherPos = other.getPosition();
     float minDY = 0;
     float minDX = 0;

     if(pos.x + this.getWidth() > otherPos.x && pos.x < otherPos.x && pos.y + this.getHeight() > otherPos.y && pos.y < otherPos.y) {
        minDY = (otherPos.y) - (pos.y + this.getHeight());
        minDX = 0;
     }

     if(pos.x + this.getWidth() > otherPos.x && pos.y < otherPos.y){
        minDY = (otherPos.y + other.getHeight()) - (pos.y);
        minDX = 0;
        println("corner");
     }

     return new PVector(minDX, minDY);
   //   if(this.isColliding(other)){
   //      PVector pos = this.getPosition();
   //      PVector otherPos = other.getPosition();
   //
   //      if(pos.x > otherPos.x)
     //
   //      PVector proj = new PVector(this.getPosition().x - other.getPosition().x, this.getPosition().y - other.getPosition().y);
   //      return proj;
   //   }
   //   return new PVector(0, 0);
 }

}
class Gate extends Collidable{
  PVector pos;
  int widthG, heightG;
  String type = "gate";
  PImage sprite;
  boolean isOpen = false;

  Gate(int x, int y){
    sprite = loadImage("assets/Gate.png");
    pos = new PVector(x, y);
    widthG = 32;
    heightG = 32;
  }

  public void draw(){
   image(sprite, pos.x , pos.y);
  }

  public void openGate(){
    this.isOpen = true;
  }

  //Returns the width, the height, type and a position vector

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
  PImage[] images;
  int currentFrame;

  Goal(float x, float y){
    pos = new PVector(x, y);
    this.widthG = 16;
    this.heightG = 16;

    images = new PImage[32];
    for(int i = 0; i < images.length; i++){ //Populates the array with the images
      images[i] = loadImage("assets/portal/Portal" + (i + 1) + ".png");
      images[i].resize(32, 0); //resize image to 32x32
    }
    
  }

  public void draw(){
    currentFrame = (currentFrame + 1) % 32;
    image(images[(currentFrame) % 32], pos.x, pos.y);
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
  String type = "key";
  int widthK, heightK;
  boolean active = true;
  PImage unobtained, obtained;

  Key(int x, int y){
    pos = new PVector(x, y);
    widthK = 16;
    heightK = 16;
    unobtained = loadImage("assets/Key.png");
    obtained = loadImage("assets/KeyObtained.png");
  }

  public void draw(){
    if(active) image(unobtained, pos.x, pos.y);
    else image(obtained, pos.x, pos.y);
  }

  public void notActive(){
    active = false;
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

  public PVector getPosition(){
      return pos;
    }

}
class Lava extends Collidable{
  String type = "lava";
  float widthL, heightL;
  PVector pos;
  PImage sprite;

  Lava(int x, int y){
    this.pos = new PVector(x, y);
    widthL = 16;
    heightL = 16;
    sprite = loadImage("assets/hazards/Mine.png");

  }

  public void draw(){
   image(sprite, pos.x, pos.y);
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
  int jumpSpeed, walkSpeed, jumpNum, frame = 0, lastTime, delay;
  boolean isJumping = false, alive, left, right;
  PImage sprite;
  PImage[] idle, runLeft, runRight, jumpImage;

  Player(float x, float y){
    this.right = false;
    this.left = false;
    this.pos = new PVector(x, y);
    this.vel = new PVector(6, 0);
    this.grav = 0.8f;
    this.walkSpeed = 6;
    this.jump = new PVector(0, -11);
    this.jumpSpeed = 8;
    this.widthP = 30; //20
    this.heightP = 32; //20
    sprite = loadImage("assets/player.png");
    alive = true;
    delay = 180;
    lastTime = millis();

    //Idle animation
    idle = new PImage[4];
    for(int i = 0; i < idle.length; i++){
      idle[i] = loadImage("assets/player/idle/idle_" + i + ".png");
    }

    //Running Right animation
    runRight = new PImage[6];
    for(int i = 0; i < runRight.length; i++){
      runRight[i] = loadImage("assets/player/run/right/run_" + i + ".png");
    }

    //Running Left animation
    runLeft = new PImage[6];
    for(int i = 0; i < runLeft.length; i++){
      runLeft[i] = loadImage("assets/player/run/left/run_" + i + ".png");
    }

    //Jump image
    jumpImage = new PImage[2];
    jumpImage[0] = loadImage("assets/player/jump/jumpRight.png");
    jumpImage[1] = loadImage("assets/player/jump/jumpLeft.png");

  }

  public void draw(){
     if(alive){
       if(isJumping) {
          if(keyA) image(jumpImage[1], pos.x, pos.y);
          else image(jumpImage[0], pos.x, pos.y);
       }else if(keyD) this.runRightAnimation(); //Right
       else if(keyA) this.runLeftAnimation();  //Left
       else {
         this.idleAnimation();
       }
     }else {
       widthP = 0;
       heightP = 0;
       pos = new PVector(0, 0);
     }
   }

   //Animation Handels
   public void idleAnimation(){
     image(idle[(frame) % 4], pos.x, pos.y);
     if(millis() - lastTime >= delay){
       frame = (frame + 1) % 4;  // Use % to cycle through frames
       lastTime = millis();
     }
   }

   public void runRightAnimation(){
     image(runRight[(frame) % 4], pos.x, pos.y + 2);
     if(millis() - lastTime >= delay - 20){
       frame = (frame + 1) % 4;  // Use % to cycle through frames
       lastTime = millis();
     }
   }

   public void runLeftAnimation(){
     image(runLeft[(frame) % 4], pos.x - 15, pos.y + 2);
     if(millis() - lastTime >= delay - 20){
       frame = (frame + 1) % 4;  // Use % to cycle through frames
       lastTime = millis();
     }
   }

   public void activeLeft(){
     this.left = true;
   }

   public void activeRight(){
     this.right = true;
   }

   public void deactivateLeft(){
     this.left = false;
   }

   public void deactivateRight(){
     this.right = false;
   }

  //Sets the player to be dead
  public void dead(){
   this.alive = false;
  }

//Adds vel to posplacement to move right
  public void moveRight(){
    pos.x += vel.x;
   }

//Adds negative x vel to posplacement to move Left
  public void moveLeft(){
    pos.x -= vel.x;
   }

//Handels Jumping and double jumping
  public void jump(){
    this.jumpNum++;
    isJumping = true;

    this.vel.y = this.jump.y;

    this.pos.y -= 1;
    pos.add(jump);
   }

  //Resets jump number, double jumping helper method
  public void resetJump(){
    this.jumpNum = 0;
    isJumping = false;
   }

//Returns the width, the height, type and a position vector
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
class Shard {
  PVector pos, vel;
  int c;
  int size = 10;

  Shard(int c, PVector pos){
    this.pos = pos;
    this.c = c;
    this.vel = new PVector(random(-20, 20), random(-20, 20));
  }

  //Draws the shard and updates the position
  public void draw(){
    pos.add(vel);
    fill(c);
    rect(pos.x + 5, pos.y + 5, size, size);
  }

  public PVector getPosition(){
    return this.pos;
  }

  public void updatePos(PVector p){
    this.pos = p;
  }

}
class Slope{
   String type = "slope";
   PVector pos;
   float widthS, heightS;

   Slope(int x, int y){
      this.pos = new PVector(x, y);
      this.widthS = 16;
      this.heightS = 16;
   }



}
class Turret extends Collidable{
   PVector pos, bulletSpawn;
   ArrayList<Bullet> bullets;
   int widthT, heightT, rotation = 0;
   String type = "turret";
   PImage sprite;

   Turret(int x, int y){
      pos = new PVector(x, y);
      bulletSpawn = new PVector(x + 8, y + 8);
      widthT = 16;
      heightT = 16;
      sprite = loadImage("assets/enemies/Turret.png");
      bullets = new ArrayList<Bullet>();
   }

   public void draw(){
      pushMatrix();
      translate(pos.x, pos.y);
      ellipse(0, 0, 2, 2);
      rotate(radians(rotation));
      translate(-sprite.width/2, -sprite.height/2);
      image(sprite, 0, 0);
      popMatrix();
      rotation++;
      if(rotation >= 360) rotation = 0;
      for(Bullet b : bullets){
         b.draw();
      }
   }

   //Creates a new Bullet Object
   public void fire(){
      if(bullets.size() < 1) bullets.add(new Bullet(bulletSpawn));
   }

   //Returns the array of bullets
   public ArrayList getBullets(){
      return this.bullets;
   }

   //Returns the width, the height, a position vector and the type of the object
   public float getWidth(){
       return this.widthT;
     }

   public float getHeight(){
       return this.heightT;
     }

   public String getType(){
       return this.type;
     }

   public PVector getPosition(){
       return pos;
     }

}
class Wall extends Collidable{
  String type = "wall";
  float x, y;
  float widthW, heightW;
  public PImage sprite;

  Wall(float x, float y, float widthW, float heightW){
    this.x = x;
    this.y = y;
    this.widthW = widthW;
    this.heightW = heightW;
    //sprite = loadImage("assets/levels/floorTile.png");
    sprite = wallSprite;
  }

  public void draw(){
    image(sprite, x, y);
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
  public void settings() {  size(1280, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "MainGame" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
