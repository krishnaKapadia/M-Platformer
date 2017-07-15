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

   void draw(){
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

   void explode(){
     dead = true;
   }

   //Returns the width, the height, a position vector and the type of the object
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
