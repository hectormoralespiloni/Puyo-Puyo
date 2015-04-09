/*-----------------------------------------
    Board Class for puyo demo
 
    Author: Héctor Morales Piloni, MSc.
	    http://www.piloni.net
    Date:   October 2, 2005
------------------------------------------*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.Random;

class Board extends Canvas implements Runnable
{
    //canvas max size
    final static int WIDTH = 6;
    final static int HEIGHT = 12;
   
    //max number of puyos on board
    final static int MAX_PUYOS = WIDTH*HEIGHT;
    
    //bi-dimensional array which represents this board
    //it contains the index of some puyo stalled in it
    public static int board[][];
   
    private boolean playing;
    private int cicle;
    private int speed;
    private int puyos_falling;
    private int combo_counter;
    private Thread animator;
    private Puyo puyos[] = new Puyo[MAX_PUYOS];
    
    public Board()
    {
	board = new int[WIDTH+1][HEIGHT+1];
	
	//set window size
	setBounds(0, 0, WIDTH*Puyo.WIDTH, HEIGHT*Puyo.HEIGHT);
	setBackground(Color.WHITE);

	//show window and start game
	setVisible(true);
	startGame();	
    }
    
    public void startGame()
    {
	if(animator == null && !playing)
	{
	    animator = new Thread(this);
	    animator.start();
	}
    }
    
    public void stopGame() {
	playing = false;
    }
    
    public void init()
    {
	int i,j;
		
	//init board positions
	for(i=1; i<=WIDTH; i++)
	    for(j=1; j<=HEIGHT; j++)
		board[i][j] = -1;
	
	for(i=0; i<MAX_PUYOS; i++)
	{
	    puyos[i] = new Puyo(1);
	    puyos[i].off();
	}
	
	playing = true;
	cicle = 1;
	speed = 10;
	puyos_falling = 0;
	combo_counter = 0;
    }
    
    public void puyoUpdate()
    {
	int i,j;
	Random rand = new Random();
	
	//used to handle illegal moves and to know what puyos 
	//must be rolled back
	int index[] = {-1,-1};
	int count = 0;
	
	//update puyos every X cicles
	if(cicle%speed == 0)
	{
	    //look for 2 free slots
	    if(puyos_falling == 0)
	    {
		for(i=0; i<MAX_PUYOS; i++) 
		{
		    if(!puyos[i].isActive()) 
		    {
			puyos_falling++;
			puyos[i].on();
			puyos[i].visited(false);
			puyos[i].setState(Puyo.FALLING);
			puyos[i].setX(1);

			//pick a number between 0 and 3 (random color)
			int color = java.lang.Math.abs(rand.nextInt()%4);
			switch(color)  {
			case Puyo.BLUE:
			    puyos[i].setColor(Puyo.BLUE);
			    puyos[i].addFrame(1,"images/puyo_blue.png");
			    break;
			case Puyo.RED:
			    puyos[i].setColor(Puyo.RED);
			    puyos[i].addFrame(1,"images/puyo_red.png");
			    break;
			case Puyo.YELLOW:
			    puyos[i].setColor(Puyo.YELLOW);
			    puyos[i].addFrame(1,"images/puyo_yellow.png");
			    break;
			 case Puyo.GREEN:
			    puyos[i].setColor(Puyo.GREEN);
			    puyos[i].addFrame(1,"images/puyo_green.png");
			    break;
			}
	    
			if(puyos_falling == 1)
			{
			    puyos[i].setY(0);
			    puyos[i].setPos(Puyo.NONE);
			    puyos[i].setRotable(false);
			}
			else if(puyos_falling == 2)
			{
			    puyos[i].setY(-1);
			    puyos[i].setPos(Puyo.TOP);
			    puyos[i].setRotable(true);
			    break;
			}
		    }
		}//for
	    }

	    //move puyos
	    for(i=0,j=0; i<MAX_PUYOS; i++)
	    {
		if(puyos[i].isActive() && 
		  (puyos[i].getState() == Puyo.FALLING ||
		   puyos[i].getState() == Puyo.FALLING_AUTO))
		{
		    //store all falling puyos
		    index[j++] = i;
		    if(puyos[i].moveDown() < 0)
			count++;
		}
	    }
	    
	    //when a puyo has reached the bottom or another STALLED 
	    //puyo, we have an illegal move... rollback.
	    if(count == 2)
	    {
		//two puyos made illegal moves (aligned horizontally)
		rollBackMove(index[0],index[1]);
		checkExplosions(index[0]);
		checkExplosions(index[1]);
		checkCombos();
	    }
	    else if(count == 1)
	    {
		if(index[0]>=0 && index[1]>=0)
		{
		    if(puyos[index[0]].getY() == puyos[index[1]].getY())
		    {
			//Case #1: 2 puyos aligned horizontally
			//1 puyo blocked and the other keeps falling
			i = puyos[index[0]].getX();
			j = puyos[index[0]].getY();
			
			if(board[i][j] >= 0)
			{
			    rollBackMove(index[0], -1);
			    puyos[index[1]].setState(Puyo.FALLING_AUTO);
			    puyos[index[1]].setRotable(false);
			}
			else
			{
			    rollBackMove(index[1], -1);
			    puyos[index[0]].setState(Puyo.FALLING_AUTO);
			    puyos[index[0]].setRotable(false);
			}
			
			checkExplosions(index[0]);
			checkExplosions(index[1]);
			checkCombos();
		    }
		    else
		    {
			//Case #2: 2 puyos aligned vertically, one has
			//an illegal position, the other does not
			rollBackMove(index[0],index[1]);
			checkExplosions(index[0]);
			checkExplosions(index[1]);
			checkCombos();
		    }
		}
		else
		{
		    //Case #3: 1 puyo falling alone
		    rollBackMove(index[0], -1);
		    checkExplosions(index[0]);
		    checkCombos();
		}
	    }
	    //else do nothing... legal move
	    
	}//if cicle%speed
    }
    
    /*------------------------------------
     When we have an illegal move like
     going out of boundaries, we must 
     rollback to the previous position
     ------------------------------------*/
    public void rollBackMove(int i1, int i2)
    {
	if(i1 >= 0)
	{
	    puyos[i1].moveUp();
	    puyos[i1].setState(Puyo.STALLED);
	    board[puyos[i1].getX()][puyos[i1].getY()] = i1;
	    puyos_falling--;
	}
	
	if(i2 >= 0)
	{
	    puyos[i2].moveUp();
	    puyos[i2].setState(Puyo.STALLED);
	    board[puyos[i2].getX()][puyos[i2].getY()] = i2;
	    puyos_falling--;
	}	
    }
    
    /*-------------------------------------
     This function visits recursively
     every adjacent puyo to the one passed
     If we have 4 or more of the same color
     they are exploded
     -------------------------------------*/
    public void puyoExplode(int index, boolean delete)
    {
	int row, col;
	int color;
	int nextColor, nextIndex;
	
	//it should never get here
	if(index < 0)
	    return;
	
	combo_counter++;
	col = puyos[index].getX();
	row = puyos[index].getY();
	color = puyos[index].getColor();
	puyos[index].visited(true);
	nextIndex = nextColor = -1;
	
	//visit adjacent nodes
	//LEFT node
	if(col-1 >= 1)
	{
	    //get index and color of adjacent node
	    nextIndex = board[col-1][row];
	    if(nextIndex >= 0)
	    {
		nextColor = puyos[nextIndex].getColor();
	    
		//check if adjacent node has the same color and it hasn't
		//been visited previously
		if(color == nextColor && !puyos[nextIndex].isVisited())
		    puyoExplode(nextIndex, delete);
	    }
	}
	//RIGHT node
	if(col+1 <= WIDTH)
	{
	    nextIndex = board[col+1][row];
	    if(nextIndex >= 0)
	    {
		nextColor = puyos[nextIndex].getColor();
		if(color == nextColor && !puyos[nextIndex].isVisited())
		    puyoExplode(nextIndex, delete);	 
	    }
	}
	//UP node
	if(row-1 >= 1)
	{
	    nextIndex = board[col][row-1];
	    if(nextIndex >= 0)
	    {
		nextColor = puyos[nextIndex].getColor();
		if(color == nextColor && !puyos[nextIndex].isVisited())
		    puyoExplode(nextIndex, delete);
	    }
	}
	//BOTTOM node
	if(row+1 <= HEIGHT)
	{
	    nextIndex = board[col][row+1];
	    if(nextIndex >= 0)
	    {
		nextColor = puyos[nextIndex].getColor();
		if(color == nextColor && !puyos[nextIndex].isVisited())
		    puyoExplode(nextIndex, delete);	
	    }
	}
	
	puyos[index].visited(false);
	
	if(delete)
	{
	    puyos[index].off();
	    board[puyos[index].getX()][puyos[index].getY()] = -1;
	}
    }
    
    /*--------------------------------
     For a given puyo index check any
     adjacent nodes of the same color
     to explode, return true when a 
     explosion occurred
     ---------------------------------*/
    public boolean checkExplosions(int index)
    {
	combo_counter = 0;
	
	//count how many adjacent nodes of the same color
	puyoExplode(index, false);
	
	//4 or more adjacent puyos? delete them!
	if(combo_counter >= 4)
	{
	    puyoExplode(index, true);
	    repaint();
	    arrangeBoard();
	    return true;
	}
	
	return false;
    }
    
    /*--------------------------------
     Traverse board and check for 
     combo explosions produces by 
     re-arranging the board
     --------------------------------*/
    public void checkCombos()
    {
	int col,row;
	boolean cleared = false;
	
	while(!cleared)
	{
	    for(row=1; row<=HEIGHT; row++) 
	    {
		for(col=1; col<=WIDTH; col++) 
		{
		    if(board[col][row] >= 0) 
		    {
			if(checkExplosions(board[col][row])) {
			    //used to break both for loops and start over
			    row = col = WIDTH*HEIGHT;
			    cleared = false;
			}
			else
			    cleared = true;
		    }
		}
	    }
	}//while
    }
    
    /*--------------------------------
     arranges the board after a puyo
     explosion, pulling down every puyo
     which remains floating...
     --------------------------------*/
    public void arrangeBoard()
    {
	int i,j,k;
	int count;

	try{
	    Thread.sleep(200);
	}
	catch(InterruptedException ex) {
	    System.out.println(ex.toString());
	}
	
	for(i=1; i<=WIDTH; i++) 
	{
	    count = 0;
	    for(j=HEIGHT; j>=1; j--) 
	    {
		k = board[i][j];
		if(k < 0)
		    count++;
		else if(count > 0)
		{
		    //pull down floating puyo
		    puyos[k].setY(puyos[k].getY()+count);
		    
		    //clear previous position in board
		    board[i][j] = -1;
		    
		    //set new position in board
		    board[puyos[k].getX()][puyos[k].getY()] = k;
		}
	    }
	}
    }
    
    public void onKeyPressed(int key)
    {
	int i;
	boolean error = false;
	
	for(i=0; i<MAX_PUYOS; i++)
	{
	    if(puyos[i].isActive())
	    {
		switch(key)
		{
		    case KeyEvent.VK_LEFT:
			if(puyos[i].moveLeft() < 0) 
			    error = true;
			break;
		    case KeyEvent.VK_RIGHT:
			if(puyos[i].moveRight() < 0)
			    error = true;
			break;
		    case KeyEvent.VK_A:
			puyos[i].rotateLeft();
			break;
		    case KeyEvent.VK_S:
			puyos[i].rotateRight();
			break;
		}//switch
	    }//if
	}//for
	
	if(error)
	{
	    //Rollback because an illegal move occurred;
	    //this implementation is because we have 
	    //two puyos moving out there...
	    switch(key)
	    {
		case KeyEvent.VK_LEFT:
		    onKeyPressed(KeyEvent.VK_RIGHT);
		    break;
		case KeyEvent.VK_RIGHT:
		    onKeyPressed(KeyEvent.VK_LEFT);
		    break;
	    }
	}
    }
    
    /*------------------------------
     Checks if puyos have reached 
     the top of the board
     ------------------------------*/
    public boolean gameOver()
    {
	for(int i=1; i<WIDTH; i++)
	    if(board[i][1] >= 0)
		return true;
	
	return false;
    }
    
    /*------------------------------
     Implements method run() of 
     java.lang.Runnable interface
     ------------------------------*/
    public void run()
    {
	init();
	
	while(playing)
	{
	    //update positions
	    puyoUpdate();
	    
	    //increment cicles
	    cicle++;
	    
	    if(gameOver())
		playing = false;
	    
	    //update screen
	    repaint();
	    
	    //sleep this thread for a little while
	    try{
		Thread.sleep(40);
	    }
	    catch(InterruptedException ex) {
		System.out.println(ex.toString());
	    }
	}//while
    }

    /*-----------------------------------
     Overrides method paint() of class
     Canvas
     -----------------------------------*/
    public void paint(Graphics g)
    {
	//create a back buffer (off-screen) to draw in
        BufferedImage backbuffer = new BufferedImage(WIDTH*Puyo.WIDTH,HEIGHT*Puyo.HEIGHT,BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D)backbuffer.getGraphics();
	
	//clear back buffer
	g2.setColor(Color.WHITE);
	g2.fillRect(0, 0, WIDTH*Puyo.WIDTH, HEIGHT*Puyo.HEIGHT);
	
	for(int i=0; i<MAX_PUYOS; i++)
	{
	    if(puyos[i].isActive())
		puyos[i].draw(g2);
	}
	
	if(!playing){
	    g2.setColor(Color.BLACK);
	    g2.setFont(new Font("Arial", Font.BOLD,  20));
	    g2.drawString("GAME OVER",40,200);
	}
	
	//draw the back buffer
	g.drawImage(backbuffer,0,0,this);
    }
    
    /*----------------------------------------
     Overrides method update() of class Canvas
     This is because the default implementation 
     always calls clearRect causing unwanted 
     flicker
     ----------------------------------------*/
    public void update(Graphics g) {
	paint(g);
    }
}

