package me.ineson.gradle

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class MochaJsPluginTest {
    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'me.ineson.gradle.mochaJs'

        assertTrue(project.tasks.hello instanceof MochaJsTask)
    }
}
