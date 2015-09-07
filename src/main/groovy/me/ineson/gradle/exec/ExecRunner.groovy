package me.ineson.gradle.exec

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.variant.Variant
import com.moowork.gradle.node.variant.VariantBuilder
import org.gradle.api.Project
import org.gradle.process.ExecResult

abstract class ExecRunner

{
    protected Project project

    protected NodeExtension ext

    protected Variant variant

    def Map<String, ?> environment

    def File workingDirFile
	
	def Object workingDir
	
    def List<?> arguments = []

    def boolean ignoreExitValue

    def Closure execOverrides

    public ExecRunner( final Project project )
    {
        this.project = project
    }

    protected final ExecResult run( final String exec, final List<?> args )
    {
        def realExec = exec
        def realArgs = args
/*
        if ( this.variant.windows )
        {
            realExec = 'cmd'
            realArgs.add( 0, exec )
            realArgs = createWinArg( realArgs )
        }
*/
        return this.project.exec( {
            it.executable = realExec
            it.args = realArgs

            if ( this.environment != null )
            {
                it.environment = this.environment
            }

            if ( this.workingDir != null )
            {
                it.workingDir = this.workingDir
            }

            if ( this.ignoreExitValue != null )
            {
                it.ignoreExitValue = this.ignoreExitValue
            }

            if ( this.execOverrides != null )
            {
                this.execOverrides( it )
            }
        } )
    }

    private static List<?> createWinArg( final List<?> args )
    {
        def result = []
        args.each {
            result += '"' + it + '"'
        }

        return ['/c', '"' + result.join( ' ' ) + '"']
    }

    public final ExecResult execute()
    {
        this.ext = NodeExtension.get( this.project )
        this.variant = VariantBuilder.build( this.ext )
        return doExecute()
    }

    protected abstract ExecResult doExecute()
}
