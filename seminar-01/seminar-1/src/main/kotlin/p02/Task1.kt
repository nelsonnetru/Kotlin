package p02

class Holder {
    var number: Int = 0
        set(value) {
            listener?.onNewValue(value)
            field = value
        }

    var listener: ValueChangeListener? = null

    companion object {
        var DEFAULT_NUMBER = 0

        fun createHolder(_DEFAULT_NUMBER: Int) : Holder {
            this.DEFAULT_NUMBER = _DEFAULT_NUMBER
            return Holder()
        }
    }


}

interface ValueChangeListener {
    fun onNewValue(number: Int)
}

fun main() {
    val holder = Holder.createHolder(Holder.DEFAULT_NUMBER)
    holder.number = 9
        holder.listener = object : ValueChangeListener {
            override fun onNewValue(number: Int) {
                println("New value is $number")
            }
        }
    holder.number = 1
}
