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
public class TestGPIONames implements HBAction, HBReset {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;

    boolean exitThread = false;
    private final static String GPIO_NAME_PREFIX = "GPIO ";


    /**
     * Get the Name of the GPIO pin in Pi4J
     * @param pin_number the pin Number
     * @return what the RaspiPin name is for it
     */
    public static String getRaspPinName(int pin_number){
        return GPIO_NAME_PREFIX + pin_number;
    }

    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");


        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        // create gpioController controller
        final GpioController gpio = GpioFactory.getInstance();

        for (int i = 0; i < 50; i++){
            String pin_name =  getRaspPinName(i);
            Pin pin =  RaspiPin.getPinByName(pin_name);
            if (pin != null){
                System.out.println("Found " + pin_name);
            }
            else
            {
                System.out.println("Fail " + pin_name);
            }
        }
        // provision gpioController pin #01 as an output pin and turn on
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);

        // set shutdown state for this pin
        pin.setShutdownOptions(true, PinState.LOW);

        System.out.println("--> GPIO state should be: ON");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // turn off gpioController pin #01
        pin.low();
        System.out.println("--> GPIO state should be: OFF");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // toggle the current state of gpioController pin #01 (should turn on)
        pin.toggle();
        System.out.println("--> GPIO state should be: ON");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // toggle the current state of gpioController pin #01  (should turn off)
        pin.toggle();
        System.out.println("--> GPIO state should be: OFF");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // turn on gpioController pin #01 for 1 second and then off
        System.out.println("--> GPIO state should be: ON for only 1 second");
        pin.pulse(1000, true); // set second argument to 'true' use a blocking call

        // we really have to unprovision pin, otherwise we can't get it again
        gpio.unprovisionPin(pin);
        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();

        System.out.println("Exiting ControlGpioExample");
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
