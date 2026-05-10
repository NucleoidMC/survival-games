package supercoder79.survivalgames.game.map.biome;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.survivalgames.game.map.gen.IceSpikesMapGen;
import xyz.nucleoid.substrate.gen.MapGen;

public class IceSpikesGen implements BiomeGen {
    public static final BiomeGen INSTANCE = new IceSpikesGen();

    @Override
    public ResourceKey<Biome> getFakingBiome() {
        return Biomes.ICE_SPIKES;
    }

    @Override
    public double upperNoiseFactor() {
        return BiomeGen.super.upperNoiseFactor();
    }

    @Override
    public double lowerNoiseFactor() {
        return BiomeGen.super.lowerNoiseFactor();
    }

    @Override
    public double upperLerpHigh() {
        return BiomeGen.super.upperLerpHigh();
    }

    @Override
    public double upperLerpLow() {
        return BiomeGen.super.upperLerpLow();
    }

    @Override
    public double lowerLerpHigh() {
        return BiomeGen.super.lowerLerpHigh();
    }

    @Override
    public double lowerLerpLow() {
        return BiomeGen.super.lowerLerpLow();
    }

    @Override
    public double detailFactor() {
        return BiomeGen.super.detailFactor();
    }

    @Override
    public BlockState topState(RandomSource random, int x, int z) {
        return Blocks.SNOW_BLOCK.defaultBlockState();
    }

    @Override
    public BlockState underState(RandomSource random, int x, int z) {
        return Blocks.SNOW_BLOCK.defaultBlockState();
    }

    @Override
    public BlockState underWaterState(RandomSource random, int x, int z) {
        return Blocks.STONE.defaultBlockState();
    }

    @Override
    public MapGen tree(int x, int z, RandomSource random) {
        return IceSpikesMapGen.INSTANCE;
    }

    @Override
    public double modifyTreeChance(double original) {
        return original * 2;
    }

    @Override
    public int grassChance(int x, int z, RandomSource random) {
        return 512;
    }

    @Override
    public MapGen grass(int x, int z, RandomSource random) {
        return BiomeGen.super.grass(x, z, random);
    }
}
