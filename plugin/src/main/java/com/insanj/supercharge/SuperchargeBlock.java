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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.Style;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

public class SuperchargeBlock extends RedstoneOreBlock {
    public SuperchargeBlock() {
      super(FabricBlockSettings.of(Material.COBWEB).hardness(1.0f).lightLevel(15).build());
      // TODO: customize color? .materialColor(MaterialColor.QUARTZ)
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity) {
      // super.onBreak(world, pos, state, playerEntity);

      PlayerInventory playerInventory = playerEntity.inventory;
      ItemStack itemStack = playerInventory.getMainHandStack();
      if (itemStack == null || itemStack.isEmpty()) {
        sendErrorMessage(playerEntity, "You need an item in your hand to Supercharge it!");
        return; // invalid item in main hand (or no item at all?)
      }

      EquipmentSlot slot = EquipmentSlot.HAND_MAIN;

      Multimap existingModifiers = itemStack.getAttributeModifiers(slot);
      String attributeItemStackModifierName = EntityAttributes.ATTACK_SPEED.getId();

      final String attributeName = "supercharged_attribute";
      final double attackSpeedModifierAmount = 1000.0;

      if (existingModifiers.containsKey(attributeItemStackModifierName)) {
        for (Object em : existingModifiers.get(attributeItemStackModifierName)) {
          if (((EntityAttributeModifier)em).getAmount() >= attackSpeedModifierAmount) {
            sendErrorMessage(playerEntity, "Already Supercharged this item!");
            return; // already superchared this item!
          }
        }
      } else {
        sendErrorMessage(playerEntity, "This thing isn't meant to be Supercharged...");
        return; // this thing should not be supercharged; it has no attack speed
      }

      EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.values()[0];
      EntityAttributeModifier modifier = new EntityAttributeModifier(attributeName, attackSpeedModifierAmount, operation);
      itemStack.addAttributeModifier(attributeItemStackModifierName, modifier, slot);

      // done!
      playerEntity.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, 0.35F, 1.0F);
      return;
    }


    public void sendErrorMessage(PlayerEntity player, String message) {
      TranslatableTextComponent textComponent = new TranslatableTextComponent(message);
      textComponent.setStyle(new Style().setColor(TextFormat.RED));
      player.addChatMessage(textComponent, true);
    }
}
