#Chapter 1: Running your first Gradle task
The basic usage of Gradle CLI is `gradle <options> <tasks>`. You  can run a task in gradle by typing the characters 
that uniquely identifies a tasks instead of the complete name. i.e: _deployToProductionServer_ can be invoked as `gradle dTPS`.
We can speed up the builds by invoking the gradle daemon with the `--daemon` option, but only subsequent builds will 
be faster, not the current one. Use --no-daemon to skip daemon usage.
The gradle wrapper `gradlew` is a script that can be used to avoid using the CLI. To generate the graple wrapper type
 `gradle wrapper` and the files needed will be generated.

#Chapter 2: Building Java Projects
To add a plugin in the gradle file, simply add `apply plugin: <id>` 
`gradle test` generates reports in the form of html inside the folder:
`<project>/build/reports/tests/index.html`
To run a Java application with gradle, add the plugin _application_, which accepts the parameters:
    - mainClassName: Defines the class that contains the main method
    - run.args: \[ param1, param2\]: parameter to pass to the executing class.
    
What `run.args` does here is accessing the property args of the run object defined in tasks. 
To generate a project IDE specific from existing sources, we can add the plugins `apply plugin: ['idea' | 'eclipse']`
 and then call the `gradle idea` or `gradle eclipse tasks`.

#Chapter 3: Building a Web Application
The plugin _war_ makes available a series of tasks to create java web applications. To package a standard web java 
project, run the _war_ task. To run the application in development mode (faster deployment, quick server start and 
shutdown, very low server footprint), we can add the plugin `plugins {id "org.akhikhl.gretty" version "1.2.4"}`. For 
applying an external plugin, we must use the fully qualified plugin ID and version. For internal plugins, we don’t 
need to specify a version.
We can define which application server to use, in the following snippet:
```
gretty {
      servletContainer = 'tomcat8'
      port = 8080
}
```

In gradle, we can add dependencies in the form _group:name:version_ under the _dependencies_ block or in the most 
verbose form _group:'\<group>', name:'\<name>', version:'\<version>'_. We can use the notation 
_version:'\<version>+'_ to denote any version that is above the one declared. Gradle resolves conflicts of transitive
 dependencies by giving preference to the latest conflicting versions. The transitive parameter set to false disables
  transitive dependencies, We can force (the version marked as so will remain even if the transitive dependency is 
  newer) a version like this: 
 ```
 runtime ('\<version>') {
    force = true
    transitive = false
 }
 ```
Dependencies are defined as a group called _configuration_ for example the configuration named _testComile_ added by 
the java plugin includes dependencies needed to compile the test sources, but are not included in the final build 
distribution. Configuration does not just include dependencies, but also the artifacts produced by this configuration.
The _repository_ block configures the repositories where Gradle will look for dependencies. Examples:
```
mavenCentral()  // shortcut to maven central
maven {
  url"http://intranet.example.com/repo"
}
```

#Chapter 4. Demystifying Build Scripts
##Groovy syntax:
Defining variables in groovy `def a = 10` or for typed variables we can write `Integer a = 10`.  Everything in Groovy
 is a first class object (primitive types are wrapped). To define a multiline String in Groovy we use:
```
def multilineString = '''\
    Hello
    World '''
```
It is also possible to enclose a string within `/` to avoid escape enclosed characters, like in regex expressions. To 
match a regex pattern we can use the `~` operator: `if(chain ==~ /(\d)+/){...}`. 
_Closure_ are to groovy like lambdas to java, closures can be call using the `call()` method. They can accept 
parameters and return values (using the last statement of the code).

_Data Structures_ in Groovy uses the _Collections_ framework. 
*Lists*: The `def list = ['a','b']` defines a List, not an array. Common operators for the list includes addition 
`def list1 = [10, 20, 30] + [30, 50]`, appending values `list1 << 90`, substraction `list1 -= [30, 70]`, iteration 
`list.each {it * it}`.

*Sets*: To define a set we need to define it like a list and use _as Set_ after values declaration `def set1 = [1,2] 
as Set` (which would use a HashSet), or use the specific type in the declaration `TreeSet set2 = [5,6]`. 

*Maps*: Can be define a map like this `def map1 = [:]`, the default implementation is _LinkedHashMap_. A value can be
 accessed like:
 ```
 tool.name
 tool["name"]
 tool.get("name")
 ```
We can set default values to parameters on a method call like `def helloWorld(greet, name="World"){ greet + name}`. 
It is possible to replicate named parameters using maps `map(a:10, b:20)`and use them in a method like this:
```
def method(Map options) {
    def a = options.a ?: 10 // This is equal to: def a = options.a ? options.a : 10
    def b = options.b ?: 20
}
```
Also possible to use varargs `method(...param){3.times{println params(it}}`, there is no need for type definition. 
If a method receives a closure as the last parameter, it can be called like `myMethod(3, {...})`. 

