package com.insanj.supercharge;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.UUID;

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
import net.minecraft.nbt.CompoundTag;
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

  private final String SUPERCHARGE_ATTRIBUTE_MOD_KEY = "Supercharge modifier";
  private final String SUPERCHARGE_ITEM_PREFIX = "Supercharged ";
  private final String SUPERCHARGE_ATTRIBUTE_MOD_TAG = "AttributeModifiers";

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

    // --> grab the existing modifiers on the itemStack to check if anything is effectively supercharging it
    Multimap existingModifiers = itemStack.getAttributeModifiers(slot);
    if (existingModifiers.containsKey(attributeItemStackModifierName) == false) {
      sendErrorMessage(playerEntity, SUPERCHARGE_ERROR_THING_CANNOT_WORK);
      world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 0.35F, 1.0F);
      return; // --X this thing should not be supercharged; it has no attack speed
    }

    Collection<EntityAttributeModifier> existingAttributes = (Collection<EntityAttributeModifier>)existingModifiers.get(attributeItemStackModifierName);

    // --> create a new EntityAttributeModifier with the above specs
    for (EntityAttributeModifier existingAttr : existingAttributes) {
      if (existingAttr.getAmount() >= attackSpeedModifierAmount) {
        sendErrorMessage(playerEntity, SUPERCHARGE_ERROR_ALREADY_SUPERCHARGED);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 0.35F, 1.0F);
        return; // --X already superchared this item!
      }
    }

    // --> add localized supercharge name
    TranslatableTextComponent superchargedDisplayName = new TranslatableTextComponent(SUPERCHARGE_ITEM_PREFIX + itemStack.getDisplayName().getText());
    itemStack.setDisplayName(superchargedDisplayName);

    // --> add the supercharged modifier
    CompoundTag attributeModTag = itemStack.getOrCreateSubCompoundTag(SUPERCHARGE_ATTRIBUTE_MOD_TAG);
    CompoundTag superchargeTag = new CompoundTag();
    superchargeTag.putDouble("Amount", 1000);
    superchargeTag.putString("AttributeName", "generic.attackSpeed");
    superchargeTag.putString("Name", "Supercharge Modifier");
    superchargeTag.putInt("Operation", 0);
    superchargeTag.putString("Slot", "mainhand");

    UUID superchargeTagUUID = UUID.randomUUID();
    superchargeTag.putLong("UUIDLeast", superchargeTagUUID.getLeastSignificantBits());
    superchargeTag.putLong("UUIDMost", superchargeTagUUID.getMostSignificantBits());
    itemStack.setChildTag(SUPERCHARGE_ATTRIBUTE_MOD_TAG, superchargeTag);

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
