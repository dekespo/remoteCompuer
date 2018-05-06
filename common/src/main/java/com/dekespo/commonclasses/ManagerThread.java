package com.dekespo.commonclasses;

public abstract class ManagerThread extends Thread {

  public abstract void run();

  protected abstract IStreamConnection JavaIOStreamConnection();
}
