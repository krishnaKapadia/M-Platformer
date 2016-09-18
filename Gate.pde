class Gate extends Collidable{
  PVector pos;
  int widthG, heightG;
  String type = "gate";
  PImage sprite;
  boolean isOpen = false;

  Gate(int x, int y){
    sprite = loadImage("assets/Gate.png");
    pos = new PVector(x, y);
  }

  void draw(){
    if(!isOpen) image(sprite, pos.x, pos.y);
    else{
      fill(0, 0, 255);
      rect(pos.x, pos.y, widthG, heightG);
    }
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