Changelog
=========

<a id="version-history">Version history</a>
===========================================

<ul>
    <li><a href="#version-0.2.2">0.2.2 - 04.09.2015</a></li>
</ul>


<a id="version-0.2.2">0.2.2</a>
===============================

- Bumped `outworkers-util` dependency to `0.9.11`.
- Bumped `scoverage` version to `1.3.1`.
- Added the new scoverage resolver to `plugins.sbt`.
- Settings MySQL password in Travis CI manually. Authentication without password doesn't work.
- Bumped `finagle-mysql` version to `6.28.0`.
- Updated travis script to include `sbt-coveralls` execution.
- Updated Travis configuration to include caching the build directory and account for Scala versions.