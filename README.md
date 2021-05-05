# AI Project 2

This algorithm creates an agent that plays the game CHESS-TUC using Minimax, Minimax with some extensions and Monte Carlo Tree Search.

## Installation 

Create a java project and add external jar _"yannis_chris_AI_Client.jar"_. <br />
In Eclipse:<br />
Right click on project --> Build Path --> Configure Build Path --> Libraries --> Add External JARs... -> Select _"AI_Ex6.jar"_ --> run executable as Java application

## Usage

You can run the server by executing command:
```java
java -jar tuc-chess-server.jar.jar
```
Running the agent, you can add 2 arguments, where the first is about the delay added and the second is  about which algorithm to use. <br />
You can run the agent with arguments:
- 0 for minimax without extensions
- 1 for minimax with alpha beta pruning
- 2 for minimax with forward pruning
- 3 for minimax with singular extensions
- 4 for Monte Carlo Tree Search 

E.g. running minimax with alpha beta pruning and delay of 100 microseconds
```java
java -jar yannis_chris_AI_Client.jar 100 1
```

The first player is assigned white, and the second black.
We believe running minimax with alpha beta pruning gives the best outcome.