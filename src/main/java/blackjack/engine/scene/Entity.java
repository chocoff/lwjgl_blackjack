package blackjack.engine.scene;

import org.joml.*;

public class Entity {

    private final String id;
    private final String modelId;
    private final boolean isSelectable;
    private Matrix4f modelMatrix;
    private Vector3f position;
    private Quaternionf rotation;
    private float scale;
    private AnimationData animationData;


    public Entity(String id, String modelId, boolean isSelectable) {
        this.id = id;
        this.modelId = modelId;
        this.isSelectable = isSelectable;
        modelMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = 1;
    }

    //getters & setters
    public String getId() {
        return id;
    }
    
    public String getModelId() {
        return modelId;
    }
    public boolean isSelectable() {
        return isSelectable;
    }
    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public AnimationData getAnimationData() {
        return animationData;
    }
    
    public final void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setRotation(float x, float y, float z, float angle) {
        this.rotation.fromAxisAngleRad(x, y, z, angle);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void updateModelMatrix() {
        modelMatrix.translationRotateScale(position, rotation, scale);
    }
    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }
}