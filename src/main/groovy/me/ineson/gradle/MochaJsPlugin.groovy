package me.ineson.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

/*
 *  TODO:
 *  1) Run before - install nodeJS 
 *  2) Default package.json (reporters, phantomJs)
 *  3) npm install ( clean cache?)
 *  4) run tests. Run mocha test (phantomJs. Notes: need to supply files to test (incremental),
 *     Unix/Dos script file, output directory for report files. Other mocha parameters.
 *     , 
 *
 */
class MochaJsPlugin implements Plugin<Project> {

    void apply(Project target) {
        // Add the 'greeting' extension object
        
        target.extensions.create('mochaJs', MochaJsPluginExtension)
//        target.extensions.create('mode', MochaJsPluginExtension)
    
        target.task('hello', type: MochaJsTask)
    }
}
