using Crafting;
using Inventory;
using UnityEngine;
using Utilities;

namespace Interactive
{
    public class InventoryInteractable : Singleton<InventoryInteractable>
    {
        [SerializeField] private Inventory.Inventory _otherInventory;
        [SerializeField] private FixedSizeInventory _playerInventory;
        [SerializeField] private CraftingManager _craftingManager;

        public Inventory.Inventory PlayerInventory => _playerInventory;

        public Inventory.Inventory CurrentOtherInventory
        {
            get => _otherInventory;
            set => _otherInventory = value;
        }

        public CraftingManager CurrentCraftingManager
        {
            get => _craftingManager;
            set
            {
                _craftingManager = value;
                _otherInventory = value.Slots[0].Inventory;
            }
        }

        public Inventory.Inventory GetCurrentOpenOppositeInventoryBySlot(SlotPresenter slotPresenter)
        {
            return GetCurrentOpenOppositeInventory(slotPresenter.Inventory);
        }

        public Inventory.Inventory GetCurrentOpenOppositeInventory(Inventory.Inventory inventory)
        {
            if (_otherInventory == null) return null;


            var toreturn = inventory == _playerInventory ? _otherInventory : _playerInventory;


            return toreturn;
        }
    }
}