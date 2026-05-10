package supercoder79.survivalgames.game.map.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.survivalgames.game.map.gen.BranchingTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.ShrubGen;

public final class JungleGen implements BiomeGen {
    public static final JungleGen INSTANCE = new JungleGen();

	@Override
	public BlockState topState(RandomSource random, int x, int z) {
		return Blocks.GRASS_BLOCK.defaultBlockState();
	}

	@Override
	public BlockState underState(RandomSource random, int x, int z) {
		return Blocks.DIRT.defaultBlockState();
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
		if (random.nextInt(3) == 0) {
			return ShrubGen.INSTANCE;
		}

		return BranchingTreeGen.JUNGLE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return -32; // 96 - 32 = 64
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.JUNGLE;
	}

	@Override
	public int grassChance(int x, int z, RandomSource random) {
		return 4;
	}
}
