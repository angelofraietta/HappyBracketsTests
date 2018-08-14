package gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import net.happybrackets.core.HBAction;
import net.happybrackets.core.HBReset;
import net.happybrackets.core.control.TriggerControl;
import net.happybrackets.device.HB;

import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Purpose of the test is to Test GPIO Coming in
 *
 * Currently gives this error
 * http://pi4j.com/example/control.html
 *
 * <--Pi4J--> GPIO Control Example ... started.
 * Unable to determine hardware version. I see: Hardware   : BCM2835
 * ,
 *  - expecting BCM2708 or BCM2709. Please report this to projects@drogon.net
 */
public class FollowGPIO implements HBAction, HBReset {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;

    long lastPulseTime =  0;
    long minTime =  1000;
    long maxTime = 0;

    boolean exitThread = false;

    List<GpioPin> provisionedPins = new ArrayList<>();
    GpioController gpioController;

    GpioPinDigitalInput inputPin;

    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");


        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        // create gpioController controller
        gpioController = GpioFactory.getInstance();

        inputPin = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_02,
                PinPullResistance.PULL_DOWN);

        provisionedPins.add(inputPin);

        // provision gpioController pin #01 as an output pin and turn on
        // This is actually GPIO_GEN01 - not GPIO 1. THis is also known as GPIO 18.
        // http://pi4j.com/pins/model-zerow-rev1.html
        final GpioPinDigitalOutput outputPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.LOW);

        provisionedPins.add(outputPin);

        // Initialise our value
        lastPulseTime = getUptime();

        // create a gpioController callback trigger on gpioController pin#4; when #4 changes state, perform a callback
        // invocation on the user defined 'Callable' class instance
        inputPin.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
            public Void call() throws Exception {

                // let us find out how long it takes to get the
                long current_time = getUptime();
                long time_diff = current_time - lastPulseTime;

                boolean display = false;

                if (time_diff < minTime){
                    minTime = time_diff;
                    display = true;
                }

                if (time_diff > maxTime){
                    maxTime = time_diff;
                    display = true;
                }

                outputPin.toggle();

                if (display)
                {
                    System.out.println("Max: " + maxTime + "  Min: " + minTime);
                }

                lastPulseTime =  current_time;
                return null;
            }
        }));

        // now toggle out pin to start oscillation

        /*************************************************************
         * Create a Trigger type Dynamic Control that displays as a button
         * Simply type triggerControl to generate this code
         *************************************************************/
        TriggerControl startTriggerControl = new TriggerControl(this, "Start") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/
                outputPin.toggle();
                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl triggerControl code ***/

        /*************************************************************
         * Create a Trigger type Dynamic Control that displays as a button
         * Simply type triggerControl to generate this code
         *************************************************************/
        TriggerControl resetTimeTriggerControl = new TriggerControl(this, "Reset Time") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/
                minTime =  1000;
                maxTime = 0;
                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl resetTimeTriggerControl code ***/


        /***** Type your HBAction code above this line ******/
    }


    /**
     * Get the time JVM has been runnings
     * @return
     */
    private long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
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

    @Override
    public void doReset() {
        inputPin.removeAllTriggers();
        inputPin.removeAllListeners();
        exitThread = true;
        for (GpioPin pin : provisionedPins)
        {
            gpioController.unprovisionPin(pin);
        }

    }
    //</editor-fold>
}
