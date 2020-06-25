package com.kchen52.yetanothertranslinkapp.map

interface MapsActivityView {
    /**
     * Renders the current state of the application given a [MapsActivityState]
     */
    fun render(state: MapsActivityState)
}