using System;
using System.Collections.Generic;
using System.Linq;
using Crafting;
using Inventory.Scriptable_Items;
using Menu.Main_Menu;
using UnityEngine;
using UnityEngine.SceneManagement;
using Utilities;

namespace Inventory
{
    public class Inventory : MonoBehaviour
    {
        [SerializeField] private string _name;
        [SerializeField] private CraftingManager _forCrafting;
        [SerializeField] protected List<SlotPresenter> _slots = new();
        private String _activeSceneName;
        public List<SlotPresenter> Slots
        {
            get => _slots;
            set => _slots = value;
        }

        protected void Start()
        {
            _activeSceneName = SceneManager.GetActiveScene().name;
            if (_forCrafting != null && Slots.Count <= 0) Slots = _forCrafting.Slots;

            foreach (var slot in Slots)
            {
                slot.Initialize(new SlotModel());
                slot.Inventory = this;
            }

            LoadInventory();
            SceneManager.activeSceneChanged += SaveInventory;
        }

        private void SaveInventory()
        {
            var serializableSlots = new List<SerializableSlotModel>();
            foreach (var slot in Slots)
            {
                var serializableSlot = new SerializableSlotModel
                {
                    item = slot.Item,
                    currentStack = slot.CurrentStack,
                    slotStateType = slot.Model.slotStateType,
                    hadStateChanged = slot.Model.hadStateChanged
                };
                serializableSlots.Add(serializableSlot);
            }
            
            var saveData = SaveSystem.LoadGame(PlayerPrefs.GetInt("SaveNumber"));
            saveData.AddAdditionalData(_name, serializableSlots);
        }
        
        public void SaveInventory(Scene oldScene, Scene newScene)
        {
            SceneManager.activeSceneChanged -= SaveInventory;
            if(newScene.name == SceneNames.MenuSceneName && _activeSceneName == SceneNames.TestingSceneName) return;
            SaveInventory();
            _activeSceneName = newScene.name;
        }

        private void LoadInventory()
        {
            var slotsData = SaveSystem.LoadGame(PlayerPrefs.GetInt("SaveNumber"))
                .GetAdditionalDataByKey<List<SerializableSlotModel>>(_name);

            if (slotsData == null || slotsData.Count <= 0) return;
            
            for (var i = 0; i < slotsData.Count; i++)
            {
                var item = slotsData[i].item;
                item?.RestoreData();

                var model = new SlotModel
                {
                    slotStateType = slotsData[i].slotStateType,
                    hadStateChanged = slotsData[i].hadStateChanged
                };

                _slots[i].Initialize(model);
                _slots[i].SetItem(item, slotsData[i].currentStack);
                _slots[i].Inventory = this;
            }
        }

        public void SetItem(Item item, int index)
        {
            if (index < 0 || index >= _slots.Count)
                return;
            _slots[index].SetItem(item);
        }

        public bool TryMoveItem(SlotModel fromSlotPresenter)
        {
            if (fromSlotPresenter.IsEmpty) return false;
            foreach (var slot in _slots)
            {
                if (slot.IsEmpty || slot.Item.DebugName == fromSlotPresenter.Item.DebugName) 
                    slot.HandleItemDropped(fromSlotPresenter);

                if (fromSlotPresenter.IsEmpty) return true;
            }
            return false;
        }

        public bool TryAddItem(Item item, int amount = 1)
        {
            var remaining = amount;
            var emptySlotPresent = false;
            foreach (var slot in _slots)
            {
                if (slot.IsEmpty) emptySlotPresent = true; 
                //osłona jeżeli zakładamy ze itemek może mieć większy rozmiar niż max stack (raczej do podnoszosnych itemków)

                if (!slot.IsEmpty && !slot.IsLocked && slot.Item.DebugName == item.DebugName)
                {
                    remaining = slot.AddToStack(remaining);
                    if (remaining == 0) return true;
                }
            }

            if (!emptySlotPresent) return false;

            foreach (var slot in _slots)
            {
                if (!slot.IsEmpty || slot.IsLocked) continue;
                if (!slot.AcceptsItemType(item.ItemType)) continue;
                slot.SetItem(item);
                remaining = slot.AddToStack(remaining - 1);

                if (remaining == 0) return true;
            }
            return false;
        }

        public SlotPresenter GetEmptySlot()
        {
            return _slots.FirstOrDefault(slot => slot.IsEmpty && !slot.IsLocked);
        }

        public bool HasItem(Item item)
        {
            return HasItem(item.GetData());
        }

        public bool HasItem(ItemType type)
        {
            return _slots.Any(slot => !slot.IsEmpty && !slot.IsLocked && slot.Item.ItemType == type);
        }

        public bool HasItem(ItemScriptableObject item)
        {
            return _slots.Any(slot => !slot.IsEmpty && !slot.IsLocked && slot.Item.DebugName == item.name.ToLower());
        }
        public bool HasItem(string itemName)
        {
            foreach (var slot in _slots)
                if (!slot.IsEmpty && slot.Item.Name == itemName)
                {
                    return true;
                }
            return false;
        }
        public SlotPresenter GetSlotForItem(string itemName)
        {
            foreach (var slot in _slots)
                if (!slot.IsEmpty && slot.Item.Name == itemName)
                {
                    return slot;
                }
            return null;
        }


        public bool HasItemAmount(string itemDebugName, int requiredAmount)
        {
            var totalAmount = 0;

            foreach (var slot in _slots.Where(slot => !slot.IsEmpty && slot.Item.DebugName == itemDebugName.ToLower()))
            {
                totalAmount += slot.CurrentStack;
                if (totalAmount >= requiredAmount) return true;
            }
            return false;
        }

        public bool HasItemWithProperty(string itemName, string property)
        {
            foreach (var slot in _slots)
                if (!slot.IsEmpty && slot.Item.Name == itemName)
                {
                    switch (property)
                    {
                        case "SHAPED":
                            if (slot.Item != null && slot.Item is WeaponItem)
                            {
                                return true;
                            }
                            break;
                        case "SHARPENED":
                            var weapon = slot.Item as WeaponItem;
                            if (weapon != null && weapon.IsSharpened)
                            {
                                return true;
                            }
                            break;
                        case "UPGRADED":
                            var weapon2 = slot.Item as WeaponItem;
                            if (weapon2 != null && weapon2.IsUpgraded)
                            {
                                return true;
                            }
                            break;
                    }
                }
            return false;
        }

        public void ClearInventory()
        {
            foreach (var slot in _slots) slot.ClearItem();
        }
    }

    [Serializable]
    public class SerializableSlotModel
    {
        [SerializeField] public Item item;
        [SerializeField] public int currentStack;
        [SerializeField] public SlotState slotStateType;
        [SerializeField] public bool hadStateChanged;
    }
}