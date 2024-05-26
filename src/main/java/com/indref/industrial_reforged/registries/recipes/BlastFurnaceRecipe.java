package com.indref.industrial_reforged.registries.recipes;

import com.indref.industrial_reforged.api.recipes.IRRecipe;
import com.indref.industrial_reforged.util.recipes.IngredientUtils;
import com.indref.industrial_reforged.util.recipes.IngredientWithCount;
import com.indref.industrial_reforged.util.recipes.RecipeUtils;
import com.indref.industrial_reforged.util.Utils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BlastFurnaceRecipe(NonNullList<IngredientWithCount> ingredients, FluidStack resultFluid,
                                 int duration) implements IRRecipe<SimpleContainer> {
    public static final String NAME = "blast_furnace";
    public static final RecipeType<BlastFurnaceRecipe> TYPE = RecipeUtils.newRecipeType(NAME);
    public static final RecipeSerializer<BlastFurnaceRecipe> SERIALIZER =
            RecipeUtils.newRecipeSerializer(IRRecipeSerializer.BlastFurnace.CODEC, IRRecipeSerializer.BlastFurnace.STREAM_CODEC);

    public BlastFurnaceRecipe(List<IngredientWithCount> ingredients, FluidStack resultFluid, int duration) {
        this(Utils.listToNonNullList(ingredients), resultFluid, duration);
    }

    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        if (level.isClientSide) return false;

        return RecipeUtils.compareItems(simpleContainer.getItems(), ingredients);
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return Utils.listToNonNullList(IngredientUtils.iWCToIngredientsSaveCount(ingredients));
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return TYPE;
    }

    public FluidStack resultFluid() {
        return resultFluid.copy();
    }
}
