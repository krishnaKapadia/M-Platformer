# M-Platformer

<p> A platformer implemented using the processing library thats based of the game, N the way of a ninja http://www.thewayoftheninja.org/nv2.html </p>

<h2>The concept</h2>
<p>The aim of the game is to get through as many levels as possible within the given time. High Scores will be based on the fastest time taken to complete all the levels. The game is a basic platformer with physics based movement. Each level will get more and more difficult and more time consuming. Each level will be created by passing in an image file with the level laid out in a 1280 x 800 size, the game will then generate all the game objects locations from that image, therefore creating a level. This will mean that I will have to handle the collisions generally in terms of each game object. This also means that I can theoretically have 100’s of levels without having to coding each one individually. Once the player spawns they must move to the key which will unlock the gate to the portal. Then the player must get to the portal to advance to the next level. Throughout the level there will be coins that the player can get which will increase the time remaining by a small amount. Through out the levels there will be hazards such as mines and enemies that will slow down the player.</p>


<h2>The levels</h2>
<p> Each level is created from an image. The level creator crawls the image in 16x16 pixel blocks and based upon the centre pixel's rgb value, the game would place a object at that location such as walls, doors, mines, keys, turrets etc. This method proved valid and worked without issue, however in future I would allow levels to become scaleable as opposed to being fixed at a native 1280 x 800 resolution. The problem with this was that the collisions became a lot more complex as now I was checking if the player was colliding with a multitude of little blocks as opposed to 1 big wall etc. This created many issues such as the player seeming not to be able to move between each tile and jump collisions not working properly.</p>

<h2>Collision</h2>
<p>The collision system is based off grouping individual tiles by their location and basing the collision checks off the players location. In practise, each wall was added to a spesific list based upon its location in relation to other wall/platform tiles. This was done through a simple iteration over each blocks positions. For example a block would be considered the ground of there was no Wall object above its location on the grid etc. This grouping allowed for more spesified collision detection and therefore dramatic performance improvements as the system did not have to check over tiles that the player was far away from in relation to their grid locations.</p>

<h2>Textures</h2>
<p>The game originally had no textures and was instead using basic squares filled with solid colors. However I later decided that adding textures would make the game more interesting. In order to do this all that I had to do was interate over all the tiles that were the ground and give them a seperate texture and repeat this will all the other tiles. This would save me alot of time in comparision to doing it manually for each tiles location. This also meant that the system would be scalable and vastly more efficient. As the ground tiles were already grouped by location for the collisions this was very easy.</p>

<h2>Animations</h2>
<p>The animations in the game such as the portals were done using an array of PImages and iterating through them based on the current frame. If I was to do this project again I would go for a more complex design as my current games sprites were very minimalistic as by design. Also adding more mechanics such as wall jumping, a wider range of enemies and also more animations. I did meet all the requirements of my plan and created a time based platforming game.</p>

<h2>Overall</h2>
<p>On reflection I would have liked to create a more complete game with also a ghost player that would show the players previous attempt at the level, this would have added more complexity to my game while also adding another level of depth. Overall I think I did a good job and would be happy to attempt a project like this again.</p>
