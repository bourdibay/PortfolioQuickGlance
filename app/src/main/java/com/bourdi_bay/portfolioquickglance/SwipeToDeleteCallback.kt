package com.bourdi_bay.portfolioquickglance

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeToDeleteCallback(context: Context, private val swipeDeleteDelegate: (Int) -> Unit) :
    ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

    private val icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete)!!
    private val background = ColorDrawable(Color.RED)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        swipeDeleteDelegate(viewHolder.adapterPosition)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20 //so background is behind the rounded corners of itemView

        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        when {
            dX > 0 -> { // Swiping to the right
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
                )
            }
            dX < 0 -> { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            }
            else -> // view is unSwiped
                background.setBounds(0, 0, 0, 0)
        }

        background.draw(c)
        icon.draw(c)
    }
}