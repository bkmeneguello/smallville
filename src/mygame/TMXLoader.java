/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author bruno
 */
public class TMXLoader extends DefaultHandler implements AssetLoader {
    
    private static final Logger logger = Logger.getLogger(TMXLoader.class.getName());
    
    private static final Quaternion PITCH270 = new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(1,0,0));
    
    private AssetManager assetManager;
    
    private static enum Mode {
        TILESET,
        LAYER;
    }
    
    private Node root;
    
    private int width;
    
    private int height;
    
    private int tileWidth;
    
    private int tileHeight;
    
    private int tileIndex;
    
    private Mode mode;
    
    private Map<Integer, Texture> textures = new HashMap<Integer, Texture>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException {
        if (qName.equals("map")) {
            String version = attribs.getValue("version");
            if (version == null || !version.equals("1.0")) {
                logger.log(Level.WARNING, "Unrecognized version number in dotScene file: {0}", version);
            }
            
            this.width = Integer.parseInt(attribs.getValue("width"));
            this.height = Integer.parseInt(attribs.getValue("height"));
            
            this.tileWidth = Integer.parseInt(attribs.getValue("tilewidth"));
            this.tileHeight = Integer.parseInt(attribs.getValue("tileheight"));
        } else if (qName.equals("tileset")) {
            this.mode = Mode.TILESET;
        } else if (qName.equals("image")) {
            //TODO: tileWidth = imageWidth/tileSetWidth
        } else if (qName.equals("layer")) {
            this.mode = Mode.LAYER;
            String name = attribs.getValue("name");
        } else if (qName.equals("data")) {
            //TODO
        } else if (qName.equals("tile")) {
            if (this.mode.equals(Mode.LAYER)) {
                int gid = Integer.parseInt(attribs.getValue("gid"));
                createTile(gid);
            } else if (this.mode.equals(Mode.TILESET)) {
                //else if mode tileset
                //...
            }
        }
    }

    private void createTile(int gid) {
        final int tileX = this.tileIndex % this.width;
        final int tileY = this.tileIndex / this.width;
        
        if (gid != 0) {
            //x e y são coords do tile
            //extrair as coords da textura do gid
            final int texX = (gid-1)%16;
            final int texY = (gid-1)/16;
        
            final Quad quad = new Quad(1, 1);
            quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{
                                                        ((float)texX)/16, (16f - (texY+1))/16, //0,0 - LB
                                                        ((float)texX+1)/16, (16f - (texY+1))/16, //1,0 - RB
                                                        ((float)texX+1)/16, (16f - texY)/16, //1,1 - RT
                                                        ((float)texX)/16, (16f - texY)/16, //0,1 - LT
                                                        });
            final Geometry geom = new Geometry("Quad(" + tileX + "," + tileY + ")", quad);

            final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setTexture("ColorMap", this.textures.get(1));
            geom.setMaterial(mat);
            geom.rotate(PITCH270);
            geom.setLocalTranslation(tileX, 0, tileY);
            root.attachChild(geom);   
        }
        
        this.tileIndex++;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("scene")) {
            //TODO
        } else if (qName.equals("tileset")) {
            this.mode = null;
        } else if (qName.equals("image")) {
            //TODO
        } else if (qName.equals("layer")) {
            this.mode = null;
        } else if (qName.equals("data")) {
            //TODO
        } else if (qName.equals("tile")) {
            //TODO
        }
    }
    
    public Object load(AssetInfo info) throws IOException {
        this.assetManager = info.getManager();
        this.root = new Node();
        
        this.textures.put(1, assetManager.loadTexture("Textures/tiles.png"));
        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XMLReader xr = factory.newSAXParser().getXMLReader();

            xr.setContentHandler(this);
            xr.setErrorHandler(this);

            InputStreamReader r = null;

            try {
                r = new InputStreamReader(info.openStream());
                xr.parse(new InputSource(r));
            } finally {
                if (r != null) {
                    r.close();
                }
            }

            return root;
        } catch (SAXException ex) {
            throw new IOException("Error while parsing .tmx map", ex);
        } catch (ParserConfigurationException ex) {
            throw new IOException("Error while parsing .tmx map", ex);
        }
    }
    
}
