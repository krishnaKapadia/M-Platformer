class Gate extends Collidable{
  PVector pos;
  int widthG, heightG;
  String type = "gate";
  PImage sprite;
  boolean isOpen = false;

  Gate(int x, int y){
    sprite = loadImage("assets/Gate.png");
    pos = new PVector(x, y);
    widthG = 32;
    heightG = 32;
  }

  void draw(){
   image(sprite, pos.x , pos.y);
  }

  void openGate(){
    this.isOpen = true;
  }

  //Returns the width, the height, type and a position vector

  float getWidth(){
      return this.widthG;
    }

  float getHeight(){
      return this.heightG;
    }

  PVector getPosition(){
      return pos;
    }

    String getType(){
        return this.type;
      }

}
