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

   void draw(){
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
   void move(String dir){
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
  float getWidth(){
      return this.widthB;
    }

  float getHeight(){
      return this.heightB;
    }

  String getType(){
      return this.type;
    }

  PVector getPosition(){
      return pos;
    }

}