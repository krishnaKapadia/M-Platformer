class Key extends Collidable{
  PVector pos;
  String type = "key";
  int widthK, heightK;
  boolean active = true;
  PImage unobtained, obtained;

  Key(int x, int y){
    pos = new PVector(x, y);
    widthK = 16;
    heightK = 16;
    unobtained = loadImage("assets/Key.png");
    obtained = loadImage("assets/KeyObtained.png");
  }

  void draw(){
    if(active) image(unobtained, pos.x, pos.y);
    else image(obtained, pos.x, pos.y);
  }

  void notActive(){
    active = false;
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

  PVector getPosition(){
      return pos;
    }

}
