package supercoder79.survivalgames.game.map.biome.highland;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.survivalgames.game.map.biome.BiomeGen;
import supercoder79.survivalgames.game.map.gen.ThistleGen;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.ShrubGen;

public class HighlandRiverGen implements BiomeGen {
    public static final BiomeGen INSTANCE = new HighlandRiverGen();

    @Override
    public ResourceKey<Biome> getFakingBiome() {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("survivalgames", "highland"));
    }

    @Override
    public double baseHeight() {
        return -12;
    }

    @Override
    public double upperNoiseFactor() {
        return 1;
    }

    @Override
    public double lowerNoiseFactor() {
        return 1;
    }

    @Override
    public double upperLerpHigh() {
        return 1;
    }

    @Override
    public double upperLerpLow() {
        return 1;
    }

    @Override
    public double lowerLerpHigh() {
        return 1;
    }

    @Override
    public double lowerLerpLow() {
        return 1;
    }

    @Override
    public double detailFactor() {
        return 0;
    }

    @Override
    public BlockState topState(RandomSource random, int x, int z) {
        return Blocks.GRASS_BLOCK.defaultBlockState();
    }

    @Override
    public BlockState underState(RandomSource random, int x, int z) {
        return Blocks.DIRT.defaultBlockState();
    }

    @Override
    public BlockState underWaterState(RandomSource random, int x, int z) {
        return BiomeGen.super.underWaterState(random, x, z);
    }

    @Override
    public MapGen tree(int x, int z, RandomSource random) {
        if (random.nextDouble() < 0.6) return ShrubGen.INSTANCE;
        return BiomeGen.super.tree(x, z, random);
    }

    @Override
    public double modifyTreeChance(double original) {
        return 5;
    }

    @Override
    public int grassChance(int x, int z, RandomSource random) {
        return 6;
    }

    @Override
    public MapGen grass(int x, int z, RandomSource random) {
        return ThistleGen.INSTANCE;
    }
}
