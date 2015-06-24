package me.ineson.gradle.mocha.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult

import me.ineson.gradle.mocha.exec.MochaExecRunner

import com.moowork.gradle.node.task.SetupTask

class MochaTask
//    extends Test
    extends DefaultTask
{
    private MochaExecRunner runner

    private String dir;

    private String test;

    private File script

    private Iterable<?> args = []

    private ExecResult result

    public MochaTask()
    {
        this.runner = new MochaExecRunner( this.project )
        dependsOn( SetupTask.NAME )
    }

    void setScript( final File value )
    {
        this.script = value
    }

    void setArgs( final Iterable<?> value )
    {
        this.args = value
    }

    void setEnvironment( final Map<String, ?> value )
    {
        this.runner.environment = value
    }

    void setWorkingDir( final Object value )
    {
        this.runner.workingDir = value
    }

    void setIgnoreExitValue ( final boolean value )
    {
        this.runner.ignoreExitValue = value
    }

    void setExecOverrides ( final Closure closure )
    {
        this.runner.execOverrides = closure
    }

    ExecResult getResult()
    {
        return this.result
    }

    Iterable<?> getArgs()
    {
        return this.args
    }

    @TaskAction
    void exec()
    {
        if ( this.script == null )
        {
            throw new IllegalStateException( 'Required script property is not set.' )
        }

        def execArgs = []
        execArgs.add( this.script.absolutePath )
        execArgs.addAll( this.args as List )

        this.runner.arguments = execArgs
        this.result = this.runner.execute()
    }
    
    
}