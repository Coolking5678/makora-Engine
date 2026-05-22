package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderprogramID;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;


    public Shader (String filepath){
            this.filepath=filepath;
            try {
                 String source=new String(Files.readAllBytes(Paths.get(filepath)));
                System.out.println("SOURCE: [" + source + "]");

                 String[] splitString=source.split("(#type)( )([a-zA-Z]+)");

                 //Find the first pattern after #type 'pattern'
                 int index=source.indexOf("#type") + 6;
                 int eol= source.indexOf("\r\n", index);
                 String firstPattern=source.substring(index,eol).trim();

                //Find the second pattern after #type 'pattern'
                 index=source.indexOf("#type", eol) + 6;
                 eol= source.indexOf("\r\n", index);
                 String secondPattern=source.substring(index,eol).trim();


                 if (firstPattern.equals("vertex")){
                     vertexSource=splitString[1];
                 }
                 else if(firstPattern.equals("fragment")){
                     fragmentSource=splitString[1];
                 }
                 else throw new IOException("unexpected token"+ firstPattern);


                if (secondPattern.equals("vertex")){
                    vertexSource=splitString[2];
                }
                else if(secondPattern.equals("fragment")){
                    fragmentSource=splitString[2];
                }
                else throw new IOException("unexpected token"+ secondPattern);
            }
            catch (IOException e){
                e.printStackTrace();
                assert false : "Error could not open file for shader" +filepath+ ".";
            }

    }
    public void compile(){

        // ===================================================
        // compile and link shaders
        // ===================================================
        int vertexId;
        int fragmentId;

        //1 first load and compile the vertex shader
        vertexId=glCreateShader(GL_VERTEX_SHADER);

        //2 pass it to gupu
        glShaderSource(vertexId,vertexSource);
        glCompileShader(vertexId);

        //check error
        int success=glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success==GL_FALSE){
            int len=glGetShaderi(vertexId,GL_INFO_LOG_LENGTH);
            System.out.println("Error: '"+filepath+"'\n\tVertex shader compilation failed. ");
            System.out.println(glGetShaderInfoLog(vertexId,len));
            assert false:"";
        }

        fragmentId=glCreateShader(GL_FRAGMENT_SHADER);

        //2 pass it to gupu
        glShaderSource(fragmentId,fragmentSource);
        glCompileShader(fragmentId);

        //check error
        success=glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success==GL_FALSE){
            int len=glGetShaderi(fragmentId,GL_INFO_LOG_LENGTH);
            System.out.println("Error: '"+filepath);
            System.out.println(glGetShaderInfoLog(fragmentId,len));
            assert false:"";
        }
        //link and check error
        shaderprogramID=glCreateProgram();
        glAttachShader(shaderprogramID,vertexId);
        glAttachShader(shaderprogramID,fragmentId);
        glLinkProgram(shaderprogramID);

        //errors now
        success=glGetProgrami(shaderprogramID,GL_LINK_STATUS);
        if (success==GL_FALSE){
            int len=glGetProgrami(shaderprogramID,GL_INFO_LOG_LENGTH);
            System.out.println("Error: '"+filepath+"'\n\tShaderProgram compilation failed. ");
            System.out.println(glGetProgramInfoLog(shaderprogramID,len));
            assert false:"";
        }
    }
    public void use(){
        glUseProgram(shaderprogramID);
    }
    public void detach(){
        glUseProgram(0);
    }


}
