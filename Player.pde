class Player extends Collidable{
  public PVector pos, vel, jump, midJump;
  String type = "player";
  float widthP, heightP, accel, grav;
  int jumpSpeed, walkSpeed, left, right, jumpNum;
	boolean isJumping = false;

  Player(float x, float y){
    this.pos = new PVector(x, y);
    this.vel = new PVector(4, 0);
    this.grav = 0.5;
		this.walkSpeed = 6;
    this.jump = new PVector(0, -8);
    this.midJump = new PVector(0, -8);
    this.jumpSpeed = 10;
    this.widthP = 16;
    this.heightP = 16;
  }

  void draw(){
    fill(#466CB7);
    rect(pos.x, pos.y, widthP, heightP);
  }


  void update(boolean isGrounded, boolean right, boolean left, boolean jump){
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
  void moveRight(){
    pos.add(vel);
  }

//Adds negative x vel to posplacement to move Left
  void moveLeft(){
    PVector temp = new PVector();
    temp.x = vel.x * -1;
    temp.y = vel.y;
    pos.add(temp);
  }

//Handels Jumping and double jumping
  void jump(){
    this.jumpNum++;
    if(jumpNum > 1) this.vel.add(this.midJump);
    else this.vel.add(this.jump);
    //this.vel.y = -jumpSpeed;
    this.pos.y -= 1;
  }
  
  //Resets jump number, double jumping helper method
  void resetJump(){
    this.jumpNum = 0;
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