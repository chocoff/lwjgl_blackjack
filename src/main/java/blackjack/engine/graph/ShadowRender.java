package blackjack.engine.graph;

import blackjack.engine.Consts;
import blackjack.engine.scene.*;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;

//We put in use CascadeShadow, ArrTexture, and ShadowBuffer classes for CSM
public class ShadowRender {

    private ArrayList<CascadeShadow> cascadeShadows;
    private ShaderProgram shaderProgram;
    private ShadowBuffer shadowBuffer;
    private UniformsMap uniformsMap;

    public ShadowRender() {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("/shaders/shadow.vert", GL_VERTEX_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);

        shadowBuffer = new ShadowBuffer();

        cascadeShadows = new ArrayList<>();
        for (int i = 0; i < Consts.SHADOW_MAP_CASCADE_COUNT; i++) {
            CascadeShadow cascadeShadow = new CascadeShadow();
            cascadeShadows.add(cascadeShadow);
        }

        createUniforms();
    }

    //free resources
    public void cleanup() {
        shaderProgram.cleanup();
        shadowBuffer.cleanup();
    }

    private void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("projViewMatrix");
        uniformsMap.createUniform("bonesMatrices");
    }

    //getters
    public ArrayList<CascadeShadow> getCascadeShadows() {
        return cascadeShadows;
    }

    public ShadowBuffer getShadowBuffer() {
        return shadowBuffer;
    }

    //RENDER OMG RENDER
public void render(Scene scene) {
        CascadeShadow.updateCascadeShadows(cascadeShadows, scene);

        glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer.getDepthMapFBO());
        glViewport(0, 0, Consts.SHADOW_MAP_WIDTH, Consts.SHADOW_MAP_HEIGHT);

        shaderProgram.bind();

        Collection<Model> models = scene.getModelMap().values();
        for (int i = 0; i < Consts.SHADOW_MAP_CASCADE_COUNT; i++) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getDepthMapTexture().getIds()[i], 0);
            glClear(GL_DEPTH_BUFFER_BIT);

            CascadeShadow shadowCascade = cascadeShadows.get(i);
            uniformsMap.setUniform("projViewMatrix", shadowCascade.getProjViewMatrix());

            for (Model model : models) {
                List<Entity> entities = model.getEntitiesList();
                for (Material material : model.getMaterialList()) {
                    for (Mesh mesh : material.getMeshList()) {
                        glBindVertexArray(mesh.getVaoId());
                        for (Entity entity : entities) {
                            uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                            AnimationData animationData = entity.getAnimationData();
                            if (animationData == null) {
                                uniformsMap.setUniform("bonesMatrices", Consts.DEFAULT_BONES_MATRICES);
                            } else {
                                uniformsMap.setUniform("bonesMatrices", animationData.getCurrentFrame().boneMatrices());
                            }
                            glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                        }
                    }
                }
            }
        }

        shaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
