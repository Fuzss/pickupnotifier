package fuzs.pickupnotifier.mixin;

import fuzs.pickupnotifier.api.event.EntityItemPickupCallback;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
abstract class ItemEntityFabricMixin extends Entity {

    public ItemEntityFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"))
    public void playerTouch(Player player, CallbackInfo callback) {
        EntityItemPickupCallback.EVENT.invoker().onEntityItemPickup(player, ItemEntity.class.cast(this));
    }
}
