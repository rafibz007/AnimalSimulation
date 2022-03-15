# AnimalSimulation

This project is simple simulation of animals living in rough enviroment and their will to survive. <br/>
Project aim was to learn more about OOP and Java. <br/><br/>

Animals(dots) live in world(rectangle map) with only grass(light green square) as an energy source. 
Grass grow one per each of two biomes(Plains and Jungle in the middle).
Animals each day pick one action, to go forward, backward or rotate some angle eating grass when stepping on it and losing some energy every day.
Their chance of picking actions depends on their DNA which they gain from their parents.
Two strongest animals at tile have a baby, giving it their DNA and energy.
Animals with 0 energy die :( <br/><br/>

Two world types: DarwinWorld - nothing special, MagicWorld - when animals amount reaches 5 they get cloned

## Functionalities
  - Fully customizable
  - Program can simulate up to two worlds with different parameters.
  - When simulation stops, every animal with dominant gene can be highlighted
  - Animal can be selected when simulation paused, to track more detailed information about it and highlight during simulation
  - World data is projected on plot
  - World data can be save into CSV files when simulation is paused


## Video of usage
https://youtu.be/FSxAeO9tZYk


## How to use
You can run it simply by ./gradlew run. <br/>
Then chose how many simulations you want to use, customize them and enjoy :)
