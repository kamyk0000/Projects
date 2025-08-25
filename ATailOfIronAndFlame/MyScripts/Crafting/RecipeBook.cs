using System;
using System.Collections.Generic;
using System.Linq;
using Crafting.Scriptable_Recipes;
using Menu.Main_Menu;
using UnityEngine;

namespace Crafting
{
    public class RecipeBook : MonoBehaviour
    {
        [SerializeField] private Transform _recipesSection;
        [SerializeField] private Transform _tabsSection;
        [SerializeField] private GameObject _recipeUIprefab;
        [SerializeField] private GameObject _tabUIprefab;
        [SerializeField] private GameObject _tabSpacer;
        [SerializeField] private List<RecipeScriptableObject> _knownRecipes = new();
        [SerializeField] private List<CraftingManager> _craftingManagers;
        
        private readonly List<TabUI> _allTabs = new();
        private CraftingType _currentFilter = CraftingType.None;
        private TabUI _currentTab;

        private void Start()
        {
            //_knownRecipes = Resources.LoadAll<RecipeScriptableObject>("ScriptableObjects/Recipes").ToList();
            LoadKnownRecipes();
            Initialize(_knownRecipes, Enum.GetValues(typeof(CraftingType)));
            foreach (var craftingStation in _craftingManagers)
            {
                craftingStation.OnItemCrafted += LearnRecipe;
                craftingStation.OnStationInteracted += FilterRecipeBookByType;
            }
        }

        private void OnDestroy()
        {
            SaveKnownRecipes();
        }

        private void OnApplicationQuit()
        {
            SaveKnownRecipes();
        }

        private void Initialize(List<RecipeScriptableObject> recipes, Array types)
        {
            InitializeFilterTabs(types);

            if (recipes is null) return;

            InitializeRecipes(recipes);
        }

        private void InitializeRecipes(List<RecipeScriptableObject> recipes)
        {
            ClearChildUIObjects(_recipesSection);

            foreach (var recipe in recipes)
            {
                var recipeSlotUI = Instantiate(_recipeUIprefab, _recipesSection);
                var recipeUI = recipeSlotUI.GetComponentInChildren<RecipeUI>();
                recipeUI.Initialize(recipe);
            }
        }

        private void InitializeFilterTabs(Array types)
        {
            ClearChildUIObjects(_tabsSection);

            foreach (CraftingType type in types)
            {
                var tabUIWrapper = Instantiate(_tabUIprefab, _tabsSection);
                var tabUI = tabUIWrapper.GetComponentInChildren<TabUI>();
                tabUI.Initialize(type);
                _allTabs.Add(tabUI);

                tabUI.OnTabClicked += FilterRecipeBookByTab;
            }

            Instantiate(_tabSpacer, _tabsSection);
        }

        private void ClearChildUIObjects(Transform parentTransform)
        {
            for (var i = 0; i < parentTransform.childCount; i++) Destroy(parentTransform.GetChild(i).gameObject);
        }

        private void FilterRecipeBookByTab(TabUI tabUI)
        {
            _currentTab?.UnselectTab();
            _currentTab = tabUI;
            _currentTab.SelectTab();
            _currentFilter = _currentTab.GetCraftingType();

            if (_currentFilter == CraftingType.None)
            {
                InitializeRecipes(_knownRecipes);
                return;
            }

            var filteredRecipes = _knownRecipes.FindAll(recipe => recipe.type == _currentFilter)
                .Select(recipe => recipe).ToList();
            InitializeRecipes(filteredRecipes);
        }

        private void FilterRecipeBookByType(CraftingType type)
        {
            if (_currentFilter == type) return;
            _currentFilter = type;
            _currentTab?.UnselectTab();
            _currentTab = _allTabs.FirstOrDefault(tabUi => tabUi.GetCraftingType() == type);
            _currentTab?.SelectTab();

            var filteredRecipes = _knownRecipes.FindAll(recipe => recipe.type == _currentFilter)
                .Select(recipe => recipe).ToList();
            InitializeRecipes(filteredRecipes);
        }

        private void ClearFilterRecipeBook()
        {
            _currentFilter = CraftingType.None;
            InitializeRecipes(_knownRecipes);
        }

        private void LearnRecipe(RecipeScriptableObject recipe)
        {
            if (recipe is null) return;
            if (_knownRecipes.Contains(recipe)) return;

            _knownRecipes.Add(recipe);
            if (_currentFilter == CraftingType.None || recipe.type == _currentFilter)
            {
                var recipeSlotUI = Instantiate(_recipeUIprefab, _recipesSection);
                var recipeUI = recipeSlotUI.GetComponentInChildren<RecipeUI>();
                recipeUI.Initialize(recipe);
            }
        }

        private void SaveKnownRecipes()
        {
            var data = new SerializableRecipes { recipes = _knownRecipes.Select(recipe => recipe.name).ToArray() };
            SaveSystem.LoadGame(PlayerPrefs.GetInt("SaveNumber")).AddAdditionalData("PlayerRecipes", data);
        }

        private void LoadKnownRecipes()
        {
            var saveData = SaveSystem.LoadGame(PlayerPrefs.GetInt("SaveNumber"))
                .GetAdditionalDataByKey<SerializableRecipes>("PlayerRecipes");
            if (saveData.recipes == null || saveData.recipes.Length <= 0) return;
            foreach (var recipe in saveData.recipes) _knownRecipes.Add(RecipesManager.Instance.GetResource(recipe));
        }

        [Serializable]
        public struct SerializableRecipes
        {
            [SerializeField] public string[] recipes;
        }
    }
}