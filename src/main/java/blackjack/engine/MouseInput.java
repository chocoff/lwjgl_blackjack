package blackjack.engine;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private Vector2f currentPos;
    private Vector2f displVec;
  //  private boolean inWindow;
    private boolean leftButtonPressed;
    private boolean leftButtonClicked;
    private Vector2f previousPos;
    private boolean rightButtonPressed;
    private boolean rightButtonClicked;



    public MouseInput(long windowHandle) {
        previousPos = new Vector2f(-1, -1);
        currentPos = new Vector2f();
        displVec = new Vector2f();
        leftButtonPressed = false;
        rightButtonPressed = false;
      //  inWindow = false;

        glfwSetCursorPosCallback(windowHandle, (handle, xpos, ypos) -> {
            currentPos.x = (float) xpos;
            currentPos.y = (float) ypos;
        });
     //   glfwSetCursorEnterCallback(windowHandle, (handle, entered) -> inWindow = entered);
        // glfwSetMouseButtonCallback(windowHandle, (handle, button, action, mode) -> {
        //     leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
        //     rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        // });
        glfwSetMouseButtonCallback(windowHandle, (handle, button, action, mods) -> {

            // // LEFT CLICK
            // if (button == GLFW_MOUSE_BUTTON_LEFT) {
            //     leftButtonPressed = (action == GLFW_PRESS);
            // }

            if (button == GLFW_MOUSE_BUTTON_1) {

                if (action == GLFW_PRESS) {
                    if (!leftButtonPressed) {   // ← transition detection
                        leftButtonClicked = true;     // is TRUE only 1 frame
                    }
                    leftButtonPressed = true;
                } 
                else if (action == GLFW_RELEASE) {
                    leftButtonPressed = false;
                }
            }

            // RIGHT CLICK
            if (button == GLFW_MOUSE_BUTTON_2) {

                if (action == GLFW_PRESS) {
                    if (!rightButtonPressed) {   // ← transition detection
                        rightButtonClicked = true;     // is TRUE only 1 frame
                    }
                    rightButtonPressed = true;
                } 
                else if (action == GLFW_RELEASE) {
                    rightButtonPressed = false;
                }
            }
        });
    }

    public boolean isLeftClicked() {
        boolean clicked = leftButtonClicked;
        leftButtonClicked = false;   // reset event after reading
        return clicked;
    }

    public boolean isRightClicked() {
        boolean clicked = rightButtonClicked;
        rightButtonClicked = false;   // reset event after reading
        return clicked;
    }

    public void forcePosition(double xpos, double ypos) {
    // Override all internal mouse states
    currentPos.x = (float) xpos;
    currentPos.y = (float) ypos;

    previousPos.x = (float) xpos;
    previousPos.y = (float) ypos;

    displVec.x = 0;
    displVec.y = 0;
    }


    public Vector2f getCurrentPos() {
        return currentPos;
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public void input() {
        displVec.x = 0;
        displVec.y = 0;
        /*if (previousPos.x > 0 && previousPos.y > 0) {
            if(inWindow){
                System.out.println("in window");
            }
            else{
                System.out.println("not in window");
            }*/

            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.y = (float) deltax;
            }
            if (rotateY) {
                displVec.x = (float) deltay;
            }
       // }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }
}