package com.volcaniccoder.bottomify

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView

class BottomifyNavigationView @JvmOverloads constructor(context: Context,
                                                        attrs: AttributeSet? = null,
                                                        defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    /**
     * Bottomify params knows about how the item will render
     */
    private val params = BottomifyParams()

    /**
     * The item that is active and chosen
     */
    private val navigationItems = arrayListOf<NavigationItem>()

    /**
     * The item position that is active and chosen
     */
    private var selectedPosition: Int = 0

    /**
     * For the value of view's getDrawingRect
     */
    private val outRect = Rect()

    /**
     * For the value of view's getLocationOnScreen
     */
    private val location = IntArray(2)

    /**
     * If an item touched and released outside this flag will be false to prevent double animation
     */
    private var needsToScale = true

    /**
     * The listener that triggered when active navigation item changed
     */
    private var itemChangeListener: OnNavigationItemChangeListener? = null

    /**
     * A data class for navigation item
     */
    data class NavigationItem(val position: Int, val view: View, val textView: TextView, val imageView: ImageView)

    /**
     * Initialize Bottomify by getting the values from xml
     */
    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BottomifyNavigationView)

        if (a.hasValue(R.styleable.BottomifyNavigationView_menu)) {
            val popupMenu = PopupMenu(context, null)
            val menu = popupMenu.menu
            MenuInflater(context).inflate(a.getResourceId(R.styleable.BottomifyNavigationView_menu, 0), menu)
            params.menu = menu
        }

        if (a.hasValue(R.styleable.BottomifyNavigationView_active_color)) {
            val activeColor = a.getColor(R.styleable.BottomifyNavigationView_active_color, 0)
            params.activeColor = activeColor
        }

        if (a.hasValue(R.styleable.BottomifyNavigationView_passive_color)) {
            val passiveColor = a.getColor(R.styleable.BottomifyNavigationView_passive_color, 0)
            params.passiveColor = passiveColor
        }

        if (a.hasValue(R.styleable.BottomifyNavigationView_pressed_color)) {
            val pressedColor = a.getColor(R.styleable.BottomifyNavigationView_pressed_color, 0)
            params.pressedColor = pressedColor
        }

        if (a.hasValue(R.styleable.BottomifyNavigationView_item_padding)) {
            val px = a.getDimension(R.styleable.BottomifyNavigationView_item_padding, 0f)
            params.itemPadding = px
        }

        if (a.hasValue(R.styleable.BottomifyNavigationView_item_text_size)) {
            val px = a.getDimensionPixelSize(R.styleable.BottomifyNavigationView_item_text_size, 0)
            params.itemTextSize = px.toFloat()
        }

        if (a.hasValue(R.styleable.BottomifyNavigationView_animation_duration)) {
            val animDuration = a.getInteger(R.styleable.BottomifyNavigationView_animation_duration, 0)
            params.animationDuration = animDuration
        }

        if (a.hasValue(R.styleable.BottomifyNavigationView_scale_percent)) {
            val scalePercent = a.getInteger(R.styleable.BottomifyNavigationView_scale_percent, 0)
            params.endScale = 1 - scalePercent.toFloat() / 100
        }

        a.recycle()
        prepareView()

    }

    /**
     * For setting OnNavigationItemChangeListener
     */
    public fun setOnNavigationItemChangedListener(listener: OnNavigationItemChangeListener) {
        this.itemChangeListener = listener
    }

    /**
     * Inflates NavigationItem view and fills it with attrs params
     */
    private fun prepareView() {
        val menu = if (params.menu == null) {
            throw NoSuchFieldException("Bottomify: You need to declare app:menu in xml")
        } else {
            params.menu!!
        }

        for (index in 0 until menu.size()) {

            // Prepare navigation item to display and get items view references
            val navigationItem = menu.getItem(index)
            val navigationItemView = LayoutInflater.from(context).inflate(R.layout.item_bottomify, this, false)
            val imageView = navigationItemView.findViewById<ImageView>(R.id.navigation_item_image)
            val textView = navigationItemView.findViewById<TextView>(R.id.navigation_item_text)

            // Set navigation item's icon and title
            imageView.setImageDrawable(navigationItem.icon)
            textView.text = navigationItem.title

            // Set navigation item's color. If it's pressed then color it with active color
            var navigationItemColor: Int
            if (navigationItem.isChecked) {
                navigationItemColor = params.activeColor
                selectedPosition = index
            } else {
                navigationItemColor = params.passiveColor
            }
            textView.setTextColor(navigationItemColor)
            imageView.setColorFilter(navigationItemColor)

            // Set text and image view size
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, params.itemTextSize)

            // Set navigation item's padding
            val padding = params.itemPadding.toInt()
            navigationItemView.setPadding(padding, padding, padding, padding)

            // Add navigation item to list
            val item = NavigationItem(index, navigationItemView, textView, imageView)
            navigationItems.add(item)

            // Set a touch listener to navigation item
            addListener(item)

            // Add navigation item to view.
            val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.weight = 1f
            params.gravity = Gravity.CENTER
            this.addView(navigationItemView, params)

        }
    }

    /**
     * Listener to handle click and release events for navigation item
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun addListener(navigationItem: NavigationItem) {
        navigationItem.view.setOnTouchListener { _, event ->

            // If user touched to item and then moved finger to outside of the item, make view how it looks was before touching
            // This method will fire lots of times. So if you scale the view to how it looks was before once, then no need to scale anymore.
            if (isTouchingOutsideOfItem(navigationItem.view, event.rawX.toInt(), event.rawY.toInt()) && needsToScale) {
                scaleNavigationItem(navigationItem, params.endScale, params.startScale)
                colorAnim(navigationItem, false)
                needsToScale = false
                Log.w("POS", "OUT")
            }
            // If navigation item is pressed and touched position is not outside of item view make animations
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (needsToScale) {
                    scaleNavigationItem(navigationItem, params.startScale, params.endScale)
                    colorAnim(navigationItem, true)
                    Log.w("POS", "DOWN")
                }
                needsToScale = true

            } // If navigation item is released and touched position is not outside of item view make animations
            else if (event.action == MotionEvent.ACTION_UP) {
                if (needsToScale) {
                    scaleNavigationItem(navigationItem, params.endScale, params.startScale)
                    selectItem(navigationItem, true)
                    colorAnim(navigationItem, false)
                    Log.w("POS", "UP")
                }
                needsToScale = true

            }
            true
        }

    }

    /**
     * Selects the given navigation item and updates current navigation item
     */
    private fun selectItem(navigationItem: NavigationItem, withClickSound: Boolean) {

        // Get previous selected item and color it passive
        val selectedNavigationItem = navigationItems[selectedPosition]
        selectedNavigationItem.textView.setTextColor(params.passiveColor)
        selectedNavigationItem.imageView.setColorFilter(params.passiveColor)

        // Play click sound
        if (withClickSound) navigationItem.view.playSoundEffect(android.view.SoundEffectConstants.CLICK)

        // Update selected position
        selectedPosition = navigationItem.position

        // Notify active view changed
        itemChangeListener?.onNavigationItemChanged(navigationItem)

    }

    /**
     * If active navigation item need to change programmatically, this method will do it
     */
    fun setActiveNavigationIndex(index: Int) {
        selectItem(navigationItems[index], false)
    }

    /**
     * Determines if touch event exceed the last pressed navigation item
     */
    private fun isTouchingOutsideOfItem(view: View, x: Int, y: Int): Boolean {
        view.getDrawingRect(outRect)
        view.getLocationOnScreen(location)
        outRect.offset(location[0], location[1])
        return !outRect.contains(x, y)
    }

    /**
     * The click and release scale animation
     */
    private fun scaleNavigationItem(navigationItem: NavigationItem, startScale: Float, endScale: Float) {

        val animation = ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_PARENT, 0.5f)
        animation.fillAfter = true
        animation.duration = params.animationDuration.toLong()
        navigationItem.view.startAnimation(animation)
    }

    /**
     * The click and release color animation
     */
    private fun colorAnim(navigationItem: NavigationItem, activeToPassive: Boolean) {

        // If view is selected then color it with active color, if it is not, color view it with passive color
        val colorActive = if (navigationItem.view == navigationItems[selectedPosition].view) params.activeColor else params.passiveColor
        val colorPassive = params.pressedColor

        // If the transition between activeToPassive or passiveToActive
        val colorFrom = if (activeToPassive) colorActive else colorPassive
        var colorTo = if (activeToPassive) colorPassive else colorActive

        // If view is selected one and touched finger released from selected navigation item, color it active
        if (navigationItem.view == navigationItems[selectedPosition].view && !activeToPassive) {
            colorTo = params.activeColor
        }

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = params.animationDuration.toLong() // milliseconds
        colorAnimation.addUpdateListener { animator ->

            val navigationItemColor = animator.animatedValue as Int
            navigationItem.textView.setTextColor(navigationItemColor)
            navigationItem.imageView.setColorFilter(navigationItemColor)
        }

        colorAnimation.start()
    }

}