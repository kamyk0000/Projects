using UnityEngine;

namespace Inventory
{
    public class FixedSizeInventory : Inventory
    {
        [SerializeField] protected int _inventorySize;
        public int InventorySize => _inventorySize;
    }
}