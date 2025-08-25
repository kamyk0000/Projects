using System;
using System.Collections.Generic;
using System.Linq;
using Inventory.Scriptable_Items;
using UnityEngine;

namespace Crafting.Scriptable_Recipes
{
    [CreateAssetMenu(menuName = "Scriptable Objects/Recipes/Create Recipe", fileName = "New Recipe")]
    public class RecipeScriptableObject : ScriptableObject
    {
        public List<ResourceWrapper> resourcesList;
        public ItemScriptableObject resultItem;
        public float craftingTime;
        public CraftingType type;

        public Dictionary<string, int> ResourcesNeeded =>
            new(resourcesList.ToDictionary(item => item.resource.name.ToLower(),
                item => item.count > 0 ? item.count : 1));
    }

    [Serializable]
    public struct ResourceWrapper
    {
        public ItemScriptableObject resource;
        public int count;
    }
}