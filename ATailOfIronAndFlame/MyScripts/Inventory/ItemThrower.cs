using System;
using Outdoor.drops;
using Player;
using UnityEngine;
using UnityEngine.EventSystems;

namespace Inventory
{
    public class ItemThrower : MonoBehaviour, IDropHandler
    {
        public DropBase dropItem;
        public Transform playerPos;

        private void Start()
        {
            playerPos = GameObject.FindWithTag("Player").GetComponent<Transform>();
        }

        public void OnDrop(PointerEventData eventData)
        {
            var draggedItem = DragAndDrop.CurrentlyDraggedObject?.GetComponent<SlotUI>();
            if (draggedItem == null || draggedItem is RuneWordSlotUI) return;
            
            var slot = SlotRepository.GetPresenterForSlotUI(draggedItem);
            var item = slot.Item;

            for (int i = 0; i < slot.CurrentStack; i++)
            {
                var drop = Instantiate(dropItem);
                drop.transform.position = playerPos.position + new Vector3(0.5f, 0.5f);
                drop.GetComponent<CircleCollider2D>().enabled = false;
                drop.GetComponent<SpriteRenderer>().sprite = item.ItemSprite;
                drop.RealItem = item;
                drop.item = item.GetData();
            }
            
            slot.SetItem(null);
        }
    }
}