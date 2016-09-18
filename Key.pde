class Key extends Collidable{
  PVector pos;
  String type = "key";
  int widthK, heightK;
  boolean active = true;

  Key(int x, int y){
    pos = new PVector(x, y);
    widthK = 16;
    heightK = 16;
    //sprite = loadImage("assets/Key.png");
  }

  void draw(){
    //image(sprite);
    if(active) fill(#1AD3D2);
    else fill(#D9DBDB);  
    rect(pos.x, pos.y, widthK, heightK);
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