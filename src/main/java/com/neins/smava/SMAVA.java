package com.neins.smava;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class SMAVA implements ModInitializer {
	public static final String MOD_ID = "smava";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public static final Map<Block, Block> MOSSY_MAP = Map.ofEntries(
            Map.entry(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE),
            Map.entry(Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB),
            Map.entry(Blocks.COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS),
            Map.entry(Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE_WALL),
            Map.entry(Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS),
            Map.entry(Blocks.STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB),
            Map.entry(Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS),
            Map.entry(Blocks.STONE_BRICK_WALL, Blocks.MOSSY_STONE_BRICK_WALL),
            Map.entry(Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS)
    );

    private static final Map<Block, Block> DEMOSS_MAP = MOSSY_MAP.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Smava Initialising...");

        SmavaConfig.loadConfig();

        registerMossInteraction();

	}

    private void registerMossInteraction() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            // if (world.isClient) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            ItemStack held = player.getStackInHand(hand);

            // Moss
            if (MOSSY_MAP.containsKey(state.getBlock()) &&
                    (held.isOf(Items.VINE) || held.isOf(Items.MOSS_BLOCK))) {
                if (!world.isClient) {
                    Block mossy = MOSSY_MAP.get(state.getBlock());

                    world.setBlockState(pos, mossy.getStateWithProperties(state));
                    if (!player.isCreative()) { held.decrement(1); }
                }
                return ActionResult.SUCCESS;
            }

            if (DEMOSS_MAP.containsKey(state.getBlock()) && held.isOf(Items.SHEARS)) {
                if (!world.isClient) {
                    Block base = DEMOSS_MAP.get(state.getBlock());
                    world.setBlockState(pos, base.getStateWithProperties(state));
                    if(!player.isCreative()) {
                        world.spawnEntity(new ItemEntity(world,
                                pos.getX() + 0.5,
                                pos.getY() + 1,
                                pos.getZ() + 0.5,
                                new ItemStack(Items.MOSS_BLOCK)
                        ));
                        held.damage(1, player, hand);
                    }
                }
            }

            return ActionResult.PASS;

        });

    }

}