package com.olaraabius.uppgift1.thirty.model

import com.olaraabius.uppgift1.thirty.R

/**
 * Created by: Ola Råbius Magnusson
 * Date: 2023-06-20
 *
 * A representation of a 6-sided dice
 * With the possibility to:
 * Roll the dice
 * Choose the dice to be saved, which prevents it from being re-rolled
 * It returns an image depending on the value and if saved
 */
class Dice(var value: Int = 0) {
    var isSaved = false

    init {
        roll()
    }

    /**
     * Roll the dice
     * Only if not saved
     */
    fun roll() {
        if (!isSaved)
            value = (1..6).random()
    }

    /**
     * Return the dice image depending of value and state
     */
    fun getImage(): Int {
        when (value) {
            1 -> return if (isSaved) R.drawable.red1 else R.drawable.white1
            2 -> return if (isSaved) R.drawable.red2 else R.drawable.white2
            3 -> return if (isSaved) R.drawable.red3 else R.drawable.white3
            4 -> return if (isSaved) R.drawable.red4 else R.drawable.white4
            5 -> return if (isSaved) R.drawable.red5 else R.drawable.white5
            6 -> return if (isSaved) R.drawable.red6 else R.drawable.white6
        }
        return -1
    }
}