import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;
import net.happybrackets.core.HBAction;
import net.happybrackets.core.control.BooleanControl;
import net.happybrackets.core.control.ControlScope;
import net.happybrackets.core.control.TriggerControl;
import net.happybrackets.device.HB;

import java.lang.invoke.MethodHandles;

/**
 * This sketch will play a three octave scale starting from MIDI note C2
 * We increment an index from zero, and each time the number incremented
 * it plays next note in the scale
 *
 * Uses function Pitch.getRelativeMidiNote to obtain the MIDI note
 * When our last note reaches 3 octaves, we start again at base note
 */
public class ResyncMajorScale implements HBAction {

    final int NUMBER_AUDIO_CHANNELS = 1; // define how many audio channels our device is using
    
    // we need to define class variables so we can access them inside the clock

    // we will increment this number to get to next note is scale
    int nextScaleIndex = 0;
    final int BASE_TONIC = 48; // This will be the tonic of our scale. This correlates to C2 in MIDI

    final int NUMBER_OCTAVES = 3;

    final int MAXIMUM_PITCH = BASE_TONIC + 12 * NUMBER_OCTAVES; // This is the highest note we will play

    @Override
    public void action(HB hb) {

        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");

        // define where we will store our calulated notes. THese will modify the wavePlayer
        Glide waveformFrequency = new Glide(Pitch.mtof(BASE_TONIC));
        
        final float INITIAL_VOLUME = 0.1f; // define how loud we want the sound
        Glide audioVolume = new Glide(INITIAL_VOLUME);

        // create a wave player to generate a waveform based on frequency and waveform type
        WavePlayer waveformGenerator = new WavePlayer(waveformFrequency, Buffer.SQUARE);

        // set up a gain amplifier to control the volume
        Gain gainAmplifier = new Gain(NUMBER_AUDIO_CHANNELS, audioVolume);

        // connect our WavePlayer object into the Gain object
        gainAmplifier.addInput(waveformGenerator);

        // Now plug the gain object into the audio output
        hb.ac.out.addInput(gainAmplifier);

        /************************************************************
         * start clockTimer
         * Create a clock with a interval based on the clock duration
         *
         * To create this, just type clockTimer
         ************************************************************/
        // create a clock and start changing frequency on each beat
        final float CLOCK_INTERVAL = 200;

        // Create a clock with beat interval of CLOCK_INTERVAL ms
        Clock clock = new Clock(CLOCK_INTERVAL);


        /*************************************************************
         * Create a Trigger type Dynamic Control
         *
         * Simply type globalTriggerControl to generate this code
         *************************************************************/
        TriggerControl resyncTrigger = new TriggerControl(this, "Resync") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/
                nextScaleIndex = 0;
                clock.reset();
                System.out.println("Reset Clock Count");
                /*** Write your DynamicControl code above this line ***/
            }
        };
        resyncTrigger.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl resyncTrigger code ***/

        // let us handle triggers
        clock.addMessageListener(new Bead() {
            @Override
            protected void messageReceived(Bead bead) {
                // see if we are at the start of a beat
                boolean start_of_beat = clock.getCount() % clock.getTicksPerBeat() == 0;
                if (start_of_beat) {
                    /*** Write your code to perform functions on the beat below this line ****/

                    // we are going to next note in scale
                    nextScaleIndex++;

                    // Get the Midi note number based on Scale
                    int key_note = Pitch.getRelativeMidiNote(BASE_TONIC, Pitch.major, nextScaleIndex);

                    // if it exceeds our maximum, then start again and use our start
                    if (key_note > MAXIMUM_PITCH)
                    {
                        key_note = BASE_TONIC;
                        nextScaleIndex = 0;

                        if (hb.myIndex() == 1)
                        {
                            resyncTrigger.send();
                        }

                        System.out.println("Index " + hb.myIndex());
                    }
                    // convert our MIDI pitch to a frequency
                    waveformFrequency.setValue(Pitch.mtof(key_note));


                    /*** Write your code to perform functions on the beat above this line ****/
                } else {
                    /*** Write your code to perform functions off the beat below this line ****/

                    /*** Write your code to perform functions off the beat above this line ****/
                }
            }
        });

        /*************************************************************
         * Create a Boolean type Dynamic Control pair that displays as a check box
         * Simply type globalBooleanControl to generate this code
         *************************************************************/
        BooleanControl booleanControl = new BooleanControl(this, "Start / Stop", true) {
            @Override
            public void valueChanged(Boolean control_val) {
                /*** Write your DynamicControl code below this line ***/
                if (control_val){
                    nextScaleIndex = 0;
                    audioVolume.setValue(INITIAL_VOLUME);
                    clock.reset();
                    clock.pause(false);
                }
                else{
                    audioVolume.setValue(0);
                    clock.pause(true);
                }
                System.out.println(control_val);
                /*** Write your DynamicControl code above this line ***/
            }
        };
        booleanControl.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl booleanControl code ***/

        /*********************** end clockTimer **********************/
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
