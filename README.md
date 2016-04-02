# SpleefLeague Core

The core plugin of [SpleefLeague](https://swc.cubecraft.net/), which handles the general data of the server, and provides an API for the other plugins to build on.

This project is required for the other plugins to compile and run.

## Compilation

SpleefLeague uses  [PaperSpigot](https://github.com/PaperMC/Paper), which means that **[Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)** is required. For the dependency handling we use [Maven](https://maven.apache.org/download.cgi).

Because some dependencies require an older version of Spigot the first thing you have to do is download and run [BuildTools](https://hub.spigotmc.org/jenkins/job/BuildTools/).

```shell
mkdir buildtools && cd buildtools
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
java -jar BuildTools.jar --rev 1.8.7
```

This will install spigot and spigot-api into your local maven repository. After this we need to also get [PaperSpigot](https://github.com/PaperMC/Paper) on the 1.8 branch, and install that as well.

```shell
git clone --branch ver/1.8.8 https://github.com/PaperMC/Paper.git
cd Paper
./build.sh --jar
```

Right now both Spigot 1.8.7 and PaperSpigot 1.8.8 should be installed. Now you can clone the SpleefLeague core and compile it.

```shell
git clone https://github.com/SpleefLeague/Core.git
cd Core
mvn package
```

## Troubleshooting

If the dependencies for the core aren't resolving properly, check whether your `JAVA_HOME` has been set to the Java 8 JDK properly. You can see the version maven is using by running `mvn -V`. If your `JAVA_HOME` wasn't properly set to Java 8 before try re-running `./build.sh --jar`. If any problems persist with the PaperSpigot dependency try manually running `mvn install` in the project root, or seek help by creating a ticket or contacting a developer.

## Contributing

To contribute code feel free to create pull requests, which a developer will then look over as soon as they have time. Please follow the general format of the project, and the same style indentation, and prevent duplicate code. Sometimes a developer will ask you to fix some parts of the code, which you're able to also add to the pull request, and as soon as the code is decent enough we'll merge the code and update it usually within the week of merging.

## License

See [here](https://github.com/SpleefLeague/Core/blob/master/LICENSE).
