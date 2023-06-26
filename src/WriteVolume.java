//NAME: ARNAV BATRA
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WriteVolume {
    static File wavFile = new File("C:\\Users\\linka\\IdeaProjects\\MicrophoneVolume\\InputAudio.wav"); //CHANGE THE FILE PATH TO MATCH YOUR SYSTEMS'S LOCATION
    TargetDataLine line; //line to start capturing mic data
    public static AudioInputStream ais; //initial store of data to be transferred to wavFile
    public AudioFormat getAudioFormat() { //initial conditions for wavFile format
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        return format;
    }
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); //characteristics of DataLine
            line = (TargetDataLine) AudioSystem.getLine(info); //modifies line to be of the correct format
            line.open(format); //opens the line
            line.start();   // start capturing


            ais = new AudioInputStream(line); //puts info of line into ais

            // start recording
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile); //copies AIS values to wavFile

        } catch (LineUnavailableException | IOException ex) { //handles exceptions for missing line or file
            ex.printStackTrace();
        }
    }
    //Closes target data line so that the resulting file can be read in computeVolume() within the Microphone Volume Class
    void finish() {
        line.stop();
        line.close();
    }

}
