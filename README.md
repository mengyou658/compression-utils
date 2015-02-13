# Simple Tar-GZ Compression
Simple and easy to use library to compress and decompress files in the .tar.gz format. The library is simple a
wrapper for the Apache Commons Compress library, but it provides a much easier to use File-orientated interface.

## Installation
You can use this library as you seen fit, for whatever purpose. I provide no assurances that this works, but you can
if you like. To use this, you can take this source code if you like or, you can download and import the .jar file in
the root of the repo.

## The Package
The two most important classes are the `Compressor` and `Decompressor` classes. Both of these have static methods
to perform their function, but they also provide a non-static interface too.

### What is the Difference?
You may be wondering why there are two ways of using these classes and which one you should use. The answer is "it 
depends". The static methods do not produce errors and are thus much simpler when used in your code. If, however, you
want more contorl over your error handling, use the non-static methods.

Code to perform compression and decompression for both methods is shown below. There are many helpers in both classes,
check the source and the javadocs for more information.

## Usage
### Simple (Static Helpers)
Simple call `Compressor.compress()` or `Decompressor.decompress()`. That's it! Easy right? You do have to check your
own File inputs this way, though.

    ```java
    Compressor.compress(rootFolder, filesToCompress, outputFile, listener);
    Decompressor.decompress(compressedInput, uncompressedOutput, listener);
    ```

### Detailed
If you want more control over how errors are handled you can create Compressor or Decompressor objects yoruself
and handle the exceptions the constructors and instance methods produce.

#### Compression

    ```java
    // will throw error if input files are no good
    Compressor compresssor = new Compressor(directory, directory, output); 
    // will throw an error if an IOException occurs
    compressor.compress();
    ```

#### Decompression

    ```java
    // will throw error if input files are no good
    Decompressor decompresssor = new Decompressor(archive, outputDirectory);
    // will throw an error if an IOException occurs
    decompressor.decompress();
    ```

### Progress Listener
You can optionally specify a ProgressListener for each of the classes. Check the source and the javadoc for more info.
The example source folder also includes an example of how to use this.
