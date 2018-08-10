package stellarium;

import net.happybrackets.core.HBAction;
import net.happybrackets.core.HBReset;
import net.happybrackets.core.control.TriggerControl;
import net.happybrackets.device.HB;

import java.lang.invoke.MethodHandles;

public class WaitThread implements HBAction, HBReset {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;

    boolean exitThread = false;

    Object syncObject = new Object();

    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");


        /***********************************************************
         * Create a runnable thread object
         * simply type threadFunction to generate this code
         ***********************************************************/
        Thread thread = new Thread(() -> {

            while (!exitThread) {
                /*** write your code below this line ***/

                synchronized (syncObject) {
                    try {
                        syncObject.wait();
                        System.out.println("Rewceived Message");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                /*** write your code above this line ***/

            }
        });
        /*** write your code you want to execute before you start the thread above this line ***/

        thread.start();

        /*** write your code you want to execute before you start the thread below this line ***/

        /*************************************************************
         * Create a Trigger type Dynamic Control that displays as a button
         * Simply type triggerControl to generate this code
         *************************************************************/
        TriggerControl triggerThread = new TriggerControl(this, "TriggerThread") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/
                try {
                    synchronized (syncObject) {
                        syncObject.notifyAll();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl triggerThread code ***/


        /****************** End threadFunction **************************/
        /***** Type your HBAction code above this line ******/
    }


    synchronized void waitForExe(){

    }
    /**
     * Add any code you need to have occur when a reset occurs
     */
    @Override
    public void doReset() {
        /***** Type your HBReset code below this line ******/
        exitThread = true;
        syncObject.notifyAll();
        /***** Type your HBReset code above this line ******/
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
