package com.thoughtcrafters.homie.domain.behaviours;

public interface Switchable {

    public void turnOn();

    public void turnOff();

    public SwitchState state();

}
