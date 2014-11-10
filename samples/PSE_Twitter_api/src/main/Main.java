package main;

import java.io.IOException;

import processing.core.PApplet;
import twitter4j.TwitterException;

public class Main {

    public static void main(String[] args) throws TwitterException,
            IOException, InterruptedException {

        // Thread thread1 = new Thread(new TestClass(1));
        Thread thread2 = new Thread(new TestClass(2));
        // thread1.start();
        thread2.start();

        PApplet.main(new String[] {MapTest.class.getName() });

        // thread1.join();
        thread2.join();

    }
}