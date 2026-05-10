package supercoder79.survivalgames.game.map.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.ShrubGen;
import xyz.nucleoid.substrate.gen.tree.PoplarTreeGen;

public final class PlainsGen implements BiomeGen {
	public static final PlainsGen INSTANCE = new PlainsGen();
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
		if (random.nextInt(3) == 0) {
			return PoplarTreeGen.INSTANCE;
		}

		return ShrubGen.INSTANCE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return 600;
	}

	@Override
	public int grassChance(int x, int z, RandomSource random) {
		return 12;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.PLAINS;
	}
}
