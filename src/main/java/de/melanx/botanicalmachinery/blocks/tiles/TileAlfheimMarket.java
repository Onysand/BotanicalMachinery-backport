package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.IWorkingTile;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.config.BMConfig;
import de.melanx.botanicalmachinery.core.LibNames;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeElvenTrade;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class TileAlfheimMarket extends TileBase implements IWorkingTile {

    private static final int RECIPE_COST = BMConfig.SERVER.recipeCosts.alfheimMarket;
    public static final int MAX_MANA_PER_TICK = 25;

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(5, slot -> {
        this.update = true;
        this.sendPacket = true;
    }, this::isValidStack);
    private RecipeElvenTrade recipe = null;
    private boolean initDone;
    private int progress;
    private boolean update;
    private ItemStack currentInput = ItemStack.EMPTY;
    private ItemStack currentOutput = ItemStack.EMPTY;

    public TileAlfheimMarket() {
        super(BMConfig.SERVER.capacities.alfheimMarket);
        this.inventory.setInputSlots(IntStream.range(0, 4).toArray());
        this.inventory.setOutputSlots(4);
        this.update = true;
    }
    
    @Override
    protected String getName() {
        return LibNames.ALFHEIM_MARKET;
    }
    
    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (slot >= this.inventory.getInputSlots().length) return false;
        
        for (RecipeElvenTrade r : BotaniaAPI.elvenTradeRecipes) {
            boolean match = r.getInputs().stream().anyMatch(input -> RecipeHelper.isInputMatch(input, stack));
            if (match) {
                return true;
            }
        }
        
        return Arrays.stream(this.inventory.getInputSlots()).noneMatch(x -> x == slot);
    }
    
    private void updateRecipe() {
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                inputs.add(inventory.getStackInSlot(i));
            }
        }
        
        recipe = null;
        for (RecipeElvenTrade r : BotaniaAPI.elvenTradeRecipes) {
            if (r.matches(inputs, false)) {
                recipe = r;
                currentInput = inputs.get(0).copy();
                currentOutput = r.getOutputs().get(0).copy();
                return;
            }
        }
        currentInput = ItemStack.EMPTY;
        currentOutput = ItemStack.EMPTY;
    }

    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        super.writePacketNBT(cmp);
        cmp.setInteger(TileTags.PROGRESS, this.progress);
        cmp.setTag(TileTags.CURRENT_INPUT, this.currentInput.serializeNBT());
        cmp.setTag(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
    }

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        super.readPacketNBT(cmp);
        this.progress = cmp.getInteger(TileTags.PROGRESS);
        this.currentInput = new ItemStack(cmp.getCompoundTag(TileTags.CURRENT_INPUT));
        this.currentOutput = new ItemStack(cmp.getCompoundTag(TileTags.CURRENT_OUTPUT));
    }
    
    @Override
    public void update() {
        super.update();
        if (this.world != null && !this.world.isRemote) {
            if (!this.initDone) {
                this.update = true;
                this.initDone = true;
            }
            boolean done = false;
            if (this.recipe != null) {
                ItemStack output = this.recipe.getOutputs().get(0).copy();
                if (this.inventory.getUnrestricted().insertItem(4, output, true).isEmpty()) {
                    int manaTransfer = Math.min(this.mana, Math.min(this.getMaxManaPerTick(), this.getMaxProgress() - this.progress));
                    this.progress += manaTransfer;
                    this.recieveMana(-manaTransfer);
                    
                    if (this.progress >= RECIPE_COST) {
                        List<ItemStack> inputs = new ArrayList<>();
                        for (int i = 0; i < 4; i++) inputs.add(inventory.getStackInSlot(i));
                        Optional<List<ItemStack>> toShrink = RecipeHelper.isInputsMatch(this.recipe.getInputs(), inputs);
                        if (toShrink.isPresent()) {
                            this.inventory.getUnrestricted().insertItem(4, output, false);
                            toShrink.get().forEach(stack -> stack.shrink(1));
                        }
                        
                        this.update = true;
                        done = true;
                    }
                }
            }
            if (this.update) {
                this.updateRecipe();
                this.markDirty();
                this.update = false;
            }
            if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
                this.progress = 0;
                this.markDirty();
                this.markDispatchable();
            }
            if (this.mana > 0) {
                for (int i : this.inventory.getInputSlots()) {
                    if (this.inventory.getStackInSlot(i).getItem() == Items.BREAD) {
                        this.world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
                        this.world.createExplosion(null, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 3F, true);
                        break;
                    }
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
        return MAX_MANA_PER_TICK * BMConfig.SERVER.multipliers.alfheimMarket;
    }
    
    public ItemStack getCurrentInput() {
        return this.currentInput;
    }

    public ItemStack getCurrentOutput() {
        return this.currentOutput;
    }
}
