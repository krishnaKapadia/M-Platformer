import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;

abstract class Collidable{

  abstract float getWidth();
  abstract float getHeight();
  abstract PVector getPosition();
  abstract String getType();
  abstract void draw();
//  abstract String type;

  //Gets the bounds off the current Object
  Rectangle2D getBounds() {
    return new Rectangle2D.Float(getPosition().x, getPosition().y, getWidth(), getHeight());
  }

  Ellipse2D getBoundsEllipse() {
    return new Ellipse2D.Float(getPosition().x, getPosition().y, getWidth(), getHeight());
  }

//Returns true if it is colliding with the other object
  boolean isColliding(Collidable other) {
    return (getBounds().intersects(other.getBounds()));
  }

  boolean isCollidingEllipse(Collidable other) {
    return (getBoundsEllipse().intersects(other.getBounds()));
  }


}
