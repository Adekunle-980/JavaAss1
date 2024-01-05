/**
 * Hangman Game
 * Group 6
 * Student Names:  Adekunle Omonihi, Chioma Chimere Kamalu, Gagandeep Kaur, Pelumi Owoshagba
 * Student IDs:   n01583104, n01511618, n01511618, n01600998
 * Section: Fall 2023--- IGB
 *
 * This Hangman game allows the player to guess letters and complete a hidden word. 
 * The player is given a limited number of misses before losing the game.
 */
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.*;


/**
 * Hangman game implementation with a graphical user interface.
 */
public class Hangman extends JFrame {
	
	//GUI variables 
	private String currentWord;          // The word to be guessed by the player
	private Set<Character> guessedLetters; // Set to keep track of guessed letters
	private int misses;                   // Number of misses by the player
	private String tip;                   // Tips for the player
	private JTextField guessInput;       // Input field for the player's guesses
	private JLabel wordLabel;            // Display for the word with correct guesses
	private JLabel tipLabel;             // Display for game tips
	private JLabel missesLabel;          // Display for the number of misses
	protected JMenuBar menuBar;          // The application's menu bar
	private JMenu fileMenu;              // File menu
	private JMenu textMenu;              // Text menu
	private JMenu fontMenu;              // Font menu
	private JMenuItem exitItem;          // Exit menu item
	private JRadioButtonMenuItem blackItem; // Option for black text color
	private JRadioButtonMenuItem redItem;   // Option for red text color
	private JRadioButtonMenuItem blueItem;  // Option for blue text color
	private JCheckBoxMenuItem visibleItem;  // Option to make text visible or invisible
	private JMenu sizeSubMenu;              // Submenu for font size options
	private JRadioButtonMenuItem smallSizelItem;  // Option for small font size
	private JRadioButtonMenuItem middleSizelItem; // Option for middle font size
	private JRadioButtonMenuItem largeSizelItem;  // Option for large font size

     
    
    
 	
 	//Game GUI Inputs Starts here 

	public Hangman() {
	    super(" HANGMAN GAME");

	    List<String> words = loadWords("hangman.txt");

	    if (words.isEmpty()) {
	        // Show an error message and exit the program if no words are found
	        JOptionPane.showMessageDialog(this, "Error: No words found in the file 'hangman.txt'", "No Words Found", JOptionPane.ERROR_MESSAGE);
	        System.exit(1);
	    }

	    Random random = new Random();
	    currentWord = words.get(random.nextInt(words.size()));
	    guessedLetters = new HashSet<>();
	    misses = 0;
	    //tip = "Make sure to use different letters for each guess.\nPress CTRL + N to add a new word.\nPress ENTER to make a guess.";
	    buildMenuBar(); // Build the menu bar.
	    initComponents();
	    initGame();
	}

	private void initComponents() {
	    guessInput = new JTextField(1);
	    guessInput.setColumns(1);

	    guessInput.setDocument(new PlainDocument() {
	        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
	            if (str == null || !str.matches("[a-zA-Z]")) {
	                return;
	            }
	            if ((getLength() + str.length()) <= 1) {
	                super.insertString(offset, str, attr);
	            }
	        }
	    });
        
