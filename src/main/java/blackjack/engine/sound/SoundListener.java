package blackjack.engine.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

/*
 * a difference with buffer and source is that we don't need to create a listener
 * there will always be one listener, the player
 * we have mehtods for defining the position and the speed but then we also have orientation
 * 
 * Orientation:
 * "at" vector points where the listener is facing
 * "up" vector determines which direction is up for the listener
 * 
 * note: if we want to simulate a moving source or listener, we need to update
 * their positions in the game loop (using setPosition)
 */

public class SoundListener {

    public SoundListener(Vector3f position){
        alListener3f(AL_POSITION, position.x, position.y, position.z);
        alListener3f(AL_VELOCITY, 0, 0, 0);
    }

    public void setOrientation(Vector3f at, Vector3f up){

        float[] data = new float[6];
        data[0] = at.x;
        data[1] = at.y;
        data[2] = at.z;
        data[3] = up.x;
        data[4] = up.y;
        data[5] = up.z;

        alListenerfv(AL_ORIENTATION, data);
    }

    //setters
    public void setPosition(Vector3f position){
        alListener3f(AL_POSITION, position.x, position.y, position.z);
    }

    public void setSpeed(Vector3f speed){
        alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z);
    }

}
