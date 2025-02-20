# Welcome to Apache Joshua (Incubating)
<img src="https://s.apache.org/joshua_logo" align="right" width="300" />

Joshua is a statistical machine translation toolkit for both
phrase-based (new in version 6.0) and syntax-based decoding. It can be
run with pre-built language packs available for download, and can also
be used to build models for new language pairs. Among the many features of
Joshua are:

 * Support for both phrase-based and syntax-based decoding models
 * Translation of weighted input lattices
 * [Thrax](http://joshua.incubator.apache.org/6.0/thrax.html): a Hadoop-based, scalable grammar extractor
 * A [sparse feature architecture](http://cs.jhu.edu/~post/joshua-docs/md_sparse_features.html) supporting an arbitrary number of features

The latest release of Joshua is always linked to directly from the [Home Page](http://joshua.incubator.apache.org)

## New in 6.X

Joshua 6.X includes the following new features:

 * A fast phrase-based decoder with the ability to read [Moses](http://statmt.org/moses) phrase tables
 * Large speed improvements compared to the previous syntax-based decoder
 * Special input handling
 * A host of bugfixes and stability improvements

## Quick start

Joshua must be run with a Java JDK 1.8 minimum. Anything older than that will not run.

To run the decoder in any form requires setting a few basic environment
variables: `$JAVA_HOME`, `$JOSHUA`, and potentially `$MOSES`.

    export JAVA_HOME=/path/to/java  # maybe /usr/java/home
    export JOSHUA=/path/to/joshua

You might also find it helpful to set these:

    export LC_ALL=en_US.UTF-8
    export LANG=en_US.UTF-8

Then, compile Joshua by typing:

    cd $JOSHUA
    ant 

The basic method for invoking the decoder looks like this:

    cat SOURCE | $JOSHUA/bin/joshua-decoder -m MEM -c CONFIG OPTIONS > OUTPUT

Some example usage scenarios and scripts can be found in the [examples/](https://github.com/apache/incubator-joshua/tree/master/examples) directory.

## Working with "language packs"

Joshua includes a number of "language packs", which are pre-built models that
allow you to use the translation system as a black box, without worrying too
much about how machine translation works. You can browse the models available
for download on the [Joshua website](http://joshua.incubator.apache.org/language-packs/).

## Building new models

Joshua includes a pipeline script that allows you to build new models, provided
you have training data.  This pipeline can be run (more or less) by invoking a
single command, which handles data preparation, alignment, phrase-table or
grammar construction, and tuning of the model parameters. See [the documentation](http://joshua.incubator.apache.org/pipeline.html)
for a walkthrough and more information about the many available options.

# License
Joshua is licensed and released under the permissive [Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0), a copy of which ships with the Joshua source code.