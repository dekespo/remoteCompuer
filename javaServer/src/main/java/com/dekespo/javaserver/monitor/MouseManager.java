package com.dekespo.javaserver.monitor;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class MouseManager {

  private Robot robot;
  private Point currentPosition;

  public MouseManager() {
    this.currentPosition = MouseInfo.getPointerInfo().getLocation();
    try {
      this.robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }

  public void move(MouseDirections direction, int speed) {
    int x = (int) this.currentPosition.getX();
    int y = (int) this.currentPosition.getY();
    switch (direction) {
      case UP:
        this.robot.mouseMove(x, y + speed);
        break;
      case DOWN:
        this.robot.mouseMove(x, y - speed);
        break;
      case RIGHT:
        this.robot.mouseMove(x + speed, y);
        break;
      case LEFT:
        this.robot.mouseMove(x - speed, y);
        break;
    }
    this.currentPosition = MouseInfo.getPointerInfo().getLocation();
    System.out.println(this.currentPosition);
  }

  public enum MouseDirections {
    UP,
    DOWN,
    RIGHT,
    LEFT
  }
}
