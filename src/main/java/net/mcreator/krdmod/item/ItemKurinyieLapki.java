
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemKurinyieLapki extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:kurinyie_lapki")
	public static final Item block = null;

	public ItemKurinyieLapki(ElementsKrdModMod instance) {
		super(instance, 61);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:kurinyie_lapki", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("kurinyie_lapki");
			setRegistryName("kurinyie_lapki");

			setCreativeTab(TabFoods.tab);
			setMaxStackSize(64);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.EAT;
		}

	}

}
