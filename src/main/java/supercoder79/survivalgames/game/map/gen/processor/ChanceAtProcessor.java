package supercoder79.survivalgames.game.map.gen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.Nullable;

public class ChanceAtProcessor implements StructureProcessor {
	public static MapCodec<ChanceAtProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockPos.CODEC.fieldOf("pos").forGetter(p -> p.pos),
			BlockState.CODEC.fieldOf("state").forGetter(p -> p.state),
			Codec.BOOL.fieldOf("match_terrain").orElse(false).forGetter(p -> p.matchTerrain),
			Codec.DOUBLE.fieldOf("chance").forGetter(p -> p.chance)
	).apply(instance, ChanceAtProcessor::new));

	private final BlockPos pos;
	private final boolean matchTerrain;
	private final BlockState state;
	private final double chance;

	public ChanceAtProcessor(BlockPos pos, BlockState state, boolean matchTerrain, double chance) {
		this.pos = pos;
		this.matchTerrain = matchTerrain;
		this.state = state;
		this.chance = chance;
	}

	@Override
	public StructureTemplate.@Nullable StructureBlockInfo processBlock(LevelReader world, BlockPos targetPosition, BlockPos referencePos, BlockPos templateRelativePos, StructureTemplate.StructureBlockInfo worldInfo, StructurePlaceSettings settings) {
		if (settings.getRandom(worldInfo.pos()).nextDouble() < this.chance) {
			if (templateRelativePos.asLong() == this.pos.asLong()) {

				BlockState state = this.state;
				if (state.hasProperty(BlockStateProperties.FACING)) {
					state = state.setValue(BlockStateProperties.FACING, settings.getRotation().rotate(state.getValue(BlockStateProperties.FACING)));
				} else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
					state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, settings.getRotation().rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
				}

				if (this.matchTerrain) {
					int y = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, worldInfo.pos().getX(), worldInfo.pos().getZ());

					BlockPos topPos = new BlockPos(worldInfo.pos().getX(), y, worldInfo.pos().getZ());
					return new StructureTemplate.StructureBlockInfo(topPos, state, null);
				}

				return new StructureTemplate.StructureBlockInfo(worldInfo.pos(), state, null);
			}
		}

		return worldInfo;
	}

	@Override
	public MapCodec<? extends StructureProcessor> codec() {
		return CODEC;
	}
}
