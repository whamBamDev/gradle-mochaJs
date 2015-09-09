package me.ineson.gradle.mocha.exec


import org.gradle.api.Project
import org.gradle.process.ExecResult

import me.ineson.gradle.exec.ExecRunner

class MochaExecRunner extends ExecRunner
{

    public MochaExecRunner( final Project project ) {
        super( project )
    }

    @Override
    protected ExecResult doExecute()
    {
		def execFile = new File ( workingDirFile, 'node_modules\\.bin\\mocha.cmd')
		if( !execFile.exists() ) {
			throw new Exception( "Cannot find exec script '" + execFile.absolutePath + "'");
		}

		
//        System.out.println( " ---------------------------------------- mocha runner XXXX" )
//       def exec = 'node'
        def exec = execFile.absolutePath;
//        def exec = 'node node_modules/mocha/bin/mocha'
//        def exec = 'mocha/bin/mocha'



        if ( this.ext.download )
        {
           def nodeEnvironment = [:]
           def nodeBinDirPath = this.variant.nodeBinDir.getAbsolutePath()

           nodeEnvironment << System.getenv()
            // Take care of Windows environments that may contain "Path" OR "PATH" - both existing
            // possibly (but not in parallel as of now)
		   def unixPath = System.getenv('PATH');
		   def windowsPath = System.getenv('Path');
		   
           System.out.println( "\n====== Working version! -------\n");
           System.out.println( "\nCurrent Unix PATH : " + unixPath );
           System.out.println( "\nCurrent Windows Path : " + windowsPath);
		   if (windowsPath != null) {
                environment['Path'] = nodeBinDirPath + File.pathSeparator + windowsPath;
                nodeEnvironment['Path'] = nodeBinDirPath + File.pathSeparator + windowsPath;
           System.out.println( "\nNew Windows Path : " + environment['Path'] )
            }
		    if (unixPath != null) {
                environment['PATH'] = nodeBinDirPath + File.pathSeparator + unixPath;
                nodeEnvironment['PATH'] = nodeBinDirPath + File.pathSeparator + unixPath;
           System.out.println( "\nNew NIX PATH : " + environment['PATH'] )
            }

			nodeEnvironment['SUITE_NAME'] = environment['SUITE_NAME'];
			nodeEnvironment['NODE_PATH'] = environment['NODE_PATH'];
			nodeEnvironment['XUNIT_FILE'] = environment['XUNIT_FILE'];
			
			
//            nodeEnvironment['SUITE_NAME'] = "js.myTest"
//            nodeEnvironment['NODE_PATH'] = "/home/peter/Dev/demo/Demo/DemoApp/build/nodeJsTest/node_modules"
//            nodeEnvironment['XUNIT_FILE'] = "/home/peter/Dev/demo/Demo/DemoApp/build/runJsTest-results/TEST-js.myTest.xml"

			System.out.println( "\n------------------ New NODE PATH : " + nodeEnvironment['NODE_PATH'] )
			
			this.environment = nodeEnvironment
//            exec = this.variant.nodeExec
        }

        System.out.println( "\nrun exec: " + exec )

		def bufferedOutput = new ByteArrayOutputStream()
		def bufferedError = new ByteArrayOutputStream()
		setExecOverrides({
		  it.standardOutput = bufferedOutput
		  it.errorOutput = bufferedError
		})
		
		try { 
			return run( exec, this.arguments )
		} finally {
            println "\nOutput;"
		    println bufferedOutput.toString()
			println "\nError;"
			println bufferedError.toString()

        }
    }
}