package de.melanx.botanicalmachinery.blocks.base;

import com.google.common.base.Predicates;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.util.inventory.ItemStackHandlerWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IKeyLocked;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IThrottledPacket;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public abstract class TileBase extends TileMod implements IManaPool, IManaMachineTile, IKeyLocked, ISparkAttachable, IThrottledPacket, ITickable {

    public int mana;
    private final int manaCap;
    public String inputKey = "";
    public String outputKey = "";

    public boolean sendPacket = false;

    private final IItemHandlerModifiable handler = this.createHandler(this::getInventory);

    public TileBase(int manaCap) {
        super();
        this.manaCap = manaCap;
    }

    /**
     * This can be used to add canExtract or canInsert to the wrapper used as capability. You may not call the supplier
     * now. Always use IItemHandlerModifiable.createLazy. You may call the supplier inside the canExtract and canInsert
     * lambda.
     */
    protected IItemHandlerModifiable createHandler(Supplier<IItemHandlerModifiable> inventory) {
        return ItemStackHandlerWrapper.createFromSup(inventory);
    }

    @Nonnull
    public abstract BaseItemStackHandler getInventory();

    public abstract boolean isValidStack(int slot, ItemStack stack);

    @Nonnull
    @Override
    public <X> X getCapability(@Nonnull Capability<X> cap, EnumFacing facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
	        //noinspection unchecked
	        return (X) this.handler;
        }
        return super.getCapability(cap, facing);
    }

    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        cmp.setTag(TileTags.INVENTORY, this.getInventory().serializeNBT());
        cmp.setInteger(TileTags.MANA, this.getCurrentMana());
        cmp.setString(TileTags.INPUT_KEY, this.inputKey);
        cmp.setString(TileTags.OUTPUT_KEY, this.outputKey);
    }

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        this.getInventory().deserializeNBT(cmp.getCompoundTag(TileTags.INVENTORY));
        this.mana = cmp.getInteger(TileTags.MANA);
        if (cmp.hasKey(TileTags.INPUT_KEY)) this.inputKey = cmp.getString(TileTags.INPUT_KEY);
        if (cmp.hasKey(TileTags.OUTPUT_KEY)) this.outputKey = cmp.getString(TileTags.OUTPUT_KEY);
    }

    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc) {
        ItemStack block = new ItemStack(this.getWorld().getBlockState(this.pos).getBlock());
        String name = block.getDisplayName();
        int color = 0x4444FF;
        HUDHandler.drawSimpleManaHUD(color, this.getCurrentMana(), this.getManaCap(), name, new ScaledResolution(mc));
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.getTextureManager().bindTexture(HUDHandler.manaBar);

        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    @Override
    public void update() {
        if (this.world != null) {
            if (this.sendPacket) {
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
                this.sendPacket = false;
            }
        }
    }

    @Override
    public String getInputKey() {
        return this.inputKey;
    }

    @Override
    public String getOutputKey() {
        return this.outputKey;
    }

    @Override
    public void markDispatchable() {
        this.sendPacket = true;
    }

    @Override
    public boolean canAttachSpark(ItemStack itemStack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity iSparkEntity) {
    }

    @Override
    public int getAvailableSpaceForMana() {
        return Math.max(Math.max(0, this.getManaCap() - this.getCurrentMana()), 0);
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        @SuppressWarnings("ConstantConditions")
        List<Entity> sparks = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.pos.up(), this.pos.up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
        if (sparks.size() == 1) {
            Entity entity = sparks.get(0);
            return (ISparkEntity) entity;
        }
        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public boolean isFull() {
        return this.getCurrentMana() >= this.getManaCap();
    }

    @Override
    public void recieveMana(int i) {
        int old = this.getCurrentMana();
        this.mana = Math.max(0, Math.min(this.getCurrentMana() + i, this.getManaCap()));
        if (old != this.getCurrentMana()) {
            this.markDirty();
            this.markDispatchable();
        }
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }

    @Override
    public int getCurrentMana() {
        return this.mana;
    }

    @Override
    public int getManaCap() {
        return this.manaCap;
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    @Override
    public EnumDyeColor getColor() {
        return null;
    }
}
