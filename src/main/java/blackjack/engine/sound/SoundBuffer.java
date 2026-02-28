package blackjack.engine.sound;

import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/*
 * we provide a file path and the constructor creates a buffer from it
 * the sound buffer will be identified by an integer which is like a pointer to the data it holds
 * once created, we dump the audio in the buffer
 * files must be OGG, then they are transformed to PCM with readVorbis method
 */
public class SoundBuffer {

    private final int bufferId;

    private ShortBuffer pcm;

    public SoundBuffer(String filePath){
        this.bufferId = alGenBuffers();

        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            pcm = readVorbis(filePath, info);

            //copy to buffer
            alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());        
        }
    }

    //free resources
    public void cleanup(){
        alDeleteBuffers(this.bufferId);

        if (pcm != null){
            MemoryUtil.memFree(pcm);
        }
    }

    //getters and setters
    public int getBufferId() {
        return bufferId;
    }


    // transform OGG format into PCM format
    private ShortBuffer readVorbis(String filePath, STBVorbisInfo info){
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_filename(filePath, error, null);

            if (decoder == NULL){
                throw new RuntimeException("Failed to open OGG, error: " + error.get(0));
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();
            
            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            ShortBuffer result = MemoryUtil.memAllocShort(lengthSamples * channels);
            
            result.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, result) * channels);
            stb_vorbis_close(decoder);

            return result;
        }
    }
}
