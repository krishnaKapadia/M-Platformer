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

public class M-Platformer extends PApplet {
  public void setup() {
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
  String type = "key";
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
    noLoop();
  }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "M-Platformer" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
