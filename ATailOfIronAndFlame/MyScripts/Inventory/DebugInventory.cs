using UnityEngine;

namespace Inventory
{
    public class DebugInventory : FixedSizeInventory
    {
        [SerializeField] private GameObject _UI;

        private new void Start()
        {
            _inventorySize = ItemsManager.Instance.AllResources.Count;
            base.Start();
            
            DebugPopulate();
        }

        private void Update()
        {
            if (!Input.GetKeyDown(KeyCode.L)) return;
            _UI.SetActive(!_UI.activeSelf);
        }

        private void DebugPopulate()
        {
            var i = 0;
            foreach (var itemSO in ItemsManager.Instance.AllResources.Values)
            {
                _slots[i].SetItem(new Item(itemSO), itemSO.maxStack);
                _slots[i].SetState(SlotState.Infinite, null);
                i++;
            }
        }
    }
}