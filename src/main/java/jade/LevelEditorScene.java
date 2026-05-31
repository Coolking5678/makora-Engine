package jade;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc="#version 330 core\n" +
            "\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n"+
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main(){\n" +
            "    fColor=aColor;\n" +
            "    gl_Position=vec4(aPos,1.0);\n" +
            "}";

    private String fragmentShaderSrc="#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "out vec4 color;\n" +
            "\n" +
            "\n" +
            "void main(){\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexId, fragmentId, shaderProgram;

    private float[] vertexArray={
        //postion             //color
        0.5f,-0.5f,0.0f,    1.0f,0.0f,0.0f,1.0f, //bottright 0
       -0.5f,0.5f,0.0f,     1.0f,1.0f,1.0f,1.0f,  //topleft 1
        0.5f,0.5f,0.0f,     0.0f,0.0f,1.0f,1.0f,  //topright 2
       -0.5f,-0.5f,0.0f,    1.0f,0.0f,1.0f,1.0f,  //bottleft 3
        1.0f,0.0f,0.0f,     1.0f,0.0f,1.0f,1.0f,   //farright 4
        0.0f,0.0f,0.0f,     0.0f,1.0f,0.0f,1.0f,   //center
        1.0f,1.0f,0.0f,     1.0f,1.0f,0.0f,1.0f    //topright
    };
    //countercockwise
    private int[] elementArray={

            2,1,0,//top tri
            0,1,3,//bott tri
            4,6,5

    };
    private int vaoID,vboID,eboID;
    private Shader pluh;
    public LevelEditorScene() {


    }

    @Override
    public void init(){
        this.camera=new Camera(new Vector2f());
        pluh=new Shader("assets/shaders/default.glsl");
        pluh.compile();
        // ===================================================
        // gen VAO,VBO and EBO buffer objects
        // ===================================================
        vaoID=glGenVertexArrays();
        glBindVertexArray(vaoID);

        //float buff for vertices
        FloatBuffer vertexBuffer= BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //create VBO upload vertex buffer
        vboID=glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER,vertexBuffer,GL_STATIC_DRAW);

        //create the indices and upload
        IntBuffer elementBuffer=BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID=glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementArray,GL_STATIC_DRAW);

        //vertex attributes (numbering positions and colours)
        int positionsSize=3;
        int colorSize=4;
        int floatSizeBytes=4;
        int vertexSizeBytes=(positionsSize+colorSize)*floatSizeBytes;
        glVertexAttribPointer(0,positionsSize,GL_FLOAT,false,vertexSizeBytes,0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1,colorSize,GL_FLOAT,false,vertexSizeBytes,positionsSize*floatSizeBytes);
        glEnableVertexAttribArray(1);

    }

    @Override
    public void update(float dt) {
        //bind shader program
        pluh.use();
        pluh.uploadMat4f("uProjection",camera.getProjectionMatrix());
        pluh.uploadMat4f("uView",camera.getViewMatrix());

        //bind VAO
        glBindVertexArray(vaoID);

        //enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //draw
        glDrawElements(GL_TRIANGLES,elementArray.length,GL_UNSIGNED_INT,0);

        //unbind
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        pluh.detach();

    }
}