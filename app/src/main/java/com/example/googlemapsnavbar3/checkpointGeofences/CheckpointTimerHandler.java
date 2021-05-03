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

    /**
     * <p>Used the singleton design pattern</p>
     * <p>If an instance of this class already exists, then return it.</p>
     * <p>If an instance of this class doesn't exists, then make a new one and return it.</p>
     * @return An instance of CheckpointTimerHandler
     */
    public static CheckpointTimerHandler getInstance(){
       if (instance == null){
           instance = new CheckpointTimerHandler();
       }
        return instance;
    }

    /**
     * Starts the countdown of the first timer.
     */
    public void startFirstCheckpoint(){
        checkpointTimerArrayList.get(0).start();
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
        if (checkpointTimerArrayList.size() > pointer + 1){
            pointer++;
            CheckpointTimer newTimer = checkpointTimerArrayList.get(pointer);
            newTimer.start();
        }

    }

    private class CheckpointTimer{
        CountDownTimer timer;
        private boolean canceled = false;

        /**
         * <p>Constructer for a timer.</p>
         * <p>Has 2 finish states: Cancelled, Not Cancelled</p>
         * <p>If the timer was cancelled the alarm isn't sounded.</p>
         * <p>If the timer wasn't cancelled the alarm is sounded.</p>
          * @param milli The length of the countdown in milliseconds.
         */
        public CheckpointTimer(long milli){
            this.timer = new CountDownTimer(milli, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    if (canceled){
                        Log.d("Checkpoint Timer", "Timer Removed");
                    }
                    else{
                        Log.d("Checkpoint Timer", "Timer Removed");
                        Log.d("Checkpoint Timer", "Contact Called");
                    }
                }
            };
        }

        /**
         * <p>Begin the countdown of the timer</p>
         */
        public void start(){
           timer.start();
        }

        /**
         * <p>Cancel the timer.</p>
         */
        public void cancel(){
            timer.cancel();
            canceled = true;
        }

    }
}
