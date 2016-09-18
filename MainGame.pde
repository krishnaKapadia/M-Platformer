float frame;
long coinTime;
float time;
Player player;
ArrayList<Wall> objects;
ArrayList[] walls;
Coin[] coins;
boolean gameOver = false;
boolean gameLoop, keyPressed, isGrounded, isJumping, keyA, keyW, keyD;
PImage level;
PVector previous, spawnPoint;
Collidable[][] levelObjects;
float gravity = 0.5;
Gate gate;
Key gateKey;

void setup(){
  frameRate(60);
  fullScreen();
  noStroke();

  println(width + " - " + height);

  frame = 0;

  level = loadImage("assets/levels/Level1.png");

//Creates level from image
  this.createLevel();
  this.populateWalls();

//Creates a new player in the bottom left of the screen
  player = new Player(spawnPoint.x, spawnPoint.y);
  isGrounded = false;
  isJumping = false;
}

void draw(){
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
void updateGravity(){
  player.vel.y += gravity;
  player.pos.y += player.vel.y;
}

//Draws the level
void drawLevel() {
  for(int y = 0; y < levelObjects[0].length; y++){
    for(int x = 0; x < levelObjects.length; x++){
      if(levelObjects[x][y] != null){
        if(levelObjects[x][y].getType().equals("goal")) this.sample32(x, y, "goal");
        else if(levelObjects[x][y].getType().equals("gate")) this.sample32(x, y, "gate");
        else if(levelObjects[x][y].getType().equals("key")) {this.gateKey = (Key) levelObjects[x][y]; levelObjects[x][y].draw();}
        else levelObjects[x][y].draw();
      }
    }
  }
}

//Creates the level from the file
void createLevel(){
  levelObjects = new Collidable[this.width / 16][this.height / 16];

  for(int y = 0; y < levelObjects[0].length; y++){
    for(int x = 0; x < levelObjects.length; x++){
      PImage c = level.get(x * 16, y * 16, 16, 16);
      color pixel = c.get(8, 8);

      float r = red(pixel);
      float g = green(pixel);
      float b = blue(pixel);

      color wall = color(35, 31, 32);
      color coin = color(255, 242, 0); // 248.0, 239.0, 36.0 //255, 222, 23
      color door = color(0, 174, 239); // 40.0, 172.0, 226.0
      color gate = color(0, 166, 81);
      color spawn = color(46.0, 49.0, 146.0); //41.0, 60.0, 146.0
      color lava = color(236, 0, 140);
      color movingPlatform = color(139.0, 94.0, 60.0); //139.0, 94.0, 59.0
      color gateKey = color(247.0, 148.0, 30.0); //247.0, 148.0, 30.0

      // if((r != 0.0 || g != 0.0 || b != 0.0 ) && (r != 35.0 || g != 31.0 || b != 32.0) && (r != 0.0 || g != 174.0 || b != 239.0)
      // && (r != 236.0 || g != 0 || b != 140 ) && (r != 248.0 || g != 239 || b != 36 ) && (r != 139.0 || g != 94 || b != 59 )
      // && (r != 40.0 || g != 172 || b != 226 ) && (r != 41.0 || g != 60 || b != 146 ))
      // if((r != 0.0 || g != 0.0 || b != 0.0 ))
      // println(r + ", " + g + ", " + b);

      if(pixel == wall) levelObjects[x][y]                = new Wall(x * 16, y * 16, 16, 16);
      else if(pixel == door) levelObjects[x][y]           = new Goal(x * 16, y * 16);
      else if(pixel == coin) levelObjects[x][y]           = new Coin(x * 16, y * 16);
      else if(pixel == spawn) spawnPoint                  = new PVector(x * 16, y * 16);
      else if(pixel == gate) levelObjects[x][y]           = new Gate(x * 16, y * 16);
      else if(pixel == lava) levelObjects[x][y]           = new Lava(x * 16, y * 16);
      else if(pixel == movingPlatform) levelObjects[x][y] = new Platform(x * 16, y * 16);
      else if(pixel == gateKey) levelObjects[x][y]        = new Key(x * 16, y * 16);
    }
  }
}

//Takes a given postition, draws the object in a 32 x 32 box
void sample32(int x, int y, String type){
  if(levelObjects[x + 1][y] != null && levelObjects[x][y + 1] != null && levelObjects[x + 1][y + 1] != null){
    if(levelObjects[x + 1][y].getType().equals(type) && levelObjects[x][y + 1].getType().equals(type)
    && levelObjects[x + 1][y + 1].getType().equals(type)){
      levelObjects[x][y].draw();
      if(levelObjects[x][y].getType().equals("gate")) gate =(Gate) levelObjects[x][y];
    }
  }
}

//Populates the walls array with the position of walls
void populateWalls(){
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
void playerCollisions(){

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
        //Gate Key Collision
        if(tile.getType().equals("key")){
          gate.openGate();
          gateKey.notActive();
        }

        if(tile.getType().equals("gate")){
          if(!gate.isOpen) player.pos = previous;
        }

  		}

  	}
  }

}

void wallCollision(Collidable tile){
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
    //player.vel.y *= -1;
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
void keyStrokes(){
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
void keyPressed() {
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
void keyReleased(){
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
void createTimer(){
  float length = (float) (width - 40 * 2) - time / 100 + coinTime;
  if(length > width - 40 * 2) length = width - 40 * 2;

  PFont f = loadFont("Carlito-14.vlw");
  createFont("Century Gothic",14);

  if(length > 0){
    fill(#466CB7);
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
