class Lava extends Collidable{
  String type = "lava";
  float widthL, heightL;
  PVector pos;
  
  Lava(int x, int y){
    this.pos = new PVector(x, y);
    widthL = 16;
    heightL = 16;
  }
  void draw(){
    fill(236, 0, 140);
    rect(pos.x, pos.y, widthL, heightL);
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