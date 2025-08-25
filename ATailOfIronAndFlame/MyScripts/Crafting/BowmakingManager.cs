using Audio;
using Crafting.Scriptable_Recipes;
using Inventory;
using Inventory.Scriptable_Items;

namespace Crafting
{
    public class BowmakingManager : CraftingManager
    {
        protected override void Start()
        {
            base.Start();

            _resultSlot.OnSlotItemRemoved += FinishCrafting;

            HandleSlotItemChange();
        }

        protected override void HandleSlotItemChange()
        {
            var materials = GetResources();
            var newRecipe = RecipesManager.Instance.GetResourceMatchingRecipe(materials);

            if (materials.Count <= 0 || newRecipe is null)
            {
                ClearCrafting();
                return;
            }

            PrepareCrafting(newRecipe);
        }

        private void PrepareCrafting(RecipeScriptableObject recipe)
        {
            _currentRecipe = recipe;
            var stack = recipe.resultItem.itemType == ItemType.Arrow ? 5 : 1;

            if (_resultSlot.IsEmpty ||
                !_resultSlot.Item.DebugName.ToLower()
                    .Equals(_currentRecipe.resultItem.name
                        .ToLower()))
                _resultSlot.SetItem(new Item(_currentRecipe.resultItem), stack);
        }

        private void FinishCrafting()
        {
            if (_currentRecipe == null) return;
            _isWorking = false;
            AudioManager.Instance.PlaySFX(_clip, transform);


            ConsumeResources();
            HandleSlotItemChange();
        }

        private void ClearCrafting()
        {
            StopCrafting();
            if (!_resultSlot.IsEmpty) _resultSlot.SetItem(null);
        }
    }
}