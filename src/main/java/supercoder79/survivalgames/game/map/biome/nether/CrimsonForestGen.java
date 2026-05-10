package supercoder79.survivalgames.game.map.biome.nether;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.survivalgames.game.map.biome.BiomeGen;
import supercoder79.survivalgames.game.map.gen.BranchingTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;

public final class CrimsonForestGen implements BiomeGen {
    public static final CrimsonForestGen INSTANCE = new CrimsonForestGen();
    public static final OpenSimplexNoise WART_NOISE = new OpenSimplexNoise(13);
    public static final OpenSimplexNoise LIGHT_NOISE = new OpenSimplexNoise(12);

    @Override
    public BlockState topState(RandomSource random, int x, int z) {
        if (random.nextDouble() <= 0.1 + WART_NOISE.eval(x / 30.0, z / 30.0) * 0.1) {
            return Blocks.NETHER_WART_BLOCK.defaultBlockState();
        }
        if (random.nextDouble() <= 0.05 + LIGHT_NOISE.eval(x / 40.0, z / 40.0) * 0.1) {
            return Blocks.SHROOMLIGHT.defaultBlockState();
        }
        return Blocks.CRIMSON_NYLIUM.defaultBlockState();
    }

    @Override
    public BlockState underState(RandomSource random, int x, int z) {
        return Blocks.NETHERRACK.defaultBlockState();
    }

    @Override
    public double upperNoiseFactor() {
        return 32;
    }

    @Override
    public double lowerNoiseFactor() {
        return 8;
    }

    @Override
    public double upperLerpHigh() {
        return 16;
    }

    @Override
    public double upperLerpLow() {
        return 8;
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
    public MapGen tree(int x, int z, RandomSource random) {
        return BranchingTreeGen.CRIMSON;
    }

    @Override
    public double modifyTreeChance(double original) {
        return 8;
    }

    @Override
    public ResourceKey<Biome> getFakingBiome() {
        return Biomes.CRIMSON_FOREST;
    }

    @Override
    public int grassChance(int x, int z, RandomSource random) {
        return 4;
    }
}
