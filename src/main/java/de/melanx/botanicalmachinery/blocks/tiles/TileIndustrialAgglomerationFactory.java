package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.IWorkingTile;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.config.BMConfig;
import de.melanx.botanicalmachinery.core.LibNames;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.lib.LibOreDict;

import javax.annotation.Nonnull;

public class TileIndustrialAgglomerationFactory extends TileBase implements IWorkingTile {

    public static final int RECIPE_COST = TilePool.MAX_MANA / 2;
    public static final int MAX_MANA_PER_TICK = RECIPE_COST / 100;

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(4, slot -> this.sendPacket = true, this::isValidStack);
    private int progress;
    private boolean recipe;

    public TileIndustrialAgglomerationFactory() {
        super(BMConfig.SERVER.capacities.agglomerationFactory);
        this.inventory.setOutputSlots(3);
        this.inventory.setSlotValidator(this::isValidStack);
    }
    
    @Override
    protected String getName() {
        return LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY;
    }
    
    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) return false;
        
        switch (slot) {
            case 0: // ManaSteel Ingot
                return OreDictionary.getOres(LibOreDict.MANA_STEEL).contains(stack);
            case 1: // Mana Diamond
                return OreDictionary.getOres(LibOreDict.MANA_DIAMOND).contains(stack);
            case 2: // Mana Pearl
                return OreDictionary.getOres(LibOreDict.MANA_PEARL).contains(stack);
            default:
                return false;
        }
    }

    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        super.writePacketNBT(cmp);
        cmp.setInteger(TileTags.PROGRESS, this.progress);
    }

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        super.readPacketNBT(cmp);
        this.progress = cmp.getInteger(TileTags.PROGRESS);
    }

    @Override
    public void update() {
        super.update();
        if (this.world != null && !this.world.isRemote) {
            ItemStack manasteel = this.inventory.getStackInSlot(0);
            ItemStack manadiamond = this.inventory.getStackInSlot(1);
            ItemStack manapearl = this.inventory.getStackInSlot(2);
            ItemStack output = this.inventory.getStackInSlot(3);
            if (!manasteel.isEmpty() && !manadiamond.isEmpty() &&
                    !manapearl.isEmpty() && output.getCount() < 64) {
                this.recipe = true;
                int manaTransfer = Math.min(this.mana, Math.min(this.getMaxManaPerTick(), this.getMaxProgress() - this.progress));
                this.progress += manaTransfer;
                this.recieveMana(-manaTransfer);
                if (this.progress >= this.getMaxProgress()) {
                    manasteel.shrink(1);
                    manadiamond.shrink(1);
                    manapearl.shrink(1);
                    ItemStack terraSteel = OreDictionary.getOres(LibOreDict.TERRA_STEEL).get(0).copy();
                    this.inventory.getUnrestricted().insertItem(3, terraSteel, false);
                    this.recipe = false;
                }
                this.markDirty();
            }
            if (!this.recipe && this.progress > 0) {
                this.progress = 0;
                this.markDirty();
                this.markDispatchable();
            } else if (this.recipe) {
                this.recipe = false;
            }
        } else if (this.world != null && BMConfig.CLIENT.rendering.all && BMConfig.CLIENT.rendering.agglomerationFactory) {
            if (this.progress > 0) {
                double time = this.progress / (double) this.getMaxProgress();
                if (time < 0.8) {
                    time = time * 1.25;
                    double y = this.pos.getY() + 6 / 16d + ((5 / 16d) * time);
                    double x1 = this.pos.getX() + 0.2 + (0.3 * time);
                    double x2 = this.pos.getX() + 0.8 - (0.3 * time);
                    double z1 = this.pos.getZ() + 0.2 + (0.3 * time);
                    double z2 = this.pos.getZ() + 0.8 - (0.3 * time);
                    float size = 0.1f + (0.05f * (float) time);
                    float r = 0.0f;
                    float g = (float) time;
                    float b = (float) (1.0 - time);
                    Botania.proxy.wispFX(x1, y, z1, r, g, b, size, 0, 0, 0);
                    Botania.proxy.wispFX(x1, y, z2, r, g, b, size, 0, 0, 0);
                    Botania.proxy.wispFX(x2, y, z1, r, g, b, size, 0, 0, 0);
                    Botania.proxy.wispFX(x2, y, z2, r, g, b, size, 0, 0, 0);
                }
            }
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return RECIPE_COST;
    }

    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK / BMConfig.SERVER.multipliers.agglomerationFactory;
    }
}
