package supercoder79.survivalgames.game.map.gen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.Nullable;
import supercoder79.survivalgames.game.map.loot.LootHelper;
import supercoder79.survivalgames.game.map.loot.LootProvider;
import supercoder79.survivalgames.game.map.loot.LootProviders;

public class LootChestProcessor implements StructureProcessor {
	public static MapCodec<LootChestProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockPos.CODEC.fieldOf("pos").forGetter(p -> p.chestPos),
			Codec.BOOL.fieldOf("match_terrain").orElse(false).forGetter(p -> p.matchTerrain),
			Codec.STRING.fieldOf("type").forGetter(p -> p.lootType)
	).apply(instance, LootChestProcessor::new));

	private final BlockPos chestPos;
	private final boolean matchTerrain;
	private final String lootType;

	public LootChestProcessor(BlockPos chestPos, boolean matchTerrain, String lootType) {
		this.chestPos = chestPos;
		this.matchTerrain = matchTerrain;
		this.lootType = lootType;
	}

	@Override
	public StructureTemplate.@Nullable StructureBlockInfo processBlock(LevelReader level, BlockPos targetPosition, BlockPos referencePos, BlockPos templateRelativePos, StructureTemplate.StructureBlockInfo processedBlock, StructurePlaceSettings settings) {
		BlockPos pos = processedBlock.pos();
		if (pos.asLong() == this.chestPos.asLong()) {
			// TODO: rotation

			if (this.matchTerrain) {
				int y = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, targetPosition.getX(), targetPosition.getZ());

				BlockPos topPos = new BlockPos(targetPosition.getX(), y, targetPosition.getZ());
				LootHelper.placeProviderChest((LevelAccessor) level, topPos, getLootProvider(this.lootType));
			} else {
				LootHelper.placeProviderChest((LevelAccessor) level, targetPosition, getLootProvider(this.lootType));
			}

			// Place air here
			return null;
		}


		return processedBlock;
	}

	@Override
	public MapCodec<? extends StructureProcessor> codec() {
		return CODEC;
	}

	// TODO: loot provider registry
	private static LootProvider getLootProvider(String name) {
		return switch (name) {
			case "house" -> LootProviders.HOUSE;
			case "tower" -> LootProviders.TOWER;
			case "enchanting_table" -> LootProviders.ENCHANTING_TABLE;
			case "ore_pile" -> LootProviders.ORE_PILE;
			default -> LootProviders.GENERIC;
		};
	}
}
