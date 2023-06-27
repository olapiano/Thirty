package com.olaraabius.uppgift1.thirty.model

/**
 * Created by: Ola Råbius Magnusson
 * Date: 2023-06-22
 *
 * A representation of Points for a category in the game Thirty
 *
 * type: Category
 * points: Amount of points
 * isPointGiven: true if point is given
 */

data class Points(val category: PointsCategory, var points: Int = 0, var isPointGiven: Boolean = false)