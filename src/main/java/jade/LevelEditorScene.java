package jade;

import org.lwjgl.BufferUtils;

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
       -0.5f,0.5f,0.0f,     0.0f,0.0f,1.0f,1.0f,  //topleft 1
        0.5f,0.5f,0.0f,     0.0f,1.0f,0.0f,1.0f,  //topright 2
       -0.5f,-0.5f,0.0f,    1.0f,0.0f,1.0f,1.0f  //bottleft 3

    };
    //countercockwise
    private int[] elementArray={

            2,1,0,//top tri
            0,1,3//bott tri


    };
    private int vaoID,vboID,eboID; 
    public LevelEditorScene() {

    }

    @Override
    public void init(){
        // ===================================================
        // compile and link shaders
        // ===================================================

        //1 first load and compile the vertex shader
        vertexId=glCreateShader(GL_VERTEX_SHADER);

        //2 pass it to gupu
        glShaderSource(vertexId,vertexShaderSrc);
        glCompileShader(vertexId);

        //check error
        int success=glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success==GL_FALSE){
            int len=glGetShaderi(vertexId,GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultShader.glsl'\n\tVertex shader compilation failed. ");
            System.out.println(glGetShaderInfoLog(vertexId,len));
            assert false:"";
        }

        fragmentId=glCreateShader(GL_FRAGMENT_SHADER);

        //2 pass it to gupu
        glShaderSource(fragmentId,fragmentShaderSrc);
        glCompileShader(fragmentId);

        //check error
        success=glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success==GL_FALSE){
            int len=glGetShaderi(fragmentId,GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultShader.glsl'\n\tFragment shader compilation failed. ");
            System.out.println(glGetShaderInfoLog(fragmentId,len));
            assert false:"";
        }

        //link and check error
        shaderProgram=glCreateProgram();
        glAttachShader(shaderProgram,vertexId);
        glAttachShader(shaderProgram,fragmentId);
        glLinkProgram(shaderProgram);

        //errors now
        success=glGetProgrami(shaderProgram,GL_LINK_STATUS);
        if (success==GL_FALSE){
            int len=glGetProgrami(shaderProgram,GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultShader.glsl'\n\tShaderProgram compilation failed. ");
            System.out.println(glGetProgramInfoLog(shaderProgram,len));
            assert false:"";
        }
        
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
        glUseProgram(shaderProgram);
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
        glUseProgram(0);

    }
}