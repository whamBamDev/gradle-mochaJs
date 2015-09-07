package me.ineson.gradle.mocha.task

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult

import me.ineson.gradle.mocha.exec.MochaExecRunner

import com.moowork.gradle.node.task.SetupTask

class MochaExec
{
    private Project project;
    
    private MochaExecRunner runner;

    private String step;
    
    private File modulesDir;

    private File sourceDir;

    private File resultDir;


    private String dir;

    private String test;

    private File script;

    private Iterable<?> args = [];

    private ExecResult result;

    public MochaExec( final Project project, final String step, final File modulesDir)
    {
        this.project = project;
        this.step = step;
        this.modulesDir = modulesDir;
        this.runner = new MochaExecRunner( this.project );

        sourceDir = new File( project.projectDir, "src" + File.separator + step + File.separator + "javascript");

        resultDir = new File( project.buildDir, step + "-results");
        
        if( !resultDir.exists() ) {
            resultDir.mkdirs();
        }
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

    
    void runTest( String testPackage, String testScriptFile)
    {
        File sourceFile = new File( sourceDir, testPackage + File.separator + testScriptFile + ".js");
        File resultFile = new File( resultDir, "TEST-" + testPackage + "." + testScriptFile + ".xml");

        def execArgs = [ sourceFile.absolutePath, '--reporter', 'xunit-file'];
//        def execArgs = [ 'node_modules/mocha/bin/mocha', sourceFile, '--reporter', 'xunit-file'];
        def nodeEnvironment = [:];

        if( !sourceFile.exists() ) {
            throw new Exception( "Cannot find test script '" + sourceFile.absolutePath + "'");
        }

        nodeEnvironment['SUITE_NAME'] = testPackage + "." + testScriptFile;
        nodeEnvironment['NODE_PATH'] = modulesDir.absolutePath + File.separator + "node_modules";
        nodeEnvironment['XUNIT_FILE'] = resultFile.absolutePath;

		println( "SUITE_NAME=" + nodeEnvironment['SUITE_NAME']);
		println( "NODE_PATH=" + nodeEnvironment['NODE_PATH']);
		println( "XUNIT_FILE=" + nodeEnvironment['XUNIT_FILE']);

		        //execArgs.add( this.script.absolutePath )
        execArgs.addAll( this.args as List )

        this.runner.workingDirFile = modulesDir;
        this.runner.workingDir = modulesDir;
        this.runner.arguments = execArgs;
        this.runner.environment = nodeEnvironment;
        this.runner.ignoreExitValue = true;

        this.result = this.runner.execute()
    }
    
}