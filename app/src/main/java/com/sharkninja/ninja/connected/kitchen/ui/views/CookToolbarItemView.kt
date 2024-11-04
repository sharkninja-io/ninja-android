package com.sharkninja.ninja.connected.kitchen.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharkninja.grillcore.Grill
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.models.ConnectionStatusToolbar
import com.sharkninja.ninja.connected.kitchen.data.models.ConnectionStatusToolbar.*
import com.sharkninja.ninja.connected.kitchen.databinding.ToolbarCookPrecookBinding
import com.sharkninja.ninja.connected.kitchen.ext.setBGColor
import com.sharkninja.ninja.connected.kitchen.ext.setIconTintColor
import com.sharkninja.ninja.connected.kitchen.ui.adapters.CustomApplianceSelectorAdapter

class CookToolbarItemView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    private var binding: ToolbarCookPrecookBinding

    private var textAndIconColor: Int = R.color.grey3
    private var toolbarBackgroundColor: Int = R.color.theme_background
    private var adapter: CustomApplianceSelectorAdapter? = null
    private var isPreCook: Boolean = true
    private var isEditScreen: Boolean = false

    private var isOpen = false

    var applianceList: MutableList<Grill> = mutableListOf()

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ToolbarCookPrecookBinding.inflate(inflater, this, true)

        attrs?.let { setUpAttrs(it, context) }
        setUpViewLayout()
    }

    private fun setUpAttrs(attrs: AttributeSet, context: Context) {
        context.obtainStyledAttributes(attrs, R.styleable.CookPreCookToolbarItemView).apply {

            getBoolean(R.styleable.CookPreCookToolbarItemView_isPreCook, true).also {
                isPreCook = it
                binding.controlScreenBottomDivider.isVisible = it
                textAndIconColor = if (it) R.color.black1 else R.color.mc_text_and_icon_color
                toolbarBackgroundColor = if (it) R.color.theme_background else R.color.deep_black
            }

            getBoolean(R.styleable.CookPreCookToolbarItemView_isEditScreen, false).also {
                    isEditScreen = it
                    binding.controlScreenToolbarContainer.isVisible = it.not()
                    binding.editScreenToolbarContainer.isVisible = it
            }

        }.recycle()
    }

    private fun setUpViewLayout() {
        if(isEditScreen) {
            binding.editScreenNavIcon.setIconTintColor(context, textAndIconColor)
            binding.editScreenTitle.setTextColor(resources.getColor(textAndIconColor, context.theme))
            binding.editScreenToolbarContainer.setBackgroundColor(ContextCompat.getColor(context, toolbarBackgroundColor))
        } else {
            binding.selectedApplianceName.setTextColor(resources.getColor(textAndIconColor, context.theme))
            binding.iconOpenCloseApplianceList.setIconTintColor(context, textAndIconColor)
        }
        setConnectionStatusColors()
    }

    fun setConnectionStatus(connectionStatus: ConnectionStatusToolbar) {
        when(connectionStatus) {
            WifiOnly -> showWifiOnly()
            BluetoothOnly -> showBluetoothOnly()
            Online -> showAllOnline()
            Offline -> showOffline()
        }
    }

    private fun setConnectionStatusColors() {
        val pillAndIconColor = if(isPreCook) R.color.medium_grey else R.color.error_red
        val currentView = if(isEditScreen) binding.editScreenConnectionStatus else binding.connectionStatusControlScreen
        with(currentView) {
            allOfflineCard.strokeColor = ContextCompat.getColor(context, pillAndIconColor)
            allOfflineTv.setTextColor(ContextCompat.getColor(context, pillAndIconColor))
            allOfflineBtIc.setIconTintColor(context, pillAndIconColor)
            allOfflineWifiIc.setIconTintColor(context, pillAndIconColor)
            ivBluetoothOffline.setIconTintColor(context, pillAndIconColor)
            ivWifiOffline.setIconTintColor(context, pillAndIconColor)
            ivInfoAllOffline.setIconTintColor(context, pillAndIconColor)
        }
    }

    private fun showWifiOnly() {
        if(isPreCook.not()) setBackgroundColor(ContextCompat.getColor(context, toolbarBackgroundColor))
        val currentView = if(isEditScreen) binding.editScreenConnectionStatus else binding.connectionStatusControlScreen
        with(currentView) {
            allOfflineContainer.isVisible = false
            onlineContainer.isVisible = true
            ivWifiOnline.isVisible = true
            ivBtOnline.isVisible = false
            ivBluetoothOffline.isVisible = true
            ivWifiOffline.isVisible= false
        }
    }

    private fun showBluetoothOnly() {
        if(isPreCook.not()) setBackgroundColor(ContextCompat.getColor(context, toolbarBackgroundColor))
        val currentView = if(isEditScreen) binding.editScreenConnectionStatus else binding.connectionStatusControlScreen
        with(currentView) {
            allOfflineContainer.isVisible = false
            onlineContainer.isVisible = true
            ivWifiOnline.isVisible = false
            ivBtOnline.isVisible = true
            ivBluetoothOffline.isVisible = false
            ivWifiOffline.isVisible = true
        }
    }

    private fun showAllOnline() {
        val currentView = if(isEditScreen) binding.editScreenConnectionStatus else binding.connectionStatusControlScreen
        with(currentView) {
            if(isPreCook.not()) setBackgroundColor(ContextCompat.getColor(context, toolbarBackgroundColor))
            allOfflineContainer.isVisible = false
            onlineContainer.isVisible = true
            ivWifiOnline.isVisible = true
            ivBtOnline.isVisible = true
            ivBluetoothOffline.isVisible = false
            ivWifiOffline.isVisible = false
        }
    }

    private fun showOffline() {
        val currentView = if(isEditScreen) binding.editScreenConnectionStatus else binding.connectionStatusControlScreen
        with(currentView) {
            if(isPreCook.not()) {
                setBackgroundColor(ContextCompat.getColor(context, R.color.mc_offline_background))
                allOfflineCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.mc_offline_background))
            }

            allOfflineContainer.isVisible = true
            onlineContainer.isVisible = false
        }
    }
    fun setUpApplianceListRv(list: MutableList<Grill>, onGrillSelected: (Grill) -> Unit) {
        applianceList = list
        val layoutManager = LinearLayoutManager(context)
        binding.rvGrillPicker.layoutManager = layoutManager
        adapter = CustomApplianceSelectorAdapter(list)
        binding.rvGrillPicker.adapter = adapter

        if(isPreCook.not()) {
            setApplianceSelectorDarkModeView()
        }

        //init onclick for each item
        adapter?.setOnItemClickListener(object : CustomApplianceSelectorAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                adapter?.let {
                    val item = it.getItem(position)
                    binding.selectedApplianceName.text = item.getName()
                    onGrillSelected(item)
                    binding.rvGrillPicker.visibility = View.GONE
                    binding.iconOpenCloseApplianceList.setImageResource(R.drawable.ic_close_selector)
                    isOpen = false
                    binding.iconOpenCloseApplianceList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_open_selector))
                }
            }
        })
    }

    fun updateApplianceList(grills: List<Grill>) {
        val grillList = grills.toMutableList()
        adapter?.updateList(grillList)
    }

    fun setEditScreenToolbarTitle(title: String) {
        binding.editScreenTitle.text = title
    }

    fun setNavIconOnClickListener(block: () -> Unit) {
        binding.editScreenNavIcon.setOnClickListener { block() }
    }

    fun setCurrentApplianceName(grill: Grill) {
        binding.selectedApplianceName.text = grill.getName()
    }

    fun setApplianceSelectorOnClickListener(block: (Boolean) -> Unit) {
        binding.applianceSelectionContainer.setOnClickListener {
            if(isOpen) {
                binding.iconOpenCloseApplianceList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_open_selector))
                binding.rvGrillPicker.visibility = View.GONE
            } else {
                binding.iconOpenCloseApplianceList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close_selector))
                binding.rvGrillPicker.visibility = View.VISIBLE
            }

            isOpen = !isOpen
            block(isOpen)
        }
    }

    fun closeApplianceSelector() {
        binding.iconOpenCloseApplianceList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_open_selector))
        binding.rvGrillPicker.visibility = View.GONE

        isOpen = false
    }

    private fun setApplianceSelectorDarkModeView() {
        binding.appliancePickerContainer.setBGColor(context, R.color.darkest_grey)
        binding.rvGrillPicker.setBackgroundColor(ContextCompat.getColor(context, R.color.darkest_grey))
        adapter?.changeColorThemeDarkModeCook(isDefault = false)
    }
}