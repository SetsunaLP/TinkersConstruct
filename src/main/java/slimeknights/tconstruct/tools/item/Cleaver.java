package slimeknights.tconstruct.tools.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.tools.TinkerTools;

public class Cleaver extends BroadSword {

  public Cleaver() {
    super(PartMaterialType.handle(TinkerTools.toughToolRod),
          PartMaterialType.head(TinkerTools.largeSwordBlade),
          PartMaterialType.head(TinkerTools.largePlate),
          PartMaterialType.extra(TinkerTools.toughToolRod));
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
  }

  @Override
  public float damagePotential() {
    return 0.9f;
  }

  @Override
  public double attackSpeed() {
    return 0.7d;
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    target.hurtResistantTime += 12;
    target.hurtTime += 12;
    return super.hitEntity(stack, target, attacker);
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
    super.onUpdate(stack, worldIn, entity, itemSlot, isSelected);
    if (entity instanceof EntityPlayer)
    {
      EntityPlayer player = (EntityPlayer) entity;
      ItemStack equipped = player.getHeldItemMainhand();
      if (equipped == stack)
      {
        player.addPotionEffect(new PotionEffect(MobEffects.digSlowdown, 2, 2, true, false));
      }
    }
  }

  @Override
  public int[] getRepairParts() {
    return new int[] {1,2};
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(HandleMaterialStats.TYPE);
    HeadMaterialStats head     = materials.get(1).getStatsOrUnknown(HeadMaterialStats.TYPE);
    HeadMaterialStats shield   = materials.get(2).getStatsOrUnknown(HeadMaterialStats.TYPE);
    ExtraMaterialStats guard   = materials.get(3).getStatsOrUnknown(ExtraMaterialStats.TYPE);

    ToolNBT data = new ToolNBT();
    data.head(head, shield);
    data.extra(guard);
    data.handle(handle);

    data.attack *= 1.4f;
    data.attack += 2f;

    // triple durability!
    data.durability *= 2f;
    data.modifiers = 2;

    return data.get();
  }
}
