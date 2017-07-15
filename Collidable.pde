abstract class Collidable{

  abstract float getWidth();
  abstract float getHeight();
  abstract PVector getPosition();
  abstract String getType();
  abstract void draw();

  //Returns wether an object is colliding with another
   boolean isColliding(Collidable other) {
    if (this.getPosition().x + getWidth() > other.getPosition().x && this.getPosition().x < other.getPosition().x + other.getWidth()
    && this.getPosition().y + getHeight() > other.getPosition().y && this.getPosition().y < other.getPosition().y + other.getHeight())
    return true;
    return false;
  }

  //Calculates projection vector to reset position after collision
  PVector calcProjection(Collidable other){
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
