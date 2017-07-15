class Coin extends Collidable{
  String type = "coin";
  float widthC, heightC, addTime, delay;
  int numFrames, currentFrame, lastTime;
  PImage[] images;
  PVector pos;

  Coin(float x, float y){
    pos = new PVector(x, y);
    this.widthC = 16;
    this.heightC = 16;
    this.addTime = 10;

    //Sprite animation
    delay = 90;
    numFrames = 10;  // The number of frames in the animation, # of images
    currentFrame = 0;

    images = new PImage[numFrames];
    for(int i = 0; i < numFrames; i++){ //Populates the array with the images
      images[i] = loadImage("assets/coin/coin" + (i) + ".png");
    }

    lastTime = millis();

  }

  void draw(){

   image(images[(currentFrame) % numFrames], pos.x - images[0].width / 2, pos.y - images[0].height / 2);

   if(millis() - lastTime >= delay){
      currentFrame = (currentFrame + 1) % numFrames;  // Use % to cycle through frames
      lastTime = millis();
    }
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
