package com.olaraabius.uppgift1.thirty

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.olaraabius.uppgift1.thirty.controller.ThirtyGame
import com.olaraabius.uppgift1.thirty.model.PointsCategory

class GameViewModel(): ViewModel() {
    private var game = ThirtyGame()

    // --- Getters

    fun getImages(): List<Int> {
        return game.getImages()
    }

    fun getPoints(): List<Int> {
        return game.getPoints()
    }

    fun getPointsCategoriesAvailable(): List<PointsCategory> {
        return game.getPointsCategoriesAvailable()
    }

    fun getPointsCategorySelected(): String {
        return game.getPointsCategorySelected()
    }

    fun getPointsFromDices(): List<Int> {
        return game.getPointsFromDices()
    }

    fun getRolls(): Int {
        return game.getRolls()
    }

    fun isRollAllowed(): Boolean {
        return game.isRollAllowed()
    }

    fun isSaveAllowed(): Boolean {
        return game.isSaveAllowed()
    }

    // --- Actions

    fun addPoints(): Boolean {
        return game.addPoints()
    }

    fun clickDice(diceIndex: Int) {
        game.clickDice(diceIndex)
    }

    fun restartGame() {
        game = ThirtyGame(6, 3)
    }

    fun rollDices() {
        game.rollDices()
    }

    fun selectPointsCategory(category: PointsCategory) {
        game.selectPointsCategory(category)
    }
}