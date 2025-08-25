using UnityEngine;

namespace Inventory.Scriptable_Items
{
    [CreateAssetMenu(menuName = "Scriptable Objects/Items/Create Basic Item", fileName = "New Basic Item")]
    public class ItemScriptableObject : ScriptableObject
    {
        public string itemName;

        [TextArea] public string itemDescription;

        public Sprite itemSprite;
        public Rarity itemRarity;
        public ItemType itemType;
        public int maxStack;
        public float baseValue;

        public virtual float GetBrunTime()
        {
            return -1;
        }

        public virtual WeaponStats GetWeaponStats()
        {
            return null;
        }
    }
}