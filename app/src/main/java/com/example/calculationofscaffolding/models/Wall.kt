package com.example.calculationofscaffolding.models

import java.io.Serializable

data class Wall(
    val id: Int, // Уникальный ID стены
    var width: Double,
    var height: Double,
    var tiers: Number,
    var elements: List<Element>,
    var isHeelSelected:Boolean,
    var isJaskSelected:Boolean
):Serializable