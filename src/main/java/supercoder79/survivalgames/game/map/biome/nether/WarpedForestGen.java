package supercoder79.survivalgames.game.map.biome.nether;

import java.util.Random;

import kdotjpg.opensimplex.OpenSimplexNoise;
import supercoder79.survivalgames.game.map.biome.BiomeGen;
import supercoder79.survivalgames.game.map.gen.BranchingTreeGen;
import xyz.nucleoid.plasmid.game.gen.MapGen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class WarpedForestGen implements BiomeGen {
    public static final WarpedForestGen INSTANCE = new WarpedForestGen();
    public static final OpenSimplexNoise WART_NOISE = new OpenSimplexNoise(13);

    @Override
    public BlockState topState(Random random, int x, int z) {
        if (random.nextDouble() <= 0.1 + WART_NOISE.eval(x / 30.0, z / 30.0) * 0.1) {
            return Blocks.WARPED_WART_BLOCK.getDefaultState();
        }
        return Blocks.WARPED_NYLIUM.getDefaultState();
    }

    @Override
    public BlockState underState(Random random, int x, int z) {
        return Blocks.NETHERRACK.getDefaultState();
    }

    @Override
    public double upperNoiseFactor() {
        return 24;
    }

    @Override
    public double lowerNoiseFactor() {
        return 8;
    }

    @Override
    public double upperLerpHigh() {
        return 20;
    }

    @Override
    public double upperLerpLow() {
        return 12;
    }

    @Override
    public double lowerLerpHigh() {
        return 12;
    }

    @Override
    public double lowerLerpLow() {
        return 4;
    }

    @Override
    public double detailFactor() {
        return 4.5;
    }

    @Override
    public MapGen tree(int x, int z, Random random) {
        return BranchingTreeGen.WARPED;
    }

    @Override
    public double modifyTreeChance(double original) {
        return 8;
    }

    @Override
    public RegistryKey<Biome> getFakingBiome() {
        return BiomeKeys.WARPED_FOREST;
    }

    @Override
    public int grassChance(int x, int z, Random random) {
        return 4;
    }
}
