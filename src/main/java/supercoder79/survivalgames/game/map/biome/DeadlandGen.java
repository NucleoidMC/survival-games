package supercoder79.survivalgames.game.map.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.ShrubGen;
import xyz.nucleoid.substrate.gen.tree.DeadTreeGen;

public final class DeadlandGen implements BiomeGen {
	public static final DeadlandGen INSTANCE = new DeadlandGen();

	@Override
	public double upperLerpHigh() {
		return 4;
	}

	@Override
	public double upperLerpLow() {
		return 3;
	}

	@Override
	public double lowerLerpHigh() {
		return 4;
	}

	@Override
	public double lowerLerpLow() {
		return 3;
	}

	@Override
	public double detailFactor() {
		return 1.75;
	}

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		if (random.nextInt(4) == 0) {
			return DeadTreeGen.INSTANCE;
		}

		return ShrubGen.INSTANCE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return original * 2.5;
	}

	@Override
	public int grassChance(int x, int z, RandomSource random) {
		return 32;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.SAVANNA;
	}
}
