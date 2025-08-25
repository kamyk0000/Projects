using UnityEngine;

namespace Inventory.Scriptable_Items
{
    [CreateAssetMenu(menuName = "Scriptable Objects/Items/Create Weapon Item", fileName = "New Weapon Item")]
    public class WeaponItemScriptableObject : ItemScriptableObject
    {
        public Sprite billetSprite, unsharpenedSprite, sharpenedSprite, enchantedSprite;
        public int attackPower;
        public float attackSpeed;
        public float durability;

        public override WeaponStats GetWeaponStats()
        {
            return new WeaponStats
            {
                attackPower = attackPower,
                attackSpeed = attackSpeed,
                durability = durability
            };
        }
    }
}