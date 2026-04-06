package jade;

import static java.awt.event.KeyEvent.VK_SPACE;

public class LevelEditorScene extends Scene{
    private boolean changingScene=false;
    private float timeToChangeScene=2.00f;
    public LevelEditorScene(){
        System.out.println("in level editor scene");
    }

    @Override
    public void update(float dt) {
        if(!changingScene && KeyListener.isKeyPressed(VK_SPACE)){
            changingScene=true;
        }
        if (changingScene&&timeToChangeScene>0){
            timeToChangeScene-=dt;
            Window.get().r-=dt*5.0f;
            Window.get().g-=dt*5.0f;
            Window.get().b-=dt*5.0f;
        }
        else if(changingScene){
            Window.changeScene(1);
        }
    }
}
