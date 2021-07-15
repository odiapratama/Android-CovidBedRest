package com.bedrest.app.util

enum class ZoomLevel(val value: Float) {
    WORLD(1f),
    CONTINENT(5f),
    CITY(10f),
    STREETS(15f),
    BUILDINGS(20f)
}