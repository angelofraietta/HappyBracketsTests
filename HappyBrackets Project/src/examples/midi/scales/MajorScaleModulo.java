package examples.midi.scales;

import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;
import net.happybrackets.core.HBAction;
import net.happybrackets.device.HB;

import java.lang.invoke.MethodHandles;

/**
 * This sketch will play a three octave scale starting from MIDI note C2
 * We increment an index from zero, and each time the number incremented
 * it plays next note in the scale
 *
 * The scale degree and register uses modulo and division.
 * For example, Consider we are starting at MIDI note number 48 (C2) and we want to play the
 * note 9 scale degrees above. This would mean we would need to play the note
 * which is a E in the next register. E is the third note in the C Major scale (index 2)
 *
 * When our last note reaches 3 octaves, we start again at base note
 */
public class MajorScaleModulo implements HBAction {

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
        final float CLOCK_INTERVAL = 300;

        // Create a clock with beat interval of CLOCK_INTERVAL ms
        Clock clock = new Clock(CLOCK_INTERVAL);


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

                    // get the scale degree from the scale
                    // eg, if nextScaleIndex is 9, scale_degree = 9 % 7 = 2
                    int scale_degree = nextScaleIndex % Pitch.major.length;

                    // If our scale pitch is 2,Pitch.major[2] = 4 (major third)
                    int scale_pitch = Pitch.major[scale_degree];

                    // Now get the register of our note
                    // eg, if nextScaleIndex is 9, scale_degree = 9 / 7 = 1
                    int note_register = nextScaleIndex / Pitch.major.length;

                    // we multiply our register x 12 because that is an octave in MIDI
                    // if nextScaleIndex is 9 then 1 x 12 + 4 = 16
                    int note_pitch = note_register * 12 + scale_pitch;

                    // add the number to our base tonic to get the note based on key
                    // if nextScaleIndex is 9 then 48 + 16 = 64. This is E3 in MIDI
                    int key_note = BASE_TONIC + note_pitch;

                    // if it exceeds our maximum, then start again
                    if (key_note > MAXIMUM_PITCH)
                    {
                        key_note = BASE_TONIC;
                        nextScaleIndex = 0;
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
