package de.melanx.botanicalmachinery.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundEvent;

public class SoundHelper {
    public static void playSound(SoundEvent sound) {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1));
    }

    public static void playSound(ISound sound) {
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }
}
