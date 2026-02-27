package io.fletchly.comparator.port.out

interface LogPort {
    fun info(message: String)
    fun warn(message: String)
}