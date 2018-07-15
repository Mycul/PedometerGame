package com.example.michael.pedometertest;

/**
 * Created by Michael on 7/12/2018.
 */

// Will listen to step alerts
public interface StepListener {

    public void step(long timeNs);

}
