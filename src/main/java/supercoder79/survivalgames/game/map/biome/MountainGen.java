package supercoder79.survivalgames.game.map.biome;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.survivalgames.game.map.gen.TaigaTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;

public final class MountainGen implements BiomeGen {
	public static final MountainGen INSTANCE = new MountainGen();
	private static final OpenSimplexNoise STONE_NOISE = new OpenSimplexNoise(80);

	@Override
	public BlockState topState(RandomSource random, int x, int z) {
		return STONE_NOISE.eval(x / 45.0, z / 45.0) > 0 ? Blocks.STONE.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState();
	}

	@Override
	public BlockState underState(RandomSource random, int x, int z) {
		return STONE_NOISE.eval(x / 45.0, z / 45.0) > 0 ? Blocks.STONE.defaultBlockState() : Blocks.DIRT.defaultBlockState();
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
		return 24;
	}

	@Override
	public double upperLerpLow() {
		return 12;
	}

	@Override
	public double lowerLerpHigh() {
		return 18;
	}

	@Override
	public double lowerLerpLow() {
		return 9;
	}

	@Override
	public double detailFactor() {
		return 4.5;
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return TaigaTreeGen.INSTANCE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return original * 3.0;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.WINDSWEPT_HILLS;
	}
}
