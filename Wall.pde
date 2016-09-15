class Wall extends Collidable{
  String type = "wall";
  float x, y;
  float widthW, heightW;

  Wall(float x, float y, float widthW, float heightW){
    this.x = x;
    this.y = y;
    this.widthW = widthW;
    this.heightW = heightW;
  }

  void draw(){
    fill(#A5A5A5);
    rect(x, y, widthW, heightW);
  }

  //Returns the width, the height and a position vector

  float getWidth(){
    return this.widthW;
  }

  float getHeight(){
    return this.heightW;
  }

  PVector getPosition(){
    return new PVector(x, y);
  }

  String getType(){
      return this.type;
    }




}
