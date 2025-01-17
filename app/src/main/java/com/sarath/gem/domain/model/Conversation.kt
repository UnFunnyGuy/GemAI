package com.sarath.gem.domain.model

data class Conversation(val id: Long = 0, val timestamp: Long, val title: String?, val lastMessageTimestamp: Long)
