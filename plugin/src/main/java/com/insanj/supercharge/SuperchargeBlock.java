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

import com.google.common.collect.Multimap;

public class SuperchargeBlock extends RedstoneOreBlock {
    public SuperchargeBlock() {
      super(FabricBlockSettings.of(Material.STONE).hardness(1.0f).materialColor(MaterialColor.QUARTZ).build());
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity) {
      super.onBreak(world, pos, state, playerEntity);

      PlayerInventory playerInventory = playerEntity.inventory;
      ItemStack itemStack = playerInventory.getMainHandStack();
      if (itemStack == null || itemStack.isEmpty()) {
        playerEntity.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 0.1F, 1.0F);
        return; // invalid item in main hand (or no item at all?)
      }

      EquipmentSlot slot = EquipmentSlot.HAND_MAIN;

      Multimap existingModifiers = itemStack.getAttributeModifiers(slot);
      String attributeName = "supercharged_attribute";
      String attributeItemStackModifierName = EntityAttributes.ATTACK_SPEED.getId();
      if (existingModifiers.containsKey(attributeItemStackModifierName)) {
        playerEntity.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 0.1F, 1.0F);
        return; // already superchared this item!
      }

      EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.values()[0];
      EntityAttributeModifier modifier = new EntityAttributeModifier(attributeName, 1000, operation);
      itemStack.addAttributeModifier(attributeItemStackModifierName, modifier, slot);

      // done!
      playerEntity.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, 0.35F, 1.0F);

      return;
    }
}
