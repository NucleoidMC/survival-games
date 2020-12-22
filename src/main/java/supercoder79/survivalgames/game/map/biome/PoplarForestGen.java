package supercoder79.survivalgames.game.map.biome;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;

public final class PoplarForestGen implements BiomeGen {
	public static final PoplarForestGen INSTANCE = new PoplarForestGen();

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BiomeKeys.PLAINS;
	}
}
