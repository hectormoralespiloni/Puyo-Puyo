Puyo-puyo demo (Sep 2005)
-------------------------

![](https://github.com/hectormoralespiloni/Puyo-Puyo/blob/master/puyo_full.jpg)

1. SUMMARY 
	This is a demo of the well known puyo-puyo game made with Java.

2. REQUIREMENTS TO RUN THE DEMO
	* Java 2 Runtime Environment
	
3. HOW TO PLAY
	* If you have J2RE installed on your system, you should be able to click 
	the puyo.jar to start playing
	* Left / Right arrows => move puyos to the left / right
	* A => rotates puyos counterclockwise
	* S => rotates puyos clockwise
	
4. HOW TO COMPILE
	* The easiest way to go is download the Netbeans IDE from: netbeans.org
	There's already an nbproject folder for netbeans you just have to 
	select the puyo folder in netbeans to open it.

5. CODE STURCTURE
	* The images folder contains all the png used in the game.
	* There are 4 classes: 
	    * Sprite.java: 	this class manages the basic sprite stuff such as get and set
			its position, collision detection and drawing the sprite.
	    * Puyo.java: 	this class inherits from sprite and adds functionality to move
			and rotate the puyos as well as handling collision detection.
	    * Board.java:	this is the main threaded (implements runnable) canvas and it
			represents the game board (so to say) because it keeps track 
			of the position of the puyos which have reahced their final
			destination.
	    * GameApp.java: this is the main entry point of the demo, it creates a new window	
			attach a board and starts the main thread of the game.