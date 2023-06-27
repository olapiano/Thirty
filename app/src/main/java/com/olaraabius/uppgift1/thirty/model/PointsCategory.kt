package com.olaraabius.uppgift1.thirty.model

import androidx.compose.ui.res.stringResource

/**
 * Created by: Ola Råbius Magnusson
 * Date: 2023-06-22
 *
 * Each points category in the game Thirty
 * target:
 *      In LOW represents the max dice value allowed
 *      In all the other the target sum for each combination
 */

enum class PointsCategory(val target: Int) {
    LOW(3, ),
    FOURS(4),
    FIVES(5),
    SIXES(6),
    SEVENS(7),
    EIGHTS(8),
    NINES(9),
    TENS(10),
    ELEVENS(11),
    TWELVES(12),
}
