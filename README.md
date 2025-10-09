# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Server
[Sequence Diagram](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWOZVYSnfoccKQCLAwwAIIgQKAM4TMAE0HAARsAkoYMhZkzowUAJ4TcRNAHMYARk2ZjUCAFdsAYgAsAZgAcAJgCcIGA+TGABZgCgi2qgBKKMZIEjqCSBBomIioGAC0AHwU1JQAXADaAAoA8mQAKgC6MAD0tipQADpoAN71lGjAALYoADQwuFIA7tAy-ShdwEgIAL6YClAowADWMIsAjuFxMLEwaBDMVMDIMpg5NLDpWanaeU4ADPfNAELAMjBRm5JY6KfnlFlzuw8osYnFKAAKdpQTo9fqDCQjKBjGATKYIACUZ1Y7CyAiEonEUjyxhQYAAqg0oQ1YX0BspEaMsfiRGJJBIsuo8gAxJBod6UygsmAKXQwaG0mDQVGTabzRYrGDPWwyGRi3R2GDHBVqmCBYA0LXihowIZIMCBGAWwQ7NAAMwgVv1zCmpGMSU6pugy0wLMJ7Ku2Rx4jygqgLOx1FxgNY+RgwgQOt05RW6AAogAPcTYAhJSMXQM3DB3e4uZoJpMwFPLdBqYBDYxafm++T+qSBoEhtC2BAIfO4zJ+tnEkAKsAoMMQsMs5mt4cSGBZNQKPLCVUwafyfviPFzokSPKjpbj4S2C0Q08WmctgnzxeZZer9eXwIRzsoGO5KCrs+vrf-KBrmQW4PEeZo2hpbo6WAX9yggGs0DmX5LGsOxHGgXhST8bk4DTPg4GEGAABkIBiZIi2YJcALyGBijKKpahUKREjQcCJSg+EGSRGQ5gA+8WCjEMYAQUi+WpDoOPpYYmW3VQlyHfcaNJCkqXYuEpMZZFZ1vfd+K5GBeX5DcGmFUVjQknp5SWVY1NUfYXSkCAiEEFB3jNC1zKgG9WV0wMBLYITu17WTFyo2Nv2MygADkDm5Ox+UzbNc2SPi-IomiHieNBXneT4th+ZsFPZUKH3fGjNyEKzFSKLjRjUCBJD2A4YEmMAQEtGIaDQTzaRCvzqI3Tpf2gJAAC9XMSlAcxY-MASXdKYAeMxmnJIaLRG8bTmQ99dx09lDzHFAXwvX9ryK9tMi5Nd3hfN9gw-T8Lho27-3CwtgOLUCsogizoNg+D0CQ5tNFQ+wHGsFBa38JAgjACHaxI90zxSD7KNK8KaIKPg0yItNyjTaoaiYiQWOaGCLTghDZsudHBJQGiRKRsAIXJwJKfQLF33486DxgZTjtZ9m0G0nyA0uldDJu075BFMVBYB5IFmspqXRgI4Ti1f6qZ5-r7pooK+x2sKv3KtbAg2iasym5LqfetIwBLZbuzNi2tsKvcA0e4EXsqnbBw94lFi6CAaGOn3gBFtsOXFvIomDw1w9kjsMYN22gPtvIvvAoHTisGwwcWd4HGwPlViI2JmAAcSg6QKP4gaCkrvHCeMKCya19BbaDOmSTJauegkFmO+F7yo85CW+Slq8ZbM+WqaVxV7KNdWkHeOfO5142AvpvYe0N+7dZNwbWddybprzAC7duJbmlsF2oDG1ym1OTfu+3n9p99g--b24llP7yQEJI7znHjySeWpewwAAcKCACgABWKBwDSFfmVEAEBeyIOSlKO0UCoIwPgZg6QRs375DQRg8ALFsG4J6PghBSC06ZHSlnVorcB45xBvnRwMgADsXh7goHuH4NMLg4C4QAGzwEOtQ1QKN7b1wxrRUoFQW5tzNkLcCrCUBRSgnMBeqwNj5R2AuJeK8-jhRKulTKLw3gfBQF8OIz8u6oMOgAoeFMFb9E0dononN7rcwDrzfmv43FswVsA-coDJbxmlkIWWmt3HzwVKsJewA1bHFXvE0J2sAmH3frvYKxD-JxlWifB+m0z420vuna+9wnZ31KY-N2L8clexDInP2PMaJHhcq46B8hwliyuuuPpX86bJxNpogAknwBhTCwIsKgtM9hec0IOF0CgXsEAhh+ACMEdZmztkACkIB8mkX4UIoAfR1y3nGYo5IGI1E0e3BJ6BwLYAQMAdZUA4DoOgJ4xZfBeJvTfsCOBJy0CuPXmgXo7zPmUB+SJWAvQpkyF8XTfxv9AlkgFsPAZF0uRRPDnEqFVVknNVSaYzJQtR4gK3sCfJ+8xk3IiiU4aZTLZJRmlUxhqNHa33vo0xxKC9btO-p0-+UEIRTL4Hi6OBLwEjNSWZaVpK+YAqMSrW0dYhBKBUDSiJrT6ap2IQNABMUwBxTvjICpXLzFX2LFY7KNi8rfCFS0kFIZFWqqKB8r5MAJAiWYLsbUSxdRCCpiajGJTQgoDgscvkNqL5vWqQ60sZNEyhrFOGzu20xUBLyPYOQ45XHSv+TQ-p+rBkrj4BsskqhFVxJVTrGO11pF3SZSQ78xrk08ozswloyzQaOGsJ8nZMNggjrFIgJYsBgDYHeYQBISQYCyO0PIo+WMcZ4wJrUdQ1N115JAAgGdQC+oPk6TAI9J7ZV6RXPAY9eAYAsl1RwZt-l6XdvRcy-We993zV5TAft7CgA)

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
