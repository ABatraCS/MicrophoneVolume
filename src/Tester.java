import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

public class Tester {
    static File wavFile = new File("C:\\Users\\linka\\IdeaProjects\\MicrophoneVolume\\InputAudio.wav");
    static float maxAmplitude = 0.0f;
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        AudioInputStream in = AudioSystem.getAudioInputStream(wavFile);
        AudioFormat baseFormat = in.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 2, 4, 44100, true);
        AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);

        byte[] array = new byte[4];
        int read = din.read(array);
        while (read != -1) {
            ByteBuffer bb = ByteBuffer.wrap(array);
            bb.order(ByteOrder.BIG_ENDIAN);
            float amplitude = bb.asFloatBuffer().get();
            if (Math.abs(amplitude) > maxAmplitude){
                maxAmplitude = Math.abs(amplitude);
            }

            read = din.read(array);
        }
        System.out.println(Math.log10(maxAmplitude));

    }

}
