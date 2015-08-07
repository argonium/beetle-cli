package io.miti.beetle.util;

public final class DoublePoint
{
  private double x = 0.0;
  private double y = 0.0;
  
  public DoublePoint() {
    super();
  }
  
  public DoublePoint(final double px, final double py) {
    x = px;
    y = py;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }
}
