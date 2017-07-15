class Wall extends Collidable{
  String type = "wall";
  float x, y;
  float widthW, heightW;
  public PImage sprite;

  Wall(float x, float y, float widthW, float heightW){
    this.x = x;
    this.y = y;
    this.widthW = widthW;
    this.heightW = heightW;
    //sprite = loadImage("assets/levels/floorTile.png");
    sprite = wallSprite;
  }

  void draw(){
    image(sprite, x, y);
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
