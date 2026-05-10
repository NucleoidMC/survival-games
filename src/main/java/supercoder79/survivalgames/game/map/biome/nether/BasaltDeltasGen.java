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
import supercoder79.survivalgames.game.map.gen.LavaHoleGen;
import xyz.nucleoid.substrate.gen.MapGen;

public class BasaltDeltasGen implements BiomeGen {
    public static final BasaltDeltasGen INSTANCE = new BasaltDeltasGen();
    public static final OpenSimplexNoise BLACKSTONE_NOISE = new OpenSimplexNoise(21);

    @Override
    public ResourceKey<Biome> getFakingBiome() {
        return Biomes.BASALT_DELTAS;
    }

    @Override
    public double upperNoiseFactor() {
        return 20;
    }

    @Override
    public double lowerNoiseFactor() {
        return 12;
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
        return 24;
    }

    @Override
    public BlockState topState(RandomSource random, int x, int z) {
        if (random.nextDouble() <= 0.2 + BLACKSTONE_NOISE.eval(x / 30.0, z / 30.0) * 0.1) {
            return Blocks.BLACKSTONE.defaultBlockState();
        }

        return Blocks.BASALT.defaultBlockState();
    }

    @Override
    public BlockState underState(RandomSource random, int x, int z) {
        return Blocks.BASALT.defaultBlockState();
    }

    @Override
    public BlockState underWaterState(RandomSource random, int x, int z) {
        return Blocks.BLACKSTONE.defaultBlockState();
    }

    @Override
    public MapGen tree(int x, int z, RandomSource random) {
        if(random.nextInt(4) == 0) {
            return BranchingTreeGen.BASALT_COLUMN;
        }
        return LavaHoleGen.INSTANCE;
    }

    @Override
    public double modifyTreeChance(double original) {
        return 2;
    }

}
