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
float gravity = 0.8;
Gate gate;
Key gateKey;
int currentLevel = 1;
int currentFrame;
public PImage wallSprite;

void setup(){
  frameRate(60);
  size(1280, 800);
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

void draw(){
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
void displayGameOverScreen(){
   PImage lose = loadImage("assets/levels/GameOver.png");
   image(lose, 0, 0);
}
//Displays the win screen
void displayWinScreen(){
  PImage win = loadImage("assets/levels/gameWon.png");
  image(win, 0, 0);
}

//Handels all game particle effects
void particleEffects(){
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
boolean noShards(){
  for(int i = 0; i < explosion.length; i++){
      if(explosion[i] == null) return true;
  }
  return false;
}

//Handels all enemy Ai protocols
void enemyMovements(){
   //Blue bot movement
   this.moveBot();

   //Turret
   for(Turret t : turrets){
      if(dist(player.pos.x, player.pos.y, t.getPosition().x, t.getPosition().y) < 250) t.fire();
   }
}

//Moves the bots
void moveBot(){
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
void updateGravity(){
  player.vel.y += gravity;
  player.pos.y += player.vel.y;
}

//Draws the level
void drawLevel() {
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
void explode(){
  dead = true;
}

//Redraws all game objects
void restart(){
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
      color blueBot = color(0, 104, 56);
      color turret = color(158, 31, 99);

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
void sample32(int x, int y, String type){
  if(levelObjects[x + 1][y] != null && levelObjects[x][y + 1] != null && levelObjects[x + 1][y + 1] != null){
    if(levelObjects[x + 1][y].getType().equals(type) && levelObjects[x][y + 1].getType().equals(type)
    && levelObjects[x + 1][y + 1].getType().equals(type)){
      levelObjects[x][y].draw();
      if(levelObjects[x][y].getType().equals("gate")) gate = (Gate) levelObjects[x][y];
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
void collisions(){
  int checkBoundX = (int) Math.floor(player.pos.x / 16.0);
  int checkBoundY = (int) Math.floor(player.pos.y / 16.0);

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
void keyReleased(){
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
void createTimer(){
   time = millis();
   frame++;
   //float length = (float) (width - 40 * 2);
   float length = (float) (width - 40 * 2) - time / 100 + coinTime;
   if(length > width - 40 * 2) length = width - 40 * 2;

   PFont f = loadFont("Carlito-14.vlw");

   if(length > 0){
       fill(#466CB7);
       rect(40, 40 / 2 - 10, length, 40 / 2);

       textFont(f,14);
       fill(255);
       text(round(length) , length, 40 / 3 + 12);

   }else if(!gameOver){
    gameOver = true;
   }
}
