package com.example.todolist

interface ItemButtonsClickListener {

    fun onMoveUpClicked(position: Int)
    fun onMoveDownclicked(position: Int)
    fun onDeleteClicked(position: Int)
    fun onItemSetCompleteClicker(position: Int, isComplete: Boolean)

}