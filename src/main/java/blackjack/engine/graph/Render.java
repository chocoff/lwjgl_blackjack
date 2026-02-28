package blackjack.engine.graph;

import org.lwjgl.opengl.GL;
import blackjack.engine.Window;
import blackjack.engine.scene.Scene;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

public class Render {
    
    private GBuffer gBuffer;
    private GuiRender guiRender;
    private LightsRender lightsRender;
    private SceneRender sceneRender;
    private ShadowRender shadowRender;
    private SkyBoxRender skyBoxRender;

    public Render(Window window) {
        GL.createCapabilities();
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);

        //support for transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        sceneRender = new SceneRender();
        guiRender = new GuiRender(window);
        skyBoxRender = new SkyBoxRender();
        shadowRender = new ShadowRender();
        lightsRender = new LightsRender();
        gBuffer = new GBuffer(window);
    }

    //free resources
    public void cleanup() {
        sceneRender.cleanup();
        guiRender.cleanup();
        skyBoxRender.cleanup();
        shadowRender.cleanup();
        lightsRender.cleanup();
        gBuffer.cleanup();
    }

    private void lightRenderFinish() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void lightRenderStart(Window window) {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        glBindFramebuffer(GL_READ_FRAMEBUFFER, gBuffer.getGBufferId());
    }

    public void render(Window window, Scene scene) {
        shadowRender.render(scene);
        sceneRender.render(scene, gBuffer);
        lightRenderStart(window);
        lightsRender.render(scene, shadowRender, gBuffer);
        skyBoxRender.render(scene);
        lightRenderFinish();
        guiRender.render(scene);
    }

    public void resize(int width, int height) {
        guiRender.resize(width, height);
    }
    
    @SuppressWarnings("unused")
    private void renderCrosshair(Window window){
        float crosshairSize = 10.0f;
        float thickness = 2.0f;

        float centerX = window.getWidth() / 2.0f;
        float centerY = window.getHeight() / 2.0f;

        glDisable(GL_DEPTH_TEST);
        
        // Switch to a Projection Matrix that maps window coordinates (0, 0 to width, height)
        // Note: We need to use glMatrixMode for fixed-function pipeline setup.
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // Set up orthographic projection (left, right, bottom, top, near, far)
        glOrtho(0.0, window.getWidth(), window.getHeight(), 0.0, -1.0, 1.0);
        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity(); // Reset the modelview matrix

        // 2. Draw the Crosshair
        
        // Set white color
        glColor3f(1.0f, 1.0f, 1.0f); 
        glLineWidth(thickness);

        glBegin(GL_LINES);

        // Horizontal Line
        glVertex2f(centerX - (crosshairSize / 2.0f), centerY);
        glVertex2f(centerX + (crosshairSize / 2.0f), centerY);

        // Vertical Line
        glVertex2f(centerX, centerY - (crosshairSize / 2.0f));
        glVertex2f(centerX, centerY + (crosshairSize / 2.0f));

        glEnd();

        // 3. Restore 3D State
        glEnable(GL_DEPTH_TEST);
    }
}