*Classes*: Classes in groovy are _public_ by default, can _implements_ interfaces or _extends_ other classes. Classes
in groovy has the default constructor and another constructor that takes a map where the keys are the name of the 
properties `def person = new Person(name:"Jorge", age:32)`. Static and instance methods are also supported `static 
method(){...}`.
   
*Properties*: Groovy has language level support for properties. In the preceding class, name and age are properties 
with their getters and setters defined. Fields are private and their getters and setters public. However, we can 
make this call `person.age = 35` and it would work because under the covers, groovy is calling the `getAge(35)` 
method. This is due to the groovy support for properties.

*Scripts*: Scripts are classes, groovy wraps the method definitions into a class

##Gradle
Gradle has two important classes to consider:
    - *Project*: Object that represent a system that is being build. In fact`apply plugin: java` in the build script 
    can be translated to `project.apply([plugin:java])`.
    - *Task*: Pieces of logic to be build.

The gradle lifecycle follows three steps: Initialization, Configuration and Execution. When a gradle command is 
invoked, only the blocks of code relevant to the phase are executed. 
 
    - *Initialization*: In multiproject builds, gradle figures out what projects to execute.
    - *Configuration*: Build script tasks are evaluated (not executed). For multimodule projects, the sibling 
    projects are executed before its descendants. A task action is actually a closure; hence, it is only attached to 
    a task during the statement execution.
    - *Execution*: Gradle executes the tasks passed as command line arguments, here is where the task actions are 
    executed. 

### Project object
All the top-level method calls in the build scripts are called on a project object if no explicit reference is  
provided. Blocks like _dependencies_ or _repositories_ are methods that accept closures. Project properties can be 
read only (name, path parent), or can be editable. The rules about properties defined before applied here.
 To store user defined properties in a project, we can use the _ext_ namespace:
 ```
 ext.p1 = "abc"
 task test<<{
    println project.p1 + project.ext.p1 + project.property("p1")
 }
 ```
This properties can be seen in submodules of the project
 
### Task Object

The task block is a method call on the project object: `project.task("mytaskName")`, we create an object of type 
Task, and we pass a Closure to configure it `Task "name" {...}` invokes the method `Task task(String name, Closure 
config)`.

A Task objects needs Actions to perform, `myTask.doLast({...})` executes all the closures passed to this method in 
the order they were passed. Task can declare dependencies: 
    - `dependsOn: xxx` which declares the task to be executed before this one 
    - `finalizedBy: xxx` which declares the tasks that should be executed after this one
    - `onlyIf:` declares a condition that should be meet for the task to run 
    - `mustRunAfter` and `shouldRunAfter` declares ordering of a task that takes precedence over the command line, 
    for example `build.mustRunAfter clean`.
    
We can create tasks dynamically:
```
5.times { number ->
  task "dynamicTask$number" << {
    println "this is dynamic task number # $number "
  }
}
```
We can declare default tasks: `defaultTasks "task1", "task2"`. This tasks are running by simply invoking `gradle`.
We can also define custom task classes that exposes configurable parameters to avoid code duplication, this tasks are 
called enhanced tasks. For example:
```
class Print extends DefaultTask {
  @Input
  String message = "Welcome to Gradle"
  @TaskAction
  def print() {
    println "$message"
  }
}
task thanks(type: Print) {
  message = "Thanks for trying custom tasks"
}
```

#Chapter 5: Multiprojects build
In the initialization phase, gradle reads the _settings.gradle_ file to figure out which modules take part of the 
build in a multiproject. This file is where build.gradle is, or in another location if we pass the path with the `-c`
parameter. An object of type _Settings_ is created before any build.gradle is read. The methods included in this file
are  implicitly calling the Settings Object, like explained before for the Project object. 
To include a project in the build, we can add the following to the settings.gradle file: `include ':projectName'`. 
The `:` denotes the project path relative to the root folder and it can be ommitted for level 1 subprojects. For 
example a nested 2 level would be `include :projectName:projectChild`.

To include a task in all subproject we can include a call to `allprojects` passing it a closure with the task, and it
will be passed to all subprojects. in this closure we can include plugins to apply, other tasks, definitions... in 
essence, the whole content of a _build.gradle_ file. If we call gradle with the name of the task included here, this
task will be executed in every subproject. To execute some logic only in subprojects but not to the parent project, 
we use the method `subprojects` from the Settings object and pass the closure to apply.

To declare dependecies between projects, we use the project method, which accepts a closure just like the  
allprojects and subprojects methods in addition to the project name on which the closure will be applied.
```
project(':services') {
  dependencies {
    compile project(':repository')
  }
}
```

