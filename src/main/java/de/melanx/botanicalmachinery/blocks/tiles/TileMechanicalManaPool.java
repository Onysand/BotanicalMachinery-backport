package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.recipe.RecipeManaInfusion;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.handler.ManaNetworkHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileMechanicalManaPool extends TileBase {
    public static final List<Item> CATALYSTS = Arrays.asList(
        Item.getItemFromBlock(ModBlocks.alchemyCatalyst),
        Item.getItemFromBlock(ModBlocks.conjurationCatalyst),
        Item.getItemFromBlock(ModBlocks.manaVoid)
    );
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(3, this::onSlotChanged, this::isValidStack);
    public boolean validRecipe = true;
    private int cooldown = ServerConfig.multiplierManaPool;

    public TileMechanicalManaPool() {
        super(ServerConfig.capacityManaPool);
        this.inventory.addSlotLimit(0, 1);
        this.inventory.setOutputSlots(2);
    }

    public RecipeManaInfusion getMatchingRecipe(@Nonnull ItemStack stack, @Nonnull ItemStack cat) {
        List<RecipeManaInfusion> matchingNonCatRecipes = new ArrayList<>();
        List<RecipeManaInfusion> matchingCatRecipes = new ArrayList<>();

        for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {
            if (recipe.matches(stack)) {
                if (recipe.getCatalyst() == null) {
                    matchingNonCatRecipes.add(recipe);
                } else if (Item.getItemFromBlock(recipe.getCatalyst().getBlock()) == cat.getItem()) {
                    matchingCatRecipes.add(recipe);
                }
            }
        }

        // Recipes with matching catalyst take priority above recipes with no catalyst specified
        return !matchingCatRecipes.isEmpty() ? matchingCatRecipes.get(0) : !matchingNonCatRecipes.isEmpty() ? matchingNonCatRecipes.get(0) : null;
    }

    @Nonnull
    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    private void onSlotChanged(int slot) {
        if (slot == 1) {
            ItemStack stack = this.getInventory().getStackInSlot(1);
            ItemStack cat = this.getInventory().getStackInSlot(0);
            RecipeManaInfusion recipe = this.getMatchingRecipe(stack, cat);
            if (recipe != null) {
                this.validRecipe = recipe.getManaToConsume() <= this.getCurrentMana();
            } else {
                this.validRecipe = stack.isEmpty();
            }
        }
        this.sendPacket = true;
        this.markDirty();
    }

    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        if (slot == 0) return CATALYSTS.contains(stack.getItem());
        if (slot == 1) return BotaniaAPI.manaInfusionRecipes.stream().anyMatch(recipe -> recipe.matches(stack));
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (!ManaNetworkHandler.instance.isPoolIn(this)) {
            ManaNetworkEvent.addCollector(this);
        }

        if (this.world != null) {
            ItemStack stack = this.getInventory().getStackInSlot(1);
            ItemStack cat = this.getInventory().getStackInSlot(0);
            RecipeManaInfusion recipe = this.getMatchingRecipe(stack, cat);
            if (!this.world.isRemote) {
                if (recipe != null) {
                    if (this.getCooldown() > 1) {
                        this.cooldown--;
                    } else {
                        int mana = recipe.getManaToConsume();
                        if (this.getCurrentMana() >= mana && (this.getInventory().getStackInSlot(2).isEmpty() ||
                                (recipe.getOutput().getItem() == this.getInventory().getStackInSlot(2).getItem() &&
                                        this.getInventory().getStackInSlot(2).getMaxStackSize() > this.getInventory().getStackInSlot(2).getCount()))) {
                            this.recieveMana(-mana);
                            stack.shrink(1);

                            ItemStack output = recipe.getOutput().copy();
                            this.inventory.getUnrestricted().insertItem(2, output, false);
                            this.markDirty();
                            this.cooldown = ServerConfig.multiplierManaPool;
                        }
                    }
                }
            } else if (ClientConfig.everything && ClientConfig.agglomerationFactory) {
                double particleChance = (this.getCurrentMana() / (double) this.getManaCap()) * 0.1D;
                if (Math.random() < particleChance) {
                    float red = 0.0F;
                    float green = 0.7764706F;
                    float blue = 1.0F;
                    Botania.proxy.wispFX(this.pos.getX() + 0.3D + (this.world.rand.nextDouble() * 0.4), this.pos.getY() + 0.5D + (this.world.rand.nextDouble() * 0.25D), this.pos.getZ() + 0.3D + (this.world.rand.nextDouble() * 0.4), red, green, blue, (float) Math.random() / 3.0F, 0, this.world.rand.nextFloat() / 25, 0);
                }
            }
        }
    }

    @Override
    public boolean hasValidRecipe() {
        return this.validRecipe;
    }
    
    @Override
    public void recieveMana(int i) {
        if (this.inventory.getStackInSlot(0).getItem() == Item.getItemFromBlock(ModBlocks.manaVoid))
            this.mana = Math.min(this.getCurrentMana() + i, this.getManaCap());
        else super.recieveMana(i);
    }

    @Override
    public boolean isFull() {
        return this.inventory.getStackInSlot(0).getItem() != Item.getItemFromBlock(ModBlocks.manaVoid) && super.isFull();
    }

    public int getCooldown() {
        return this.cooldown;
    }
}
