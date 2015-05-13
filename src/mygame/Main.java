package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetLoader;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import java.io.InputStreamReader;
import javax.xml.stream.XMLInputFactory;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    private float scale = 5;
    
    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(0, 10, 0));
        cam.lookAtDirection(new Vector3f(1, -1, 1), Vector3f.UNIT_Y);
        cam.setParallelProjection(true);
        
        assetManager.registerLoader(TMXLoader.class, "tmx");
        
        inputManager.addMapping("My Action", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addListener(new AnalogListener() {
            public void onAnalog(String name, float value, float tpf) {
                scale -= value;
            }
        }, "My Action");
        inputManager.addMapping("My Action2", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addListener(new AnalogListener() {
            public void onAnalog(String name, float value, float tpf) {
                scale += value;
            }
        }, "My Action2");

        final Spatial level = (Spatial) assetManager.loadModel("Level/sample.tmx");
        rootNode.attachChild(level);
    }

    @Override
    public void simpleUpdate(float tpf) {
        final Vector2f cur = inputManager.getCursorPosition();
        if (cur.x < 100) {
            cam.setLocation(cam.getLocation().add(.1f, 0, -.1f));
        } else if (cur.x > cam.getWidth() - 100) {
            cam.setLocation(cam.getLocation().add(-.1f, 0, .1f));
        }
        if (cur.y < 100) {
            cam.setLocation(cam.getLocation().add(-.1f, 0, -.1f));
        } else if (cur.y > cam.getHeight() - 100) {
            cam.setLocation(cam.getLocation().add(.1f, 0, .1f));
        }
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustum(1, 1000, -scale * aspect, scale * aspect, scale, -scale);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
