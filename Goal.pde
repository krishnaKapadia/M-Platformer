class Goal extends Collidable{
  String type = "goal";
  float widthG, heightG;
  PVector pos;

  Goal(float x, float y){
    pos = new PVector(x, y);
    this.widthG = 16;
    this.heightG = 16;
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
