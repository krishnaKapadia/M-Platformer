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

  void draw(){
   image(sprite, pos.x, pos.y);
  }

  //Returns the width, the height, a position vector and type
  float getWidth(){
      return this.widthL;
  }

  float getHeight(){
      return this.heightL;
  }

  PVector getPosition(){
      return pos;
  }

  String getType(){
      return this.type;
  }

}
