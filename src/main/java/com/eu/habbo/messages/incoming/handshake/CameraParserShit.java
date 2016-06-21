package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.incoming.MessageHandler;

import java.io.ByteArrayOutputStream;
import java.util.zip.Inflater;

public class CameraParserShit extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.packet.getBuffer().readFloat();
        byte[] buffer = new byte[4096 * 3];
        byte[] data = this.packet.getBuffer().readBytes(this.packet.getBuffer().readableBytes()).array();

        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();

        inflater.end();
        //Emulator.getLogging().logDebugLine(new String(output));
    }
}
