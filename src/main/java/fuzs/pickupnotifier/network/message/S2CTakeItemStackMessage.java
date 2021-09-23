package fuzs.pickupnotifier.network.message;

import fuzs.pickupnotifier.client.handler.AddEntriesHandler;
import fuzs.pickupnotifier.config.ConfigHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class S2CTakeItemStackMessage implements IMessage {

    private ItemStack stack;

    public S2CTakeItemStackMessage() {

    }

    public S2CTakeItemStackMessage(ItemStack stack) {

        this.stack = stack;
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeItem(this.stack);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        this.stack = buf.readItem();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {

        ctx.enqueueWork(() -> {

            if (!ConfigHolder.getGeneralConfig().clientSideOnly) {

                AddEntriesHandler.addItemEntry(this.stack);
            }
        });
    }

}
