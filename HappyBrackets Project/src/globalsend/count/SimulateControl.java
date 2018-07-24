package globalsend.count;

import de.sciss.net.OSCMessage;
import net.happybrackets.core.HBAction;
import net.happybrackets.core.OSCVocabulary;
import net.happybrackets.core.control.BooleanControl;
import net.happybrackets.core.control.ControlType;
import net.happybrackets.core.control.IntegerTextControl;
import net.happybrackets.core.control.TriggerControl;
import net.happybrackets.device.HB;
import net.happybrackets.device.network.UDPCachedMessage;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SimulateControl implements HBAction {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;
    InetAddress broadcastAddress;
    DatagramSocket advertiseTxSocket = null;
    TriggerControl showTrigger;
    TriggerControl resetTrigger;
    BooleanControl dualPorts;

    final int DEVICE_PORT = 2226;
    final int DEVICE_PORT_2 = 2227;
    final String GLOBAL_INT = "Global Integer";
    final String RESET = "Reset";
    final String SHOW = "Display";

    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");

        try {
            broadcastAddress = InetAddress.getByName("255.255.255.255");

            advertiseTxSocket = new DatagramSocket();
            advertiseTxSocket.setBroadcast(true);

            /*************************************************************
             * Create an integer type Dynamic Control that displays as a text box
             * Simply type intTextControl to generate this code
             *************************************************************/
            IntegerTextControl sleepDuration = new IntegerTextControl(this, "Sleep Duration", 0) {
                @Override
                public void valueChanged(int control_val) {
                    /*** Write your DynamicControl code below this line ***/

                    /*** Write your DynamicControl code above this line ***/
                }
            };/*** End DynamicControl sleepDuration code ***/


            /*************************************************************
             * Create a Boolean type Dynamic Control that displays as a check box
             * Simply type booleanControl to generate this code
             *************************************************************/
            dualPorts = new BooleanControl(this, "Dual Ports", false) {
                @Override
                public void valueChanged(Boolean control_val) {
                    /*** Write your DynamicControl code below this line ***/

                    /*** Write your DynamicControl code above this line ***/
                }
            };/*** End DynamicControl dualPorts code ***/


            /*************************************************************
             * Create an integer type Dynamic Control that displays as a text box
             * Simply type intTextControl to generate this code
             *************************************************************/
            IntegerTextControl integerTextControl = new IntegerTextControl(this, "Num messages", 1000) {
                @Override
                public void valueChanged(int control_val) {
                    /*** Write your DynamicControl code below this line ***/

                    /*** Write your DynamicControl code above this line ***/
                }
            };/*** End DynamicControl integerTextControl code ***/

            /*************************************************************
             * Create a Trigger type Dynamic Control that displays as a button
             * Simply type triggerControl to generate this code
             *************************************************************/
            TriggerControl sendTrigger = new TriggerControl(this, "Send Integers") {
                @Override
                public void triggerEvent() {
                    /*** Write your DynamicControl code below this line ***/

                    resetTrigger.send();
                    for (int i = 1; i <= integerTextControl.getValue(); i++) {
                        try {
                            UDPCachedMessage cached_message = new UDPCachedMessage(buildGlobalIntMessage(i));
                            DatagramPacket packet = cached_message.getCachedPacket();
                            packet.setAddress(broadcastAddress);
                            packet.setPort(DEVICE_PORT);
                            if (sleepDuration.getValue() > 0) {
                                Thread.sleep(sleepDuration.getValue());
                            }
                            advertiseTxSocket.send(packet);

                            if (dualPorts.getValue()) {
                                // now do secondary port packet
                                UDPCachedMessage cached_message2 = new UDPCachedMessage(buildGlobalIntMessage(i));
                                DatagramPacket packet2 = cached_message2.getCachedPacket();
                                packet2.setAddress(broadcastAddress);
                                packet2.setPort(DEVICE_PORT_2);

                                advertiseTxSocket.send(packet2);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    showTrigger.send();
                    /*** Write your DynamicControl code above this line ***/
                }
            };/*** End DynamicControl sendTrigger code ***/

            /*************************************************************
             * Create a Trigger type Dynamic Control that displays as a button
             * Simply type triggerControl to generate this code
             *************************************************************/
            resetTrigger = new TriggerControl(this, "Reset") {
                @Override
                public void triggerEvent() {
                    /*** Write your DynamicControl code below this line ***/
                    try {
                        UDPCachedMessage cached_message = new UDPCachedMessage(buildGlobalTriggerMessage(RESET));
                        DatagramPacket packet = cached_message.getCachedPacket();
                        packet.setAddress(broadcastAddress);
                        packet.setPort(DEVICE_PORT);
                        advertiseTxSocket.send(packet);
                        if (dualPorts.getValue()) {
                            UDPCachedMessage cached_message2 = new UDPCachedMessage(buildGlobalTriggerMessage(RESET));
                            DatagramPacket packet2 = cached_message2.getCachedPacket();
                            packet2.setAddress(broadcastAddress);
                            packet2.setPort(DEVICE_PORT_2);

                            advertiseTxSocket.send(packet2);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /*** Write your DynamicControl code above this line ***/
                }
            };/*** End DynamicControl triggerControl code ***/


        } catch (Exception e) {
            e.printStackTrace();
        }

        /*************************************************************
         * Create a Trigger type Dynamic Control that displays as a button
         * Simply type triggerControl to generate this code
         *************************************************************/
        showTrigger = new TriggerControl(this, "Show") {
            @Override
            public void triggerEvent() {
                /*** Write your DynamicControl code below this line ***/
                try {
                    UDPCachedMessage cached_message = new UDPCachedMessage(buildGlobalTriggerMessage(SHOW));
                    DatagramPacket packet = cached_message.getCachedPacket();
                    packet.setAddress(broadcastAddress);
                    packet.setPort(DEVICE_PORT);
                    advertiseTxSocket.send(packet);
                    if (dualPorts.getValue()) {
                        UDPCachedMessage cached_message2 = new UDPCachedMessage(buildGlobalTriggerMessage(SHOW));
                        DatagramPacket packet2 = cached_message2.getCachedPacket();
                        packet2.setAddress(broadcastAddress);
                        packet2.setPort(DEVICE_PORT_2);

                        advertiseTxSocket.send(packet2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl showTrigger code ***/


        /***** Type your HBAction code above this line ******/
    }

    /**
     * Send The Integer message
     * @param ival value to send
     * @return OSC Message
     */
    public OSCMessage buildGlobalIntMessage(int ival){
        return new OSCMessage(OSCVocabulary.DynamicControlMessage.GLOBAL,
                new Object[]{
                        "Simulate",
                        GLOBAL_INT,
                        ControlType.INT.ordinal(),
                        ival,
                });

    }

    /**
     * Send trigger Dynamic Control Message
     * @param control_name Name of message
     * @return OSC Message
     */
    public OSCMessage buildGlobalTriggerMessage(String control_name){
        return new OSCMessage(OSCVocabulary.DynamicControlMessage.GLOBAL,
                new Object[]{
                        "Simulate",
                        control_name,
                        ControlType.TRIGGER.ordinal(),
                });

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
