package crazypants.structures.creator.block.tree;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import crazypants.structures.Log;

public class Icons {

  public static final String TEXTURE_PATH = "/assets/enderstructurescreator/textures/";
  
  public static final ImageIcon SAVE = createIcon("save");
  public static final ImageIcon SAVE_AS = createIcon("saveAs");
  public static final ImageIcon OPEN = createIcon("open");
  public static final ImageIcon NEW = createIcon("new");
  public static final ImageIcon DELETE = createIcon("delete");
  public static final ImageIcon GENERATE = createIcon("generate");
  public static final ImageIcon CLEAR = createIcon("clear");
  public static final ImageIcon ADD = createIcon("add");
  
  public static final ImageIcon COMPONENT = createIcon("component");
  public static final ImageIcon TEMPLATE = createIcon(TEXTURE_PATH + "blocks/", "blockTemplateEditor");
  public static final ImageIcon GENERATOR = createIcon(TEXTURE_PATH + "blocks/", "blockGeneratorEditor");
  public static final ImageIcon LOOT_EDITOR = createIcon(TEXTURE_PATH + "blocks/", "blockLootCategoryEditor");
  
  public static final ImageIcon LOCATION_SAMPLER = createIcon("locationSampler");
  public static final ImageIcon PREPERATION = createIcon("preperation");
  public static final ImageIcon VALIDATOR = createIcon("validator");
  public static final ImageIcon DECORATOR = createIcon("decorator");
  public static final ImageIcon BEHAVIOUR = createIcon("behaviour");
  public static final ImageIcon CONDITION = createIcon("condition");
  public static final ImageIcon ACTION = createIcon("action");
  
  public static final ImageIcon LOCATION = createIcon("location");
  public static final ImageIcon ROTATION = createIcon("rotation");
  public static final ImageIcon CHECK_BOX = createIcon("checkBox");
  
  public static final ImageIcon DOT = createIcon("dot");

  
  
  public static ImageIcon createIcon(String name) {
    return createIcon (TEXTURE_PATH + "icons/" , name);      
  }
  
  private static ImageIcon createIcon(String path, String name) {
    String resPath = path + name + ".png";
    try {
      return new ImageIcon(ImageIO.read(Icons.class.getResourceAsStream(resPath)));
    } catch (Exception e) {
      Log.warn("Could not icon: " + name + " from " + resPath + " Error: " + e);
      return null;
    }
  }
  
}
