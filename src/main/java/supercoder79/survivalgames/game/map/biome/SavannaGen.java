package supercoder79.survivalgames.game.map.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import supercoder79.survivalgames.game.map.gen.BranchingTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.ShrubGen;

public class SavannaGen implements BiomeGen {
	public static final SavannaGen INSTANCE = new SavannaGen();
	@Override
	public double upperLerpHigh() {
		return 3;
	}

	@Override
	public double upperLerpLow() {
		return 2;
	}

	@Override
	public double lowerLerpHigh() {
		return 3;
	}

	@Override
	public double lowerLerpLow() {
		return 2;
	}

	@Override
	public double detailFactor() {
		return 1.25;
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		if (random.nextInt(2) == 0) {
			return BranchingTreeGen.ACACIA;
		}

		return ShrubGen.INSTANCE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return 512;
	}

	@Override
	public int grassChance(int x, int z, RandomSource random) {
		return 12;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.SAVANNA;
	}
}

