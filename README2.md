#Summary of the book: Gradle beyong the basics

##Chapter 1: File operations
### Copy task
`from`: Defines the origin to copy files from
`into`: destination
`exclude`: `**` will recursively match any subdirectory name, and `*` will match any part of a filename.
`include`: When used with exclude, the latest takes priority. Use more inclusive include patterns which are then limited by less inclusive exclude patterns. Can be called multiple times:
```
task complexCopy(type: Copy) {
  from('src/main/templates') {
    include '**/*.gtpl'
  }
from('i18n')
from('config') {
    exclude 'Development*.groovy'
    into 'config'
}
  into 'build/resources'
}
```
As seen before, you can put directory structures (see the config folder) inside the destination folder.
`rename`: Accepts a regular expression or a closure. The regex capture the parts of the filename that should be carried over from the source to the destination filename with the $1/$2 format. If we are using the closure, te argument of the method is the filename and the output its the renamed file.
`rename{ file -> "test$[(file - 'development')]"} //this would produce something like "filedevelopment"-->"testfile"`

### Transforming files
`expand`: Recives a map in the form `expand([val:'replacement'])` and it would replace something in the form `${val}` by `replacement`.
`filter`: It has two forms, the first receives a closure where the closure would be call for every line in the file: `filter{line -> line.toUpperCase}`. The second form receives a class that extends from `java.io.FilterReader`.
`eachFile`: Applies the logic passed in a closure to each file one by one instead to all the files like the expand or filter methods. The closure takes a _FileCopyDetails_ that allows you to rename, change the path of the copy, duplicate content...

##File methods
`file()`: Method to create a _java.io.File_ object converting the project relative path to an absolute one. Takes a String, Url, file or closure returning string, file or url.
`files()`: Returns a collection of files, attempts to produce project-relative absolute paths in the File objects it creates. Returns a _FileCollection_ object.
`fileTree()`: It is used to traverse a directory tree and collect the files. You point this method to a root folder, and it can be used in combination _include_ and _exclude_. The directory and the include/exclude patters can be pass like this:
```
fileTree(dir: 'src/main/java', excludes: ['**/*~'])
```

##FileCollection Interface
_files()_ and _filetree()_ returns a _FileCollection_ object which does not have a toString() method. the _files_ property of _FileCollection_ is useful returns an object of type _Set<File>_
To convert a path into an OS specific string, use the _asPath_ method of the _FileCollection_ object.
FileCollections can also be added and subtracted using the + and - operators:
```
task run(type: JavaExec) {
  main = 'org.gradle.example.PoetryEmitter'
  classpath = configurations.compile + sourceSets.main.output //compile time + generated classes
}
```
_SourceSets_ are the representation of source folders in a project, the method _allSource_ returns a _FileCollection_. FileCollections are lazily evaluated whenever it is meaningful to do so, for example calling `filetree('build/main/classes')` will be evaluated in the execution phase, not in the configuration one. File collections are designed to be static descriptions of the collection semantics, and the actual contents of the collection are not materialized until they are needed at a particular time in the build execution.

##Chapter 2: Custom Plug-Ins
 
