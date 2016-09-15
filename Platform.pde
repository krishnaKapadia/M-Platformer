class Platform extends Collidable{
  PVector pos;
  int widthP, heightP;
  String type = "platform";
  
  Platform(int x, int y){
    this.pos = new PVector(x, y);
    widthP = 16;
    widthP = 16;
  }
  
  void draw(){
    fill(#A5A5A5);
    rect(pos.x, pos.y, widthP, heightP);
  }

  //Returns the width, the height and a position vector

  float getWidth(){
      return this.widthP;
    }

  float getHeight(){
      return this.heightP;
    }

  PVector getPosition(){
      return pos;
    }

    String getType(){
        return this.type;
      }
}