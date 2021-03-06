package gpio;

import com.pi4j.io.gpio.*;
import net.happybrackets.core.HBAction;
import net.happybrackets.core.HBReset;
import net.happybrackets.device.HB;

import java.lang.invoke.MethodHandles;

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
public class ToggleGPIO implements HBAction, HBReset {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;

    boolean exitThread = false;
    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");


        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        // create gpioController controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpioController pin #01 as an output pin and turn on
        // This is actually GPIO_GEN01 - not GPIO 1. THis is also known as GPIO 18.
        // http://pi4j.com/pins/model-zerow-rev1.html
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);

        /***********************************************************
         * Create a runnable thread object
         * simply type threadFunction to generate this code
         ***********************************************************/
        Thread thread = new Thread(() -> {
            int SLEEP_TIME = 1000;
            while (!exitThread) {
                /*** write your code below this line ***/

                    // toggle the current state of gpioController pin #01 (should turn on)
                    pin.toggle();

                /*** write your code above this line ***/

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    /*** remove the break below to just resume thread or add your own action***/
                    break;
                    /*** remove the break above to just resume thread or add your own action ***/

                }
            }

            // we really have to unprovision pin, otherwise we can't get it again
            gpio.unprovisionPin(pin);
            // stop all GPIO activity/threads by shutting down the GPIO controller
            // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
            gpio.shutdown();

            System.out.println("Exiting ControlGpioExample");

        });

        /*** write your code you want to execute before you start the thread below this line ***/

        /*** write your code you want to execute before you start the thread above this line ***/

        thread.start();
        /****************** End threadFunction **************************/

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

    @Override
    public void doReset() {
        exitThread = true;
    }
    //</editor-fold>
}
