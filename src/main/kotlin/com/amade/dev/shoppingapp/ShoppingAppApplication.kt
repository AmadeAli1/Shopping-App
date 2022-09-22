package com.amade.dev.shoppingapp

import com.google.cloud.spring.autoconfigure.core.GcpProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.FileUrlResource
import org.springframework.scheduling.annotation.EnableAsync
import java.io.File

@EnableAsync
@SpringBootApplication
class ShoppingAppApplication(properties: GcpProperties) {
    @Value("\${credentials}")
    var config: String? = null

    init {
        println("CALLLEDDD: $config")
        val file = File("src/main/resources/google-credentials.json")
        file.writeText(config!!)
        properties.credentials!!.location = FileUrlResource(file.absolutePath)
    }
}

fun main(args: Array<String>) {
    runApplication<ShoppingAppApplication>(*args)
}
