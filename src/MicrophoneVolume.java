//NAME: ARNAV BATRA
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.lang.Float.NaN;

//NAME : ARNAV BATRA
//Goal : Read in microphone as input field, with volume as attribute. Use volume input from microphone to determine set output volume value.
//NOTE : USES GRAPHICS
//CREDIT : Basis for recording from microphone by https://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
public class MicrophoneVolume implements ActionListener{
    private final JFrame frame; //Next three lines are the components of the graphics
    private final JPanel panel;
    private final JButton startRec;
    static final long recordTime = 5000; //Time recording runs for in milliseconds = 5 seconds

    public MicrophoneVolume(){ //Initializes graphics and sets the close operations.
        frame = new JFrame();
        frame.setSize(400,400);
        startRec = new JButton("Audio Capture starts when clicked"); //Button used to capture audio
        startRec.setBounds(150,200,100,50);
        startRec.addActionListener(this); //Action listener for the button so that recording starts when button is pressed.
        panel = new JPanel();
        panel.add(startRec);
        frame.add(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) { //Start for recording
        final WriteVolume recorder = new WriteVolume();
        // creates a new thread that waits for a specified amount of time before stopping
        Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(recordTime); //Runs the thread for 5 seconds
            } catch (InterruptedException ex) { //catches exception if Thread.sleep() is interrupted in the middle of execution
                ex.printStackTrace();
            }
            recorder.finish(); //Closes the Data line
        });
        stopper.start(); //begins execution of Thread...basically, compiler jumps up to the lambda expression defining what the Thread should do once started

        // start recording
        recorder.start(); //starts recording.
        try {
            System.out.printf("%.3fdB\n", computeVolume()); //formats resulting decibel level to three decimal places via the computeVolume() method.
        } catch (IOException | UnsupportedAudioFileException ex) { //handles exceptions in computeVolume()
            throw new RuntimeException(ex);
        }
    }

    public double computeVolume() throws IOException, UnsupportedAudioFileException { //goal : calculate the average level (in dB) of the .wav file
        float averageAmplitude = 0.0f; //resets the averageAmplitude in the case of one recording after another
        int count = 0; //count for the number of times individual amplitude is determined...allows for the computation of average
        AudioInputStream in = AudioSystem.getAudioInputStream(WriteVolume.wavFile); //converts wavFile to AIS so that AIS methods can be used
        AudioFormat fileFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 2, 4, 44100, true); //same settings as AIS in WriteVolume.java to provide "handshake" of format between both classes
        AudioInputStream resultingStream = AudioSystem.getAudioInputStream(fileFormat, in); //applies format to wavFile

        byte[] array = new byte[4]; //4 bytes in an int
        int read = resultingStream.read(array); //calls read() on resultingStream to read at specific point
        while (read != -1) { //While there is still data to read
            ByteBuffer bb = ByteBuffer.wrap(array); //wraps byte array into buffer
            bb.order(ByteOrder.BIG_ENDIAN); //wavFile is written in Big Endian, so proper reading must be Big Endian as well.
            float amplitude = bb.asFloatBuffer().get(); //individual amplitude at certain point
            if (amplitude >= 1.4E-45 && amplitude <= 3.4028235E38){ //this if statement averages the float values of each indivudal amplitude
                averageAmplitude = ((averageAmplitude * count) + Math.abs(amplitude)) / ++count;
            }
            read = resultingStream.read(array); //updates read so that while loop covers the whole .wav file
        }
        return Math.abs(Math.log10(averageAmplitude)); //returns the magnitude of the amplitude in decibels

    }
    public static void main(String[] args) {
        MicrophoneVolume test = new MicrophoneVolume();
    }
}