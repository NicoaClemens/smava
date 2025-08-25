package com.neins.smava.mixin;

import com.neins.smava.SmavaConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.neins.smava.SMAVA;

@Mixin(AbstractBlock.class)
public abstract class WeatheringBlocksMixin {

    @Inject(method = "hasRandomTicks", at = @At("HEAD"), cancellable = true)
    private void enableWeathering(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        Block self = state.getBlock();

        if (SMAVA.MOSSY_MAP.containsKey(self)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void addWeathering(BlockState state, ServerWorld world,
                               BlockPos pos, Random random, CallbackInfo ci) {
        Block block = state.getBlock();

        int rad_water = (int)SmavaConfig.radius_water;
        int rad_mossy = (int)SmavaConfig.radius_moss;
        int speed_water = SmavaConfig.speed_water; // 0 = 0ff, 1-6000 on;
        int speed_rain = SmavaConfig.speed_rain;
        int speed_spread = SmavaConfig.speed_moss;

        if (SMAVA.MOSSY_MAP.containsKey(block)) {

            if (speed_water > 6000) { speed_water = speed_water % 6000; }
            if (speed_rain > 6000) { speed_rain = speed_rain % 6000; }
            if (speed_spread > 6000) { speed_spread = speed_spread % 6000; }

            boolean raining = world.isRaining() && world.isSkyVisible(pos.up());

            boolean nearWater = BlockPos.stream(
                    pos.add(-rad_water, -rad_water, -rad_water),
                    pos.add(rad_water, rad_water, rad_water)).anyMatch(p -> world.getFluidState(p).isIn(FluidTags.WATER));

            boolean nearMossy = BlockPos.stream(
                    pos.add(-rad_mossy, -rad_mossy, -rad_mossy),
                    pos.add(rad_water, rad_water, rad_water)).anyMatch(p -> SMAVA.MOSSY_MAP.containsKey(world.getBlockState(p).getBlock())
            );

            if (raining && random.nextInt(6001 - speed_rain) == 0) {
                if (speed_rain > 0) {
                    Block mossy = SMAVA.MOSSY_MAP.get(block);
                    world.setBlockState(pos, mossy.getStateWithProperties(state));
                }
            }

            if (nearWater && random.nextInt(6001 - speed_water) == 0) {
                if (speed_water > 0) {
                    Block mossy = SMAVA.MOSSY_MAP.get(block);
                    world.setBlockState(pos, mossy.getStateWithProperties(state));
                }
            }

            if (nearMossy && random.nextInt(6001 - speed_rain) == 0) {
                if (speed_spread > 0) {
                    Block mossy = SMAVA.MOSSY_MAP.get(block);
                    world.setBlockState(pos, mossy.getStateWithProperties(state));
                }
            }
        }

    }
}
