package com.androidsrc.client;

import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessageReader extends Thread {

    byte[] buffer = new byte[1024];
    int bytesRead;
    ByteArrayOutputStream byteArrayOutputStream;
    InputStream inputStream;
    String response = "";
    TextView textView;

    MessageReader(ByteArrayOutputStream byteArrayOutputStream, InputStream inputStream, TextView textResponse)
    {
        this.byteArrayOutputStream = byteArrayOutputStream;
        this.inputStream = inputStream;
        textView = textResponse;
    }

    @Override
    public void run() {
        try {
            if((bytesRead = inputStream.read(buffer)) != -1)
            {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
                textView.setText(textView.getText() + response);
                new MessageReader(byteArrayOutputStream,inputStream,textView).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
