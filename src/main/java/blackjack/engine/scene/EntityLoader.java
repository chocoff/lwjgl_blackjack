package blackjack.engine.scene;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import blackjack.engine.Window;
import blackjack.engine.graph.Material;
import blackjack.engine.graph.Mesh;
import blackjack.engine.graph.Model;
import imgui.ImGuiIO;

// Testeo
import org.lwjgl.glfw.GLFWKeyCallback;
import blackjack.logic.*;
import blackjack.engine.MouseInput;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
public class EntityLoader {

    private AnimationData animationData;
    private Entity terrainEntity;
    private Entity bobEntity;
    private Entity cubeEntity;
    private Entity chairEntity;
    private Entity tableEntity;
    private Entity casinoEntity;
    private Entity newTableEntity;

    private Entity[] chipsEntities;
    private static Map<String, Model> chipModels = new HashMap<>();
    private static List<Entity> allChipEntities = new ArrayList<>();
    public static final String[] CHIP_VALUES = {"10", "50", "100", "500", "1000"};


    private Entity chipEntity;
    private static Entity hiddenCardEntity;
    private static Entity backCardEntity;
    private static Entity chip10Entity;
    private static Entity chip50Entity;
    private static Entity chip100Entity;
    private static Entity chip500Entity;
    private static Entity chip1000Entity;
    private static Entity hoveredEntity = null;
    private static String ChipSelected = null;
    private static float offsetY_Chips;

    // Variables for moveChips function
    private static float offsetY_start = 0.87f;
    private static float offsetY_10 = 0;
    private static float offsetY_50 = 0;
    private static float offsetY_100 = 0;
    private static float offsetY_500 = 0;
    private static float offsetY_1000 = 0;
    private static float counter[] = {0, 0, 0, 0, 0 };

    public static void getChipSelected(String chipSelected, Scene scene, float z) {
        BlackJackLogic.betChips(chipSelected, scene, z);
    }

    public static void removeChipSelected(String chipSelected, Scene scene, float z) {
        BlackJackLogic.undoBetChips(chipSelected, scene, z);
    }


    private static Map<String, Model> cardModels = new HashMap<>();
    public static Map<String, Model> getCardModels() {
        return cardModels;
    }

    private static float cardScale = 0.06f;
    private static final float HIDDEN_X = -0.40f;
    private static final float DEALER_START_X = -0.40f;
    private static final float PLAYER_START_X = -0.40f;
    private static final float Y = 0.87f;
    private static final float DEALER_START_Z = 0.55f;
    private static final float PLAYER_START_Z = 1.05f;

    private static float dealerOffsetX = 0f;
    private static float playerOffsetX = 0f;
    private static float dealerOffsetY = 0f;
    private static float playerOffsetY = 0f;
    private static float dealerOffsetZ = 0f;
    private static float playerOffsetZ = 0f;

    public enum CardType {
        HIDDEN,
        DEALER,
        PLAYER
    }
    // Temporal
    private class Card {
        String value;
        String type; 

        // Now we created a constructor
        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return type + "_" + value;
        }

        public int getValue() {
            if(value == "1") {
                return 11;
            }
            else if(value == "11" || value == "12" || value == "13") {
                return 10;
            }
            else {
                return Integer.parseInt(value); // 2 ~ 10
            }
            
        }

        public boolean isAce() {
            if(value == "1") {
                return true;
            }
            else {
                return false;
            }
        }

