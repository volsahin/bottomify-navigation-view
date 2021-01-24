package com.volcaniccoder.bottomify

import android.view.Menu
import androidx.annotation.ColorRes

class BottomifyParams {
    var menu: Menu? = null
    @ColorRes var activeColor: Int = R.color.bottomifyActiveColor
    @ColorRes var passiveColor: Int = R.color.bottomifyPassiveColor
    @ColorRes var pressedColor: Int = R.color.bottomifyPressedColor
    var itemPadding: Float = 16f
    var itemTextSize : Float = 40f
    var animationDuration: Int = 300
    var endScale: Float = 0.95f
    var startScale: Float = 1f
}