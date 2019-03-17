package com.insanj.supercharge;

import java.util.Map;
import java.util.HashMap;

import net.fabricmc.fabric.api.block.FabricBlockSettings;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.BlockState;
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

class SuperchargeBlock extends Block {
    public SuperchargeBlock() {
      super(FabricBlockSettings.of(Material.STONE).hardness(0.2f).build());
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity) {
      super.onBreak(world, pos, state, playerEntity);

      PlayerInventory playerInventory = playerEntity.inventory;
      ItemStack itemStack = playerInventory.getMainHandStack();
    //  int inventorySlot = playerInventory.getSlotWithStack(itemStack);

      String displayName = "Supercharged!";
      //Operation operation = new Operation(0);

      String attributeName = EntityAttributes.ATTACK_SPEED.getId();
      EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.values()[0];
      EntityAttributeModifier modifier = new EntityAttributeModifier(displayName, 1000, operation);
      EquipmentSlot slot = EquipmentSlot.HAND_MAIN;
      itemStack.addAttributeModifier(attributeName, modifier, slot);

      //   MiningToolItem tool = (MiningToolItem)item;
      // itemStack.getOrCreateSubCompoundTag("AttributeName").getTag("")

    //  playerInventory.insertStack(inventorySlot, itemStack);

  //    playerInventory.markDirty();
      playerEntity.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

      return;
    }
}
