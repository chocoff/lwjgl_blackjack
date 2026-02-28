package blackjack.engine.scene.lights;

import org.joml.Vector3f;

/*
 * Directional lighting hits all the objects by parallel rays all 
 * coming from the same direction. It models light sources that are far 
 * away but have a high intensity such as the Sun.
 */
public class DirLight {

    private Vector3f color;

    private Vector3f direction;
    
    private float intensity;

    public DirLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    //getters & setters
    public Vector3f getColor() {
        return color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setColor(float r, float g, float b) {
        color.set(r, g, b);
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setPosition(float x, float y, float z) {
        direction.set(x, y, z);
    }
}