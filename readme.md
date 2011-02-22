BigBrother
===========


Compilation
-----------

For compilation, you need :

* JDK 6 (Sun JDK or OpenJDK Highlight recommanded)
* Install [Maven 3](http://maven.apache.org/download.html)
* Check out and install [Bukkit](http://github.com/Bukkit/Bukkit) and [CrraftBukkit](http://github.com/Bukkit/CraftBukkit)
* Check out this repo 
* Get the Permissions plugin jar and run `mvn install:install-file -Dfile=Permissions.jar -DgroupId=org.bukkit -DartifactId=permissions -Dversion=2.0 -Dpackaging=jar`
* `mvn clean install`
