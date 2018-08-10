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

public class SendUptime implements HBAction, HBReset {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;

    long referenceTime = getUptime();

    String hostename = "";
    /**
     * Get the time JVM has been runnings
     * @return
     */
    private long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        //hb.reset();
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
                referenceTime = getUptime();
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
                long relative_time = getRelativeTime();
                relativeUptime.setValue((int)relative_time);

                String send_text = hostename + " " + relative_time;
                deviceTimesDisplay.setValue(send_text);
                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl sendRelativerTime code ***/
        /***** Type your HBAction code above this line ******/
    }

    /**
     * Get relative time since we synchronised
     * @return elapsed time in ms since sychronisation
     */
    private long getRelativeTime() {
        return getUptime() - referenceTime;
    }


    /**
     * Add any code you need to have occur when a reset occurs
     */
    @Override
    public void doReset() {
        /***** Type your HBReset code below this line ******/

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
