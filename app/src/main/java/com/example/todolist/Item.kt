package com.example.todolist

class Item(content: String, position: Int, isComplete: Boolean) {

    var id: Int = -1
    var content: String = content
    var position: Int = position
    var isComplete: Boolean = isComplete

    constructor(content: String, position: Int, isComplete: Boolean, id: Int): this(content, position, isComplete) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (id != other.id) return false

        return true
    }

    override fun toString(): String {
        return "Item(id=$id, content='$content', position=$position, isComplete=$isComplete)"
    }

}
