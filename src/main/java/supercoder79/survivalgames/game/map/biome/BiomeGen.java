package supercoder79.survivalgames.game.map.biome;

import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.GrassGen;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.substrate.biome.BaseBiomeGen;
import xyz.nucleoid.substrate.gen.tree.PoplarTreeGen;

public interface BiomeGen extends BaseBiomeGen {
	default double baseHeight() {
		return 0;
	}
	default double upperNoiseFactor() {
		return 14;
	}

	default double lowerNoiseFactor() {
		return 12;
	}

	default double upperLerpHigh() {
		return 12;
	}

	default double upperLerpLow() {
		return 8;
	}

	default double lowerLerpHigh() {
		return 8;
	}

	default double lowerLerpLow() {
		return 6;
	}

	default double detailFactor() {
		return 3.25;
	}

	default BlockState topState(RandomSource random, int x, int z) {
		return Blocks.GRASS_BLOCK.defaultBlockState();
	}

	default BlockState underState(RandomSource random, int x, int z) {
		return Blocks.DIRT.defaultBlockState();
	}

	default BlockState underWaterState(RandomSource random, int x, int z) {
		return underState(random, x, z);
	}

	default MapGen tree(int x, int z, RandomSource random) {
		return PoplarTreeGen.INSTANCE;
	}

	default double modifyTreeChance(double original) {
		return original;
	}

	default int grassChance(int x, int z, RandomSource random) {
		return 16;
	}

	default MapGen grass(int x, int z, RandomSource random) {
		return GrassGen.INSTANCE;
	}
}