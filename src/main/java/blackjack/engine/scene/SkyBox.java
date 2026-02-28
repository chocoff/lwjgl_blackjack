package blackjack.engine.scene;

import blackjack.engine.graph.*;

public class SkyBox {

    private Entity skyBoxEntity;
    private Model skyBoxModel;

    public SkyBox(String skyBoxModelPath, TextureCache textureCache) {
        skyBoxModel = ModelLoader.loadModel("skybox-model", skyBoxModelPath, textureCache, false);
        skyBoxEntity = new Entity("skyBoxEntity-entity", skyBoxModel.getId(), false);
    }

    public Entity getSkyBoxEntity() {
        return skyBoxEntity;
    }

    public Model getSkyBoxModel() {
        return skyBoxModel;
    }
}