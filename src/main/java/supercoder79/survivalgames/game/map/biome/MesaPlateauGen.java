package supercoder79.survivalgames.game.map.biome;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.ShrubGen;
import xyz.nucleoid.substrate.gen.tree.PoplarTreeGen;

public final class MesaPlateauGen implements BiomeGen {
	public static final MesaPlateauGen INSTANCE = new MesaPlateauGen();
	private static final OpenSimplexNoise RED_NOISE = new OpenSimplexNoise(23);
	private static final OpenSimplexNoise ORANGE_NOISE = new OpenSimplexNoise(24);
	private static final OpenSimplexNoise GRASS_NOISE = new OpenSimplexNoise(25);

	@Override
	public BlockState topState(RandomSource random, int x, int z) {
		if (random.nextDouble() <= 0.1 + RED_NOISE.eval(x / 30.0, z / 30.0) * 1.5) {
            return Blocks.DYED_TERRACOTTA.red().defaultBlockState();
        }

        if (random.nextDouble() <= 0.1 + ORANGE_NOISE.eval(x / 45.0, z / 45.0) * 1.5) {
            return Blocks.DYED_TERRACOTTA.orange().defaultBlockState();
        }

		if (random.nextDouble() <= 0.1 + GRASS_NOISE.eval(x / 30.0, z / 30.0) * 0.1) {
            return Blocks.GRASS_BLOCK.defaultBlockState();
        }

		return Blocks.TERRACOTTA.defaultBlockState();
	}

	@Override
	public BlockState underState(RandomSource random, int x, int z) {
		return RED_NOISE.eval(x / 45.0, z / 45.0) > 0 ? Blocks.DYED_TERRACOTTA.red().defaultBlockState() : Blocks.TERRACOTTA.defaultBlockState();
	}

	@Override
	public double baseHeight() {
		return 20;
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
		return 32;
	}

	@Override
	public double upperLerpLow() {
		return 24;
	}

	@Override
	public double lowerLerpHigh() {
		return 24;
	}

	@Override
	public double lowerLerpLow() {
		return 16;
	}

	@Override
	public double detailFactor() {
		return 4.5;
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		if (random.nextInt(2) == 0) {
			return PoplarTreeGen.INSTANCE;
		}

		return ShrubGen.INSTANCE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return 0;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.DESERT;
	}
}
