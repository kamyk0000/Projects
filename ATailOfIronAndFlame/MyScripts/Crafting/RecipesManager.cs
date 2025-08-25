using System.Collections.Generic;
using System.Linq;
using Crafting.Scriptable_Recipes;
using Utilities;

namespace Crafting
{
    public class RecipesManager : ResourceManager<RecipesManager, RecipeScriptableObject>
    {
        private Dictionary<Dictionary<string, int>, RecipeScriptableObject> _allRecipesWithResources;

        private new void Awake()
        {
            base.Awake();
            _allRecipesWithResources =
                AllResources.ToDictionary(recipe => recipe.Value.ResourcesNeeded, recipe => recipe.Value);
        }

        public RecipeScriptableObject GetExactMatchingRecipe(Dictionary<string, int> resources)
        {
            if (resources == null) return null;

            foreach (var recipe in _allRecipesWithResources)
            {
                var resourcesCountAndType = recipe.Key.All(req =>
                    resources.TryGetValue(req.Key, out var count) && count >= req.Value);
                var sameKeys = resources.Keys.All(recipe.Key.ContainsKey);
                if (resourcesCountAndType && sameKeys) return recipe.Value;
            }

            return null;
        }

        public RecipeScriptableObject GetResourceMatchingRecipe(Dictionary<string, int> resources)
        {
            if (resources == null) return null;

            foreach (var recipe in _allRecipesWithResources)
            {
                var sameKeys = resources.Keys.Count == recipe.Key.Keys.Count &&
                               resources.Keys.All(recipe.Key.ContainsKey);
                if (sameKeys) return recipe.Value;
            }
            
            return null;
        }
    }
}