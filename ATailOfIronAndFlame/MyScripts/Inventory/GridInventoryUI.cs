using System;
using System.Collections.Generic;
using UnityEngine;

namespace Inventory
{
    public class GridInventoryUI : MonoBehaviour
    {
        [SerializeField] private GameObject _inventorySlotPrefab;
        [SerializeField] private Transform _inventoryGridPanel;
        [SerializeField] private FixedSizeInventory _inventory;

        private void Awake()
        {
            var slots = new List<SlotPresenter>();
            for (var i = 0; i < _inventory.InventorySize; i++)
            {
                var inventorySlot = Instantiate(_inventorySlotPrefab, _inventoryGridPanel);
                var slotPresenter = inventorySlot.GetComponentInChildren<SlotPresenter>();
                slots.Add(slotPresenter);
            }

            _inventory.Slots = slots;
        }
    }
}