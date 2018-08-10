package stellarium.control;

import net.happybrackets.core.HBAction;
import net.happybrackets.core.control.ControlScope;
import net.happybrackets.core.control.FloatBuddyControl;
import net.happybrackets.core.control.FloatControl;
import net.happybrackets.core.control.FloatTextControl;
import net.happybrackets.device.HB;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class StellariumSlave implements HBAction {
    // Change to the number of audio Channels on your device
    final int NUMBER_AUDIO_CHANNELS = 1;

    @Override
    public void action(HB hb) {
        /***** Type your HBAction code below this line ******/
        // remove this code if you do not want other compositions to run at the same time as this one
        hb.reset();
        hb.setStatus(this.getClass().getSimpleName() + " Loaded");


        /*************************************************************
         * Create a Float type Dynamic Control that displays as a text box
         * Simply type floatTextControl to generate this code
         *************************************************************/
        FloatTextControl altRadians = new FloatTextControl(this, "Alt Radians", 0) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/

                /*** Write your DynamicControl code above this line ***/
            }
        };/*** End DynamicControl code lrRadians ***/
        // get our azimuth
        /*************************************************************
         * Create a Float type Dynamic Control that displays as a text box
         * Simply type floatTextControl to generate this code
         *************************************************************/
        FloatBuddyControl azimuthControl = new FloatBuddyControl(this, "global az", 0, 0, 360) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/
                // convert our value to radians
                double az = Math.toRadians(control_val);
                sendAz(az);

                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);
        /*** End DynamicControl code azimuthControl ***/

        // get our altitude
        /*************************************************************
         * Create a Float type Dynamic Control that displays as a text box
         * Simply type floatTextControl to generate this code
         *************************************************************/
        FloatBuddyControl altitudeControl = new FloatBuddyControl(this, "global alt", 0, -90, 89.999) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/
                double alt = Math.toRadians(control_val);
                sendAlt(alt);
                altRadians.setValue(alt);
                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);/*** End DynamicControl code altitudeControl ***/

        /*************************************************************
         * Create a Float type Dynamic Control that displays as a text box
         * Simply type floatTextControl to generate this code
         *************************************************************/
        FloatTextControl upDownControl = new FloatTextControl(this, "gloobal up/down", 0) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/
                sendMoveUD(control_val);
                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);/*** End DynamicControl code upDownCpntrol ***/

        /*************************************************************
         * Create a Float type Dynamic Control that displays as a text box
         * Simply type floatTextControl to generate this code
         *************************************************************/
        FloatTextControl leftRightControl = new FloatTextControl(this, "global left/right", 0) {
            @Override
            public void valueChanged(double control_val) {
                /*** Write your DynamicControl code below this line ***/
                sendMoveLR(control_val);
                /*** Write your DynamicControl code above this line ***/
            }
        }.setControlScope(ControlScope.GLOBAL);/*** End DynamicControl code leftRightControl ***/
        /***** Type your HBAction code above this line ******/
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
     * Send new azimuth position to stellarium
     * @param az new azimuth in radians
     * @return status message
     */
    boolean sendAz(double az){
        String api = "main/view";
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("az", az);

        return sendMessage(api, params);
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
     * Make stellarium move up or down in altitude simulating arrow keys
     * a negative value signifies left
     * @param qty qty to move
     * @return true on success
     */
    boolean sendMoveUD(double qty){
        String api = "main/move";
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("y", qty);

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
