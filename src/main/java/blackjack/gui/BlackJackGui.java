package blackjack.gui;

import blackjack.engine.IGuiInstance;
import blackjack.engine.Window;
import blackjack.engine.MouseInput;
import blackjack.engine.scene.Scene;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.ImGuiStyle;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class BlackJackGui implements IGuiInstance {
    public static class GuiMessage {
        public final String id; 
        public String text;
        public float x, y;      // percent of window
        public float scale;     // font size scale
        public float r, g, b, a;

        // NEW → alignment
        public String anchor = "center";  // "left", "center", "right"

        // Animation fields
        public float lifetime = 1.0f;   // fade-out duration
        public float age = 0f;          // time since message created
        public boolean fadeIn = true;   // first fade-in
        public boolean fading = false;  // if set true → fade-out
        public float pulse = 0.0f;      // internal pulse animation

        public GuiMessage(String id, String text, float x, float y, float scale,
                          float r, float g, float b, float a) {
            this.id = id;  
            this.text = text;
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }

    private final List<GuiMessage> messages = new ArrayList<>();

    public void addMessage(GuiMessage msg) { messages.add(msg); }
    public void clearMessages() { messages.clear(); }
    public void removeMessageById(String id) { messages.removeIf(msg -> msg.id.equals(id)); }

    public static class GuiButton {
        public final String id;
        public String text;
        public float x, y;
        public float width, height;
        public float r, g, b, a;
        public Runnable callback;

        public GuiButton(String id, String text,
                         float x, float y,
                         float width, float height,
                         float r, float g, float b, float a,
                         Runnable callback) {
            this.id = id;
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.callback = callback;
        }
    }

    private final List<GuiButton> buttons = new ArrayList<>();
    public void addButton(GuiButton btn) { buttons.add(btn); }
    public void removeButtonById(String id) { buttons.removeIf(b -> b.id.equals(id)); }
    public void clearButtons() { buttons.clear(); }

    private void applyStyle() {
        ImGuiStyle style = ImGui.getStyle();
        style.setFrameRounding(12.0f);
        style.setGrabRounding(12.0f);
        style.setWindowRounding(12.0f);
        style.setFramePadding(15f, 10f);
        style.setItemSpacing(10f, 10f);

        ImGui.getStyle().setColor(ImGuiCol.Text, 1f, 1f, 1f, 1f);
        ImGui.getStyle().setColor(ImGuiCol.Button,        0.15f, 0.45f, 0.15f, 1f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonHovered, 0.20f, 0.60f, 0.20f, 1f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonActive,  0.10f, 0.35f, 0.10f, 1f);
        ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0f, 0f, 0f, 0f); 
    }

    @Override
    public void drawGui() {

        applyStyle();
        ImGui.newFrame();

        if (!messages.isEmpty()) {

            ImGui.setNextWindowPos(0, 0);
            ImGui.setNextWindowSize(ImGui.getIO().getDisplaySize());
            ImGui.begin("Blackjack Messages",
                ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBackground
            );

            float w = ImGui.getWindowSizeX();
            float h = ImGui.getWindowSizeY();
            float delta = ImGui.getIO().getDeltaTime();

            for (GuiMessage msg : messages) {

                msg.age += delta;

                // Fade logic
                float alpha = msg.a;
                if (msg.fadeIn) {
                    alpha = Math.min(1f, msg.age / 0.3f);
                    if (alpha >= 1f) msg.fadeIn = false;
                }
                if (msg.fading) {
                    alpha = Math.max(0f, 1f - msg.age / msg.lifetime);
                }

                // Pulse animation
                float finalScale;
                if (msg.id.equals("Game Over")) {
                    float pulseFactor = 1.0f + 0.05f * (float)Math.sin(msg.age * 6.0);
                    finalScale = msg.scale * pulseFactor;
                } else {
                    finalScale = msg.scale;
                }

                float textW = ImGui.calcTextSize(msg.text).x * finalScale;

                float posX;
                switch (msg.anchor) {
                    case "left":
                        posX = msg.x * w;
                        break;
                    case "right":
                        posX = msg.x * w - textW;
                        break;
                    default: // center
                        posX = msg.x * w - textW / 2f;
                        break;
                }

                float posY = msg.y * h;

                ImGui.setWindowFontScale(finalScale);

                // Drop shadow
                ImGui.setCursorPos(posX + 3, posY + 3);
                ImGui.textColored(0f, 0f, 0f, alpha * 0.6f, msg.text);

                // Stroke (outline)
                ImGui.setCursorPos(posX - 1, posY);
                ImGui.textColored(0f, 0f, 0f, alpha, msg.text);
                ImGui.setCursorPos(posX + 1, posY);
                ImGui.textColored(0f, 0f, 0f, alpha, msg.text);
                ImGui.setCursorPos(posX, posY - 1);
                ImGui.textColored(0f, 0f, 0f, alpha, msg.text);
                ImGui.setCursorPos(posX, posY + 1);
                ImGui.textColored(0f, 0f, 0f, alpha, msg.text);

                // Gradient
                ImGui.setCursorPos(posX, posY - 1);
                ImGui.textColored(
                    Math.min(1f, msg.r + 0.2f),
                    Math.min(1f, msg.g + 0.2f),
                    Math.min(1f, msg.b + 0.2f),
                    alpha,
                    msg.text
                );

                // Main text
                ImGui.setCursorPos(posX, posY);
                ImGui.textColored(msg.r, msg.g, msg.b, alpha, msg.text);

                ImGui.setWindowFontScale(1f);
            }

            // Buttons (unchanged)
            for (GuiButton btn : buttons) {

                float posX = btn.x * w;
                float posY = btn.y * h;

                ImGui.setCursorPos(posX, posY);

                ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 18f);
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 20f, 12f);
                ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 10f, 20f);

                ImGui.pushStyleColor(ImGuiCol.Button,        btn.r, btn.g, btn.b, 0.90f);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, btn.r + 0.05f, btn.g + 0.10f, btn.b + 0.05f, 1f);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive,  btn.r - 0.10f, btn.g - 0.10f, btn.b - 0.10f, 1f);

                ImGui.setWindowFontScale(2.5f);

                if (ImGui.button(btn.text, btn.width, btn.height)) {
                    if (btn.callback != null) btn.callback.run();
                }

                ImGui.setWindowFontScale(1f);
                ImGui.popStyleColor(3);
                ImGui.popStyleVar(3);
            }

            ImGui.end();
        }

        ImGui.endFrame();
        ImGui.render();
    }

    @Override
    public boolean handleGuiInput(Scene scene, Window window) {

        ImGuiIO io = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f pos = mouseInput.getCurrentPos();
        
        io.addMousePosEvent(pos.x, pos.y);
        io.addMouseButtonEvent(0, mouseInput.isLeftButtonPressed());
        io.addMouseButtonEvent(1, mouseInput.isRightButtonPressed());

        return false;
    }
}
