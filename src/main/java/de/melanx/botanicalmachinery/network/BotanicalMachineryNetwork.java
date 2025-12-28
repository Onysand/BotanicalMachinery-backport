package de.melanx.botanicalmachinery.network;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class BotanicalMachineryNetwork {

    private BotanicalMachineryNetwork() {}

    private static int discriminator = 0;
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(BotanicalMachinery.MODID);

    public static void registerPackets() {
        INSTANCE.registerMessage(ManaBatteryLockedHandler.class, ManaBatteryLockedHandler.Message.class, discriminator++, Side.SERVER);
    }

    public static void updateLockedState(TileManaBattery tile) {
        if (tile.getWorld().isRemote) {
            INSTANCE.sendToServer(new ManaBatteryLockedHandler.Message(tile.getPos(), tile.isSlot1Locked(), tile.isSlot2Locked()));
        }
    }
}
