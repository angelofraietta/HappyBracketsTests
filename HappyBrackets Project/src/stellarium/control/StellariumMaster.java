package stellarium.control;

import net.happybrackets.core.HBAction;
import net.happybrackets.core.control.*;
import net.happybrackets.device.HB;
import net.happybrackets.device.sensors.AccelerometerListener;
import net.happybrackets.device.sensors.GyroscopeListener;

import java.lang.invoke.MethodHandles;

/**
 * * This sketch displays the values returning from gyroscope
 */
public class StellariumMaster implements HBAction {
    @Override
    public void action(HB hb) {

        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");


        /***** Type your HBAction code below this line ******/

        /*************************************************************
         * Create a Float type Dynamic Control pair
         * Simply type globalFloatControl to generate this code
         *************************************************************/
        FloatBuddyControl floatBuddyControl = new FloatBuddyControl(this, "global alt", 0, 0, 360) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/

                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl floatBuddyControl code ***/

        // get our azimuth
        /*************************************************************
         * Create a Float type Dynamic Control that displays as a text box
         * Simply type floatTextControl to generate this code
         *************************************************************/
        FloatTextControl leftRightControl = new FloatTextControl(this, "global left/right", 0) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/
                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);

        /*************************************************************
         * Create a Float type Dynamic Control that displays as a text box
         * Simply type floatTextControl to generate this code
         *************************************************************/
        FloatTextControl altitudeControl = new FloatTextControl(this, "global alt", 0) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/
                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);/*** End DynamicControl code altitudeControl ***/


        /*************************************************************
         * Create a Float type Dynamic Control pair that displays as a slider and text box
         *
         * Simply type floatBuddyControl to generate this code
         *************************************************************/
        DynamicControl displayYaw = hb.createControlBuddyPair(this, ControlType.FLOAT, "Yaw", 0, -1, 1);
        // Listener removed as it is unnecessary
        /*** End DynamicControl code ***/

        /*************************************************************
         * Create a Float type Dynamic Control pair that displays as a slider and text box
         *
         * Simply type floatBuddyControl to generate this code
         *************************************************************/
        DynamicControl displayPitch = hb.createControlBuddyPair(this, ControlType.FLOAT, "Pitch", 0, -1, 1);
        // Listener removed as it is unnecessary
        /*** End DynamicControl code ***/

        /*************************************************************
         * Create a Float type Dynamic Control pair that displays as a slider and text box
         *
         * Simply type floatBuddyControl to generate this code
         *************************************************************/
        DynamicControl displayRoll = hb.createControlBuddyPair(this, ControlType.FLOAT, "Roll", 0, -1, 1);
        // Listener removed as it is unnecessary
        /*** End DynamicControl code ***/


        /*****************************************************
         * Add a gyroscope sensor listener. *
         * to create this code, simply type gyroscopeSensor
         *****************************************************/
        new GyroscopeListener(hb) {
            @Override
            public void sensorUpdated(float pitch, float roll, float yaw) {
                /******** Write your code below this line ********/
                displayPitch.setValue(pitch);

                displayRoll.setValue(roll);
                displayYaw.setValue(yaw);
                //leftRightControl.setValue(pitch);
                /******** Write your code above this line ********/
            }
        }.setRounding(0);
        /*** End gyroscopeSensor code ***/

        /*****************************************************
         * Find an accelerometer sensor.
         * to create this code, simply type accelerometerSensor
         *****************************************************/
        new AccelerometerListener(hb) {
            @Override
            public void sensorUpdate(float x_val, float y_val, float z_val) {
                /* accelerometer values typically range from -1 to + 1 */
                /******** Write your code below this line ********/
                //altitudeControl.setValue(z_val * 90);
                /******** Write your code above this line ********/

            }
        }.setRounding(2);
        /*** End accelerometerSensor code ***/
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
