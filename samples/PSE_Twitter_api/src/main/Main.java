package main;

import java.io.IOException;

import processing.core.PApplet;
import twitter4j.TwitterException;

@SuppressWarnings("unused")
public class Main {

    public static void main(String[] args) throws TwitterException,
            IOException, InterruptedException {

        TestClass test = new TestClass(2);
        test.run();

        // start stream
        // Thread thread = new Thread(new TestClass(2));
        // thread.start();

        // PApplet.main(new String[] {MapTest.class.getName() });

        // thread.join();

    }
}