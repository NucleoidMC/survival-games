package supercoder79.survivalgames.game.map.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public final class PoplarForestGen implements BiomeGen {
	public static final PoplarForestGen INSTANCE = new PoplarForestGen();

	@Override
	public ResourceKey<Biome> getFakingBiome() {
		return Biomes.PLAINS;
	}
}
