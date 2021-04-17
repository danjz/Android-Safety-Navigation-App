package com.example.googlemapsnavbar3.checkpointGeofences;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.ArrayList;

public class CheckpointTimerHandler {

    private static CheckpointTimerHandler instance = null;
    private ArrayList<CheckpointTimer> checkpointTimerArrayList;
    private int pointer;

    private CheckpointTimerHandler(){
        this.checkpointTimerArrayList = new ArrayList<>();
        this.pointer = 0;
    }

    public static CheckpointTimerHandler getInstance(){
       if (instance == null){
           instance = new CheckpointTimerHandler();
       }
        return instance;
    }

    /**
     * <p>Adds a timer to the list</p>
     * @param time The amount of time to reach the checkpoint in milliseconds
     */
    public void addCheckpoint(long time){
        CheckpointTimer timer = new CheckpointTimer(time);
        this.checkpointTimerArrayList.add(timer);
    }

    /**
     * <p>Trigger this when you arrive at a checkpoint</p>
     * <p>This cancels the previous pointer and starts the next one.</p>
     */
    public void nextCheckpoint(){
        CheckpointTimer oldTimer = checkpointTimerArrayList.get(pointer);
        oldTimer.cancel();
        if (checkpointTimerArrayList.size() > pointer){
            pointer++;
            CheckpointTimer newTimer = checkpointTimerArrayList.get(pointer);
            newTimer.start();
        }

    }

    private class CheckpointTimer{
        CountDownTimer timer;
        public CheckpointTimer(long milli){
            this.timer = new CountDownTimer(milli, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Log.d("Checkpoint Timer", "Timer Finished/Removed");
                }
            };
        }

        public void start(){
           timer.start();
        }

        public void cancel(){
            timer.cancel();
        }

    }
}
