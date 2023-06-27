package com.olaraabius.uppgift1.thirty.controller

import com.olaraabius.uppgift1.thirty.model.Dice
import com.olaraabius.uppgift1.thirty.model.PointsCalculation
import com.olaraabius.uppgift1.thirty.model.PointsCategory
import com.olaraabius.uppgift1.thirty.model.Points

/**
 * A controller class for the dice game Thirty.
 * Provides methods to a UI that gives the possibility to
 * roll dices
 * select points category
 * get dices images of current dice values
 * and more
 *
 * @author Ola Råbius Magnusson
 * @since 2023-06-26
 */

// private const val TAG = "ThirtyGame"

private const val DEFAULT_MAX_ROLLS = 3
private const val DEFAULT_DICE_AMOUNT = 6

class ThirtyGame(diceAmount: Int = DEFAULT_DICE_AMOUNT, private val maxRolls: Int = DEFAULT_MAX_ROLLS) {
    private val pointsCalculation = PointsCalculation()
    private val dices = List(diceAmount) { Dice() }
    private val pointsList: List<Points> = PointsCategory.values().map { Points(it) }
    private var currentPointsFromDices: List<Int> = pointsCalculation.getCurrentDicePoints(getValues())
    private var selectedPointsCategory = PointsCategory.LOW
    private var rollAmount = 1

    // --- Getters ---

    /**
     * @return A list of images, one for each dice.
     */
    fun getImages(): List<Int> {
        return dices.map { it.getImage() }
    }

    /**
     * @return A list of the current points for each category registered
     */
    fun getPoints(): List<Int> {
        return pointsList.map { it.points }
    }

    /**
     * @return A list of the points for each category of the current dice values
     */
    fun getPointsFromDices(): List<Int> {
        return currentPointsFromDices
    }

    /**
     * @return The name of the selected points category
     */
    fun getPointsCategorySelected(): String {
        return selectedPointsCategory.name
    }

    /**
     * @return The amount of rolls done in the current round
     */
    fun getRolls(): Int {
        return rollAmount
    }

    /**
     * @return A list of the points categories where the points have not been registered
     */
    fun getPointsCategoriesAvailable(): List<PointsCategory> {
        return pointsList
            .filter { !it.isPointGiven }
            .map { it.category }
    }

    /**
     * @return If it is possible to select dices for saving
     */
    fun isSaveAllowed(): Boolean {
        return pointsList.any { !it.isPointGiven }
    }

    /**
     * @return If it is possible to roll the dices
     */
    fun isRollAllowed(): Boolean {
        if (rollAmount < maxRolls && pointsList.any { !it.isPointGiven }) return true
        return false
    }

    // --- Actions ---

    /**
     * Changes the save state of a dice if allowed
     * @param index     The index of the dice in the list dices
     */
    fun clickDice(index: Int) {
        if (isSaveAllowed()) toggleIsSaved(index)
    }

    /**
     * Register points in pointsList based on the current dice values and selected points category
     * @return true if the points registration was successful
     */
    fun addPoints(): Boolean {
        val point = pointsList.find { it.category == selectedPointsCategory }
        if (point != null) {
            if (point.isPointGiven) return false
            point.points = currentPointsFromDices[selectedPointsCategory.ordinal]
            point.isPointGiven = true
            resetDices()
            return true
        }
        return false
    }

    /**
     * Roll each dice that is not saved, if dice rolls are allowed
     * Registers the points from the new dice values in currentPointsFromDices
     */
    fun rollDices() {
        if (isRollAllowed()) {
            rollAmount++
            dices.map { it.roll() }
            currentPointsFromDices = pointsCalculation.getCurrentDicePoints(getValues())
        }
    }

    /**
     * @param category  A points category
     */
    fun selectPointsCategory(category: PointsCategory) {
        selectedPointsCategory = category
    }

    // --- Private functions ---

    /**
     * Set all Dice objects isSaved attribute to false
     * Resets the rollAmount
     * Re-roll all dices
     */
    private fun resetDices() {
        dices.map {
            it.isSaved = false
        }
        rollAmount = 0
        rollDices()
    }


    /**
     * Makes sure that the index received is valid
     * (0 >= num < amount of dices)
     * @param index dice index
     * @return      true if valid
     */
    private fun isValidDiceIndex(index: Int): Boolean {
        return (index >= 0 && index < dices.size)
    }

    /**
     * Changes the dice attribute is saved between true and false
     * @param diceIndex Index of dice
     */
    private fun toggleIsSaved(diceIndex: Int) {
        if (isValidDiceIndex(diceIndex))
            dices[diceIndex].isSaved = !dices[diceIndex].isSaved
    }

    /**
     * @return A list of the current dice values
     */
    private fun getValues(): List<Int> {
        return dices.map { it.value }.toList()
    }
}