package blackjack.engine.scene.lights;

import org.joml.Vector3f;


/*
 * Point light: This type of light models a light source that’s emitted 
 * uniformly from a point in space in all directions.
 */
public class PointLight {

    private Attenuation attenuation;
    private Vector3f color;
    private float intensity;
    private Vector3f position;
    
    public PointLight(){
        this(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, 0.0f), 1.0f);
    }
    
    public PointLight(Vector3f color, Vector3f position, float intensity) {
        attenuation = new Attenuation(0, 0, 1);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    // getters & setters
    public Attenuation getAttenuation() {
        return attenuation;
    }

    public Vector3f getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }
    
    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setColor(float r, float g, float b) {
        color.set(r, g, b);
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    /* Attenuation model
        attenuation is a function of the distance and light
        this is needed for simulating light attenuation, which consists
        on multiplying the attenuation factor by the final color
    */ 
    public static class Attenuation {

        private float constant;
        private float exponent;
        private float linear;

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        //getters & setters
        public float getConstant() {
            return constant;
        }

        public float getExponent() {
            return exponent;
        }

        public float getLinear() {
            return linear;
        }

        public void setConstant(float constant) {
            this.constant = constant;
        }

        public void setExponent(float exponent) {
            this.exponent = exponent;
        }

        public void setLinear(float linear) {
            this.linear = linear;
        }
    }
}