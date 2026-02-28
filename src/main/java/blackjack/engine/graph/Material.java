package blackjack.engine.graph;

import java.util.*;

import org.joml.Vector4f;
import blackjack.engine.Consts;

public class Material {

    private Vector4f diffuseColor;
    private Vector4f ambientColor;
    private List<Mesh> meshList;
    private String texturePath;
    private String normalMapPath;

    private float reflectance;
    private Vector4f specularColor;

    public Material() {
        meshList = new ArrayList<>();
        diffuseColor = Consts.DEFAULT_COLOR;
        ambientColor = Consts.DEFAULT_COLOR;
        specularColor = Consts.DEFAULT_COLOR;
    }

    //free resources
    public void cleanup() {
        meshList.forEach(Mesh::cleanup);
    }

    //getters and setters
    public List<Mesh> getMeshList() {
        return meshList;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getNormalMapPath() {
        return normalMapPath;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public float getReflectance() {
        return reflectance;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }
    
    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public void setNormalMapPath(String normalMapPath) {
        this.normalMapPath = normalMapPath;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }
}