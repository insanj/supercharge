package com.insanj.supercharge;

import java.util.Map;
import java.util.HashMap;

import net.fabricmc.fabric.api.block.FabricBlockSettings;


import net.minecraft.block.Block;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.item.MiningToolItem;
import net.minecraft.server.world.BlockAction;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SuperchargeBlock extends RedstoneOreBlock {
    public SuperchargeBlock() {
      super(FabricBlockSettings.of(Material.STONE).hardness(0.2f).materialColor(MaterialColor.BLUE_TERRACOTTA).build()); // REDSTONE_LAMP ?
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity) {
      super.onBreak(world, pos, state, playerEntity);

      PlayerInventory playerInventory = playerEntity.inventory;
      ItemStack itemStack = playerInventory.getMainHandStack();
      String displayName = "Supercharged!";
      String attributeName = EntityAttributes.ATTACK_SPEED.getId();
      EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.values()[0];
      EntityAttributeModifier modifier = new EntityAttributeModifier(displayName, 1000, operation);
      EquipmentSlot slot = EquipmentSlot.HAND_MAIN;
      itemStack.addAttributeModifier(attributeName, modifier, slot);

      playerEntity.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0F, 1.0F);

      return;
    }

    public void onPlace

/*
    @Override
    public void spawnParticles(World world, BlockPos pos) {
      particle.setColor(0, 1, 0);
      return particle;
    }
*/
/*
  	public static Particle spawnParticle(EnumParticleTypes type, double par2, double par4, double par6, double par8, double par10, double par12)
  	{
      MinecraftClient mc = MinecraftClient.getInstance();
  		if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null)	{
  			int var14 = mc.gameSettings.particleSetting;

  			if (var14 == 1 && mc.world.rand.nextInt(3) == 0)
  			{
  				var14 = 2;
  			}

  			double var15 = mc.getRenderViewEntity().posX - par2;
  			double var17 = mc.getRenderViewEntity().posY - par4;
  			double var19 = mc.getRenderViewEntity().posZ - par6;
  			Particle var21 = null;
  			double var22 = 16.0D;

  			if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22)
  			{
  				return null;
  			}
  			else if (var14 > 1)
  			{
  				return null;
  			}
  			else
  			{
  				if (type == EnumParticleTypes.EADORE_PORTAL)
  				{
  					var21 = new ParticleEadorePortal(mc.world, par2, par4, par6, par8, par10, par12);
  				}

  				mc.effectRenderer.addEffect(var21);
  				return var21;
  			}
  		}
  		return null;
  	}*/
}
