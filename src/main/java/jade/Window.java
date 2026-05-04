package jade;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int height, width;
    private String title;
    private long glfwWindow;
    public float r = 0.0f;
    public float g = 0.3f;
    public float b = 0.35f;
    public float a = 1.0f;
    private boolean fadeToBlack = false;
    private boolean cyan = false;

    private static Window window = null;
    private static Scene currentScene; // private, better encapsulation

    private Window() {
        this.height = 1600;
        this.width = 2560;
        this.title = "Jojo no kimyou na bouken";
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                // currentScene.init(); // hook for future scene initialization
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
                break;
        }
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public void run() {
        System.out.println("hewwo LWGJL " + Version.getVersion() + "!");
        init();
        loop();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("unable to do the thingie which is initialize GLFW yuh");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL)
            throw new IllegalStateException("Failed to create the GLFW window");

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop() {
        float targetR = 0.0f;
        float targetG = 0.3f;
        float targetB = 0.35f;

        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;
        float fpsTimer = 0.0f;
        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0)
                currentScene.update(dt);

            // normalize mouse coords to OpenGL NDC (-1 to 1)
            float x = (float) ((MouseListener.getX() / width) * 2 - 1);
            float y = (float) (1 - (MouseListener.getY() / height) * 2);

            float size = 0.01f;

            glBegin(GL_QUADS);
            glColor3f(1, 1, 1);
            glVertex2f(x - size, y - size);
            glVertex2f(x + size, y - size);
            glVertex2f(x + size, y + size);
            glVertex2f(x - size, y + size);
            glEnd();

            if (dt >= 0) {
                fpsTimer += dt;
                if (fpsTimer >= 10.0f) {
                    System.out.println("FPS: " + (int)(1.0f / dt));
                    fpsTimer = 0.0f;
                }
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_E)) {
                fadeToBlack = true;
                cyan = false;
            }
            if (KeyListener.isKeyPressed(GLFW_KEY_B)) {
                cyan = true;
                fadeToBlack = false;
            }
            if (fadeToBlack) {
                r = Math.max(r - 0.01f, 0);
                g = Math.max(g - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
            }
            if (cyan) {
                r = Math.min(r + 0.01f, targetR);
                g = Math.min(g + 0.01f, targetG);
                b = Math.min(b + 0.01f, targetB);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}