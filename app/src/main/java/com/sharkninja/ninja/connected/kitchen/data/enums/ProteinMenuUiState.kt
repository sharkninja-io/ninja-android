package com.sharkninja.ninja.connected.kitchen.data.enums

import com.sharkninja.grillcore.Food
import com.sharkninja.ninja.connected.kitchen.R


enum class FoodMenuUiState(val manual: MenuItem, val poultry: MenuItem, val beef: MenuItem, val pork: MenuItem, val fish: MenuItem, val lamb: MenuItem) {
    Poultry(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
       MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED)
    ),
    Beef(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED)
    ),
    Pork(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED)
    ),
    Fish(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED)
    ),
    Lamb(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED)
    ),
    NotSet(
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED)
    ),
    Manual(
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED)
    )
}

fun Food.getMenuItem() : FoodMenuUiState {
    return when(this) {
        Food.Chicken -> FoodMenuUiState.Poultry
        Food.Beef -> FoodMenuUiState.Beef
        Food.Pork -> FoodMenuUiState.Pork
        Food.Fish -> FoodMenuUiState.Fish
        Food.Lamb -> FoodMenuUiState.Lamb
        Food.Manual -> FoodMenuUiState.Manual
        Food.NotSet -> FoodMenuUiState.NotSet
    }
}

fun Food.getCookDashBoardCardDisplayName() : Int {
    return when(this) {
        Food.Chicken -> R.string.chicken
        Food.Beef -> R.string.beef
        Food.Pork -> R.string.pork
        Food.Fish -> R.string.fish
        Food.Lamb -> R.string.lamb
        Food.Manual -> R.string.manual
        Food.NotSet -> R.string.double_dash
    }
}
