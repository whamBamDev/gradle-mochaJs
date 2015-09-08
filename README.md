# gradle-mochaJs

<div style="width:100%; color: red;" >
This plugin is just experimental. It is currently being used for working out the
mechanisms for running mocha tests. 
</div>

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

More to come....





