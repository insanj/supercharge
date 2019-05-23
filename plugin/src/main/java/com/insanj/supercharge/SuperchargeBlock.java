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
import net.minecraft.sound.SoundCategory;
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
  private final String SUPERCHARGE_TOOLTIP_FIRST_KEY = "tooltip.insanj_supercharge.supercharge_block.first_line";
  private final String SUPERCHARGE_TOOLTIP_SECOND_KEY = "tooltip.insanj_supercharge.supercharge_block.second_line";

  private final String SUPERCHARGE_ERROR_YOU_NEED_ITEM = "error.insanj_supercharge.you_need_item";
  private final String SUPERCHARGE_ERROR_THING_CANNOT_WORK = "error.insanj_supercharge.thing_cannot_work";
  private final String SUPERCHARGE_ERROR_ALREADY_SUPERCHARGED = "error.insanj_supercharge.already_supercharged";

  private final String SUPERCHARGE_ATTRIBUTE_MOD_KEY = "generic.movementSpeed";
  private final String SUPERCHARGE_ITEM_PREFIX = "Supercharged ";

  public SuperchargeBlock() {
    super(FabricBlockSettings.of(Material.COBWEB).hardness(1.0f).lightLevel(15).build());
  }

  @Override
  public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity) {
    super.onBreak(world, pos, state, playerEntity);

    // --> get the inventory, and thus main hand item of the player who broke the block
    PlayerInventory playerInventory = playerEntity.inventory;
    ItemStack itemStack = playerInventory.getMainHandStack();
    if (itemStack == null || itemStack.isEmpty()) {
      sendErrorMessage(playerEntity, SUPERCHARGE_ERROR_YOU_NEED_ITEM);
      world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 0.35F, 1.0F);
      return; // --X invalid item in main hand (or no item at all?)
    }

    // --> setup the config for the supercharge -- In Main Hand, Attack Speed, x1000
    final EquipmentSlot slot = EquipmentSlot.HAND_MAIN;
    final String attributeItemStackModifierName = EntityAttributes.ATTACK_SPEED.getId();
    final double attackSpeedModifierAmount = 1000.0;

    // --> grab the existing modifiers on the itemStack
    // note: we need to ensure all these are still on the item at the end
    Multimap existingModifiers = itemStack.getAttributeModifiers(slot);
    if (existingModifiers.containsKey(attributeItemStackModifierName) == false) {
      sendErrorMessage(playerEntity, SUPERCHARGE_ERROR_THING_CANNOT_WORK);
      world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 0.35F, 1.0F);
      return; // --X this thing should not be supercharged; it has no attack speed
    }

    Collection<EntityAttributeModifier> existingAttributes = (Collection<EntityAttributeModifier>)existingModifiers.get(attributeItemStackModifierName);

    // --> create a new EntityAttributeModifier with the above specs
    final EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.fromId(0);
    final EntityAttributeModifier superchargedMod = new EntityAttributeModifier(SUPERCHARGE_ATTRIBUTE_MOD_KEY, attackSpeedModifierAmount, operation);

    for (EntityAttributeModifier existingAttr : existingAttributes) {
      if (existingAttr.getAmount() >= attackSpeedModifierAmount) {
        sendErrorMessage(playerEntity, SUPERCHARGE_ERROR_ALREADY_SUPERCHARGED);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 0.35F, 1.0F);
        return; // --X already supercharged this item!
      }
    }

    // --> supercharge name
    TranslatableTextComponent superchargedDisplayName = new TranslatableTextComponent(SUPERCHARGE_ITEM_PREFIX + itemStack.getDisplayName().getText());
    itemStack.setDisplayName(superchargedDisplayName);

    // --> replace the existing modifiers with the supercharged ones and refresh the player's inventory!
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
          // EntityAttributeModifier r = new EntityAttributeModifier(m.getName(), m.getAmount(), operation);
          itemStack.addAttributeModifier((String)attributeName, m, slot);
        }
      }
    }

    itemStack.getTag().putInt("HideFlags", 2); // --> Adding 2 will hide Attributes modifiers 

    // --> refresh the player's inventory!
    playerInventory.markDirty();

    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 0.35F, 1.0F);
  }

  private void sendErrorMessage(PlayerEntity player, String message) {
    TranslatableTextComponent textComponent = new TranslatableTextComponent(message);
    player.addChatMessage(textComponent, true);
  }

  @Override
  public void buildTooltip(ItemStack stack, BlockView view, List<TextComponent> tooltip, TooltipContext options) {
    tooltip.add(new TranslatableTextComponent(SUPERCHARGE_TOOLTIP_FIRST_KEY));
    tooltip.add(new TranslatableTextComponent(SUPERCHARGE_TOOLTIP_SECOND_KEY));
    super.buildTooltip(stack, view, tooltip, options);
  }
}
