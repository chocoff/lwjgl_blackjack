package blackjack.engine.scene;

import blackjack.engine.IGuiInstance;

/* 
* this class hold 3D scene elements (models, etc)
* currently it just tores the meshes (sets of vertices) of the models we want to dray
* hold a reference for SceneLights to render lights
*/

import blackjack.engine.graph.*;
import blackjack.engine.scene.lights.SceneLights;
import blackjack.engine.scene.EntityLoader;
import java.util.*;

public class Scene {

    private Camera camera;
    private Fog fog;
    private IGuiInstance guiInstance;
    private Map<String, Model> modelMap;
    private Projection projection;
    private SceneLights sceneLights;
    private SkyBox skyBox;
    private TextureCache textureCache;
    private Entity selectedEntity;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        camera = new Camera();
        fog = new Fog();
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntitiesList().add(entity);
    }

    public void removeEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntitiesList().remove(entity);
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public void removeModel(Model model) {
        modelMap.remove(model.getId());
    }

    //free resources
    public void cleanup() {
        modelMap.values().forEach(Model::cleanup);
    }
    
    public void clearCardEntities(Map<String, Model> cardModels) {
        for (Model cardModel : cardModels.values()) {
            cardModel.getEntitiesList().clear();
        }
    }

    //getters and setters
    public Camera getCamera() {
        return camera;
    }

    public Fog getFog() {
        return fog;
    }

    public IGuiInstance getGuiInstance() {
        return guiInstance;
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }


    public Projection getProjection() {
        return projection;
    }

    public SceneLights getSceneLights() {
        return sceneLights;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public void setGuiInstance(IGuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }

    public void setSceneLights(SceneLights sceneLights) {
        this.sceneLights = sceneLights;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public void setSelectedEntity(Entity selectedEntity){
        this.selectedEntity = selectedEntity;
    }
}