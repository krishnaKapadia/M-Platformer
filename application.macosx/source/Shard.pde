class Shard {
  PVector pos, vel;
  color c;
  int size = 10;

  Shard(color c, PVector pos){
    this.pos = pos;
    this.c = c;
    this.vel = new PVector(random(-20, 20), random(-20, 20));
  }

  //Draws the shard and updates the position
  void draw(){
    pos.add(vel);
    fill(c);
    rect(pos.x + 5, pos.y + 5, size, size);
  }

  PVector getPosition(){
    return this.pos;
  }

  void updatePos(PVector p){
    this.pos = p;
  }

}