package supercoder79.survivalgames.game.map.biome.nether;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.survivalgames.game.map.biome.BiomeGen;
import supercoder79.survivalgames.game.map.gen.FireGen;
import xyz.nucleoid.substrate.gen.MapGen;

public class NetherWastesGen implements BiomeGen {
    public static final NetherWastesGen INSTANCE = new NetherWastesGen();
    public static final OpenSimplexNoise GOLD_NOISE = new OpenSimplexNoise(14);

    @Override
    public ResourceKey<Biome> getFakingBiome() {
        return Biomes.NETHER_WASTES;
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
        return 8;
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
        return 4;
    }

    @Override
    public double detailFactor() {
        return 4;
    }

    @Override
    public BlockState topState(RandomSource random, int x, int z) {
        if (random.nextDouble() <= 0.1 + GOLD_NOISE.eval(x / 30.0, z / 30.0) * 0.1) {
            return Blocks.NETHER_GOLD_ORE.defaultBlockState();
        }
        return Blocks.NETHERRACK.defaultBlockState();
    }

    @Override
    public BlockState underState(RandomSource random, int x, int z) {
        return Blocks.NETHERRACK.defaultBlockState();
    }

    @Override
    public BlockState underWaterState(RandomSource random, int x, int z) {
        return Blocks.NETHERRACK.defaultBlockState();
    }

    @Override
    public double modifyTreeChance(double original) {
        return 8;
    }

    @Override
    public MapGen tree(int x, int z, RandomSource random) {
        if (random.nextInt(3) == 0) {
            return PiglinGen.INSTANCE;
        }
        return FireGen.INSTANCE;
    }
}
