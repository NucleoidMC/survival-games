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
import supercoder79.survivalgames.game.map.gen.ThistleGen;
import xyz.nucleoid.substrate.gen.MapGen;

public class HighlandPeaksGen implements BiomeGen {
    public static final BiomeGen INSTANCE = new HighlandPeaksGen();
    public final OpenSimplexNoise GRASS_NOISE = new OpenSimplexNoise(23);
    public final OpenSimplexNoise SNOW_NOISE = new OpenSimplexNoise(24);
    public final OpenSimplexNoise GRANITE_NOISE = new OpenSimplexNoise(25);

    @Override
    public ResourceKey<Biome> getFakingBiome() {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("survivalgames", "highland"));
    }

    @Override
    public double baseHeight() {
        return 90;
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
        return 8;
    }

    @Override
    public double upperLerpLow() {
        return 6;
    }

    @Override
    public double lowerLerpHigh() {
        return 4;
    }

    @Override
    public double lowerLerpLow() {
        return 4;
    }

    @Override
    public double detailFactor() {
        return 4;
    }

    @Override
    public BlockState topState(RandomSource random, int x, int z) {
        if (random.nextDouble() < GRASS_NOISE.eval(x / 45.0, z / 45.0) + 0.25) {
            return Blocks.GRASS_BLOCK.defaultBlockState();
        } else if (random.nextDouble() < SNOW_NOISE.eval(x / 45.0, z / 45.0) + 0.2) {
            return Blocks.SNOW_BLOCK.defaultBlockState();
        } else if (random.nextDouble() + 0.1 < GRANITE_NOISE.eval(x / 45.0, z / 45.0) / 12) {
            return Blocks.GRANITE.defaultBlockState();
        }

        return Blocks.STONE.defaultBlockState();
    }

    @Override
    public BlockState underState(RandomSource random, int x, int z) {
        return Blocks.STONE.defaultBlockState();
    }

    @Override
    public double modifyTreeChance(double original) {
        return 1;
    }

    @Override
    public int grassChance(int x, int z, RandomSource random) {
        return 16;
    }

    @Override
    public MapGen grass(int x, int z, RandomSource random) {
        return ThistleGen.INSTANCE;
    }
}
