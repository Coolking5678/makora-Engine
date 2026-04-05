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
    private int height,width;
    private String title;
    private long glfwWindow;
    private float r=0.0f;
    private float g=0.3f;
    private float b=0.35f;
    private float a=1.0f;
    private boolean FadeToBlack=false;
    private boolean Cyan=false;

    private static Window window=null;

    private Window(){
        this.height=1600;
        this.width=2560;
        this.title="Jojo no kimyou na bouken";
    }
    public static Window get(){
        if(Window.window==null){
            Window.window=new Window();
        }
        return Window.window;
    }
    public void run(){
        System.out.println("hewwo LWGJL "+ Version.getVersion() + "!");
        init();
        loop();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    public void init(){
        // error callback its in the name itll output the errors to the terminal
        // thats what this does duh
        GLFWErrorCallback.createPrint(System.err).set();

        //start this glfw thingie
        if(!glfwInit()){
            throw new IllegalStateException("unable to do the thingie which is initialize GLFW yuh");
        }
        //setup
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //creation arceus
        glfwWindow = glfwCreateWindow(this.width,this.height,this.title, NULL, NULL);
        if ( glfwWindow == NULL )
            throw new IllegalStateException("Failed to create the GLFW window");

        //MOUSE and KEY CALLBACKS AND SHI
        glfwSetCursorPosCallback(glfwWindow,MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow,MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow,MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow,KeyListener::keyCallback);

        //make opengl context curr
        glfwMakeContextCurrent(glfwWindow);
        //vsync
        glfwSwapInterval(1);

        //window visible now
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }

    public void loop(){
        float targetR = 0.0f;
        float targetG = 0.3f;
        float targetB = 0.35f;

        float beginTime= Time.getTime();
        float endTime=Time.getTime();



        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            // normalize mouse coords to OpenGL (-1 to 1)
            float x = (float)((MouseListener.getX() / width) * 2 - 1);
            float y = (float)(1 - (MouseListener.getY() / height) * 2);

            float size = 0.01f;

            glBegin(GL_QUADS);
            glColor3f(1, 1, 1);
            glVertex2f(x - size, y - size);
            glVertex2f(x + size, y - size);
            glVertex2f(x + size, y + size);
            glVertex2f(x - size, y + size);
            glEnd();
            //mouse box thingie
            if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                FadeToBlack=true;
                Cyan=false;
            }
            if(KeyListener.isKeyPressed(GLFW_KEY_B)){
                Cyan=true;
                FadeToBlack=false;
            }
            if(FadeToBlack){
                r=Math.max(r-0.01f,0);
                g=Math.max(g-0.01f,0);
                b=Math.max(b-0.01f,0);
            }
            if (Cyan) {
                r = Math.min(r + 0.01f, targetR);
                g = Math.min(g + 0.01f, targetG);
                b = Math.min(b + 0.01f, targetB);
            }


            glfwSwapBuffers(glfwWindow);

            endTime=Time.getTime();
            float dt=endTime-beginTime;
            beginTime=endTime;
        }
    }

}
