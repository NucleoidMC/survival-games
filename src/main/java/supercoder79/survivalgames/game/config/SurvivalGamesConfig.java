package supercoder79.survivalgames.game.config;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import supercoder79.survivalgames.game.map.biome.generator.BiomeGenerator;
import supercoder79.survivalgames.game.map.noise.NoiseGenerator;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

public record SurvivalGamesConfig(WorldBorderConfig borderConfig, WaitingLobbyConfig playerConfig, int townDepth,
								  int outskirtsBuildingCount, BiomeGenerator biomeGenerator,
								  NoiseGenerator noiseGenerator, List<ItemStackTemplate> kit, Identifier dimension,
								  Identifier outskirtsPool, BlockState defaultState, BlockState defaultFluid,
								  long time) {
	public static final MapCodec<SurvivalGamesConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			WorldBorderConfig.CODEC.fieldOf("border").forGetter(config -> config.borderConfig),
			WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
			Codec.INT.fieldOf("town_depth").forGetter(config -> config.townDepth),
			Codec.INT.fieldOf("outskirts_building_count").forGetter(config -> config.outskirtsBuildingCount),
			BiomeGenerator.CODEC.fieldOf("biome_generator").forGetter(config -> config.biomeGenerator),
			NoiseGenerator.CODEC.fieldOf("noise_generator").forGetter(config -> config.noiseGenerator),
			ItemStackTemplate.CODEC.listOf().fieldOf("kit").forGetter(config -> config.kit),
			Identifier.CODEC.optionalFieldOf("dimension", BuiltinDimensionTypes.OVERWORLD.identifier()).forGetter(config -> config.dimension),
			Identifier.CODEC.optionalFieldOf("outskirts_pool", Identifier.fromNamespaceAndPath("survivalgames", "outskirts_buildings")).forGetter(config -> config.outskirtsPool),
			BlockState.CODEC.optionalFieldOf("default_state", Blocks.STONE.defaultBlockState()).forGetter(config -> config.defaultState),
			BlockState.CODEC.optionalFieldOf("default_fluid", Blocks.WATER.defaultBlockState()).forGetter(config -> config.defaultFluid),
			Codec.LONG.optionalFieldOf("time", 6000L).forGetter(config -> config.time)
	).apply(instance, SurvivalGamesConfig::new));
}