#Chapter 6. The Real-world Project with Gradle
It is possible to import an Ant based project by using the directive `ant.importBuild '<path to ant's build.xml>'`, 
for example to create the jar we execute the task `createJar`. Gradle also makes available an object of type 
antBuilder that we can use to call ant tasks as follows:
```
task compileSrc(dependsOn:'cleanDir') << {
  ant.mkdir(dir:"build/classes")
  ant.mkdir(dir:"dist")
  ant.javac(srcdir:"src", destdir:"build/classes", includeantruntime:"false")
}
```
Gradle provides a configuration called _archives_ to upload the generated artifact to a repository. Using the task 
_uploadArchives_, the default artifact generated by a plugin would be uploaded by default as a part of the 
uploadArchives task:
```
uploadArchives {
    repositories {
        maven {
            credentials {
                username "user1"
                password "user1"
            }
            url "http://company.private.repo"
        }
    } 
}
```
The java plugin has a task called javadoc that can help to generate the docummentation of the project in the folder 
`/build/docs/javadoc`

#Chapter 7. Testing and Reporting with Gradle
We can use the TestNG framework to create integration tests within our project, to do that include the testNG 
dependency and define the _test_ closure:
```
test {
  ignoreFailures = true
  useTestNG(){
    suites("src/test/resources/testng.xml")
  }
}
```
In a testNG file you can define test classes, tests, test group names, listener information... In the example, this 
is set in the file testng.xml.
You can define integration test in a different folder, and have gradle to execute the tests by instructing it with a 
Test task:
```
sourceSets {
   integrationTest {
       java.srcDir file('src/integrationTest/java')
       resources.srcDir file('src/integrationTest/resources') // to add the
resources }
}
task runIntegrationTest(type: Test) {
   testClassesDir = sourceSets.integrationTest.output.classesDir
   classpath = sourceSets.integrationTest.runtimeClasspath
}
```
There are also plugins for code coverage like `jacoco`, and apply to our code with dependencies:
`jacocoTestReport.dependsOn test`. A report would be created in _\<build dir\>/reports/jacoco/test/html_
Code analysis can be done by integrating sonar in gradle, as a prerequisite, sonar must be installed an running. To 
configure the sonar task, we can add the following:
```
apply plugin: "sonar-runner"
...
sonarRunner {
  sonarProperties {
    property "sonar.host.url", "http://<IP_ADDRESS>:<PORT>"
    property "sonar.jdbc.url",
    "jdbc:h2:tcp://<IP_ADDRESS>:<PORT>/sonar"
    property "sonar.jdbc.driverClassName", "org.h2.Driver"
    property "sonar.jdbc.username", "sonar"
    property "sonar.jdbc.password", "sonar"
    }
}
```

#Chapter 8. Organizing Build Logic and Plugins
It is possible to create a gradle plugin directly on the build.gradle by putting the code of the classes directly 
into it:
```
apply plugin: CustomPlugin
class CustomPlugin implements Plugin<Project> {
  void apply(Project project) {
    task ....
  }
}
```
To create a custom plugin, you need to create a Groovy class that implements the _Plugin_ interface and define the 
_apply_ method. However good practices specify that the code should be put in a separate buildSrc folder inside the 
project folder, and put the groovy class there _(src/main/com...)_. You have to put the package declaration and 
import `org.gradle.api.*` in the class Plugin class. Once you do this, you need to import the plugin class and the 
apply the plugin in the gradle script file.  
To create a plugin jar, we create a project containing the buildSrc and create a properties file in the 
_src/main/resources/META-INF/gradle-plugins_ directory. The name of the properties file would be the name of the 
plugin ID, and the file content would be something like `implementation-class=...`. Inside the plugin project, 
there would be a gradle file that should contain at least the following: 
```
apply plugin: 'groovy'
version = '1.0'
dependencies {
  compile gradleApi()
  compile localGroovy()
}
```
To use this Plugin in other project, you need to mention the location of the plugin JAR in the _buildscript_ closure 
and add dependency to this JAR in the _dependencies_ closure.
To define plugin properties, you need to create one additional extension class and register the class into your plugin class.
```
class CustomPluginExtension {
def location = "/plugin/defaultlocation"
}
```
And then apply it:
```
class CustomPlugin implements Plugin<Project> {
  void apply(Project project) {
    def extension = project.extensions.create("customExt",CustomPluginExtension)
        project.tas k('task1') << {
            println "Sample task1 in custom plugin"
            println "location is "+project.customExt.location
        } 
    }
}
```
This would yield a default location, that can be change in the gradle build script by redefining the class inside it:
```
apply plugin: 'customplugin'
customExt {
  location="/plugin/newlocation"
}
```
