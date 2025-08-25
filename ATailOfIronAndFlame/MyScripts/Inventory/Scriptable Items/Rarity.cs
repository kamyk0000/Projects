using System;
using UnityEngine;

namespace Inventory.Scriptable_Items
{
    public enum Rarity
    {
        [TextColor("#000000")] Common,
        [TextColor("#5aad00")] Uncommon,
        [TextColor("#03a9fc")] Rare,
        [TextColor("#d203fc")] Epic,
        [TextColor("#ffcf05")] Legendary
    }

    public class TextColorAttribute : Attribute
    {
        public Color TextColor;

        public TextColorAttribute(string textColorHex)
        {
            ColorUtility.TryParseHtmlString(textColorHex, out var color);
            TextColor = color;
        }
    }
}