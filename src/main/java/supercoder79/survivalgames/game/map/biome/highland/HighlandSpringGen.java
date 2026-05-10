package supercoder79.survivalgames.game.map.biome.highland;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.survivalgames.game.map.biome.BiomeGen;
import supercoder79.survivalgames.game.map.gen.SpringGen;
import xyz.nucleoid.substrate.gen.MapGen;

public class HighlandSpringGen implements BiomeGen {
    public static final BiomeGen INSTANCE = new HighlandSpringGen();
    public static OpenSimplexNoise GRANITE_NOISE = new OpenSimplexNoise(25);
    public static OpenSimplexNoise GRASS_NOISE = new OpenSimplexNoise(26);

    @Override
    public ResourceKey<Biome> getFakingBiome() {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("survivalgames", "highland"));
    }

    @Override
    public double baseHeight() {
        return 15;
    }

    @Override
    public double upperNoiseFactor() {
        return 6;
    }

    @Override
    public double lowerNoiseFactor() {
        return 4;
    }

    @Override
    public double upperLerpHigh() {
        return 6;
    }

    @Override
    public double upperLerpLow() {
        return 6;
    }

    @Override
    public double lowerLerpHigh() {
        return 6;
    }

    @Override
    public double lowerLerpLow() {
        return 2;
    }

    @Override
    public double detailFactor() {
        return 2;
    }

    @Override
    public BlockState topState(RandomSource random, int x, int z) {
        if (random.nextDouble() < GRANITE_NOISE.eval(x / 45.0, z / 45.0) + 0.1) {
            return Blocks.GRANITE.defaultBlockState();
        } else if (random.nextDouble() < GRASS_NOISE.eval(x / 45.0, z / 45.0)) {
            return Blocks.GRASS_BLOCK.defaultBlockState();
        }
        return random.nextDouble() < 0.5 ? Blocks.ANDESITE.defaultBlockState() : Blocks.STONE.defaultBlockState();
    }

    @Override
    public BlockState underState(RandomSource random, int x, int z) {
        return BiomeGen.super.underState(random, x, z);
    }

    @Override
    public BlockState underWaterState(RandomSource random, int x, int z) {
        return BiomeGen.super.underWaterState(random, x, z);
    }

    @Override
    public MapGen tree(int x, int z, RandomSource random) {
        return SpringGen.INSTANCE;
    }

    @Override
    public double modifyTreeChance(double original) {
        return 16;
    }

    @Override
    public int grassChance(int x, int z, RandomSource random) {
        return 64;
    }

    @Override
    public MapGen grass(int x, int z, RandomSource random) {
        return BiomeGen.super.grass(x, z, random);
    }
}
