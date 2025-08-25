using UnityEngine;
using UnityEngine.UI;

namespace Inventory
{
    public class TradingManager : MonoBehaviour
    {
        //[SerializeField] private NPC _npc; // direct reference? for stuff like rep, inventory, etc.
        //[SerializeField] private EconomyModule economyRates; // or whatever will be used for checking item value rates etc.
        [SerializeField]
        private Inventory
            _playerInventory, _traderInventory; //PlayerInventory ? they are supposed to be loaded / reused

        [SerializeField]
        private Inventory
            _sellInventory,
            _buyInventory; //These are supposed to be instanciated / refreshed each time trading is opened (onAwake?)

        [SerializeField] private GridInventoryUI gridInventoryUI;
        [SerializeField] private GridInventoryUI _traderInventoryUI, _sellInventoryUI, _buyInventoryUI;
        [SerializeField] private Button _tradeButton; //offer button and a separate for buy now?
        [SerializeField] private Slider _playerMoneySlider, _traderMoneySlider;
        [SerializeField] private Transform _tradingPanel, _reputationBar;
        [SerializeField] private Text _NPCinfo;
        [SerializeField] private Text _playerMoneyText, _traderMoneyText;

        [SerializeField] private Text _totalSellValueText, _totalBuyValueText;
        // money where?
        
        private Inventory _snapshotPlayerInventory, _snapshotTraderInventory;
    }
}