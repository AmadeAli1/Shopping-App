package com.amade.dev.shoppingapp

import com.google.cloud.spring.autoconfigure.core.GcpProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.core.io.FileUrlResource
import org.springframework.scheduling.annotation.EnableAsync
import java.io.File

@EnableAsync
@SpringBootApplication
class ShoppingAppApplication(properties: GcpProperties,environment: Environment) {
    init {
        val file = File("src/main/resources/google-credentials.json")
        file.writeText(environment["credentials"]!!)
        properties.credentials!!.location = FileUrlResource(file.absolutePath)
    }
}

fun main(args: Array<String>) {
    runApplication<ShoppingAppApplication>(*args)
}
