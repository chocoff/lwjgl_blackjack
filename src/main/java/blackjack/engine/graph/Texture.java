package blackjack.engine.graph;

import org.lwjgl.system.MemoryStack;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private int textureId;
    private String texturePath;

    public Texture(int width, int height, ByteBuffer buf) {
        this.texturePath = "";
        generateTexture(width, height, buf);
    }
    public Texture(String texturePath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.texturePath = texturePath;
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // 1. Load the resource into a ByteBuffer from the Classpath
            ByteBuffer resourceBuffer;
            try {
                // This utilizes the Utils helper you added previously
                resourceBuffer = blackjack.engine.Utils.ioResourceToByteBuffer(texturePath, 1024 * 1024);
            } catch (java.io.IOException e) {
                throw new RuntimeException("IMAGE FILE [" + texturePath + "] NOT FOUND", e);
            }

            // 2. Decode the image from memory instead of a file path
            ByteBuffer buf = stbi_load_from_memory(resourceBuffer, w, h, channels, 4);
            if (buf == null) {
                throw new RuntimeException("IMAGE FILE [" + texturePath + "] NOT LOADED: " + stbi_failure_reason());
            }

            int width = w.get();
            int height = h.get();

            generateTexture(width, height, buf);

            stbi_image_free(buf);
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    //free resources
    public void cleanup() {
        glDeleteTextures(textureId);
    }

    private void generateTexture(int width, int height, ByteBuffer buf) {
        textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
    }
    //getter

    public String getTexturePath() {
        return texturePath;
    }
}
