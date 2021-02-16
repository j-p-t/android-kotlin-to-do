package com.example.todolist

import android.graphics.Color
import android.graphics.Paint
import android.view.ContextMenu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemViewHolder( var view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

    var textViewItemContent: TextView

    var imageViewMoveItemUp: ImageView

    var imageViewMoveItemDown: ImageView

    init {
        view.setOnCreateContextMenuListener(this)
        imageViewMoveItemUp = view.findViewById(R.id.imageView_ic_move_item_up) as ImageView
        imageViewMoveItemDown = view.findViewById(R.id.imageView_ic_move_item_down) as ImageView
        textViewItemContent = view.findViewById(R.id.textView_item_content) as TextView
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo
    ) {
        menu.add(0, v.id, 0, R.string.action_delete_item)
    }

    fun bind(item: Item, itemButtonsClickListener: ItemButtonsClickListener) {
        this.textViewItemContent.setText(item.content)
        if (item.isComplete) {
            this.textViewItemContent.setText(item.content)
            this.textViewItemContent.setTextColor(Color.LTGRAY)
            this.textViewItemContent.setPaintFlags(this.textViewItemContent.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        } else {
            this.textViewItemContent.setTextColor(Color.BLACK)
            this.textViewItemContent.setPaintFlags(this.textViewItemContent.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
        }

        this.imageViewMoveItemUp.setOnClickListener(View.OnClickListener {itemButtonsClickListener.onMoveUpClicked(item.position)})

        this.imageViewMoveItemDown.setOnClickListener(View.OnClickListener {itemButtonsClickListener.onMoveDownclicked(item.position)})

        this.textViewItemContent.setOnClickListener(View.OnClickListener {
            item.isComplete = !item.isComplete
            if (item.isComplete) {
                this.textViewItemContent.setTextColor(Color.LTGRAY)
                this.textViewItemContent.setPaintFlags(this.textViewItemContent.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                this.textViewItemContent.setTextColor(Color.BLACK)
                this.textViewItemContent.setPaintFlags(this.textViewItemContent.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
            }
            itemButtonsClickListener.onItemSetCompleteClicker(item.position, item.isComplete)
        })
    }

}
