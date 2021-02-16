package com.example.todolist

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), ItemButtonsClickListener {

    private var nextNewItemPosition: Int = 0

    private lateinit var textViewAddTaskTop: TextView

    private lateinit var textViewAddTaskBottom: TextView

    private lateinit var recyclerView: RecyclerView

    private lateinit var itemAdapter: ItemAdapter

    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var editTextCreateNewIem: EditText

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create_new_item -> {
                this.nextNewItemPosition = this.itemAdapter.items.size
                showAddNewItemDialogBox()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        this.textViewAddTaskTop = findViewById(R.id.textView_add_task_top)
        this.textViewAddTaskBottom = findViewById(R.id.textView_add_task_bottom)
        this.editTextCreateNewIem = EditText(this)
        this.recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        var items: MutableList<Item> = ItemDatabaseHelper(applicationContext).getAllItems()
        items.sortBy { it.position }
        this.itemAdapter = ItemAdapter(items, this)
        itemAdapter.notifyDataSetChanged()
        this.linearLayoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        this.recyclerView!!.setLayoutManager(linearLayoutManager)
        this.recyclerView!!.setAdapter(itemAdapter)
        registerForContextMenu(recyclerView)
        val callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
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
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX != 0f) {
                        val iconTrashCan = ContextCompat.getDrawable(applicationContext,R.drawable.ic_action_delete)
                        val itemView = viewHolder.itemView
                        val height = itemView.bottom - itemView.top
                        val width = height / 3
                        val colorDrawableBackground = ColorDrawable(ContextCompat.getColor(applicationContext, R.color.colorRed))
                        if (dX < 0) { // from right to left
                            colorDrawableBackground.setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
                            val iconTrashCanTop = itemView.top + (height - iconTrashCan!!.intrinsicHeight) / 2
                            val iconTrashCanMargin = (height - iconTrashCan!!.intrinsicHeight) / 2
                            val iconTrashCanLeft = itemView.right - iconTrashCanMargin - iconTrashCan!!.intrinsicWidth
                            val iconTrashCanRight = itemView.right - iconTrashCanMargin
                            val iconTrashCanBottom = iconTrashCanTop + iconTrashCan!!.intrinsicHeight
                            iconTrashCan.setBounds(iconTrashCanLeft, iconTrashCanTop, iconTrashCanRight, iconTrashCanBottom)
                            colorDrawableBackground.draw(c)
                            iconTrashCan.draw(c)
                        }
                        if (dX > 0) { // from left to right
                            colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                            val iconTrashCanTop = itemView.top + (height - iconTrashCan!!.intrinsicHeight) / 2
                            val iconTrashCanMargin = (height - iconTrashCan!!.intrinsicHeight) / 2
                            val iconTrashCanLeft = itemView.left + iconTrashCanMargin * 2 - iconTrashCan!!.intrinsicWidth
                            val iconTrashCanRight = itemView.left + iconTrashCanMargin * 2
                            val iconTrashCanBottom = iconTrashCanTop + iconTrashCan!!.intrinsicHeight
                            iconTrashCan.setBounds(iconTrashCanLeft, iconTrashCanTop, iconTrashCanRight, iconTrashCanBottom)
                            colorDrawableBackground.draw(c)
                            iconTrashCan.draw(c)
                        }
                    }
                }
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    if (dY != 0f) {
                        recyclerView.setBackgroundColor(resources.getColor(R.color.colorLightGrey))
                    }
                }

            }

            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromIndex = viewHolder.getAdapterPosition()
                val toIndex = target.getAdapterPosition()
                if (fromIndex < toIndex) {
                    for (i in fromIndex until toIndex) {
                        moveItemDown(i)
                    }
                } else {
                    for (i in fromIndex downTo toIndex + 1) {
                        moveItemUp(i)
                    }
                }
                updateTaskList()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.getAdapterPosition()
                deleteItem(position)
            }
        }
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)

        textViewAddTaskTop.setOnClickListener(View.OnClickListener {
            nextNewItemPosition = 0
            showAddNewItemDialogBox()
        })
        textViewAddTaskBottom.setOnClickListener(View.OnClickListener {
            nextNewItemPosition = itemAdapter.items.size
            showAddNewItemDialogBox()
        })
    }

    private fun showAddNewItemDialogBox() {
           var alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
           alertDialogBuilder.setTitle(resources.getString(R.string.content_desc_new_item))
           alertDialogBuilder.setMessage(resources.getString(R.string.action_new_item))
           alertDialogBuilder.setCancelable(false)
           alertDialogBuilder.setView(editTextCreateNewIem)
           alertDialogBuilder.setPositiveButton(
               resources.getString(R.string.action_create),
               DialogInterface.OnClickListener { dialog, whichButton ->
                   val itemContent = editTextCreateNewIem!!.getText().toString()
                   val newItem = Item(itemContent, this.nextNewItemPosition, false)
                   ItemDatabaseHelper(applicationContext).insertItem(newItem)
                   this.itemAdapter.items.add(this.nextNewItemPosition, newItem)
                   updateTaskList()
                   dialog.dismiss()
               })
           alertDialogBuilder.setNegativeButton(
               resources.getString(R.string.action_cancel),
               DialogInterface.OnClickListener { dialog, whichButton -> dialog.cancel() })
            val group = LinearLayout(this)
            group.orientation = LinearLayout.VERTICAL
            editTextCreateNewIem = EditText(this)
            group.addView(editTextCreateNewIem)
            alertDialogBuilder.setView(group)
            val alertDialog = alertDialogBuilder.create()
               alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            alertDialog.show()
    }

    override fun onMoveUpClicked(currentPosition: Int) {
        moveItemUp(currentPosition)
    }

    override fun onMoveDownclicked(currentPosition: Int) {
        moveItemDown(currentPosition)
    }

    fun moveItemDown(currentPosition: Int) {
        if (currentPosition < this.itemAdapter.items.size - 1) {
            Collections.swap(this.itemAdapter.items, currentPosition, currentPosition + 1)
        }
        updateTaskList()
    }

    fun moveItemUp(currentPosition: Int) {
        if (currentPosition > 0) {
            Collections.swap(this.itemAdapter.items, currentPosition, currentPosition - 1)
        }
        updateTaskList()
    }

    override fun onDeleteClicked(currentPosition: Int) {
        deleteItem(currentPosition)
    }

    override fun onItemSetCompleteClicker(currentPosition: Int, isComplete: Boolean) {
        this.itemAdapter.items[currentPosition].isComplete = isComplete
        updateTaskList()
    }

    fun deleteItem(currentPosition: Int) {
        Toast.makeText(
            applicationContext,
            getString(R.string.item_deleted),
            Toast.LENGTH_SHORT
        )
            .show()
        var itemToRemove = this.itemAdapter.items.get(currentPosition)
        ItemDatabaseHelper(applicationContext).deleteItem(itemToRemove)
        this.itemAdapter.items.remove(itemToRemove)
        updateTaskList()
    }

    fun updateTaskList() {
        for (i in this.itemAdapter.items.indices) {
            this.itemAdapter.items[i].position = i
            ItemDatabaseHelper(applicationContext).updateItem(this.itemAdapter.items[i])
        }
        this.itemAdapter.items.sortBy { it.position }
        this.itemAdapter.notifyDataSetChanged()
    }

}
