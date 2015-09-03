Contributing to morpheus
=======================
<a href="#table-of-contents">back to top</a>

Contributions are most welcome! Don't forget to add your name and GitHub handle to the list of contributors.

<a id="git-flow">Using GitFlow</a>
==================================
<a href="#table-of-contents">back to top</a>

To contribute, simply submit a "Pull request" via GitHub.

We use GitFlow as a branching model and SemVer for versioning.

- When you submit a "Pull request" we require all changes to be squashed.
- We never merge more than one commit at a time. All the n commits on your feature branch must be squashed.
- We won't look at the pull request until Travis CI says the tests pass, make sure tests go well.

<a id="style-guidelines">Scala Style Guidelines</a>
===================================================
<a href="#table-of-contents">back to top</a>

In spirit, we follow the [Twitter Scala Style Guidelines](http://twitter.github.io/effectivescala/).
We will reject your pull request if it doesn't meet code standards, but we'll happily give you a hand to get it right. Morpheus is even using ScalaStyle to 
build, which means your build will also fail if your code doesn't comply with the style rules.

Some of the things that will make us seriously frown:

- Blocking when you don't have to. It just makes our eyes hurt when we see useless blocking.
- Testing should be thread safe and fully async, use ```ParallelTestExecution``` if you want to show off.
- Writing tests should use the pre-existing tools.
- Use the common patterns you already see here, we've done a lot of work to make it easy.
- Don't randomly import stuff. We are very big on alphabetized clean imports.
- Morpheus uses ScalaStyle during Travis CI runs to guarantee you are complying with our guidelines. Since breaking the rules will result in a failed build, 
please take the time to read through the guidelines beforehand.


