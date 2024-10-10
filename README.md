# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[My server diagram](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5xDAaTgALdvYoALIoAIye-Ezw-g7BKABMEZI+MYEhAMxJ3sx+AXEALFlRubEhAKyY9lAQAK7YMADEaMBUljAASij2SKoWckgQaI0A7r5IYGKIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0NQZQADpoAN4ARGeUzQC2KLcrtzC3ADSfuOrD0A4bw+30+KCewCQCGBnwAvphhMsYPNWOwuJQ1ncHlBnq93p8frc-qoAVAgfjQbdwZDoRT4WxONxYCjEaI1lAuj0JlAABSdbq9SidACONTUYAAlAilqJkQtZPIlCp1CtAmAAKrnHnY3FShWKZRqVRyow6FYAMSQnBgmso+pgOjaOuAL0w+qVRuRLJlKjWaBqCAQ0pEKhN7sNKpAHLkKFtvOx+r12g96hNxhWCg4HBt531waospR4eVqhWUZQMYUNTAvh5wGrvkTbuTEeNKPTmezVZredZoZmKIZ6NWHU5gqgnVUAawQ6ZXoWiIxMCx51xHzWIMJ9ZrewgAGt0OvPnD85R5-BkOY1vEnE4rivHi7XjANwTPtvfLuD2gjyD4egOFMKpanqBpoDyGAABkIG6IYGlGcZJkvWYFyWJctl2Q4TgMdQBjQe9nReH5iVJDh4UXZkFlnDEHxxJ9KRIwFbnpNE529EMUBWBAYKtHloNgkUxV6KU+0MIsWxLVUUA1LVCJQJNFVbNMzUta043tR0YDk-NC3lCSjTWbjYJ5dTtBEn0xMHViMW7RttEwajmXY5YVmuT45OBSkPy-Q93g+ci0KchYpivG87xudzV3ovytwbHyfz8mB-04IDqjqRoqhQdAoJg2pmHgsYJkwEKUMWah0OkABRSDKr2SqjmOHDVDw+9bm8-dfM+fzT0o1FGSXO52u-N4uqShzrN60SuNy6s+JmsBBPFcyOPmYsDI4FBuAmWyeVsps1vUeZ0xkTbpMMWzzEIfpBhkezRIHOZHLWfi8snadxv6qAHoozEAvKr75hKsBr1ve94TMFLKjS0COWzSCuRgABxJ9jQKxDiuQ5hnPQxHaoa+wnyuIb0B6+YnuXNq4o6hLbmYj7h3mKbkF6ZGXlUXaGybe65gO0tmbAVm1A5nszObRSSyOs0KG4cAYGAQMYAJtmdJUB6nvhlmUbehAZwm77Atcz4lbUEb1juY2AElpBG0J4nSfJCQQiY4zXGKrluHQEFAPcXei2nCWNgA5P3mJgA4-uWB6gZWML7yNlHTfNp8rZtu2Hc+J3Yyil5PK+d3Pe932c5i+OXmD4vadhMPksAqGQMabAaigbBuHgaMJiRp8RkKpDpix1D-rWDD9nxruiap78YHvIOnwjygyYm1zKZ3anc9uGeK7pxzGYsst25QQXhbs+Qfg3+SVZQVb9MjffD727RT6fcvz95yW1k7RWnzFg0JZ3jjVWTtIC+atF6WyAdvP+Lkk4vBTisW29s54AzmNHWOEV16ANTvbMaAFUr1waJYTa3FhgwAAFIQCtJ3F4jQC4gD3BjPuJofobE2OqLCxxjbjxXt+e8LdgAEKgHACA3EoCPxgdIRBC9PqG2Xp+VeMUiRe34YI4RwIADqLALZ1WOAAIUggoOAABpb46CxFvHgfkFin1IFsgAFbkLQHfTmD8YC8KUUI6AoiUBW2WgWVWPNr6ljVI4kWJ9P5iIUj-I0b9RwWFQDQShYhX7WN9MbYB-jxYGTqFoCYwTj7AE8cRRRlBlHQAiSmVQ0T1TYGyYYY2Rg7oWRAVIsB9MmT60Hm5BRfDinuKgGojRWjdH6KMQHDBaxzFjQolHTGMdQZDE6a4npKj3jqM0ZVHRejDGUjAZg-I2DIbAXSg0KofCLwVlgMAbALcroEBumjIqQNGEGw2FVGqdUGrGFJo9Re1xLEMzmFNEA3A8A8h8aIK+GTIzAt5GU1sb9pCnQ7vLBA9T5DAMkcOVyfy2mQIGhIwGMzUG-IhoBIAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

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
