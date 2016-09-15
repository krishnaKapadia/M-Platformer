class Key extends Collidable{
  PVector pos;
  String type = "key";
  int widthK, heightK;

  Key(int x, int y){
    pos = new PVector(x, y);
    widthK = 16;
    heightK = 16;
  }

  void draw(){

  }

//Returns the width, the height, a position vector and the amount of time that is added on pickups
  float getWidth(){
      return this.widthK;
  }

  float getHeight(){
      return this.heightK;
  }

  String getType(){
      return this.type;
  }

  float getTimeAddition(){
      return this.addTime;
  }

  PVector getPosition(){
      return pos;
  }

}