        public String getPath() {
            return "/models/cards/" + toString() + ".obj";
        }
    }
    
    public void loadEntities(Scene scene){
        // define models to be rendered
        Model terrainModel = ModelLoader.loadModel(
            "terrain-model", 
            "/models/terrain/terrain.obj",
            scene.getTextureCache(),
            false
        );

        Model bobModel = ModelLoader.loadModel(
            "bob-model",
            "/models/bob/boblamp.md5mesh",
            scene.getTextureCache(),
            true
        );

        Model cubeModel = ModelLoader.loadModel(
            "cube-model",
            "/models/cube/cube.obj",
            scene.getTextureCache(),
            false
        );

        Model chairModel = ModelLoader.loadModel(
            "chair-model",
            "/models/wooden_chair/Wooden_Chair.obj",
            scene.getTextureCache(),
            false
        );

        Model tableModel = ModelLoader.loadModel(
            "table-model",
            "/models/table/blackjack_table.obj",
            scene.getTextureCache(),
            false
        );

        Model casinoModel = ModelLoader.loadModel(
        "casino-model", 
        "/models/casino/ImageToStl.com_gameready_casino_scene/gameready_casino_scene.obj", 
        scene.getTextureCache(), 
        false
        );

        Model newTableModel = ModelLoader.loadModel(
            "newTable-model",
            "/models/table/blackjack_table.obj",
            scene.getTextureCache(),
            false
        );

        scene.addModel(terrainModel); 
        scene.addModel(bobModel);
        scene.addModel(cubeModel);
        scene.addModel(chairModel);
        scene.addModel(tableModel);
        scene.addModel(casinoModel);
        scene.addModel(newTableModel);

        //define entity properties
        terrainEntity = new Entity("terrain-entity", terrainModel.getId(), false);
        terrainEntity.setScale(100.0f);

        bobEntity = new Entity("bob-entity", bobModel.getId(), false);
        bobEntity.setScale(0.05f);
        bobEntity.setPosition(0, -0.5f, -0.5f);
//        animationData = new AnimationData(bobModel.getAnimationList().get(0));
	if (!bobModel.getAnimationList().isEmpty()) {
	  animationData = new AnimationData(bobModel.getAnimationList().get(0));
	  bobEntity.setAnimationData(animationData);
	} else {
	  System.err.println("Warning: no animations found for Bob model");
	}
//        bobEntity.setAnimationData(animationData);
                
        cubeEntity = new Entity("cube-entity", cubeModel.getId(), true);
        cubeEntity.setPosition(0.0f, 0.0f, -2.0f);
        
        chairEntity = new Entity("chair-entity", chairModel.getId(), true);
        chairEntity.setPosition(0.0f, 0.0f, -2.0f);
        
        tableEntity = new Entity("table-entity", tableModel.getId(), false);
        tableEntity.setScale(1.5f);
        tableEntity.setPosition(0f, -0.44f, -0.3f);

        newTableEntity = new Entity("newTable-entity", newTableModel.getId(), false);
        newTableEntity.setScale(0.0012f);
        newTableEntity.setPosition(0.0f, 0.0f, 1.2f);

        casinoEntity = new Entity("casino-entity", casinoModel.getId(), false);
        casinoEntity.setScale(0.035f);
        casinoEntity.setPosition(24.5f, 9.58f, -13.0f);
        
        terrainEntity.updateModelMatrix();
        bobEntity.updateModelMatrix();
        //cubeEntity.updateModelMatrix();
        //chairEntity.updateModelMatrix();
        tableEntity.updateModelMatrix();
        //newTableEntity.updateModelMatrix();
        casinoEntity.updateModelMatrix();

        scene.addEntity(terrainEntity);
        scene.addEntity(bobEntity);
        //scene.addEntity(cubeEntity);
        //scene.addEntity(chairEntity);
        scene.addEntity(tableEntity);
       // scene.addEntity(newTableEntity);
        scene.addEntity(casinoEntity);

        // Dynamically add the chips
        String[] chipValues = {"10", "50", "100", "500", "1000"};

        for (int i = 0; i < chipValues.length; i++) {

            String value = chipValues[i];

            String modelId = "chipModel_" + value;
            String modelPath = "/models/blackjack chips/poker_chip_" 
                                + value + "/poker_chip_" + value + ".obj";

            Model chipModel = ModelLoader.loadModel(
                modelId, modelPath, scene.getTextureCache(), false
            );

            scene.addModel(chipModel);

            chipModels.put(value, chipModel);   

            loadChips(i, value, scene);
        }

        // Dynamically add the cards
        String[] values = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
        String [] types = {"clubs", "diamonds", "hearts", "spades"}; 
        
        // Load all cards models
        for (String type : types) {
            for (String value : values) {

                Card card = new Card(value, type);

                String modelId = card.toString();   
                String modelPath = card.getPath();  

                // Load model ONCE per card
                Model model = ModelLoader.loadModel(
                    modelId,
                    modelPath,
                    scene.getTextureCache(),
                    false
                );

                cardModels.put(modelId, model);
                scene.addModel(model);
            }
        }
    }

    public static void loadChips(int i, String chipValue, Scene scene) {

        Model chipModel = chipModels.get(chipValue);
        if (chipModel == null) {
            System.err.println("Chip model not loaded: " + chipValue);
            return;
        }

        String modelHandle = chipModel.getId();   // MUST be String (same as cards)

        float offsetY = 0.87f;

        for (int j = 0; j < 5; j++) {

            String entityId = chipValue + j;
            Entity entity = new Entity(entityId, modelHandle, true); // now correct

            float offsetX = (i - 5 / 2f) * 0.1f;
            offsetY += 0.01f;

            entity.setPosition(offsetX, offsetY, 1.55f);
            entity.updateModelMatrix();
            scene.addEntity(entity);

            allChipEntities.add(entity);

            switch (chipValue) {
                case "10":   chip10Entity = entity; break;
                case "50":   chip50Entity = entity; break;
                case "100":  chip100Entity = entity; break;
                case "500":  chip500Entity = entity; break;
                case "1000": chip1000Entity = entity; break;
            }
        }
    }

    public static void clearChips(Scene scene) {

        for (Entity e : allChipEntities) {
            scene.removeEntity(e);   // you MUST have/remove this method in Scene
        }

        allChipEntities.clear();

        // Reset your reference variables
        chip10Entity = null;
        chip50Entity = null;
        chip100Entity = null;
        chip500Entity = null;
        chip1000Entity = null;
    }

    public static void moveBetChips(Scene scene, String chipValue) {
        // Remove selected chip 
        if (hoveredEntity != null) {
            Entity newChip = hoveredEntity;
            scene.removeEntity(hoveredEntity);
            hiddenCardEntity = null;

            int index = Integer.parseInt(chipValue) / 10;
            switch (index) {
                case 10:
                    counter[0] = counter[0] + 1.0f;
                    offsetY_10 = offsetY_start + 0.01f*(counter[0]);
                    newChip.setPosition(-0.25f, offsetY_10, 1.45f);
                    break;

                case 50:
                    counter[1] = counter[1] + 1.0f;
                    offsetY_50 = offsetY_start + 0.01f*(counter[1]);
                    newChip.setPosition(-0.15f, offsetY_50, 1.45f);
                    break;

                case 100:
                    counter[2] = counter[2] + 1.0f;   
                    offsetY_100 = offsetY_start + 0.01f*(counter[2]);
                    newChip.setPosition(-0.05f, offsetY_100, 1.45f);
                    break;

                case 500:
                    counter[3] = counter[3] + 1.0f;
                    offsetY_500 = offsetY_start + 0.01f*(counter[3]);
                    newChip.setPosition(0.05f, offsetY_500, 1.45f);
                    break;

                case 1000:
                    counter[4] = counter[4] + 1.0f;
                    offsetY_1000 = offsetY_start + 0.01f*(counter[4]);
                    newChip.setPosition(0.15f, offsetY_1000, 1.45f);
                    break;
            }
            scene.addEntity(newChip);
            newChip.updateModelMatrix();
        }
    }

    public static void removeBetChips(Scene scene, String chipValue) {
        // Remove selected chip 
        if (hoveredEntity != null) {
            Entity newChip = hoveredEntity;
            scene.removeEntity(hoveredEntity);
            hiddenCardEntity = null;

            int index = Integer.parseInt(chipValue) / 10;
            switch (index) {
                case 10:
                    counter[0] = counter[0] - 1.0f;
                    offsetY_10 = offsetY_start + 0.01f*(5.0f-counter[0]);
                    newChip.setPosition(-0.25f, offsetY_10, 1.55f);
                    break;
                case 50:
                    counter[1] = counter[1] - 1.0f;
                    offsetY_50 = offsetY_start + 0.01f*(5.0f-counter[1]);
                    newChip.setPosition(-0.15f, offsetY_50, 1.55f);
                    break;
                case 100:
                    counter[2] = counter[2] - 1.0f;
                    offsetY_100 = offsetY_start + 0.01f*(5.0f-counter[2]);
                    newChip.setPosition(-0.05f, offsetY_100, 1.55f);
                    break;
                case 500:
                    counter[3] = counter[3] - 1.0f;
                    offsetY_500 = offsetY_start + 0.01f*(5.0f-counter[3]);
                    newChip.setPosition(0.05f, offsetY_500, 1.55f);
                    break;
                case 1000:
                    counter[4] = counter[4] - 1.0f;
                    offsetY_1000 = offsetY_start + 0.01f*(5.0f-counter[4]);
                    newChip.setPosition(0.15f, offsetY_1000, 1.55f);
                    break;
            }
            scene.addEntity(newChip);
            newChip.updateModelMatrix();
        }
    }

    public static void resetCounter() {
        counter[0] = 0;
        counter[1] = 0;
        counter[2] = 0;
        counter[3] = 0;
        counter[4] = 0;
    }

    public void selectEntity(Window window, Scene scene, Vector2f mousePos){
        int wdwWidth = window.getWidth();
        int wdwHeight = window.getHeight();

        float x = (2 * mousePos.x) / wdwWidth - 1.0f;
        float y = 1.0f - (2 * mousePos.y) / wdwHeight;
        float z = -1.0f;

        Matrix4f invProjMatrix = scene.getProjection().getInvProjMatrix();
        Vector4f mouseDir = new Vector4f(x, y, z, 1.0f);

        mouseDir.mul(invProjMatrix);
        mouseDir.z = -1.0f;
        mouseDir.w = 0.0f;

        Matrix4f invViewMatrix = scene.getCamera().getInvViewMatrix();
        mouseDir.mul(invViewMatrix);

        Vector4f min = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector4f max = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector2f nearFar = new Vector2f();

        Entity selectedEntity = null;
        float closestDistance = Float.POSITIVE_INFINITY;
        Vector3f center = scene.getCamera().getPosition();

        Collection<Model> models = scene.getModelMap().values();
        Matrix4f modelMatrix = new Matrix4f();

        for (Model model : models){
            List<Entity> entities = model.getEntitiesList();

            for (Entity entity : entities){

                if (!entity.isSelectable()){
                    continue;
                }
                modelMatrix.translate(entity.getPosition()).scale(entity.getScale());
                
                for (Material material : model.getMaterialList()){
                    for (Mesh mesh : material.getMeshList()){
                        
                        Vector3f aabbMin = mesh.getAabbMin();
                        min.set(aabbMin.x, aabbMin.y, aabbMin.z, 1.0f);
                        min.mul(modelMatrix);

                        Vector3f aabbMax = mesh.getAabbMax();
                        max.set(aabbMax.x, aabbMax.y, aabbMax.z, 1.0f);
                        max.mul(modelMatrix);

                        if (Intersectionf.intersectRayAab(center.x, center.y, center.z, mouseDir.x, mouseDir.y, mouseDir.z,
                                min.x, min.y, min.z, max.x, max.y, max.z, nearFar) && nearFar.x < closestDistance) {
                            closestDistance = nearFar.x;
                            selectedEntity = entity;
                        }
                    }
                }
                modelMatrix.identity();
            }
        }
        scene.setSelectedEntity(selectedEntity);
        hoveredEntity = selectedEntity; 

    }

    public static void clickChips(Scene scene, boolean isLeftClick) {
        if (hoveredEntity == null) return;

        ChipSelected = hoveredEntity.getId();
        Vector3f position = hoveredEntity.getPosition();
        System.out.println(ChipSelected);
        System.out.println("isLeftClick: " + isLeftClick);
        
        if (isLeftClick) {
            // left click = bet
            getChipSelected(ChipSelected, scene, position.z);
        } else {
            // right click = undo bet
            removeChipSelected(ChipSelected, scene, position.z);
        }
    }

    public static void loadCard(String card, Scene scene, CardType type) {
        Model model = cardModels.get(card);
        if (model == null) {
            System.err.println("card wrong idk" + card);
            return;
        }
        
        Entity entity = new Entity("card-" + System.nanoTime(), model.getId(), false);
        entity.setScale(cardScale);

        switch (type) {

            case HIDDEN:
                hiddenCardEntity = entity;

                Model backModel = ModelLoader.loadModel(
                    "back-model",
                    "/models/backCard/back.obj",
                    scene.getTextureCache(),
                    false
                );
                scene.addModel(backModel);

                backCardEntity = new Entity("back-entity", backModel.getId(), false);
                backCardEntity.setScale(cardScale);
                backCardEntity.setPosition(DEALER_START_X, Y + dealerOffsetY, DEALER_START_Z + dealerOffsetZ);
                
                dealerOffsetX += 0.10f;
                dealerOffsetY += 0.01f;
                dealerOffsetZ += 0.03f;

                backCardEntity.updateModelMatrix();
                scene.addEntity(backCardEntity);
                return; 

            case DEALER:
                entity.setPosition(DEALER_START_X + dealerOffsetX, Y + dealerOffsetY, DEALER_START_Z+ dealerOffsetZ);
                dealerOffsetX += 0.10f;
                dealerOffsetY += 0.01f;
                dealerOffsetZ += 0.03f;
                break;

            case PLAYER:
                entity.setPosition(PLAYER_START_X + playerOffsetX, Y + playerOffsetY, PLAYER_START_Z + playerOffsetZ);
                playerOffsetX += 0.10f;
                playerOffsetY += 0.01f;
                playerOffsetZ += 0.03f;
                break;
        }
        

        entity.updateModelMatrix();
        scene.addEntity(entity);
    }

    public static void replaceHiddedCard(String card, Scene scene) {
        if (hiddenCardEntity != null) {
            scene.removeEntity(hiddenCardEntity);
            hiddenCardEntity = null;
        }

        Model card1 = cardModels.get(card); 
        Entity cardEntity = new Entity("card-" + System.nanoTime(), card1.getId(), false);
        cardEntity.setPosition(HIDDEN_X, Y, DEALER_START_Z);
        cardEntity.setScale(cardScale);
        cardEntity.updateModelMatrix();
        scene.addEntity(cardEntity);
        
    }

    public static void removeHiddenCard(Scene scene) {
        if (backCardEntity != null) {
            scene.removeEntity(backCardEntity);
            backCardEntity = null;
        }
    }

    public static void resetOffsets() {
        dealerOffsetX = 0f;
        playerOffsetX = 0f;
        dealerOffsetY = 0f;
        playerOffsetY = 0f;
        dealerOffsetZ = 0f;
        playerOffsetZ = 0f;
        hiddenCardEntity = null;
    }
    
    // getters for entities in case some class needs them for updating
    public Entity getChairEntity() {
        return chairEntity;
    }

    public Entity getCubeEntity() {
        return cubeEntity;
    }

    public Entity getTableEntity() {
        return tableEntity;
    }

    public Entity getBobEntity(){
        return bobEntity;
    }

    public Entity[] getChipsEntities() {
        return chipsEntities;
    }
    
    public AnimationData getAnimationData() {
        return animationData;
    }

}
