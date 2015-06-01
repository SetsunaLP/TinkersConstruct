package tconstruct.library.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;

/**
 * Textures registered with this creator will get a texture created/loaded for each material.
 */
public class CustomTextureCreator {

  private static Set<ResourceLocation> baseTextures = Sets.newHashSet();

  /**
   * Holds all sprites built from the base-texture used as the key.
   */
  public static Map<String, Map<String, TextureAtlasSprite>> sprites = Maps.newHashMap();

  public static void registerTextures(Collection<ResourceLocation> textures) {
    baseTextures.addAll(textures);
  }

  public static void registerTexture(ResourceLocation texture) {
    baseTextures.add(texture);
  }

  @SubscribeEvent
  public void createCustomTextures(TextureStitchEvent.Pre event) {
    TextureMap map = event.map;

    for (ResourceLocation baseTexture : baseTextures) {
      // exclude missingno :I
      if (baseTexture.toString().equals("minecraft:missingno")) {
        continue;
      }

      Map<String, TextureAtlasSprite> builtSprites = Maps.newHashMap();
      for (Material material : TinkerRegistry.getAllMaterials()) {
        TextureAtlasSprite base = map.getTextureExtry(baseTexture.toString());
        if (base == null) {
          TinkerRegistry.log.error("Missing base texture: " + baseTexture.toString());
          continue;
        }

        String location = baseTexture.toString() + "_" + material.identifier;
        TextureAtlasSprite sprite;

        if (exists(location)) {
          sprite = map.registerSprite(new ResourceLocation(location));
        } else {
          sprite = material.renderInfo.getTexture(base, location);
        }

        map.setTextureEntry(location, sprite);
        builtSprites.put(material.identifier, sprite);
      }

      sprites.put(baseTexture.toString(), builtSprites);
    }
  }

  public static boolean exists(String res) {
    try {
      ResourceLocation loc = new ResourceLocation(res);
      loc = new ResourceLocation(loc.getResourceDomain(), "textures/" + loc.getResourcePath() + ".png");
      Minecraft.getMinecraft().getResourceManager().getAllResources(loc);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}