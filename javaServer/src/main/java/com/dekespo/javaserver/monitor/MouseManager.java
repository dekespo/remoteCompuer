package com.dekespo.javaserver.monitor;

import com.dekespo.commonclasses.MouseMessage;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

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

  public void move(MouseMessage data) {
    int x = (int) this.currentPosition.getX();
    int y = (int) this.currentPosition.getY();
    this.robot.mouseMove(x + data.getVelocityX(), y + data.getVelocityY());
    if (data.isFinal()) this.currentPosition = MouseInfo.getPointerInfo().getLocation();
    System.out.println(this.currentPosition);
  }

  public void click() {
    this.robot.mousePress(InputEvent.BUTTON1_MASK);
    this.robot.mouseRelease(InputEvent.BUTTON1_MASK);
  }

  public void command(MouseMessage data) {
    switch (data.getMouseCommand()) {
      case MOVE:
        move(data);
        break;
      case CLICK:
        click();
        break;
      case DOUBLE_CLICK:
        click();
        click();
        break;
      default:
        throw new RuntimeException("Undefined MouseCommand data: " + data.toString());
    }
  }
}
