package supercoder79.survivalgames.game.map.biome.alpine;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.survivalgames.game.map.biome.BiomeGen;
import supercoder79.survivalgames.game.map.gen.TaigaTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;

public final class AlpsGen implements BiomeGen {
	public static final AlpsGen INSTANCE = new AlpsGen();

	@Override
	public BlockState topState(RandomSource random, int x, int z) {
		return Blocks.STONE.defaultBlockState();
	}

	@Override
	public BlockState underState(RandomSource random, int x, int z) {
		return Blocks.STONE.defaultBlockState();
	}

	@Override
	public BlockState underWaterState(RandomSource random, int x, int z) {
		return Blocks.STONE.defaultBlockState();
	}

	@Override
	public double upperNoiseFactor() {
		return 32;
	}

	@Override
	public double lowerNoiseFactor() {
		return -32;
	}

	@Override
	public double upperLerpHigh() {
		return 24;
	}

	@Override
	public double upperLerpLow() {
		return 18;
	}

	@Override
	public double lowerLerpHigh() {
		return 20;
	}

	@Override
	public double lowerLerpLow() {
		return 16;
	}

	@Override
	public double detailFactor() {
		return 5;
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return TaigaTreeGen.INSTANCE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return 256;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.SNOWY_PLAINS;
	}
}
