package supercoder79.survivalgames.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import supercoder79.survivalgames.SurvivalGames;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.stimuli.event.EventResult;

@Mixin(BaseSpawner.class)
public class MixinMobSpawnerLogic {
    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void disableInSG(ServerLevel world, BlockPos pos, CallbackInfo ci) {
        // Disable mob spawners as we handle their behavior
        var space = GameSpaceManager.get().byLevel(world);

        if (space != null && space.getBehavior().testRule(SurvivalGames.DISABLE_SPAWNERS) == EventResult.ALLOW) {
            ci.cancel();
        }
    }
}