        guessInput.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleGuess();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                    handleNewWord();
                }
            }
        });
        
        
        wordLabel = new JLabel("", JLabel.CENTER);
        missesLabel = new JLabel("Misses: 0", JLabel.CENTER);

        JButton guessButton = new JButton("Guess");
        guessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleGuess();
            }
        });

        JButton newWordButton = new JButton("Add a New Word");
        newWordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleNewWord();
            }
        });

        // Set font and border for guessInput
        guessInput.setFont(new Font("Arial", Font.PLAIN, 18));
        guessInput.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Set font and color for wordLabel
        wordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        wordLabel.setForeground(Color.black);

        // Set font and color for tipLabel
        tipLabel = new JLabel("", JLabel.CENTER);
        tipLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        tipLabel.setForeground(Color.black);

        // Set font, size, and border for guessInput again
        guessInput.setFont(new Font("Arial", Font.PLAIN, 18));
        guessInput.setPreferredSize(new Dimension(7, guessInput.getPreferredSize().height));

        // Set font and color for missesLabel
        missesLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        missesLabel.setForeground(Color.black);

        // Set font, color, and border for guessButton
        guessButton.setFont(new Font("Arial", Font.ITALIC, 17));
        guessButton.setBackground(Color.orange);
        guessButton.setForeground(Color.black);
        guessButton.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Set font, color, and border for newWordButton
        newWordButton.setFont(new Font("Arial", Font.BOLD, 15));
        newWordButton.setBackground(Color.pink);
        newWordButton.setForeground(Color.white);
        newWordButton.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Initialize and configure panels
        JPanel guessInputPanel = new JPanel(new FlowLayout());
        guessInputPanel.add(guessInput);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.add(guessButton);
        buttonsPanel.add(newWordButton);

        int spaceBetweenButtons = 10;
        Box buttonsBox = Box.createHorizontalBox();
        buttonsBox.add(Box.createHorizontalStrut(spaceBetweenButtons));
        buttonsBox.add(buttonsPanel);
        buttonsBox.add(Box.createHorizontalStrut(spaceBetweenButtons));

        JPanel tipsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tipsPanel.add(tipLabel);

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.add(wordLabel);
        panel.add(guessInputPanel);
        panel.add(buttonsBox);
        panel.add(tipsPanel);

        JPanel missesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        missesPanel.add(missesLabel);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15)); // Add margin

        // Set layout and add components to the content pane
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panel);
        getContentPane().add(missesPanel);
        getContentPane().add(tipsPanel);

        // Set frame properties
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); }

    
    //Game Logic Starts here

    private void initGame() {	
        // Initialize the game by resetting and updating the display

    	while (isWordGuessed()) {
            resetGame();								
        }  												
        updateDisplay(); 								
    }

    
    private void updateDisplay() {
    	// Update the display with the current guessed letters and misses

        StringBuilder display = new StringBuilder(); 	
        for (char c : currentWord.toCharArray()) {
            if (guessedLetters.contains(c)) {
                display.append(c); 						
            } else {
                display.append('*'); 					
            }
        }
        wordLabel.setText("Word: " + display.toString()); 			
        missesLabel.setText("Number of Misses: " + misses);  					
        tipLabel.setText("<html>Tips:<br>" + tip + "</html>"); 
    }

    private void handleGuess() {
    	
        String guess = guessInput.getText().toLowerCase();			
        
        char guessedLetter = guess.charAt(0);					

        if (guessedLetters.contains(guessedLetter)) {				
            JOptionPane.showMessageDialog(this, "You have already guessed this letter: " + guessedLetter +". Try another letter.");
        } else {
            guessedLetters.add(guessedLetter);  						

            if (currentWord.contains(String.valueOf(guessedLetter))) {		
                if (isWordGuessed()) {
                    handleWordGuessed();				
                    guessedLetters.clear();				//Clear guessed letter for next word.
                }
            } else {									
                misses++;
                if (misses >= 5) {
                    handleGameOver();
                    guessedLetters.clear();				//Clear guessed letter for next word.
                }
            }
        }
        guessInput.setText("");
        updateDisplay();								
    }

    
    private void handleWordGuessed() {               		//Method for dialogbox for game Won
        JOptionPane.showMessageDialog(this,
                "Congratulations! You are right,. The word is : " + currentWord + "\n Number of misses: " + misses, "You Won!", JOptionPane.INFORMATION_MESSAGE);
        handleNewWord();									//Method will ask for new word after a win
    }

    
    private void handleGameOver() {                   		//Method for dialogbox for game lost
        JOptionPane.showMessageDialog(this,
                "Game over! The correct word was: " + currentWord + "\nNumber of misses: " + misses, "You Lost!", JOptionPane.INFORMATION_MESSAGE);
        handleNewWord();									//Method will ask for new word after a loss
    }	

    
    private void handleNewWord() {
        String newWord = JOptionPane.showInputDialog(this, "Please Enter a new word:"); 		//Input for new word

        if (newWord != null && newWord.length() > 1) {
            List<String> words = loadWords("hangman.txt"); 					// Load the list of words from the file 
            
            if (!words.contains(newWord.toLowerCase())) {  					//Check for repetition. If the word is not repeated it will get added
                addWordToFile("hangman.txt", newWord);
            	resetGame();												//Reset game after new word is added
            } else {
                JOptionPane.showMessageDialog(this, "The word " + newWord + " already exists. Please enter a new word."); //Error for word already exists
            }
        } else {
            JOptionPane.showMessageDialog(this, "You have not entered any word.\n To add a new word, Please click on the 'New Word' and try again."); //Error for no word entered.
            resetGame();													//Reset game after new word is not added
        }
    }

    
    private void resetGame() {
        List<String> words = loadWords("hangman.txt"); 			//Load words from the file

        if (words.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: No words found in the file 'hangman.txt'"); 
            handleNewWord(); 											// Exit the Game
        }

        Random random = new Random(); 
        currentWord = words.get(random.nextInt(words.size())); 			
        guessedLetters = new HashSet<>();  								//Guessed letter hashset initialized
        misses = 0;  													//Reset misses to 0

        updateDisplay();												//Update all labels
    }
    

    private boolean isWordGuessed() {					//Validation for guessed word
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(c)) {			//Comparison to the Array we built for HandleGuess method
                return false;							//If the word is not guessed then false
            }
        }
        return true;									//If the word is guessed then true
    }

    
    private List<String> loadWords(String filename) {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            System.err.println("File 'hangman.txt' not found. Using default words.");
            // Provide default words if the file is not found
            words.add("fish");
            words.add("bathe");
            words.add("orange");
            words.add("Exception");
        } catch (IOException e) {
            System.err.println("There is an Error reading the file 'hangman.txt'.");
            e.printStackTrace();
        }

        return words;
    }


    
    private void addWordToFile(String filename, String word) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(word.trim().toLowerCase());  		
            writer.newLine();								//Newline 
        } catch (IOException e) {
            e.printStackTrace();							
        }
    }
      //Controller Logic Ends here
    
    

      
      private void buildMenuBar() {
      	// Create the menu bar.
      	menuBar = new JMenuBar();
      	
      	// Create the file and text menus.
      	buildFileMenu();
      	buildTextMenu();
      		
      	// Add the file and text menus to the menu bar.
      	menuBar.add(fileMenu);
      	menuBar.add(textMenu);
      		
      	// Set the window's menu bar.
      	setJMenuBar(menuBar);
      }
  	
  	
  	//The buildFileMenu method builds the File menu
  	public void buildFileMenu() {
  		// Create an Exit menu item.
  		exitItem = new JMenuItem("Exit");	
  		exitItem.setMnemonic(KeyEvent.VK_X);
  		exitItem.addActionListener(new ExirListener());
  		
  		// Create a JMenu object for the File menu.
  		fileMenu = new JMenu("File");
  		fileMenu.setMnemonic(KeyEvent.VK_F);
  		// Add the Exit menu item to the File menu.
  		fileMenu.add(exitItem);
  	}
  		//The buildTextMenu method builds the Text menu
  	public void buildTextMenu() {
  		// Create the radio button menu items to change the color
  		// of the text. Add an action listener to each one.
  		blackItem = new JRadioButtonMenuItem("Pitch Black", true);
  		blackItem.setMnemonic(KeyEvent.VK_B);
  		blackItem.addActionListener(new ColorListener());
  			
  		redItem = new JRadioButtonMenuItem("Dark Red");
  		redItem.setMnemonic(KeyEvent.VK_R);
  		redItem.addActionListener(new ColorListener());
  				
  		blueItem = new JRadioButtonMenuItem("Royal Blue");
  		blueItem.setMnemonic(KeyEvent.VK_U);
  		blueItem.addActionListener(new ColorListener());
  		// Create a submenu for the radio button items.	
  		sizeSubMenu =  buildSizeSubMenu();
  		
  		// Create a button group for the radio button items.
  		ButtonGroup group = new ButtonGroup();
  		group.add(blackItem);
  		group.add(redItem);
  		group.add(blueItem);
  		// Create a check box menu item to make the text visible or invisible.
  		visibleItem = new JCheckBoxMenuItem("Visible", true);	
  		visibleItem.setMnemonic(KeyEvent.VK_V);
  		visibleItem.addActionListener(new VisibleListener());
  		// Create a JMenu object for the Text menu.
  		textMenu = new JMenu("Text");
  		textMenu.setMnemonic(KeyEvent.VK_C);

  		// Add the menu items to the Text menu.
  		textMenu.add(blackItem);
  		textMenu.add(redItem);
  		textMenu.add(blueItem);
  		textMenu.addSeparator();   // Add a separator bar.
  		textMenu.add(visibleItem);	
  		textMenu.addSeparator();   // Add a separator bar.
  		textMenu.add(sizeSubMenu);	
  		}
  		
  	public JMenu buildSizeSubMenu() {
  		JMenu subMenu = new JMenu("Size"); 
  		
  		smallSizelItem = new JRadioButtonMenuItem("Small", true);
  		smallSizelItem.setMnemonic(KeyEvent.VK_S);
  		smallSizelItem.addActionListener(new FontListener());
  		
  		middleSizelItem = new JRadioButtonMenuItem("Middle");
  		middleSizelItem.setMnemonic(KeyEvent.VK_M);
  		middleSizelItem.addActionListener(new FontListener());
  		
  		largeSizelItem = new JRadioButtonMenuItem("Large");
  		largeSizelItem.setMnemonic(KeyEvent.VK_L);
  		largeSizelItem.addActionListener(new FontListener());
  				
  		// Create a button group for the radio button items.
  		ButtonGroup group = new ButtonGroup();
  		group.add(smallSizelItem );
  		group.add(middleSizelItem);
  		group.add(largeSizelItem);		
  		// Create a JMenu object for the Size submenu.		
  		fontMenu = new JMenu("Size");
  		subMenu.setMnemonic(KeyEvent.VK_S);		
  		// Add the menu items to the  Sise submenu.
  		subMenu.add(smallSizelItem );
  		subMenu.add(middleSizelItem);
  		subMenu.add(largeSizelItem);		
  		return subMenu;
  		}
 
  	public class ExirListener implements ActionListener{
  		public void actionPerformed(ActionEvent e) {
  			System.exit(0);
  		}
  	}	
  
  	public class ColorListener implements ActionListener{
  	    // Handle action events for color selection

  		public void actionPerformed(ActionEvent e) {
  	        // Change text color based on selected color item

  			if(blackItem.isSelected()) {
  				guessInput.setForeground(Color.BLACK);				
  				wordLabel.setForeground(Color.BLACK);
  				missesLabel.setForeground(Color.BLACK);			
  			}
  			else if(redItem.isSelected()) {
  				missesLabel.setForeground(Color.RED);
  				wordLabel.setForeground(Color.RED);
  				missesLabel.setForeground(Color.RED);			
  			}
  			else if(blueItem.isSelected()) {
  				missesLabel.setForeground(Color.BLUE);
  				wordLabel.setForeground(Color.BLUE);
  				missesLabel.setForeground(Color.BLUE);	  				
  		
  			}
  		}
  	}
  
  	public class VisibleListener implements ActionListener{
  	    // Handle action events for text visibility

  		public void actionPerformed(ActionEvent e) {
  	        // Toggle visibility of text components based on selected visibility item

  			if(visibleItem.isSelected()) {
  				guessInput.setVisible(true);
  				wordLabel.setVisible(true);
  				missesLabel.setVisible(true);
		
  			}else {
  				guessInput.setVisible(false);
  				wordLabel.setVisible(false);
  				missesLabel.setVisible(false);		    
				    
  				
  			}
  		}
  	}
  	
  	public class FontListener implements ActionListener{
  		public void actionPerformed(ActionEvent e) {
  			if(smallSizelItem.isSelected()) {
  				guessInput.setFont(new Font("SansSerif", Font.BOLD, 14));
  				wordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
  				missesLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
  			}
  			else if(middleSizelItem.isSelected()) {
  				guessInput.setFont(new Font("SansSerif", Font.BOLD, 20));
  				wordLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
  				missesLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
  			}
  			else if(largeSizelItem.isSelected()) {
  				guessInput.setFont(new Font("SansSerif", Font.BOLD, 28));
  				wordLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
  				missesLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
  			}
  		}
  		}
      
  		
    //Main 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Hangman();
            }
        });
    }
}