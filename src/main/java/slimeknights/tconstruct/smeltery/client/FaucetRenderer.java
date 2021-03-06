package slimeknights.tconstruct.smeltery.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;

import org.lwjgl.opengl.GL11;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;

public class FaucetRenderer extends TileEntitySpecialRenderer<TileFaucet> {

  @Override
  public void renderTileEntityAt(TileFaucet te, double x, double y, double z, float partialTicks, int destroyStage) {
    if(!te.isPouring || te.drained == null) {
      return;
    }

    // check how far into the 2nd block we want to render
    float yMin = 0;
    IBlockState state = te.getWorld().getBlockState(te.getPos().down());
    Block block = state.getBlock();

    if(block == TinkerSmeltery.searedTank) {
      yMin = -1;
    }
    else if(block == TinkerSmeltery.castingBlock) {
      if(state.getValue(BlockCasting.TYPE) == BlockCasting.CastingType.TABLE) {
        yMin = -0.125f;
      }
      else {
        yMin = -0.725f;
      }
    }

    if(te.direction == EnumFacing.UP) {
      RenderUtil.renderFluidCuboid(te.drained, te.getPos(), x,y,z, 0.375,0,0.375, 0.625,1f,0.625);
      // render in the block beneath
      if(yMin < 0) {
        RenderUtil.renderFluidCuboid(te.drained, te.getPos(), x, y, z, 0.375, -1, 0.375, 0.625, 0f, 0.625);
      }
    }
    // for horizontal we use custom rendering so we can rotate it and have the flowing texture in the faucet part
    // default direction is north because that makes the fluid flow into the right direction through the UVs
    if(te.direction.getHorizontalIndex() >= 0) {
      float r = -90f * (2 + te.direction.getHorizontalIndex());
      float o = 0.5f;
      // custom rendering for flowing on top
      RenderUtil.pre(x,y,z);

      Tessellator tessellator = Tessellator.getInstance();
      VertexBuffer renderer = tessellator.getBuffer();
      renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
      Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      int color = te.drained.getFluid().getColor(te.drained);
      int brightness = te.getWorld().getCombinedLight(te.getPos(), te.drained.getFluid().getLuminosity());
      TextureAtlasSprite flowing = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(te.drained.getFluid().getFlowing(te.drained).toString());

      GlStateManager.translate(o, 0, o);
      GlStateManager.rotate(r, 0, 1, 0);
      GlStateManager.translate(-o, 0, -o);

      double x1 = 0.375;
      double x2 = 0.625;
      double y1 = 0.375;
      double y2 = 0.625;
      double z1 = 0;
      double z2 = 0.375;

      // the stuff in the faucet
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN,  color, brightness, true);
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness, true);
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST,  color, brightness, true);
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST,  color, brightness, true);
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP,    color, brightness, true);

      // the stuff flowing down
      y1 = 0;
      z1 = 0.375;
      z2 = 0.5;
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN,  color, brightness, true);
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness, true);
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST,  color, brightness, true);
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness, true);
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST,  color, brightness, true);
      RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP,    color, brightness, true);

      // render in the block beneath
      if(yMin < 0) {
        y1 = yMin;
        y2 = 0;
        RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN,  color, brightness, true);
        RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness, true);
        RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST,  color, brightness, true);
        RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness, true);
        RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST,  color, brightness, true);
      }

      tessellator.draw();
      RenderUtil.post();
    }
  }
}
