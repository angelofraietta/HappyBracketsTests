package soundglitch;

import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;
import net.happybrackets.core.HBAction;
import net.happybrackets.device.HB;
import net.happybrackets.device.sensors.AccelerometerListener;
import net.happybrackets.device.sensors.Sensor;

import java.lang.invoke.MethodHandles;

public class AccelerometerSound implements HBAction {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;

    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");

        final float INITIAL_FREQUENCY = 1000; // this is the frequency of the waveform we will make
        final float INITIAL_VOLUME = 0.1f; // define how loud we want the sound

        Glide audioFrequency = new Glide(INITIAL_FREQUENCY);
        Glide audioVolume = new Glide(INITIAL_VOLUME);


        // create a wave player to generate a waveform based on frequency and waveform type
        WavePlayer waveformGenerator = new WavePlayer(audioFrequency, Buffer.SINE);

        // set up a gain amplifier to control the volume
        Gain gainAmplifier = new Gain(NUMBER_AUDIO_CHANNELS, audioVolume);

        // connect our WavePlayer object into the Gain object
        gainAmplifier.addInput(waveformGenerator);

        // Now plug the gain object into the audio output
        hb.ac.out.addInput(gainAmplifier);

        /*****************************************************
         * Find an accelerometer sensor.
         * to create this code, simply type accelerometerSensor
         *****************************************************/
        new AccelerometerListener(hb) {
            @Override
            public void sensorUpdate(float x_val, float y_val, float z_val) {
                /* accelerometer values typically range from -1 to + 1 */
                /******** Write your code below this line ********/

                // scale our frequency so it will go between 100 and 200o Hz
                float new_freq = Sensor.scaleValue(100, 2000, -1, 1, x_val);
                audioFrequency.setValue(new_freq);
                /******** Write your code above this line ********/

            }
        };
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
