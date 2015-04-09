/*-----------------------------------------
    Puyo Class for puyo demo
 
    Author: Héctor Morales Piloni, MSc.
	    http://www.piloni.net
    Date:   October 2, 2005
------------------------------------------*/

class Puyo extends Sprite
{
    //positions for rotation
    final static int TOP    = 0;
    final static int RIGHT  = 1;
    final static int BOTTOM = 2;
    final static int LEFT   = 3;
    final static int NONE   = 4;
    
    //puyo colors
    final static int BLUE   = 0;
    final static int RED    = 1;
    final static int YELLOW = 2;
    final static int GREEN  = 3;
    
    //puyo states
    final static int FALLING = 0;
    final static int STALLED = 1;
    final static int FALLING_AUTO = 2;
    
    //puyo sprite size
    final static int WIDTH = 32;
    final static int HEIGHT = 32;
    
    private int color;		//puyo color (red, blue, ...)
    private int state;		//puyo state (falling, etc)
    private int pos;		//position for rotation
    private boolean rotable;	//can be rotated?
    private boolean visited;	//has been visited? (used when checking for combos)
    
    public Puyo(int nFrames) {
	super(nFrames);
    }
        
    public int getState() {
	return state;
    }
    
    public int getColor() {
	return color;
    }
    
    public int getPos() {
	return pos;
    }
    
    public boolean isVisited() {
	return visited;
    }
    
    public boolean isRotable() {
	return rotable;
    }
    
    public void setState(int state) {
	this.state = state;
    }
    
    public void setColor(int color) {
	this.color = color;
    }
    
    public void setPos(int pos) {
	this.pos = pos;
    }
    
    public void visited(boolean visited) {
	this.visited = visited;
    }
    
    public void setRotable(boolean rotable) {
	this.rotable = rotable;
    }
    
    /*--------------------------------------
     Overrides method setX() of class Sprite
     converts COL value to pixels
     -------------------------------------*/
    public void setX(int value) {
	super.setX((value-1)*WIDTH);
    }
    
    /*--------------------------------------
     Overrides method setY() of class Sprite
     converts ROW value to pixels
     -------------------------------------*/
    public void setY(int value) {
	super.setY((value-1)*HEIGHT);
    }
    
    /*--------------------------------------
     Overrides method getX() of class Sprite
     converts pixels to COL value
     -------------------------------------*/
    public int getX() {
	return (super.getX()/WIDTH) + 1;
    }
    
    /*--------------------------------------
     Overrides method getY() of class Sprite
     converts pixels to ROW value
     -------------------------------------*/
    public int getY() {
	return (super.getY()/HEIGHT) + 1;
    }
        
    /*------------------------
     Rotates puyo clockwise
     ------------------------*/
    public void rotateRight()
    {
	if(!isRotable())
	    return;
	
	if(getState() == STALLED)
	    return;
	
	switch(pos)
	{
	    case TOP:
		if(getX() == Board.WIDTH) 
		    return;
		setPos(RIGHT);
		setX(getX()+1);
		setY(getY()+1);
		break;
	    case RIGHT:
		setPos(BOTTOM);
		setX(getX()-1);
		setY(getY()+1);
		break;
	    case BOTTOM:
		if(getX() == 1)
		    return;
		setPos(LEFT);
		setX(getX()-1);
		setY(getY()-1);
		break;
	    case LEFT:
		setPos(TOP);
		setX(getX()+1);
		setY(getY()-1);
		break;
	}
    }
    
    /*----------------------------
     Rotates puyo counter-clockwise
     -----------------------------*/
    public void rotateLeft()
    {
	if(!isRotable())
	    return;
	
	if(getState() == STALLED)
	    return;

	switch(pos)
	{
	    case TOP:
		if(getX() == 1)
		    return;
		setPos(LEFT);
		setX(getX()-1);
		setY(getY()+1);
		break;
	    case LEFT:
		setPos(BOTTOM);
		setX(getX()+1);
		setY(getY()+1);
		break;
	    case BOTTOM:
		if(getX() == Board.WIDTH)
		    return;
		setPos(RIGHT);
		setX(getX()+1);
		setY(getY()-1);
		break;
	    case RIGHT:
		setPos(TOP);
		setX(getX()-1);
		setY(getY()-1);
		break;
	}
    }
    
    /*-------------------------
     Drops puyo 1 position  
     if it reaches an illegal
     position returns -1
     --------------------------*/
    public int moveDown()
    {
	int row,col;
	boolean collision = false;
	
	setY(getY() +1);
	
	//check if we have touched another puyo
	row = getY();
	col = getX();
	if(row >= 1 && row <= Board.HEIGHT && col >= 1 && col <= Board.WIDTH)
	    if(Board.board[col][row] >= 0)
		collision = true;
	
	if(getY() > Board.HEIGHT || collision) 
	    return -1;
	else
	    return 1;
    }
    
    /*--------------------------------
     This function should be called 
     IIF moveDown() produced an 
     illegal move
     ---------------------------------*/
    public void moveUp() {
	setY(getY() -1);
    }
    
    /*-------------------------
     Moves puyo 1 position to 
     the left, if it reaches an 
     illegal position returns -1
     --------------------------*/
    public int moveLeft()
    {
	int row,col;
	boolean collision = false; 
	
	if(getState() == STALLED || getState() == FALLING_AUTO)
	    return 0;
	
	setX(getX() -1);
	
	//check if we have touched another puyo
	row = getY();
	col = getX();
	if(row >= 1 && row <= Board.HEIGHT && col >= 1 && col <= Board.WIDTH)
	    if(Board.board[col][row] >= 0)
		collision = true;

	if(getX() < 1 || collision)
	    return -1;
	else
	    return 1;
    }
    
    /*-------------------------
     Moves puyo 1 position to 
     the right, if it reaches an 
     illegal position returns -1
     --------------------------*/
    public int moveRight()
    {
	int row,col;
	boolean collision = false;
	
	if(getState() == STALLED || getState() == FALLING_AUTO)
	    return 0;
	
	setX(getX() + 1);
	
	//check if we have touched another puyo
	row = getY();
	col = getX();
	if(row >= 1 && row <= Board.HEIGHT && col >= 1 && col <= Board.WIDTH)
	    if(Board.board[col][row] >= 0)
		collision = true;

	if(getX() > Board.WIDTH || collision)
	    return -1;
	else
	    return 1;
    }
}