package globalsend.ordered;

import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Glide;
import net.happybrackets.core.HBAction;
import net.happybrackets.core.control.*;
import net.happybrackets.device.HB;

import java.lang.invoke.MethodHandles;

/**
 * This call will be the master Sender of Global Trigger messages
 * We will send global trigger messages across network and count them
 * We will determine why messages are being ost
 */
public class MasterIntegerSend implements HBAction {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;
    final int CLOCK_INTERVAL = 10;

    int nextExpectedValue = 0;
    int messageCount = 0;
    int errorCount = 0;
    BooleanControl runClock;


    int maxCount = 1000; // define how many clock beats we will
    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");

        /*************************************************************
         * Create an integer type Dynamic Control that displays as a text box
         * Simply type intTextControl to generate this code
         *************************************************************/
        IntegerTextControl messageCountDisplay = new IntegerTextControl(this, "TriggerCount", 0) {
            @Override
            public void valueChanged(int control_val) {
                /*** Write your DynamicControl code below this line ***/

                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl nextExpectedValue code ***/


        /*************************************************************
         * Create an integer type Dynamic Control that displays as a text box
         * Simply type intTextControl to generate this code
         *************************************************************/
        IntegerTextControl errorCountDisplay = new IntegerTextControl(this, "Error Count", 0) {
            @Override
            public void valueChanged(int control_val) {
                /*** Write your DynamicControl code below this line ***/

                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl errorCounteDisplay code ***/

        /*************************************************************
         * Create an integer type Dynamic Control that displays as a text box
         * Simply type intTextControl to generate this code
         *************************************************************/
        IntegerTextControl globalIntSend = new IntegerTextControl(this, "Global Integer", 0) {
            @Override
            public void valueChanged(int control_val) {
                /*** Write your DynamicControl code below this line ***/

                messageCount++;
                if (control_val < nextExpectedValue) {
                    // we have message missing or out of order

                    errorCountDisplay.setValue(errorCount);
                }

                // we do not set nextExpectedValue because this is the sender
                messageCountDisplay.setValue(messageCount);

                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);

        /*** End DynamicControl globalIntSend code ***/



        /************************************************************
         * Create a clock with a interval based on the clock duration
         *
         * To create this, just type clockTimer
         ************************************************************/
        // create a clock and start changing frequency on each beat


        // Create a clock with beat interval of CLOCK_INTERVAL ms
        Clock clock = new Clock(CLOCK_INTERVAL);
        clock.pause(true);

        Glide clockInterval = new Glide(CLOCK_INTERVAL);
        clock.setIntervalEnvelope(clockInterval);

        // let us handle triggers
        clock.addMessageListener(new Bead() {
            @Override
            protected void messageReceived(Bead bead) {
                // see if we are at the start of a beat
                boolean start_of_beat = clock.getCount() % clock.getTicksPerBeat() == 0;
                if (start_of_beat) {
                    /*** Write your code to perform functions on the beat below this line ****/
                    if (maxCount < 1 || maxCount >= nextExpectedValue) {
                        globalIntSend.setValue(nextExpectedValue);
                        nextExpectedValue++;
                    }
                    else {
                        // Run clock will already be created if we are at this stage
                        // we will let it turn the clock off
                        runClock.setValue(false);
                        System.out.println("Not counting");
                    }


                    /*** Write your code to perform functions on the beat above this line ****/
                } else {
                    /*** Write your code to perform functions off the beat below this line ****/

                    /*** Write your code to perform functions off the beat above this line ****/
                }
            }
        });
        /*********************** end clockTimer **********************/
        /*************************************************************
         * Create a Trigger type Dynamic Control
         *
         * Simply type globalTriggerControl to generate this code
         *************************************************************/
        TriggerControl resetTrigger = new TriggerControl(this, "Reset") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/

                nextExpectedValue = 0;
                messageCount = 0;
                errorCount = 0;
                errorCountDisplay.setValue(errorCount);
                hb.setStatus("Reset Counts");

                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl resetTrigger code ***/

        /*************************************************************
         * Create an integer type Dynamic Control that displays as a slider
         * Simply type intSliderControl to generate this code
         *************************************************************/
        IntegerControl clockRate = new IntegerBuddyControl(this, "Clock Rate", CLOCK_INTERVAL, 10, 1000) {

            @Override
            public void valueChanged(int control_val) {
                /*** Write your DynamicControl code below this line ***/
                clockInterval.setValue(control_val);
                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl clockRate code ***/

        /*************************************************************
         * Create a Boolean type Dynamic Control that displays as a check box
         * Simply type booleanControl to generate this code
         *************************************************************/
        runClock = new BooleanControl(this, "Start / Stop", !clock.isPaused()) {
            @Override
            public void valueChanged(Boolean control_val) {
                /*** Write your DynamicControl code below this line ***/
                clock.pause(!control_val);
                System.out.println("Pause clock " + ! control_val);
                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl runClock code ***/

        /*************************************************************
         * Create an integer type Dynamic Control that displays as a text box
         * Simply type intTextControl to generate this code
         *************************************************************/
        IntegerTextControl maxCounter = new IntegerTextControl(this, "Max Messages", maxCount) {
            @Override
            public void valueChanged(int control_val) {
                /*** Write your DynamicControl code below this line ***/
                maxCount = control_val;
                System.out.println("Set max count " + control_val);
                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl maxCounter code ***/


        /***** Type your HBAction code above this line ******/
    }

    //<editor-fold defaultstate="collapsed" desc="Debug Start">

    /**
     * This function is used when running sketch in IntelliJ IDE for debugging or testing
     *
     * @param args standard args required
     */
    public static void main(String[] args) {

        try {
            HB.runDebug(MethodHandles.lookup().lookupClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
}
