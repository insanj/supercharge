package com.insanj.supercharge;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;

import net.fabricmc.fabric.api.block.FabricBlockSettings;

import net.minecraft.block.Block;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.MiningToolItem;
import net.minecraft.server.world.BlockAction;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;

import com.google.common.collect.Multimap;

public class SuperchargeBlock extends RedstoneOreBlock {
    public SuperchargeBlock() {
      super(FabricBlockSettings.of(Material.COBWEB).hardness(1.0f).lightLevel(15).build());
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity) {
      super.onBreak(world, pos, state, playerEntity);

      // step 1: get the inventory, and thus main hand item of the player who broke the block
      PlayerInventory playerInventory = playerEntity.inventory;
      ItemStack itemStack = playerInventory.getMainHandStack();
      if (itemStack == null || itemStack.isEmpty()) {
        sendErrorMessage(playerEntity, "You need an item in your hand to Supercharge it!");
        return; // invalid item in main hand (or no item at all?)
      }

      // step 2: setup the config for the supercharge -- In Main Hand, Attack Speed, x1000
      final EquipmentSlot slot = EquipmentSlot.HAND_MAIN;
      final String attributeItemStackModifierName = EntityAttributes.ATTACK_SPEED.getId();
      final double attackSpeedModifierAmount = 1000.0;

      // step 3: grab the existing modifiers on the itemStack
      // note: we need to ensure all these are still on the item at the end
      Multimap existingModifiers = itemStack.getAttributeModifiers(slot);
      if (existingModifiers.containsKey(attributeItemStackModifierName) == false) {
        sendErrorMessage(playerEntity, "This thing isn't meant to be Supercharged...");
        return; // this thing should not be supercharged; it has no attack speed
      }

      Collection<EntityAttributeModifier> existingAttributes = (Collection<EntityAttributeModifier>)existingModifiers.get(attributeItemStackModifierName);

      // step 4: create a new EntityAttributeModifier with the above specs
      final EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.values()[0];
      final EntityAttributeModifier superchargedMod = new EntityAttributeModifier("Supercharge modifier", attackSpeedModifierAmount, operation);

      for (EntityAttributeModifier existingAttr : existingAttributes) {
        if (existingAttr.getAmount() >= attackSpeedModifierAmount) {
          sendErrorMessage(playerEntity, "This item is already Supercharged!");
          return; // already superchared this item!
        }
      }

      // step 5: replace the existing modifiers with the supercharged ones and refresh the player's inventory!
      boolean superchargedOnce = false;
      for (Object attributeName : existingModifiers.keySet()) {
          if (attributeName.equals(attributeItemStackModifierName)) {
            if (superchargedOnce == false) {
              itemStack.addAttributeModifier(attributeItemStackModifierName, superchargedMod, slot);
              superchargedOnce = true;
            } // no else because we want to skip any original ATTACK_SPEED modifier
          } else {
            Collection<EntityAttributeModifier> attributes = (Collection<EntityAttributeModifier>)existingModifiers.get(attributeName);
            for (EntityAttributeModifier m : attributes) {
              itemStack.addAttributeModifier((String)attributeName, m, slot);
            }
          }
      }

      // setp 6: supercharge name?
      TranslatableTextComponent superchargedDisplayName = new TranslatableTextComponent("Supercharged " + itemStack.getDisplayName().getText());
      itemStack.setDisplayName(superchargedDisplayName);
      playerInventory.markDirty();

      // done!
      playerEntity.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, 0.35F, 1.0F);
      return;
    }

    public void sendErrorMessage(PlayerEntity player, String message) {
      TranslatableTextComponent textComponent = new TranslatableTextComponent(message);
      // ?? textComponent.setStyle(new Style().setColor(TextFormat.RED));
      player.addChatMessage(textComponent, true);
    }

    @Override
    public void buildTooltip(ItemStack stack, BlockView view, List<TextComponent> tooltip, TooltipContext options) {
      tooltip.add(new TranslatableTextComponent("tooltip_one.insanj_supercharge.supercharge_block"));
      tooltip.add(new TranslatableTextComponent("tooltip_two.insanj_supercharge.supercharge_block"));
      super.buildTooltip(stack, view, tooltip, options);
    }
}
