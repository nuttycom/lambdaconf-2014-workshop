# Complexity and Reasoning

Before the workshop begins, please take the time to do the following:

```

./bin/sbt test

```

This will download all of the required dependencies you'll need, including
the SBT launcher, the Scala compiler, and so forth, and will ensure that
your system is set up to be able to do the exercises.

When you run the tests, you should see the following:

```
[info] Set current project to asdf (in build file:/Users/kris/personal/lambdaconf-2014-workshop/)
[info] Updating {file:/Users/kris/personal/lambdaconf-2014-workshop/}lambdaconf-2014-workshop...
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Compiling 4 Scala sources to /Users/kris/personal/lambdaconf-2014-workshop/target/scala-2.10/classes...
[warn] there were 2 feature warning(s); re-run with -feature for details
[warn] one warning found
[info] Compiling 1 Scala source to /Users/kris/personal/lambdaconf-2014-workshop/target/scala-2.10/test-classes...
[info] PureASDFDBSpec
[info]
[info] pure transformations on a value should
[info] x insert values at nested paths
[error]    an implementation is missing (PureASDFDB.scala:29)
[info]
[info]
[info] Total for specification PureASDFDBSpec
[info] Finished in 18 ms
[info] 1 example, 1 failure, 0 error
[error] Failed: Total 1, Failed 1, Errors 0, Passed 0
[error] Failed tests:
[error]         asdf.PureASDFDBSpec
[error] (test:test) sbt.TestsFailedException: Tests unsuccessful
[error] Total time: 10 s, completed Apr 18, 2014 10:59:30 AM
```
