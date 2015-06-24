package me.ineson.gradle.mocha.exec


import org.gradle.api.Project
import org.gradle.process.ExecResult

import com.moowork.gradle.node.exec.ExecRunner

class MochaExecRunner extends ExecRunner
{

    public MochaExecRunner( final Project project ) {
        super( project )
    }

    @Override
    protected ExecResult doExecute()
    {
//        System.out.println( " ---------------------------------------- mocha runner" )
        def exec = 'node_modules/mocha/bin/mocha'
//        def exec = 'mocha/bin/mocha'



        if ( this.ext.download )
        {
           def nodeEnvironment = [:]
           def nodeBinDirPath = this.variant.nodeBinDir.getAbsolutePath()

           nodeEnvironment << System.getenv()
            // Take care of Windows environments that may contain "Path" OR "PATH" - both existing
            // possibly (but not in parallel as of now)
            if (System.getenv('Path') != null) {
                environment['Path'] = nodeBinDirPath + File.pathSeparator + System.getenv('Path')
           System.out.println( "new path : " + nodeEnvironment['Path'] )
            } else {
                environment['PATH'] = nodeBinDirPath + File.pathSeparator + System.getenv('PATH')
           System.out.println( "new path : " + nodeEnvironment['PATH'] )
            }



//            nodeEnvironment['SUITE_NAME'] = "js.myTest"
//            nodeEnvironment['NODE_PATH'] = "/home/peter/Dev/demo/Demo/DemoApp/build/nodeJsTest/node_modules"
//            nodeEnvironment['XUNIT_FILE'] = "/home/peter/Dev/demo/Demo/DemoApp/build/runJsTest-results/TEST-js.myTest.xml"
//            this.environment = nodeEnvironment
//            exec = this.variant.nodeExec
        }

        System.out.println( " run exec: " + exec )

        return run( exec, this.arguments )
    }
}