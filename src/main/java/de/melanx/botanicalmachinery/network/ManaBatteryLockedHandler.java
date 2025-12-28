package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

public class ManaBatteryLockedHandler implements IMessageHandler<ManaBatteryLockedHandler.Message, IMessage> {
    
    @Override
    public IMessage onMessage(Message message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (player == null) return null;
        WorldServer world = player.getServerWorld();
        
        world.addScheduledTask(() -> {
            if (world.isBlockLoaded(message.pos)) {
                TileEntity te = world.getTileEntity(message.pos);
                if (te instanceof TileManaBattery) {
                    ((TileManaBattery) te).setSlot1Locked(message.locked1);
                    ((TileManaBattery) te).setSlot2Locked(message.locked2);
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
                }
            }
        });
        
        return null;
    }
    
    public static class Message implements IMessage {
        public BlockPos pos;
        public boolean locked1;
        public boolean locked2;
        
        public Message() {}
        
        public Message(BlockPos pos, boolean locked1, boolean locked2) {
            this.pos = pos;
            this.locked1 = locked1;
            this.locked2 = locked2;
        }
        
        @Override
        public void fromBytes(ByteBuf buf) {
            this.pos = BlockPos.fromLong(buf.readLong());
            this.locked1 = buf.readBoolean();
            this.locked2 = buf.readBoolean();
        }
        
        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeLong(this.pos.toLong());
            buf.writeBoolean(this.locked1);
            buf.writeBoolean(this.locked2);
        }
    }
}
