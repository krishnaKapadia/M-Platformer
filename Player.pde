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
    this.grav = 0.8;
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

  void draw(){
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
   void idleAnimation(){
     image(idle[(frame) % 4], pos.x, pos.y);
     if(millis() - lastTime >= delay){
       frame = (frame + 1) % 4;  // Use % to cycle through frames
       lastTime = millis();
     }
   }

   void runRightAnimation(){
     image(runRight[(frame) % 4], pos.x, pos.y + 2);
     if(millis() - lastTime >= delay - 20){
       frame = (frame + 1) % 4;  // Use % to cycle through frames
       lastTime = millis();
     }
   }

   void runLeftAnimation(){
     image(runLeft[(frame) % 4], pos.x - 15, pos.y + 2);
     if(millis() - lastTime >= delay - 20){
       frame = (frame + 1) % 4;  // Use % to cycle through frames
       lastTime = millis();
     }
   }

   void activeLeft(){
     this.left = true;
   }

   void activeRight(){
     this.right = true;
   }

   void deactivateLeft(){
     this.left = false;
   }

   void deactivateRight(){
     this.right = false;
   }

  //Sets the player to be dead
  void dead(){
   this.alive = false;
  }

//Adds vel to posplacement to move right
  void moveRight(){
    pos.x += vel.x;
   }

//Adds negative x vel to posplacement to move Left
  void moveLeft(){
    pos.x -= vel.x;
   }

//Handels Jumping and double jumping
  void jump(){
    this.jumpNum++;
    isJumping = true;

    this.vel.y = this.jump.y;

    this.pos.y -= 1;
    pos.add(jump);
   }

  //Resets jump number, double jumping helper method
  void resetJump(){
    this.jumpNum = 0;
    isJumping = false;
   }

//Returns the width, the height, type and a position vector
  float getWidth(){
    return this.widthP;
   }

  float getHeight(){
    return this.heightP;
   }

  PVector getPosition(){
    return pos;
   }

  String getType(){
    return this.type;
   }

}
