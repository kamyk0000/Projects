using UnityEngine;

namespace Inventory.Scriptable_Items
{
    [CreateAssetMenu(menuName = "Scriptable Objects/Items/Create Fuel Item", fileName = "New Fuel Item")]
    public class FuelItemScriptableObject : ItemScriptableObject
    {
        public float burnTime;

        public override float GetBrunTime()
        {
            return burnTime;
        }
    }
}