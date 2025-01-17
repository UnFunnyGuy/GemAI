package com.sarath.gem.data.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.sarath.gem.UserConfig
import java.io.InputStream
import java.io.OutputStream

class UserConfigSerializer : Serializer<UserConfig> {

    override val defaultValue: UserConfig
        get() = UserConfig.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserConfig {
        try {
            return UserConfig.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserConfig, output: OutputStream) {
        t.writeTo(output)
    }
}
