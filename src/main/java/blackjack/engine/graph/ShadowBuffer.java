package blackjack.engine.graph;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

import blackjack.engine.Consts;

public class ShadowBuffer {

    private final ArrTexture depthMap;
    private final int depthMapFBO;

    public ShadowBuffer() {
        // create a FBO to render the depth map
        depthMapFBO = glGenFramebuffers();

        // create the depth map textures
        depthMap = new ArrTexture(Consts.SHADOW_MAP_CASCADE_COUNT, Consts.SHADOW_MAP_WIDTH, Consts.SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);

        // attach the the depth map texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getIds()[0], 0);

        // set only depth
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Could not create FrameBuffer");
        }

        // unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bindTextures(int start) {
        for (int i = 0; i < Consts.SHADOW_MAP_CASCADE_COUNT; i++) {
            glActiveTexture(start + i);
            glBindTexture(GL_TEXTURE_2D, depthMap.getIds()[i]);
        }
    }

    //free resources
    public void cleanup() {
        glDeleteFramebuffers(depthMapFBO);
        depthMap.cleanup();
    }

    //getters
    public int getDepthMapFBO() {
        return depthMapFBO;
    }

    public ArrTexture getDepthMapTexture() {
        return depthMap;
    }
}