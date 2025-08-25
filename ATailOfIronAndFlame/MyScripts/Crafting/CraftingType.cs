using System;
using UnityEngine;

namespace Crafting
{
    public enum CraftingType
    {
        [Sprite("all")] None,
        [Sprite("smelting")] Smelting,
        [Sprite("moulding")] Moulding,
        [Sprite("engraving")] Engraving,
        [Sprite("bowmaking")] Bowmaking
    }

    public class SpriteAttribute : Attribute
    {
        public readonly Sprite Sprite;

        public SpriteAttribute(string spriteName)
        {
            Sprite = Resources.Load<Sprite>("Sprites/UI/" + spriteName);
        }
    }
}