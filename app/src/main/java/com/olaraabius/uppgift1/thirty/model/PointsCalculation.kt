package com.olaraabius.uppgift1.thirty.model

/**
 * A class used to calculate the points for the current dice values used in the game Thirty
 *
 * @author Ola Råbius Magnusson
 * @since 2023-06-26
 */

// private const val TAG = "IndexCombinations"
private const val DEFAULT_DICE_AMOUNT = 6

class PointsCalculation(private val diceAmount: Int = DEFAULT_DICE_AMOUNT) {
    /**
     * The different possible index combinations stored from initialization,
     * used when finding the valid dice combinations
     * Example of possible index combinations with three dices:
     * 012, 02, 12, 2
     */
    private var indexCombinationsSorted: MutableList<MutableList<Int>> = mutableListOf()

    // --- public methods

    /**
     * Creates a list of all the points calculated for each points category
     * @param values    A list of integers that represents the values of dices
     * @return          A list with the calculated points for each category
     */
    fun getCurrentDicePoints(values: List<Int>): List<Int> {
        val points: MutableList<Int> = mutableListOf()

        PointsCategory.values().forEach {
            if(it == PointsCategory.LOW)
                points.add(findLow(values))
            else
                points.add(findAllCombinations(values, it.target))
        }
        return points
    }

    // --- private methods

    /**
     * Calculates the result of the points categoty: Low
     * @param values    A list of integers that represents the values of dices
     * @return          The result in the for of an integer
     */
    private fun findLow(values: List<Int>): Int {
        return values.filter { it <= PointsCategory.LOW.target }.sum()
    }

    /**
     * Finds all combinations of of dice values that give a specific sum (target)
     * Calls the {@link findOneCombination} with the dices not used in a combination
     * until no more combinations are found
     * @param values    A list of integers that represents the values of dices
     * @param target    The sum that one or more dice values combined needs to reach
     * @return          The total sum of the combinations found (= target * combinations)
     */
    private fun findAllCombinations(values: List<Int>, target: Int): Int {
        // The values sorted descended is needed to find the best solution
        var valuesTemp: MutableList<Int> = values.sortedDescending() as MutableList<Int>
        var combination: List<Int>
        var indexCombination: List<Int>
        val combinations: MutableList<Int> = mutableListOf()
        do {
            indexCombination = findOneCombination(valuesTemp, target)
            combination = indexCombination.map { valuesTemp[it] }
            valuesTemp = valuesTemp.filterIndexed { index, _ -> !indexCombination.contains(index)} as MutableList<Int>
            combinations += combination
        } while(indexCombination.isNotEmpty() && combinations.size != values.size)
        return combinations.sum()
    }

    /**
     * Finds one combination of dices that gives specific sum
     * @param target    The sum that one or more dice values combined needs to reach
     * @return          The index of the dices values used in combination
     */
    private fun findOneCombination(values: List<Int>, target: Int): List<Int> {
        val indexLists = getIndexLists(values.size - 1)
        var targetRemaining = target
        val combination: MutableList<Int> = mutableListOf()
        for (list in indexLists) {
            for (index in list) {
                if (values[index] > targetRemaining) continue
                if (values[index] == targetRemaining) {
                    combination.add(index)
                    return combination
                }
                if (values[index] < targetRemaining) {
                    combination.add(index)
                    targetRemaining -= values[index]
                }
            }
            targetRemaining = target
            combination.clear()
        }
        return combination
    }

    /**
     * @param listSize  The amount of dice values in the list
     * @return          All the possible index combinations needed to find the target sum
     */
    private fun getIndexLists(listSize: Int): List<MutableList<Int>> {
        return indexCombinationsSorted.filter { it.max() <= listSize }
    }

    /**
     * Storing all the index combinations
     * used when finding the valid dice combinations
     * Example of possible index combinations with three dices:
     * 012, 02, 12, 2
     */
    init {
        val indexCombinations: MutableList<MutableList<Int>> = mutableListOf()
        fun findAllIndexCombinations(index: Int, indexList: MutableList<Int>) {
            if (index > diceAmount) {
                indexCombinations.add(indexList)
                return
            }

            findAllIndexCombinations(index + 1, indexList)
            findAllIndexCombinations(index + 1, (indexList + mutableListOf(index)) as MutableList<Int>)
        }
        findAllIndexCombinations(0, mutableListOf())
        indexCombinationsSorted = indexCombinations.filter{ it.size >= 1 }.sortedBy { it.maxOrNull() } as MutableList<MutableList<Int>>
    }
}