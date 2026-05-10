package supercoder79.survivalgames.game.map.biome;

import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.ShrubGen;
import xyz.nucleoid.substrate.gen.tree.PoplarTreeGen;
import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BadlandsGen implements BiomeGen {
    public static final BadlandsGen INSTANCE = new BadlandsGen();
    public static final OpenSimplexNoise TERRACOTTA_NOISE = new OpenSimplexNoise(11);
    public static final OpenSimplexNoise GRASS_NOISE = new OpenSimplexNoise(12);
    public static final OpenSimplexNoise RED_TERRACOTTA_NOISE = new OpenSimplexNoise(13);

    @Override
    public BlockState topState(RandomSource random, int x, int z) {
        if (random.nextDouble() <= 0.1 + GRASS_NOISE.eval(x / 30.0, z / 30.0) * 0.1) {
            return Blocks.GRASS_BLOCK.defaultBlockState();
        }

        if (random.nextDouble() <= 0.1 + TERRACOTTA_NOISE.eval(x / 30.0, z / 30.0) * 0.1) {
            return Blocks.TERRACOTTA.defaultBlockState();
        }

        if (random.nextDouble() <= 0.1 + RED_TERRACOTTA_NOISE.eval(x / 30.0, z / 30.0) * 0.1) {
            return Blocks.RED_TERRACOTTA.defaultBlockState();
        }

        return Blocks.RED_SAND.defaultBlockState();
    }

    @Override
    public BlockState underState(RandomSource random, int x, int z) {
        if (random.nextInt(3) == 0) {
            return Blocks.RED_TERRACOTTA.defaultBlockState();
        }

        return Blocks.TERRACOTTA.defaultBlockState();
    }

    @Override
    public double baseHeight() {
        return 50;
    }

    @Override
    public double upperLerpHigh() {
        return 8;
    }

    @Override
    public double upperLerpLow() {
        return 2;
    }

    @Override
    public double lowerLerpHigh() {
        return 5;
    }

    @Override
    public double lowerLerpLow() {
        return 2;
    }

    @Override
    public double detailFactor() {
        return 1.25;
    }

    @Override
    public MapGen tree(int x, int z, RandomSource random) {
        if (random.nextInt(3) == 0) {
            return PoplarTreeGen.INSTANCE;
        }

        return ShrubGen.INSTANCE;
    }

    @Override
    public double modifyTreeChance(double original) {
        return 16;
    }

    @Override
    public int grassChance(int x, int z, RandomSource random) {
        return 12;
    }

    @Override
    public ResourceKey<Biome> getFakingBiome() {
        return Biomes.DESERT;
    }
}
