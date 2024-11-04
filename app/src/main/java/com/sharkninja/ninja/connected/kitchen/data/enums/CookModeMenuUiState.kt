package com.sharkninja.ninja.connected.kitchen.data.enums

import android.content.Context
import com.sharkninja.grillcore.CookMode
import com.sharkninja.ninja.connected.kitchen.R

data class MenuItem(
    val iconColor: IconColorItem,
    val textStyle: MenuItemFont,
)

enum class IconColorItem(val backgroundColorDefault: Int, val tintColorDefault: Int, val backgroundColorDark: Int, val tintColorDark: Int) {
    SELECTED(
        backgroundColorDefault = R.color.ninja_green,
        tintColorDefault = R.color.menu_icon_selected,
        backgroundColorDark = R.color.ninja_green,
        tintColorDark = R.color.menu_icon_selected
    ),
    UNSELECTED(
        backgroundColorDefault = R.color.menu_icon_selected,
        tintColorDefault = R.color.darkest_grey,
        backgroundColorDark = R.color.darkest_grey,
        tintColorDark = R.color.mc_text_and_icon_color
    )
}


data class AccessoryItem(
    val title: Int,
    val description: Int,
    val allAccessories: Boolean
)

enum class MenuItemFont(val fontStyle: Int, val colorDefault: Int, val colorDark: Int) {
    SELECTED(
        fontStyle = R.font.gotham_book_medium,
        colorDefault = R.color.black1,
        colorDark = R.color.mc_text_and_icon_color
    ),
    UNSELECTED(
        fontStyle = R.font.gotham_book_normal,
        colorDefault = R.color.menu_unselected_icon,
        colorDark = R.color.mc_text_and_icon_color
    )
}

enum class CookModeMenuUIState(val grill: MenuItem, val smoke: MenuItem, val aircrisp: MenuItem, val roast: MenuItem
                               , val bake: MenuItem, val broil: MenuItem, val dehydrate: MenuItem, val reheat: MenuItem, val accessoryItem: AccessoryItem) {

    Grill(
        grill = MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        smoke = MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        aircrisp = MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        roast = MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        bake = MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        reheat = MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        broil = MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        dehydrate = MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        accessoryItem = AccessoryItem(R.string.grill_accessory_dialog_title, R.string.grill_accessory_dialog_description, false)
    ),
    Smoke(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),

        AccessoryItem(R.string.smoke_accessory_dialog_title,
        R.string.grill_accessory_dialog_description, false)
    ),
    AirCrisp(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        AccessoryItem(R.string.air_crisp_accessory_dialog_title, R.string.aircrisp_accessory_dialog_description, true)
    ),
    Roast(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        AccessoryItem(R.string.roast_accessory_dialog_title, R.string.aircrisp_accessory_dialog_description, true)
    ),
    Bake(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        AccessoryItem(R.string.bake_accessory_dialog_title, R.string.grill_accessory_dialog_description, false)
    ),
    Broil(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        AccessoryItem(R.string.broil_accessory_dialog_title, R.string.grill_accessory_dialog_description, false)
    ),
    Dehydrate(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        AccessoryItem(R.string.dehydrate_accessory_dialog_title, R.string.aircrisp_accessory_dialog_description, true)
    ),
    Reheat(
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.UNSELECTED, MenuItemFont.UNSELECTED),
        MenuItem(IconColorItem.SELECTED, MenuItemFont.SELECTED),
        AccessoryItem(R.string.reheat_accessory_dialog_title, R.string.grill_accessory_dialog_description, false)
    )
}

enum class IconButtonState(val backgroundColor: Int, val tintColor: Int, val textColor: Int, val isEnabled: Boolean) {
    LightEnabled(
        IconColorItem.UNSELECTED.backgroundColorDefault,
        IconColorItem.UNSELECTED.tintColorDefault,
        MenuItemFont.UNSELECTED.colorDefault,
        true
    ),
    LightDisabled(
        R.color.icon_button_disable_light_background,
        IconColorItem.SELECTED.tintColorDefault,
        MenuItemFont.UNSELECTED.colorDefault,
        false
    ),
    DarkDisabled(
        R.color.icon_button_disable_dark_background,
        R.color.disable_dark_icon_tint_color,
        MenuItemFont.UNSELECTED.colorDark,
        false
    )
}

fun CookMode.getToolbarTitle(context: Context): String {
    return if(this == CookMode.AirCrisp) context.getString(R.string.air_crisp_settings) else "${this.name} " + context.getString(R.string.settings)
}

fun CookMode.getMenuItem(): CookModeMenuUIState {
    return when(this) {
        CookMode.Grill -> CookModeMenuUIState.Grill
        CookMode.Smoke -> CookModeMenuUIState.Smoke
        CookMode.AirCrisp -> CookModeMenuUIState.AirCrisp
        CookMode.Roast -> CookModeMenuUIState.Roast
        CookMode.Bake -> CookModeMenuUIState.Bake
        CookMode.Dehydrate -> CookModeMenuUIState.Dehydrate
        CookMode.NotSet -> CookModeMenuUIState.Grill
        CookMode.Broil -> CookModeMenuUIState.Broil
        CookMode.Reheat -> CookModeMenuUIState.Reheat
    }
}

fun String.getCookModeFromCookMethod(): CookMode
{
    return when(this.lowercase()) {
        "grill" -> CookMode.Grill
        "smoke" -> CookMode.Smoke
        "broil" -> CookMode.Broil
        "bake" -> CookMode.Bake
        "dehydrate" -> CookMode.Dehydrate
        "aircrisp",
        "air crisp" -> CookMode.AirCrisp
        "roast" -> CookMode.Roast
        else -> CookMode.Grill
    }
}



sealed class CookDashboardInfoAction {
    object ShowAccessoryModal: CookDashboardInfoAction()
    object ActionProcessed: CookDashboardInfoAction()
    object ShowThermometerUnPlugged: CookDashboardInfoAction()
    object StartCook: CookDashboardInfoAction()
    object StartCookFromChart: CookDashboardInfoAction()
    object ShowChartsAccessoryModal: CookDashboardInfoAction()
    object StartCookError: CookDashboardInfoAction()
    object StopCook: CookDashboardInfoAction()

    object StopCookError: CookDashboardInfoAction()
    object ShowThermometer1NotPluggedIn: CookDashboardInfoAction()
    object ShowThermometer2NotPluggedIn: CookDashboardInfoAction()
    object ShowProbeOneDoneModal: CookDashboardInfoAction()
    object ShowProbeTwoDoneModal: CookDashboardInfoAction()

    object SwitchCookTypeOpenProbe1: CookDashboardInfoAction()
    object SwitchCookTypeOpenProbe2: CookDashboardInfoAction()
    object SwitchCookTypeOpenEditTimeTemp: CookDashboardInfoAction()

    object EditWoodFireSettingSuccess: CookDashboardInfoAction()
    object EditWoodFireSettingError: CookDashboardInfoAction()
}

sealed class SurveyAction {
    object ShowAppRatingSurvey: SurveyAction()
    object AppRatingPositiveAction: SurveyAction()
    object AppRatingNegativeAction: SurveyAction()
    object SurveyActionProcessed: SurveyAction()
}