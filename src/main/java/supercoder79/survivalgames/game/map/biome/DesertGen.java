package supercoder79.survivalgames.game.map.biome;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.substrate.gen.CactusGen;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.tree.DeadTreeGen;

public final class DesertGen implements BiomeGen {
	public static final DesertGen INSTANCE = new DesertGen();
	private static final OpenSimplexNoise GRASS_NOISE = new OpenSimplexNoise(60);

	@Override
	public BlockState topState(RandomSource random, int x, int z) {
		if (random.nextDouble() <= 0.1 + GRASS_NOISE.eval(x / 30.0, z / 30.0) * 0.1) {
			return Blocks.GRASS_BLOCK.defaultBlockState();
		}

		return Blocks.SAND.defaultBlockState();
	}

	@Override
	public BlockState underState(RandomSource random, int x, int z) {
		return Blocks.SANDSTONE.defaultBlockState();
	}

	@Override
	public BlockState underWaterState(RandomSource random, int x, int z) {
		return Blocks.SAND.defaultBlockState();
	}

	@Override
	public double upperLerpHigh() {
		return 6;
	}

	@Override
	public double upperLerpLow() {
		return 4;
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
		return 2.0;
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return DeadTreeGen.INSTANCE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return 720;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.DESERT;
	}

	@Override
	public int grassChance(int x, int z, RandomSource random) {
		return 48;
	}

	@Override
	public MapGen grass(int x, int z, RandomSource random) {
		return CactusGen.INSTANCE;
	}
}
