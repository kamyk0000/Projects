namespace Inventory
{
    public class RuneWordSlotUI : SlotUI
    {
        public override void UpdateUI(Item item = null, int? currentStack = -1)
        {
            if (!string.IsNullOrEmpty(item?.DebugName))
            {
                _stackCounter.text = item.Name;
                _stackCounter.enabled = true;
            }
            else
            {
                _stackCounter.text = "";
                _stackCounter.enabled = false;
            }
        }
    }
}