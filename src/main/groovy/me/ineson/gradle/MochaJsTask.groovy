package me.ineson.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MochaJsTask extends DefaultTask {
    String greeting = 'hello from MochaJsTask'

    @TaskAction
    def greet() {
        println greeting
    }
}
