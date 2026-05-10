package supercoder79.survivalgames.game.map.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import supercoder79.survivalgames.game.map.gen.BranchingTreeGen;
import xyz.nucleoid.substrate.gen.MapGen;

public final class RoofedForestGen implements BiomeGen {
	public static final RoofedForestGen INSTANCE = new RoofedForestGen();

	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return BranchingTreeGen.DARK_OAK;
	}

	@Override
	public double modifyTreeChance(double original) {
		return 6;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.DARK_FOREST;
	}
}
