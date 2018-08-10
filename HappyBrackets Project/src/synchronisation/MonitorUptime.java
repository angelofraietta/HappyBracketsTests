package synchronisation;

import net.happybrackets.core.HBAction;
import net.happybrackets.core.HBReset;
import net.happybrackets.core.control.ControlScope;
import net.happybrackets.core.control.IntegerTextControl;
import net.happybrackets.core.control.TextControl;
import net.happybrackets.core.control.TriggerControl;
import net.happybrackets.device.HB;

import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MonitorUptime implements HBAction, HBReset {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;

    long referenceTime = getUptime();

    long lastRequestTime = 0;

    boolean exitThread = false;

    String hostename = "";
    /**
     * Get the time JVM has been runnings
     * @return
     */
    private long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    /**
     * Get relative time since we synchronised
     * @return elapsed time in ms since sychronisation
     */
    private long getRelativeTime() {
        return getUptime() - referenceTime;
    }

    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");

        try {
            hostename = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        /*************************************************************
         * Create a Trigger type Dynamic Control
         *
         * Simply type globalTriggerControl to generate this code
         *************************************************************/
        TriggerControl synchroniseUptime = new TriggerControl(this, "Reset Uptime") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/
                System.out.println("**********************************************");
                referenceTime = getUptime();

                System.out.println("Synchronise Up time to " + referenceTime);
                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl synchroniseUptime code ***/


        /*************************************************************
         * Create an integer type Dynamic Control that displays as a text box
         * Simply type intTextControl to generate this code
         *************************************************************/
        IntegerTextControl relativeUptime = new IntegerTextControl(this, "Relative Uptime", 0) {
            @Override
            public void valueChanged(int control_val) {
                /*** Write your DynamicControl code below this line ***/

                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl relativeUptime code ***/

        /*************************************************************
         * Create a string type Dynamic Control that displays as a text box
         * Simply type globalTextControl to generate this code
         *************************************************************/
        TextControl deviceTimesDisplay = new TextControl(this, "Device Time", "") {
            @Override
            public void valueChanged(String control_val) {
                /*** Write your DynamicControl code below this line ***/
                try {
                    // the call will come back with hostname and time
                    // we will print it out and also display the time difference from wen we sent request
                    String[] parts = control_val.split(" ");
                    if (parts.length > 1) {
                        String device_name = parts[0];
                        String time = parts[1];

                        long elapsed =  Long.parseLong(time);
                        long diff = elapsed - lastRequestTime;

                        System.out.println(control_val + " " + diff);
                    }
                }
                catch (Exception ex){}
                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl deviceTimesControl code ***/


        /*************************************************************
         * Create a Trigger type Dynamic Control
         *
         * Simply type globalTriggerControl to generate this code
         *************************************************************/
        TriggerControl sendRelativerTime = new TriggerControl(this, "Send Time") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/
                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl sendRelativerTime code ***/

        /***********************************************************
         * Create a runnable thread object
         * simply type threadFunction to generate this code
         ***********************************************************/
        Thread thread = new Thread(() -> {
            int SLEEP_TIME = 1000;
            while (true) {
                /*** write your code below this line ***/


                /*** write your code above this line ***/

                try {
                    // SStore our time, print, and then request time
                    lastRequestTime = getRelativeTime();
                    System.out.println("**********************************************");
                    int seconds = (int) (lastRequestTime / 1000) % 60 ;
                    int minutes = (int) ((lastRequestTime / (1000*60)) % 60);
                    int hours   = (int) ((lastRequestTime / (1000*60*60)) % 24);

                    String time_text = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

                    System.out.println("Reqest time " + lastRequestTime + " " + time_text);

                    sendRelativerTime.send();
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    /*** remove the break below to just resume thread or add your own action***/
                    break;
                    /*** remove the break above to just resume thread or add your own action ***/

                }
            }
        });

        /*** write your code you want to execute before you start the thread below this line ***/

        /*** write your code you want to execute before you start the thread above this line ***/

        thread.start();
        /****************** End threadFunction **************************/
        /***** Type your HBAction code above this line ******/
    }


    /**
     * Add any code you need to have occur when a reset occurs
     */
    @Override
    public void doReset() {
        /***** Type your HBReset code below this line ******/
        exitThread = true;
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
