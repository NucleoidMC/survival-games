package supercoder79.survivalgames.game.map.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.tree.AspenTreeGen;

public final class AspenForestGen implements BiomeGen {
	public static final AspenForestGen INSTANCE = new AspenForestGen();

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return AspenTreeGen.INSTANCE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return original * 0.9;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.BIRCH_FOREST;
	}
}
