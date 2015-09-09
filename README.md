# gradle-mochaJs

> WARNING: This plugin is just experimental. It is currently being used for working out the mechanisms for running mocha tests.


A plugin for gradle for executing JavaScript test cases using the [mochaJs](http://mochajs.org/) framework.

##How to build 

At the moment this plugin is not published, so to use it then it needs to be built localially. Clone from GitHib and build locally.

```shell
$ gradle build install
```

##How to use

Make that the maven local respoitory is included in the buildscript.

```gradle
buildscript {
    repositories {
        mavenLocal()
        ...
    }
    dependencies {
        classpath(
            [ "me.ineson.gradle:mochaJsPlugin:0.+" ]
        ) 
        
    }
}

```

Next the main task that runs the Mocha tests. 

```gradle
task runJsTest(dependsOn: ['setupJsTest','copyJsTestMainSrc','copyJsTestMainTest','copyJsTestWebJars']) << {
    def mocha = new MochaExec( project, 'test', jsTestModulesDir);
    mocha.runTest( "jsTest", "myTest");
}
```

First configuration of the gradle nodeJs plugin.

```gradle
node {
  download = true
  workDir = file("${project.buildDir}/nodeJs")
  nodeModulesDir = file("${project.buildDir}/nodeJsTest/test/node_modules")
}
```

Configure a few variables that are used by the tasks.

```gradle
ext {
  cacheWebJars = file("${project.buildDir}/cache/webJars")

  jsSrcDir = file("${projectDir}/src/main/webapp/js")
  // Test dirs
  jsTestModulesDir = file("${project.buildDir}/jsTest")
  jsTestSrcDir = file("${projectDir}/src/test/javascript")
}
```

Next is the setupJsTest task that downloads the nodeJs distribution and performs the npm module installs. 

```gradle
task initJsTests() << {
    if( !jsTestModulesDir.exists() ) {
        jsTestModulesDir.mkdirs();
    }
}

task initJsTestPackage(type: Copy, dependsOn: 'initJsTests') {
    from(jsTestSrcDir) {
        include 'package.json'
    }
    into jsTestModulesDir
}

task setupJsTest(type: NpmTask, dependsOn: ['initJsTestPackage']) {
  //outputs.dir node.nodeModulesDir
  workingDir jsTestModulesDir
  args = ['install' ,'--loglevel', 'error']
}
```

The package.json that is used as part of the npm install.

```json
{
  "name": "mocha-test-runner",
  "description": "Packages from running MochaJs tests",
  "version": "0.0.0",
  "private": true,
  "author": "user",
  "dependencies": {
    "mocha": "latest",
    "xunit-file": "latest",
    "jsdom": "latest"
  }
}
```

Next are copyJsTestMainSrc & copyJsTestMainTest tasks that copy the src code to under build directory, this result in easy
referencing of code and libraries in the test cases. Another byproduct is that there is protection from refactoring. 

```gradle
task copyJsTestMainSrc(type: Copy, dependsOn: ['setupJsTest']) {
    from 'src/main/webapp/js'
    into new File( jsTestModulesDir, 'src')
}

task copyJsTestMainTest(type: Copy, dependsOn: ['setupJsTest']) {
    from 'src/test/javascript'
    into new File( jsTestModulesDir, 'test')
}
```

Next there is the copyJsTestWebJars. In the application I was developing used JS libraries downloaded from [WebJars](http://www.webjars.org). This task extracts the js files out of the JARs so they can be included in the test cases. If [WebJars](http://www.webjars.org) are not being
then this task is not required.

```gradle
task extractWebJars(type: Copy) {
    configurations.compile.files { dep -> dep.group == 'org.webjars' && dep.name != 'bootstrap' }.each { file ->
        
        //println file.name + " -> " + file.path
        from zipTree(file)
    }
    into cacheWebJars
    eachFile { FileCopyDetails fcp ->
//        println 'Check -> ' + fcp.relativePath.pathString
        if (fcp.relativePath.pathString.startsWith( 'META-INF/resources/webjars')) {
            // remap the file to the root
            def segments = fcp.relativePath.segments
            def pathsegments = segments[2..-1] as String[]
//            println 'Add ' + pathsegments
            fcp.relativePath = new RelativePath(!fcp.file.isDirectory(), pathsegments)
        } else if ( ! fcp.relativePath.pathString.startsWith( 'webjars')) {
//            println 'Exclude'
            fcp.exclude()
        }
    }
}

task copyJsTestWebJars(type: Copy, dependsOn: ['extractWebJars','setupJsTest']) {
    from new File( cacheWebJars, '/webjars')
    into new File( jsTestModulesDir, 'webjars')
}
```

Finally there is the aggregateTestReports task that combines the Java and Javascript test results into one report.

```gradle
task aggregateTestReports( dependsOn: [ 'runJsTest', 'test']) << {
    aggregateReports( 
        new File( project.buildDir, '/test-results'),
        new File( project.buildDir, 'reports/allTests'));
}

test.finalizedBy aggregateTestReports
runJsTest.finalizedBy aggregateTestReports

def aggregateReports( File resultsDir, File reportDir) {
    if( !reportDir.exists() ) {
        println("creating subdir " +reportDir)

        if(reportDir.mkdirs()) {
            println("succeeded in making folder")
        } else {
            println("failed to make folder")
        }
    }

    ant.taskdef(
        name: 'junitreport_js',
        classname: 'org.apache.tools.ant.taskdefs.optional.junit.XMLResultAggregator',
        classpath: configurations.antClasspath.asPath
    )
    
    
    ant.junitreport_js(todir: resultsDir.absolutePath) {
        fileset(dir: resultsDir.absolutePath, includes: 'TEST-*.xml')
        report(todir: reportDir.absolutePath, format: "frames")
    }
}
```

Now time for the redesign and rewrite!