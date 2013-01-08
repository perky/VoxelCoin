# VoxelCoin

VoxelCoin is an 'escrow' like market/economy for Minecraft, in the style of the eve online market.
VoxelCoin aims to have individual market entities and trading will take place via a GUI.
For now there are just chat commands that access a single global market.

## Requirements.

* Ormlite.
* Xerial SQLite driver.

## Commands

* `/sell [priceEach]` place a sell order for currently held stack and set the price for each item. The stack will be taken from you and placed on the market.
* `/buy [sellOrderID] [quantity]` immediately buy from a sell order with the given id.
* `/buy [itemID] [itemDamageValue] [quantity] [priceEach]` place a buy order. The total cost of the order (quantity * priceEach) will be taken from your account and placed in escrow.
* `/orders [buy|sell]` show all buy orders or sell orders.
* `/myorders [buy|sell]` show only your buy/sell orders.
* `/balance` show your current balance.
* `/transactionlog [buy|sell] (playerName)` show buy or sell transactions a player has made.
* `/cancelorder [buy|sell] [orderID|ALL]` cancels the order by type and id, or use "ALL" to delete all of that type.
