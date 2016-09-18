class Coin extends Collidable{
  String type = "coin";
  float widthC, heightC, addTime, timer, delay;
  int numFrames, currentFrame;
  PImage[] images;
  PVector pos;

  Coin(float x, float y){
    pos = new PVector(x, y);
    this.widthC = 16;
    this.heightC = 16;
    this.addTime = 10;

    //Sprite animation
    timer = 0;
    delay = 100000000;
    numFrames = 9;  // The number of frames in the animation, # of images
    currentFrame = 0;
    images = new PImage[numFrames];
    for(int i = 0; i < numFrames; i++){ //Populates the array with the images
      images[i] = loadImage("assets/coin/coin" + (i + 1) + ".png");
    }

  }

  void draw(){
    timer++;
      currentFrame = (currentFrame + 1) % numFrames;  // Use % to cycle through frames
  if(timer % delay == 1){
    //for (int x = 0; x < width; x += images[0].width + 10) {
      image(images[(currentFrame) % numFrames], pos.x, pos.y);
      // fill(255, 0, 0);
      // rect(pos.x, pos.y, widthC, heightC);
    //}
  }else
    image(images[(currentFrame) % numFrames], pos.x - images[0].width / 2, pos.y - images[0].height / 2);
  }

  //Returns the width, the height, a position vector and the amount of time that is added on pickups

  float getWidth(){
      return this.widthC;
    }

  float getHeight(){
      return this.heightC;
    }

  String getType(){
      return this.type;
    }

  float getTimeAddition(){
      return this.addTime;
    }

  PVector getPosition(){
      return pos;
    }

}