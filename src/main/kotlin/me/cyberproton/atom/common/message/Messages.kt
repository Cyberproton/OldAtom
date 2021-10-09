package me.cyberproton.atom.common.message

import me.cyberproton.atom.api.message.Message

object Messages {
    val COMMAND_INPUT_IS_NOT_A_DOUBLE = Message("COMMAND_INPUT_IS_NOT_A_DOUBLE", "{value} is not a double")
    val COMMAND_INPUT_IS_NOT_A_INTEGER = Message("COMMAND_INPUT_IS_NOT_A_INTEGER", "{value} is not a integer")
    val COMMAND_INPUT_IS_NOT_A_BOOLEAN = Message("COMMAND_INPUT_IS_NOT_A_BOOLEAN", "{value} is not a boolean")
    val COMMAND_INPUT_IS_NOT_A_LONG = Message("COMMAND_INPUT_IS_NOT_A_LONG", "{value} is not a long")
    val PLAYER_NOT_FOUND = Message("PLAYER_NOT_FOUND", "Player {player} does not exist")
}