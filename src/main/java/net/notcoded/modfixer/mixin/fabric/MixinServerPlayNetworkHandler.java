package net.notcoded.modfixer.mixin.fabric;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1500)
public class MixinServerPlayNetworkHandler {
    @Shadow
    public ServerPlayer player;

    @TargetHandler(
            mixin = "net.fabricmc.fabric.mixin.event.interaction.MixinServerPlayNetworkHandler",
            name = "onPlayerInteractEntity"
    )
    @Redirect(method = "@MixinSquared:Handler", at = @At(value = "NEW", target = "(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/EntityHitResult;"))
    public EntityHitResult fixEntityHitResult(Entity entity, Vec3 vec3) {
        float interactionDistance = (float) new Vec3(player.getX(), player.getEyeY(), player.getZ()).distanceTo(vec3);
        return new EntityHitResult(entity, vec3, interactionDistance);
    }
}
