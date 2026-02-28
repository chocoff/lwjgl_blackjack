package blackjack.engine.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

/*
 * this class handles the sound sources we can set up and control:
 * position, gain, playing, stopping, pausing with the defined methods
 * sources can share the same buffer
 * 
 * constructor: 
 * > create the source
 * > check if the sound should be played in loop
 * > check if the position of the source is relative to the listener or not
 * note: when we set the position for a source, we are defining the distance to the listener, not the REAL position
 * so for background music we will set pos to 0 so they won't be attenuated
 * note2: long story short, if relative is false, the source will be static in the world and as u get near, it gets louder
 * if relative is true, the sound will always be at the specified distance from the listener
 */

public class SoundSource {

    private final int sourceId;
    private float volume = 1.0f;

    public SoundSource(boolean loop, boolean relative){
        this.sourceId = alGenSources();
        alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        alSourcei(sourceId, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
    }

    public void cleanup(){
        stop();
        alDeleteSources(sourceId);
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        alSourcef(sourceId, AL_GAIN, this.volume);
    }

    public float getVolume() {
        return volume;
    }

    public boolean isPlaying(){
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void pause(){
        alSourcePause(sourceId);
    }

    public void play(){
        alSourcePlay(sourceId);
    }

    public void setBuffer(int bufferId){
        stop();
        alSourcei(sourceId, AL_BUFFER, bufferId);
    }

    public void setGain(float gain){
        alSourcef(sourceId, AL_GAIN, gain);
    }

    public void setPosition(Vector3f position){
        alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
    }

    public void stop(){
        alSourceStop(sourceId);
    }

}
