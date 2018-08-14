package com.volcaniccoder.bottomify

import android.support.annotation.ColorRes
import android.view.Menu

class BottomifyParams {
    var menu: Menu? = null
    @ColorRes var activeColor: Int = R.color.bottomifyActiveTextColor
    @ColorRes var passiveColor: Int = R.color.bottomifyPassiveTextColor
    @ColorRes var pressedColor: Int = R.color.bottomifyPressedTextColor
    var itemPadding: Float = 16f
    var itemTextSize : Float = 40f
    var animationDuration: Int = 300
    var endScale: Float = 0.95f
    var startScale: Float = 1f
}