package jade;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int height,width;
    private String title;
    private long glfwWindow;
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
        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();

            glClearColor(0.0f,0.0f,1.0f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // normalize mouse coords to OpenGL (-1 to 1)
            float x = (float)((MouseListener.getX() / width) * 2 - 1);
            float y = (float)(1 - (MouseListener.getY() / height) * 2);

            float size = 0.05f;

            glBegin(GL_QUADS);
            glColor3f(1, 1, 1);
            glVertex2f(x - size, y - size);
            glVertex2f(x + size, y - size);
            glVertex2f(x + size, y + size);
            glVertex2f(x - size, y + size);
            glEnd();

            System.out.println(MouseListener.getX() + ", " + MouseListener.getY());

            glfwSwapBuffers(glfwWindow);
        }
    }

}
