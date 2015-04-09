/*-----------------------------------------
    Puyo demo Game application class
 
    Author: Héctor Morales Piloni, MSc.
	    http://www.piloni.net
    Date:   October 2, 2005
------------------------------------------*/

import java.awt.event.*;
import javax.swing.*;

public class GameApp extends JFrame
{
    //main window size
    final private int WIN_WIDTH = 200;
    final private int WIN_HEIGHT = 420;
    
    public GameApp() 
    {
	//constructor chaining
	this(null);
    }
    
    public GameApp(String title)
    {
	//create a JFrame with title
	super(title);

	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
	setResizable(false);
    }
    
    public static void main(String[] args)
    {
	//create the main drawable board
	final Board puyoBoard = new Board();
	
	//create an instance of GameApp
	GameApp mainWindow = new GameApp("Puyo demo by VerMan");
	mainWindow.add(puyoBoard);
	mainWindow.setVisible(true);

	//add a KeyListener to main window
	mainWindow.addKeyListener(new KeyAdapter()
	{
	    public void keyPressed(KeyEvent e){
		puyoBoard.onKeyPressed(e.getKeyCode());
	    }
	});
    }
}
