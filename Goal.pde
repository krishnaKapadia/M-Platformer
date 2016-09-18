class Goal extends Collidable{
  String type = "goal";
  float widthG, heightG;
  PVector pos;
  PImage[] images;
  int currentFrame;

  Goal(float x, float y){
    pos = new PVector(x, y);
    this.widthG = 16;
    this.heightG = 16;

    images = new PImage[32];
    for(int i = 0; i < images.length; i++){ //Populates the array with the images
      images[i] = loadImage("assets/portal/Portal" + (i + 1) + ".png");
      images[i].resize(32, 0); //resize image to 32x32
    }
  }

  void draw(){
    currentFrame = (currentFrame + 1) % 32;
    image(images[(currentFrame) % 32], pos.x, pos.y);
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
