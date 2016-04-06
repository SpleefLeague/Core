# SpleefLeague Core

The core plugin of [SpleefLeague](https://swc.cubecraft.net/), which handles the general data of the server, and provides an API for the other plugins to build on.

This project is required for the other plugins to compile and run.

## Compilation

SpleefLeague uses [Spigot 1.8.8](https://spigotmc.org). For the dependency handling we use [Maven](https://maven.apache.org/download.cgi).

First make sure you have Spigot installed in your local maven repository. To do this download and run  [BuildTools](https://hub.spigotmc.org/jenkins/job/BuildTools/).

```shell
mkdir buildtools && cd buildtools
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
java -jar BuildTools.jar --rev 1.8.8
```

This will install spigot and spigot-api into your local maven repository.
Right now Spigot 1.8.8 should be installed. Now you can clone the SpleefLeague core and compile it.

```shell
git clone https://github.com/SpleefLeague/Core.git
cd Core
mvn package
```

## Troubleshooting

If you have any issues seek help by creating an issue or contacting a developer.

## Contributing

To contribute code feel free to create pull requests, which a developer will then look over as soon as they have time. Please follow the general format of the project, and the same style indentation, and prevent duplicate code. Sometimes a developer will ask you to fix some parts of the code, which you're able to also add to the pull request, and as soon as the code is decent enough we'll merge the code and update it usually within the week of merging.

## License

See [here](https://github.com/SpleefLeague/Core/blob/master/LICENSE).
