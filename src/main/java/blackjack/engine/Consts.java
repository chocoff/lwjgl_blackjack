package blackjack.engine;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Consts {


    /*~~~ ENGINE/WINDOW RELATED ~~~*/
    public static final int TARGET_UPS = 60;
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    /*~~~ MESH RELATED ~~~*/
    public static final int MAX_WEIGHTS = 4;


    /*~~~ CAMERA VIEW AND PROJECTION RELATED ~~~*/
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000.f;

    /*~~~ TEXTURE RELATED ~~~*/
    public static final String DEFAULT_TEXTURE = "/models/default/default_texture.png";
    public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    
    /*~~~ LIGHT RELATED ~~~*/
    public static final int MAX_POINT_LIGHTS = 6;
    public static final int MAX_SPOT_LIGHTS = 6;

    /*~~~ SHADOW RELATED ~~~*/
    public static final int SHADOW_MAP_CASCADE_COUNT = 3;
    public static final int SHADOW_MAP_WIDTH = 4096;
    public static final int SHADOW_MAP_HEIGHT = SHADOW_MAP_WIDTH;
    public static final int TOTAL_TEXTURES = 4;

    
    /*~~~ MOUSE RELATED ~~~ */
    public static final float MOUSE_SENS = 0.05f;
    public static final float MOVEMENT_SPEED = 0.005f;

    /*~~~ ANIMATION RELATED ~~~*/
    public static final int MAX_BONES = 150;
    public static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
    public static final Matrix4f[] DEFAULT_BONES_MATRICES = new Matrix4f[MAX_BONES];

}
