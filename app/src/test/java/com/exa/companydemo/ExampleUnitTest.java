package com.exa.companydemo;

import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("doCommand: " + i);
            doCommand("am broadcast -a android.intent.action.MEDIA_MOUNTED");
        }
    }

    private void doCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec("adb shell");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("doCommand.IOException");
        }
    }
}