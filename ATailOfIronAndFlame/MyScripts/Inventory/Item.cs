#nullable enable
using System;
using System.Collections.Generic;
using Inventory.Scriptable_Items;
using UnityEngine;

namespace Inventory
{
    [Serializable]
    public class Item
    {
        [SerializeField] protected string _name, _debugName;
        [SerializeField] protected float _value;
        [SerializeField] private WeaponStats? _weaponStats;
        [NonSerialized] protected ItemScriptableObject _data;

        public Item(string debugName, WeaponStats? weaponStats = null)
        {
            var data = ItemsManager.Instance.GetResource(debugName.ToLower());
            _data = data;
            _debugName = data.name.ToLower();
            _name = data.itemName;
            _value = _value <= 0 ? data.baseValue : _value;
            _weaponStats = weaponStats;
        }

        public Item(ItemScriptableObject data, WeaponStats? weaponStats = null)
        {
            _data = data;
            _debugName = data.name.ToLower();
            _name = data.itemName;

            _value = _value <= 0 ? data.baseValue : _value;
            _weaponStats = weaponStats;
        }

        public string DebugName => _debugName;

        public float Value
        {
            get => _value;
            set => _value = value;
        }

        public WeaponStats? GeWeaponStats => _weaponStats;
        public ItemType ItemType => _data.itemType;
        public virtual Sprite ItemSprite => _data.itemSprite;
        public virtual string Name => _name;
        public string Description => _data.itemDescription;
        public Rarity Rarity => _data.itemRarity;
        public int MaxStack => _data.maxStack;

        public ItemScriptableObject GetData()
        {
            return _data;
        }

        public override string ToString()
        {
            return $"{DebugName} - {Name}";
        }

        public void RestoreData()
        {
            _data = ItemsManager.Instance.GetResource(_debugName);
        }

        public bool Matches(Item other)
        {
            return DebugName.Equals(other.DebugName);
        }

        public bool Matches(ItemScriptableObject other)
        {
            return DebugName.Equals(other.name.ToLower());
        }
    }

    [Serializable]
    public class WeaponItem : Item
    {
        [SerializeField] private bool _isSharpened, _isUpgraded;
        [SerializeField] private string _nameAddition;
        [SerializeField] private List<RuneItem> runeUpgrades = new();

        public WeaponItem(WeaponItemScriptableObject data, WeaponStats? weaponStats = null)
            : base(data, weaponStats)
        {
            _value *= 1.2f;
            _nameAddition = "Unsharpened";
            _isSharpened = false;
            _isUpgraded = false;
        }

        public bool IsUpgraded => runeUpgrades.Count > 0;

        public bool IsSharpened => _isSharpened;

        public override Sprite ItemSprite
        {
            get
            {
                if (_data is not WeaponItemScriptableObject data) return base.ItemSprite;
                if (_isUpgraded) return data.enchantedSprite;
                return _isSharpened ? data.sharpenedSprite : data.unsharpenedSprite;
            }
        }

        public override string Name => _nameAddition + " " + _name;

        public void Sharpen()
        {
            _nameAddition = "Sharpened";
            _isSharpened = true;
            _value *= 1.2f;
        }

        public void Upgrade(List<RuneItem> runes) //list of effects
        {
            _nameAddition = "Enchanted";
            _isUpgraded = true;
            runeUpgrades.AddRange(runes);
            _value *= 1f + runeUpgrades.Count / 20f;
        }
    }

    [Serializable]
    public class RuneItem : Item
    {
        private float _effectDuration;
        private float _effectStrength;

        public RuneItem(ItemScriptableObject data, WeaponStats? weaponStats = null)
            : base(data, weaponStats)
        {
        }
    }

    [Serializable]
    public class WeaponStats
    {
        // TBD
        public float attackPower;
        public float attackSpeed;
        public float durability;
        public float critChance;
        public float critDamage;
        public float armorPen;
    }
}