package blackjack.engine.scene;

import org.joml.Matrix4f;
import blackjack.engine.Consts;

public class Projection {

    private Matrix4f projMatrix;
    private Matrix4f invProjMatrix;

    public Projection(int width, int height) {
        projMatrix = new Matrix4f();
        invProjMatrix = new Matrix4f();
        updateProjMatrix(width, height);
    }

    public void updateProjMatrix(int width, int height){
        projMatrix.setPerspective(Consts.FOV, (float) width / height, Consts.Z_NEAR, Consts.Z_FAR);
        invProjMatrix.set(projMatrix).invert();
    }

    //getters
    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

    public Matrix4f getInvProjMatrix() {
        return invProjMatrix;
    }
}