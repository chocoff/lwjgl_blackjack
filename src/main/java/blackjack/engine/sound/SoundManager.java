package blackjack.engine.sound;

import org.joml.*;
import org.lwjgl.openal.*;
import blackjack.engine.scene.Camera;

import java.nio.*;
import java.util.*;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.Random;

/*
 * this class has references to the SoundBuffer and SoundSource instances to track and later cleanup
 * buffers are stored in a list and sources in a map (so they can be retrieved by name)
 * 
 * the constructor opens the default device, creates the capabilities for it, and create a sound
 * context and sets it as the current one
 * 
 * some of the methods defined here are for: 
 * adding sound sources/buffers, cleanup, manage listener and sources,
 * and also a function to active a sound using its name
 */
public class SoundManager {

    private final Map<String, SoundBuffer> soundBufferMap;
    private final Map<String, SoundSource> soundSourceMap;
    private Random random;

    private long context;
    private long device;

    private SoundListener listener;

    public SoundManager(){

        soundBufferMap = new HashMap<>();
        soundSourceMap = new HashMap<>();
        random = new Random();

        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL){
            throw new IllegalStateException("Failed to open the default OpenAL device");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL){
            throw new IllegalStateException("Failed to create OpenAL contet");
        }

        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    // Sound Type Enum
    public enum AudioType {
        // Music
        MUSIC_MAIN_MENU,
        MUSIC_GAMEPLAY_1,
        MUSIC_GAMEPLAY_2,
        MUSIC_GAMEPLAY_3,
        
        // Sound Effects
        SFX_BUTTON_1,
        SFX_BUTTON_2,
        SFX_CARD_DEAL,
        SFX_CHIP_1,
        SFX_CHIP_2,
        SFX_CHIP_3,
        SFX_CHIP_4,
        SFX_WIN,
        SFX_LOSE
    }

    public void loadSound(String name, String filePath) {
        SoundBuffer buffer = new SoundBuffer(filePath);
        soundBufferMap.put(name, buffer);
    }
    
    public void playSFX(String name) {
        SoundBuffer buffer = soundBufferMap.get(name);
        if (buffer != null) {
            // Use a separate source for SFX to allow overlapping
            SoundSource source = new SoundSource(false, false);
            source.setBuffer(buffer.getBufferId());
            source.play();
            
            scheduleSourceCleanup(source);
        }
    }

    private void scheduleSourceCleanup(SoundSource source) {
        new Thread(() -> {
            try {
                // Wait 3 seconds for short sound effects to finish
                Thread.sleep(3000);
                source.cleanup();
            } catch (InterruptedException e) {
                source.cleanup();
            }
        }).start();
    }
    
    public void playRandomChipSound() {
        int chipNum = random.nextInt(4) + 1;
        playSFX("CHIP_" + chipNum);
    }

    public void addSoundBuffer(SoundBuffer soundBuffer){
        this.soundBufferMap.put("buffer_" + soundBufferMap.size(), soundBuffer);
    }

    public void addSoundBuffer(String name, SoundBuffer soundBuffer) {
        this.soundBufferMap.put(name, soundBuffer);
    }

    public SoundBuffer getSoundBuffer(String name) {
        return this.soundBufferMap.get(name);
    }

    public void addSoundSource(String name, SoundSource soundSource){
        this.soundSourceMap.put(name, soundSource);
    }

    public void playSoundSource(String name){
        SoundSource soundSource = this.soundSourceMap.get(name);
        if (soundSource != null && !soundSource.isPlaying()){
            soundSource.play();
        }
    }

    public void removeSoundSource(String name){
        this.soundSourceMap.remove(name);
    }

    // update listener orientation given a camera position
    public void updateListenerPosition(Camera camera){
        Matrix4f viewMatrix = camera.getViewMatrix();
        listener.setPosition(camera.getPosition());

        Vector3f at = new Vector3f();
        Vector3f up = new Vector3f();

        viewMatrix.positiveZ(at).negate();
        viewMatrix.positiveY(up);

        listener.setOrientation(at, up);
    }


    //free resources
    public void cleanup(){
        // Clean up sound sources
        for (SoundSource source : soundSourceMap.values()) {
            source.cleanup();
        }
        soundSourceMap.clear();

        // Clean up sound buffers
        for (SoundBuffer buffer : soundBufferMap.values()) {
            buffer.cleanup();
        }
        soundBufferMap.clear();

        if (context != NULL){
            alcDestroyContext(context);
        }
        
        if (device != NULL){
            alcCloseDevice(device);
        }
    }
    //getters and setters

    public SoundListener getListener() {
        return this.listener;
    }

    public SoundSource getSoundSource(String name){
        return this.soundSourceMap.get(name);
    }

    public void setAttenuationModel(int model){
        alDistanceModel(model);
    }

    public void setListener(SoundListener listener) {
        this.listener = listener;
    }



}
