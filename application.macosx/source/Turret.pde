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

   void draw(){
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
   void fire(){
      if(bullets.size() < 1) bullets.add(new Bullet(bulletSpawn));
   }

   //Returns the array of bullets
   ArrayList getBullets(){
      return this.bullets;
   }

   //Returns the width, the height, a position vector and the type of the object
   float getWidth(){
       return this.widthT;
     }

   float getHeight(){
       return this.heightT;
     }

   String getType(){
       return this.type;
     }

   PVector getPosition(){
       return pos;
     }

}