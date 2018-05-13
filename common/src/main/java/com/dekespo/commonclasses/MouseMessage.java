package com.dekespo.commonclasses;

import java.util.regex.Pattern;

public class MouseMessage {
  private final MouseCommand mouseCommand;
  private final int velocityX;
  private final int velocityY;
  private final boolean isFinal;
  private final String SEPARATOR = ":";

  public MouseMessage(MouseCommand mouseCommand, int velocityX, int velocityY, boolean isFinal) {
    this.mouseCommand = mouseCommand;
    this.velocityX = velocityX;
    this.velocityY = velocityY;
    this.isFinal = isFinal;
  }

  public MouseMessage(String data) {
    if (!data.contains(SEPARATOR))
      throw new RuntimeException("The message must contain the following separator: " + SEPARATOR);
    String[] x_y = data.split(Pattern.quote(SEPARATOR));
    if (x_y.length != 4)
      throw new RuntimeException(
          "The mouse message data is wrong, must be 4. But " + data + " has " + x_y.length);
    this.mouseCommand = MouseCommand.valueOf(x_y[0]);
    this.velocityX = Integer.parseInt(x_y[1]);
    this.velocityY = Integer.parseInt(x_y[2]);
    this.isFinal = Boolean.parseBoolean(x_y[3]);
  }

  @Override
  public String toString() {
    return this.mouseCommand.name()
        + SEPARATOR
        + Integer.toString(this.velocityX)
        + SEPARATOR
        + Integer.toString(this.velocityY)
        + SEPARATOR
        + Boolean.toString(this.isFinal);
  }

  public int getVelocityX() {
    return this.velocityX;
  }

  public int getVelocityY() {
    return this.velocityY;
  }

  public boolean isFinal() {
    return this.isFinal;
  }

  public MouseCommand getMouseCommand() {
    return this.mouseCommand;
  }

  public enum MouseCommand {
    MOVE,
    CLICK,
    DOUBLE_CLICK
  }
}
