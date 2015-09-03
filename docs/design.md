
<a id="building-blocks">Building blocks</a>
=====================================

This section outlines the inner building blocks of Morpheus and tries to explain how a database is implemented in the Morpheus DSL, 
composed from various building blocks. The list of blocks is rather long and each block aims to isolate and deal with a very specific concern. The goal here 
is to allow existing and new databases to easily define custom behaviour at all levels, let it be syntax, serialisation methods and so forth.

The implementation done via the building block architecture also allows us to maintain the single import "all you can eat" buffet while invisibly enabling 
and disabling features, options and available methods with the help of a single import. There is a vast series of architecture choices that are combined and 
designed specifically for the purpose of allowing very simple usage. 

The building blocks of an SQL implementation are:

- SQL Syntax
- SQL Keys
- SQL Data Types
- SQL Primitives
- SQL Engines
- SQL QueryBuilder
- SQL Operator Set
- SQL Create Block
- SQL Insert Block
- SQL Delete Block
- SQL Update Block


Each new database, implemented as a different SBT sub-module, can use the full query power pre-built into the ```morpheus-dsl``` root module while overriding
 implementation specifics to do with any of the above. Each new database must define it's own set of features for every one of the building blocks. At the 
 same time, common functionality and a shared trunk is made possible by ```morpheus-dsl```.