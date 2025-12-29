package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.IWorkingTile;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.config.BMConfig;
import de.melanx.botanicalmachinery.core.LibNames;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.brew.IBrewContainer;
import vazkii.botania.api.brew.IBrewItem;
import vazkii.botania.api.recipe.RecipeBrew;
import vazkii.botania.common.Botania;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TileMechanicalBrewery extends TileBase implements IWorkingTile {

    public static final int MAX_MANA_PER_TICK = 50;

    public static final List<Item> BREW_CONTAINER = Arrays.asList(ModItems.vial, ModItems.brewFlask, ModItems.incenseStick, ModItems.bloodPendant);

    private final BaseItemStackHandler inventory = new BaseItemStackHandler(8, slot -> {
        this.update = true;
        this.sendPacket = true;
    }, this::isValidStack);
    private RecipeBrew recipe = null;
    private boolean initDone;
    private int progress;
    private int maxProgress = -1;
    private boolean update;
    private ItemStack currentOutput = ItemStack.EMPTY;

    public TileMechanicalBrewery() {
        super(BMConfig.SERVER.capacities.brewery);
        this.inventory.setInputSlots(IntStream.range(0, 7).toArray());
        this.inventory.setOutputSlots(7);
    }
    
    @Override
    protected String getName() {
        return LibNames.MECHANICAL_BREWERY;
    }
    
    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (slot == 0)
            return stack.getTagCompound() != null ? !stack.getTagCompound().hasKey("brewKey") : BREW_CONTAINER.contains(stack.getItem());
        for (RecipeBrew recipe : BotaniaAPI.brewRecipes)
            return recipe.getInputs().stream().anyMatch(input -> RecipeHelper.isInputMatch(input, stack));
        
        return (Arrays.stream(this.inventory.getInputSlots()).noneMatch(x -> x == slot));
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            if (this.inventory.getStackInSlot(0).isEmpty()) {
                this.recipe = null;
                return;
            }

            for (RecipeBrew recipe : BotaniaAPI.brewRecipes) {
                if (recipe.matches(this.inventory)) {
                    this.recipe = recipe;
                    if (this.inventory.getStackInSlot(0).isEmpty() || !(this.inventory.getStackInSlot(0).getItem() instanceof IBrewContainer)) {
                        this.currentOutput = ItemStack.EMPTY;
                    } else {
                        this.currentOutput = ((IBrewContainer) this.inventory.getStackInSlot(0).getItem()).getItemForBrew(this.recipe.getBrew(), this.inventory.getStackInSlot(0).copy());
                    }
                    this.sendPacket = true;
                    return;
                }
            }
        }
        this.currentOutput = ItemStack.EMPTY;
        this.recipe = null;
    }

    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        super.writePacketNBT(cmp);
        cmp.setInteger(TileTags.PROGRESS, this.progress);
        cmp.setInteger(TileTags.MAX_PROGRESS, this.maxProgress);
        cmp.setTag(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
    }

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        super.readPacketNBT(cmp);
        this.progress = cmp.getInteger(TileTags.PROGRESS);
        this.maxProgress = cmp.getInteger(TileTags.MAX_PROGRESS);
        this.currentOutput = new ItemStack(cmp.getCompoundTag(TileTags.CURRENT_OUTPUT));
    }

    @Override
    public void update() {
        super.update();
        if (!this.initDone) {
            this.update = true;
            this.initDone = true;
        }
        if (this.world != null && !this.world.isRemote) {
            this.updateRecipe();
            boolean done = false;
            if (this.recipe != null) {
                ItemStack output = this.recipe.getOutput(this.inventory.getStackInSlot(0)).copy();
                ItemStack currentOutput = this.inventory.getStackInSlot(7);
                if (!output.isEmpty() && (currentOutput.isEmpty() || (ItemStack.areItemStacksEqual(output, currentOutput) && currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize()))) {
                    this.maxProgress = this.getManaCost();
                    int manaTransfer = Math.min(this.mana, Math.min(MAX_MANA_PER_TICK, this.getMaxProgress() - this.progress));
                    this.progress += manaTransfer;
                    this.recieveMana(-manaTransfer);
                    if (this.progress >= this.getMaxProgress()) {
                        if (currentOutput.isEmpty()) {
                            this.inventory.setStackInSlot(7, output);
                        } else {
                            currentOutput.setCount(currentOutput.getCount() + output.getCount());
                        }
                        this.inventory.getStackInSlot(0).shrink(1);
                        for (Object input : this.recipe.getInputs()) {
                            for (ItemStack stack : this.inventory.getStacks()) {
                                if (RecipeHelper.isInputMatch(input, stack)) {
                                    stack.shrink(1);
                                    break;
                                }
                            }
                        }
                        this.update = true;
                        done = true;
                    }
                    this.markDirty();
                    this.markDispatchable();
                }
            }
            if (this.update) {
                this.updateRecipe();
                this.update = false;
            }
            if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
                this.progress = 0;
                this.maxProgress = -1;
                this.markDirty();
                this.markDispatchable();
            }
        } else if (this.world != null) {
            if (this.progress > 0 && BMConfig.CLIENT.rendering.all && BMConfig.CLIENT.rendering.brewery) {
                if (this.currentOutput.getItem() instanceof IBrewItem && this.world.rand.nextFloat() < 0.5f) {
                    int segments = 3;
                    for (int i = 1; i <= 6; i++) {
                        if (!this.inventory.getStackInSlot(i).isEmpty()) {
                            segments += 1;
                        }
                    }
                    if (this.progress < (segments - 1) * (this.maxProgress / (double) segments) && this.progress > (segments - 2) * (this.maxProgress / (double) segments)) {
                        int targetColor = ((IBrewItem) this.currentOutput.getItem()).getBrew(this.currentOutput).getColor(this.currentOutput);
                        float red = (targetColor >> 16 & 255) / 255f;
                        float green = (targetColor >> 8 & 255) / 255f;
                        float blue = (targetColor & 255) / 255f;
                        double xPos = this.pos.getX() + 0.25 + (this.world.rand.nextDouble() / 2);
                        double zPos = this.pos.getZ() + 0.25 + (this.world.rand.nextDouble() / 2);
                        Botania.proxy.wispFX(xPos, this.pos.getY() + 0.35, zPos, red, green, blue, 0.5f, 0, (float) (0.01 + (this.world.rand.nextDouble() / 18)), 0);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasValidRecipe() {
        return true;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public int getMaxManaPerTick() {
        return MAX_MANA_PER_TICK / BMConfig.SERVER.multipliers.brewery;
    }

    public int getManaCost() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        if (this.recipe == null || stack.isEmpty() || !(stack.getItem() instanceof IBrewContainer)) {
            return 0;
        }
        IBrewContainer container = (IBrewContainer) stack.getItem();
        return container.getManaCost(this.recipe.getBrew(), stack);
    }

    public ItemStack getCurrentOutput() {
        return this.currentOutput;
    }
}
