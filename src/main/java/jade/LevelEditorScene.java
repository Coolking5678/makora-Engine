package jade;

import java.awt.event.KeyEvent;

import static org.lwjgl.opengl.GL20.*;

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
        0.5f,0.5f,0.0f,     0.0f,1.0f,0.0f,1.0f  //topright 2
       -0.5f,-0.5f,0.0f,    1.0f,0.0f,1.0f,1.0f  //bottleft 3

    };
    //countercockwise
    private int[] elementArray={

            0,2,1,
            0,1,3


    };

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
    }

    @Override
    public void update(float dt) {

    }
}