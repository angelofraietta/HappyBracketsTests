package globalsend;

import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Glide;
import net.happybrackets.core.HBAction;
import net.happybrackets.core.control.ControlScope;
import net.happybrackets.core.control.IntegerSliderControl;
import net.happybrackets.core.control.IntegerTextControl;
import net.happybrackets.core.control.TriggerControl;
import net.happybrackets.device.HB;

import java.lang.invoke.MethodHandles;

/**
 * This call will be the slave of received messages
 * We will cont the trigger messages receieved
 */
public class SlaveReceiveTrigger implements HBAction {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;

    int triggerCount = 0;

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
        IntegerTextControl triggerCountDisplay = new IntegerTextControl(this, "TriggerCount", 0) {
            @Override
            public void valueChanged(int control_val) {
                /*** Write your DynamicControl code below this line ***/

                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl globalIntValue code ***/

        /*************************************************************
         * Create a Trigger type Dynamic Control
         *
         * Simply type globalTriggerControl to generate this code
         *************************************************************/
        TriggerControl triggerMessage = new TriggerControl(this, "trigger") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/
                triggerCount++;
                System.out.println("Trigger " + triggerCount);
                triggerCountDisplay.setValue(triggerCount);
                /*** Write your DynamicControl code above this line ***/
            }
        };
        triggerMessage.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl triggerMessage code ***/


        /*************************************************************
         * Create a Trigger type Dynamic Control
         *
         * Simply type globalTriggerControl to generate this code
         *************************************************************/
        TriggerControl resetTrigger = new TriggerControl(this, "Reset") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/

                triggerCount = 0;

                triggerCountDisplay.setValue(triggerCount);
                /*** Write your DynamicControl code above this line ***/
            }
        };
        resetTrigger.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl resetTrigger code ***/


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
