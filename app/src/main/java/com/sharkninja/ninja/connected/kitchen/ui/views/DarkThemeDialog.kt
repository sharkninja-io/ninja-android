package com.sharkninja.ninja.connected.kitchen.ui.views

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.DialogDarkThemeBinding
import com.sharkninja.ninja.connected.kitchen.ext.toPx

class DarkThemeDialog (
    private val configuration: Configuration.() -> Unit
) : DialogFragment() {
    private lateinit var binding: DialogDarkThemeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDarkThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 24.toPx())
        dialog?.window?.setBackgroundDrawable(inset)
    }

    private fun initBindings() {
        with(binding) {
            val myConfiguration = Configuration()
            configuration(myConfiguration)

            dialogTitle.text = myConfiguration.title
            myConfiguration.topButtonColor.let { binding.topButton.setBackgroundColor(ContextCompat.getColor(requireContext(), myConfiguration.topButtonColor)) }

            with(myConfiguration.topButton) {
                topButton.text = text

                topButton.setOnClickListener {
                    action.invoke()
                    dismiss()
                }
            }

            myConfiguration.bottomButton?.let { bottomButtonConfig ->
                binding.bottomButton.visibility = View.VISIBLE
                binding.bottomButton.text = bottomButtonConfig.text

                binding.bottomButton.setOnClickListener {
                    bottomButtonConfig.action.invoke()
                    dismiss()
                }
            } ?: run { binding.bottomButton.visibility = View.GONE }

            myConfiguration.description?.let { description ->
                binding.dialogDescription.visibility = View.VISIBLE
                binding.dialogDescription.text = description
            } ?: run { binding.dialogDescription.visibility = View.GONE }
        }
    }

    class Configuration {
        var title: CharSequence = ""
        var topButton: ButtonConfiguration = ButtonConfiguration("")
        var bottomButton: ButtonConfiguration? = null
        var description: CharSequence? = null
        var topButtonColor: Int = R.color.ninja_green
    }

    data class ButtonConfiguration(
        val text: String,
        var action: (() -> Unit) = {}
    )
}