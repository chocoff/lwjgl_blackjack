package blackjack.engine.scene.lights;

import org.joml.Vector3f;

import java.util.*;

/*
 * this class stores references to all the types of lights
 * we only really need one ambient light instance and one directional light
 */
public class SceneLights {
    
    private AmbientLight ambientLight;
    private DirLight dirLight;
    //private List<DirLight> dirLights;
    private List<PointLight> pointLights;
    private List<SpotLight> spotLights;

    public SceneLights() {
        ambientLight = new AmbientLight();
        pointLights = new ArrayList<>();
        spotLights = new ArrayList<>();
        // dirLights = new ArrayList<>();
        // dirLights.add(new DirLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 1.0f));
        dirLight = new DirLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 1.0f);
    }

    //getters & setters
    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public DirLight getDirLight() {
        return dirLight;
    }

    // public List <DirLight> getDirLights() {
    //     return dirLights;
    // }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(List<SpotLight> spotLights) {
        this.spotLights = spotLights;
    }
}