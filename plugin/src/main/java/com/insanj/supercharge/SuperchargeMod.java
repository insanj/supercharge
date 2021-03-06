package com.insanj.supercharge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.text.StringTextComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class SuperchargeMod implements ModInitializer {
  private static final String MOD_ID = "insanj_supercharge";
  private static final String BLOCK_ID = "supercharge_block";
  private static final String SUPERCHARGE_ITEM_GROUP_NAME = "supercharge";
  private static final String SUPERCHARGE_COMMAND = "supercharge";

  private static final Block SUPERCHARGE_BLOCK = new SuperchargeBlock();
  private static final BlockItem SUPERCHARGE_BLOCK_ITEM =  new BlockItem(SUPERCHARGE_BLOCK, new Item.Settings().itemGroup(ItemGroup.MISC));
  private static final ItemGroup SUPERCHARGE_ITEM_GROUP = FabricItemGroupBuilder.create(
      new Identifier(MOD_ID, SUPERCHARGE_ITEM_GROUP_NAME))
      .icon(() -> new ItemStack(SUPERCHARGE_BLOCK_ITEM))
      .appendItems(stacks ->{
        stacks.add(new ItemStack(SUPERCHARGE_BLOCK_ITEM));
      })
    .build();

  @Override
  public void onInitialize() {
    Registry.register(Registry.BLOCK, new Identifier(MOD_ID, BLOCK_ID), SUPERCHARGE_BLOCK);
    Registry.register(Registry.ITEM, new Identifier(MOD_ID, BLOCK_ID), SUPERCHARGE_BLOCK_ITEM);

    CommandRegistry.INSTANCE.register(false, serverCommandSourceCommandDispatcher -> serverCommandSourceCommandDispatcher.register(
      CommandManager.literal(SuperchargeMod.SUPERCHARGE_COMMAND).requires(source -> source.hasPermissionLevel(4))
        .executes(context -> {
          ServerPlayerEntity senderPlayer = context.getSource().getPlayer();
          senderPlayer.inventory.insertStack(new ItemStack(SUPERCHARGE_BLOCK_ITEM));
          senderPlayer.inventory.markDirty();
          return 1;
        })
    ));
  }
}
