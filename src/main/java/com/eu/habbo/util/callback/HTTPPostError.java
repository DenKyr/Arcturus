package com.eu.habbo.util.callback;

import com.eu.habbo.Emulator;

public class HTTPPostError implements Runnable {

    public Throwable stackTrace;

    public HTTPPostError(Throwable stackTrace) {
        this.stackTrace = stackTrace;
    }

    private void sendPost() throws Exception {
        if (!Emulator.isReady) {
            return;
        }

        int responseCode = 0;

        /*StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         stackTrace.printStackTrace(pw);
         sw.toString(); // stack trace as a string

         String url = "http://arcturus.wf/callback/error.php";
         URL obj = new URL(url);
         HttpURLConnection con = (HttpURLConnection) obj.openConnection();

         //add reuqest header
         con.setRequestMethod("POST");
         con.setRequestProperty("User-Agent", "arcturus");

         String urlParameters = "errors=1&stacktrace=" + sw.toString() + "&users=" + Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "&rooms=" + Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + "&username=" + Emulator.getConfig().getValue("username");

         // Send post request
         con.setDoOutput(true);
         DataOutputStream wr = new DataOutputStream(con.getOutputStream());
         wr.writeBytes(urlParameters);
         wr.flush();
         wr.close();

         int responseCode = con.getResponseCode();*/
    }

    @Override
    public void run() {
        try {
            this.sendPost();
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
    }
}
