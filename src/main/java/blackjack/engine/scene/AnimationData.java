package blackjack.engine.scene;

import java.util.Arrays;

import org.joml.Matrix4f;

import blackjack.engine.Consts;
import blackjack.engine.graph.Model;

public class AnimationData {


    static {
        Matrix4f zeroMatrix = new Matrix4f().zero();
        Arrays.fill(Consts.DEFAULT_BONES_MATRICES, zeroMatrix);
    }

    private Model.Animation currentAnimation;
    private int currentFrameIdx;

    public AnimationData(Model.Animation currentAnimation) {
        currentFrameIdx = 0;
        this.currentAnimation = currentAnimation;
    }

    public Model.Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public Model.AnimatedFrame getCurrentFrame() {
        return currentAnimation.frames().get(currentFrameIdx);
    }

    public int getCurrentFrameIdx() {
        return currentFrameIdx;
    }

    public void nextFrame() {
        int nextFrame = currentFrameIdx + 1;
        if (nextFrame > currentAnimation.frames().size() - 1) {
            currentFrameIdx = 0;
        } else {
            currentFrameIdx = nextFrame;
        }
    }

    public void setCurrentAnimation(Model.Animation currentAnimation) {
        currentFrameIdx = 0;
        this.currentAnimation = currentAnimation;
    }
}