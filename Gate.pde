class Gate extends Collidable{
  PVector pos;
  int widthG, heightG;
  String type = "gate";
  
  Gate(){
    
  }
  
  void draw(){
    fill(0, 255, 0);
    rect(pos.x, pos.y, widthG, heightG);
  }

  //Returns the width, the height and a position vector

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