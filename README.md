# Simple Tar-GZ Compression
Simple and easy to use library to compress and decompress files in the .tar.gz format.

## Usage
Compression and decompression are as simple as creating an instance of a compressor or a decompressor and calling `.compress()` or `.decompress()`. Below are some example pieces of code.

### Compression

    Compressor compresssor = new Compressor(directory, directory, output);
    compressor.compress();

### Decompression

    Decompressor decompresssor = new Decompressor(archive, outputDirectory);
    decompressor.decompress();
