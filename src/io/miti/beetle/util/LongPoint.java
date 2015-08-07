package io.miti.beetle.util;

public final class LongPoint
{
  private long x = 0L;
  private long y = 0L;
  
  public LongPoint() {
    super();
  }
  
  public LongPoint(final long px, final long py) {
    x = px;
    y = py;
  }

  public long getX() {
    return x;
  }

  public void setX(long x) {
    this.x = x;
  }

  public long getY() {
    return y;
  }

  public void setY(long y) {
    this.y = y;
  }
}
