package stellarium;

import com.sun.org.apache.bcel.internal.generic.FLOAD;
import net.happybrackets.core.HBAction;
import net.happybrackets.core.control.FloatBuddyControl;
import net.happybrackets.device.HB;
import net.happybrackets.device.sensors.AccelerometerListener;
import net.happybrackets.device.sensors.GyroscopeListener;
import net.happybrackets.device.sensors.Sensor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class ControlsSendPosition implements HBAction {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;
    double currentAz = 0;
    double currentAlt = 0; // 180 is level. so is zero

    //90 is up
    // 270 is down

    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");


        /*************************************************************
         * Create a Float type Dynamic Control pair that displays as a slider and text box
         * Simply type floatBuddyControl to generate this code
         *************************************************************/
        FloatBuddyControl azimuthControl = new FloatBuddyControl(this, "Left / Right", 0, -1, 1) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/
                currentAz = control_val * 90;
                sendAltAz(currentAz, currentAlt);
                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl azimuthControl code ***/


        /*************************************************************
         * Create a Float type Dynamic Control pair that displays as a slider and text box
         * Simply type floatBuddyControl to generate this code
         *************************************************************/
        FloatBuddyControl altitudeControl = new FloatBuddyControl(this, "Altitude", 0, -1, 1) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/
                // we are going to convert x axis to the alt
                currentAlt = control_val * 90;
                updatePosition();
                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl altitudeControl code ***/


        /***** Type your HBAction code above this line ******/
    }

    void updatePosition() {
        double alt = Math.toRadians(currentAlt);
        sendAlt(alt);
    }


    /**
     * Send a post message to Stellarium
     * Code ragments from https://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
     * @param api the API we are sending to
     * @param params the post parameters
     * @return true on success
     */
    boolean sendMessage(String api, Map<String,Object> params) {
        boolean ret = false;

        try {
            URL url = new URL("http://localhost:8090/api/" + api);
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }

                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] post_data_bytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(post_data_bytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(post_data_bytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);
            String response = sb.toString();
            ret = response.equalsIgnoreCase("ok");
            //System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }

        return ret;

    }

    /**
     * Make stellarium move left or right in azimuth simulating arrow keys
     * a negative value signifies left
     * @param qty qty to move
     * @return true on success
     */
    boolean sendMoveLR(double qty){
        String api = "main/move";
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("x", qty);

        return sendMessage(api, params);
    }
    /**
     * Send just a new altitude message
     * Will make stellarium move to new altitude
     * @param alt altitude in radians
     * @return message state
     */
    boolean sendAlt(double alt) {
        String api = "main/view";
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("alt", alt);

        return sendMessage(api, params);
    }

    /**
     * Send new altaz position to stellarium
     * @param az new azimuth in radians
     * @param alt new altitude in radians
     * @return status message
     */
    boolean sendAltAz(double az, double alt){
        String api = "main/view";
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("az", az);
        params.put("alt", alt);

        return sendMessage(api, params);
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
