morpheus [NOT PRODUCTION READY][![Build Status](https://travis-ci.org/websudos/morpheus.svg)](https://travis-ci.org/websudos/morpheus)[![Coverage Status](https://coveralls.io/repos/websudosuk/morpheus/badge.png?branch=develop)](https://coveralls.io/r/websudosuk/morpheus?branch=develop)

========================================================================================================================================================================================================================

![morpheus](https://s3-eu-west-1.amazonaws.com/websudos/oss/logos/morpheus.png "Websudos Morpheus")

To stay up-to-date with our latest releases and news, follow us on Twitter: [@websudos](https://twitter.com/websudos).

Named after the Greek God of Dreams, morpheus is a reactive type-safe Scala DSL for MySQL, Postgres, MSSQL,
MariaDB, Oracle and Sybase. We choose this name as it is the dream DSL for any Scala/SQL user, finessed to perfection up to the Websudos quality standard
you've gotten used to. Also, morpheus stands for morphing. With the single switch of an import, morpheus will perform a full feature swap from MySQL to
OracleSQL for example. It doesn't have a query compiler, instead it entirely mimics the functionality the database has.

We've taken it up ourselves to produce the highest quality database integration tooling for all Scala users, 
currently hitting that bar for Cassandra, Neo4J and MongoDB. But why stop there?


Using morpheus
==============

The current version is: ```val morpheusVersion = 0.1.0```.
Morpheus is published to the Websudos Maven repository at ```http://maven.websudos.co.uk/ext-release-local``` and it's actively and avidly developed. It is 
not yet production ready.

<a id="table-of-contents">Table of contents</a>
===============================================
<ol>
  <li><a href="#design-philosophy">Design philosophy</a></li>
  <li><a href="#integrating-morpheus">Integrating Morpheus</a></li>
  <li>
    <p>Supported databases and documentation</p>
    <ul>
      <li><a href="./docs/MySQL.md">MySQL</a></li>
      <li><a href="./docs/MySQL.md">MariaDB</a></li>
      <li><a href="./docs/Postgres.md">Postgres</a></li>
      <li><a href="./docs/Oracle.md">Oracle(Morpheus Enterprise)</a></li>
      <li><a href="./docs/MSSQL.md">MS SQL(Morpheus Enterprise)</a></li>
  </li>
  <li><a href="#copyright">Copyright</a></li>
</ol>


<a id="design-philosophy">Design philosophy</a>
=====================================

You're probably wondering how Morpheus fairs compared to the more established players in the Scala SQL market and why we set out to do something new in the 
first place. To sum it up, we believe Slick is an excellent tool but we do not believe you should learn about our abstractions to get things done. A DSL 
should auto-magically encode the same syntax and the logic as the tool it's designed for.

Instead of learning about primitives and rules we thought of to abstract away discrepancies between the various SQL implementations, 
Morpheus features a unique approach, what we call the auto-magical flip. Although at this point in time only MySQL is supported, 
Morpheus is designed to give you an "all-you-can-eat" buffet through a single import.

As follows: ```import com.websudos.morpheus.mysql._```.

And done, you can now define tables, query and so on. Say you have something like this:

```Recipes.select.distinctRow.where(_.name eqs "test")```. ```DISTINCTROW``` doesn't exist in the Postgres ```SELECT``` statement syntax, 
but it's a standard thing as far as MySQL is concerned.

Here's how Morpheus operates:

If you change the top level import to: ```com.websudos.morpheus.postgres._``` and you try to compile the same ```distinctRow``` query. But there 
will be none. The method will simply not exist. Morpheus has now auto-magically performed a full feature swap, 
changed communication protocol and all underlying settings, and all you get now is Postgres features.

How? Quite a lot of fun magic under the hood, have a look throughout our decently documented codebase for more information. The beauty of it is that you 
don't have to care. Slick makes it easy to move from one SQL database to the other with less code changes, but if you're well set on a database you already 
know and love, it may be counter productive to have to learn about a framework when you could use Morpheus and all you need is IDE auto-completes to get 
lightning fast development productivity. 

Oh, and did we mention it's entirely asynchronous and reactive? No JDBC.


<a id="integrating-morpheus">Integrating Morpheus</a>
======================================================
<a href="#table-of-contents">back to top</a>

Morpheus is designed to give you an all-you-can eat buffet through a single import, so all you really have to do is to pick the module corresponding to the 
database you want to use. At this point in time only MySQL is supported.

If you are using MySQL, you would simply use the following:

```scala
libraryDependencies ++= Seq(
  "com.websudos"  %% "morpheus-mysql"                % morpheusVersion
)
```

And then you can: ```import com.websudos.morpheus.mysql._```, which will give you the full set of MySQL methods and features without any overlaps or 
unsupported operations. Morpheus guarantees you can almost never write an invalid SQL query unless you try really really hard.


### Available modules ###

The full list of available modules is:

```scala
libraryDependencies ++= Seq(
  "com.websudos"  %% "morpheus-dsl"                  % morpheusVersion,
  "com.websudos"  %% "morpheus-mysql"                % morpheusVersion,
  "com.websudos"  %% "morpheus-postgres"             % morpheusVersion
)
```

<a id="contributors">Contributors</a>
=====================================
<a href="#table-of-contents">back to top</a>

Morpheus was developed at websudos as the foundation of our upcoming book, "Learning Scala by example", which covers all aspects of building an enterprise 
grade Scala framework from scratch.

* Flavian Alexandru @alexflav23
* Benjamin Edwards @benjumanji

<a id="copyright">Copyright</a>
===============================
<a href="#table-of-contents">back to top</a>

Copyright (c) 2015 websudos.