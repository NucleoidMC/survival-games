package supercoder79.survivalgames.game.map.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import xyz.nucleoid.substrate.gen.MapGen;
import xyz.nucleoid.substrate.gen.tree.TaigaTreeGen;

public final class TaigaGen implements BiomeGen {
	public static final TaigaGen INSTANCE = new TaigaGen();
	@Override
	public MapGen tree(int x, int z, RandomSource random) {
		return TaigaTreeGen.INSTANCE;
	}

	@Override
	public double modifyTreeChance(double original) {
		return original * 1.25;
	}

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.TAIGA;
	}
}
