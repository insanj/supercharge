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
    public static final String MOD_ID = "insanj_supercharge";
    public static final String BLOCK_ID = "supercharge_block";
    public static final Block SUPERCHARGE_BLOCK = new SuperchargeBlock();
    public static final BlockItem SUPERCHARGE_BLOCK_ITEM =  new BlockItem(SUPERCHARGE_BLOCK, new Item.Settings().itemGroup(ItemGroup.MISC));
    // public static final FabricItem SUPERCHARGE_BLOCK_ITEM = new FabricItem(new Item.Settings().itemGroup(ItemGroup.MISC));

    @Override
    public void onInitialize() {
      Registry.register(Registry.BLOCK, new Identifier(MOD_ID, BLOCK_ID), SUPERCHARGE_BLOCK);
      Registry.register(Registry.ITEM, new Identifier(MOD_ID, BLOCK_ID), SUPERCHARGE_BLOCK_ITEM);

      CommandRegistry.INSTANCE.register(false, serverCommandSourceCommandDispatcher -> serverCommandSourceCommandDispatcher.register(
        CommandManager.literal("supercharge").requires(source -> source.hasPermissionLevel(4))
          .executes(context -> {
            ServerPlayerEntity senderPlayer = context.getSource().getPlayer();
            senderPlayer.inventory.insertStack(new ItemStack(SUPERCHARGE_BLOCK_ITEM));
            senderPlayer.inventory.markDirty();
            return 1;
          })
      ));
    }
}
