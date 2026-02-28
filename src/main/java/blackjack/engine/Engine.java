package blackjack.engine;

import blackjack.engine.graph.Render;
import blackjack.engine.scene.Scene;

public class Engine {

    private final IAppLogic appLogic;
    private final Window window;
    private Render render;
    private boolean running;
    private Scene scene;
    private int targetFps;
    private int targetUps;

    public Engine(String windowTitle, Window.WindowOptions opts, IAppLogic appLogic) {
        window = new Window(windowTitle, opts, () -> {
            resize();
            return null;
        });
        targetFps = opts.fps;
        targetUps = opts.ups;
        this.appLogic = appLogic;
        render = new Render(window);
        scene = new Scene(window.getWidth(), window.getHeight());
        appLogic.init(window, scene, render);
        running = true;
    }

    private void cleanup() {
        appLogic.cleanup();
        render.cleanup();
        scene.cleanup();
        window.cleanup();
    }

    private void resize() {
        int width = window.getWidth();
        int height = window.getHeight();
        scene.resize(width, height);
        render.resize(width, height);
    }

    //game loop is defined in this function
    private void run() {
        long initialTime = System.currentTimeMillis();
        // parameters that control the maximum elapsed time between updates (timeU) and render calls (timeR) in ms
        float timeU = 1000.0f / targetUps;
        float timeR = targetFps > 0 ? 1000.0f / targetFps : 0;
        float deltaUpdate = 0;
        float deltaFps = 0;

        long updateTime = initialTime;
        IGuiInstance iGuiInstance = scene.getGuiInstance(); 
        while (running && !window.windowShouldClose()) {
            window.pollEvents();    //start by polling events on the window
            
            long now = System.currentTimeMillis();  // get current time in ms duh
            deltaUpdate += (now - initialTime) / timeU; //get elapsed time between update and render calls
            deltaFps += (now - initialTime) / timeR;

            //if max elapsed time for render, process user input
            if (targetFps <= 0 || deltaFps >= 1) {
                window.getMouseInput().input();
                boolean inputConsumed = iGuiInstance != null && iGuiInstance.handleGuiInput(scene, window); 
                appLogic.input(window, scene, now - initialTime, inputConsumed);
            }

            //if timeU period is consumed, update game
            if (deltaUpdate >= 1) {
                long diffTimeMillis = now - updateTime;
                appLogic.update(window, scene, diffTimeMillis);
                updateTime = now;
                deltaUpdate--;
            }

            //if timeR period is consumed, render game
            //if the targe FPS is 0, rely on vsync
            if (targetFps <= 0 || deltaFps >= 1){
                render.render(window, scene);
                deltaFps--;
                window.update();
            }
            initialTime = now;
        }

        cleanup();  //free resources
    }

    public void start() {    //since GLFW requires to be init from the main thread, we don't create a new one in the start method like in other games
        running = true;
        run();
    }

    public void stop() {
        running = false;
    }
}