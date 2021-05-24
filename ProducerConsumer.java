/*
 * cameron campbell
 * advanced java
 * occc spring 2021
 * concurrency gui (producer-consumer)
 */

import java.util.concurrent.Semaphore;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.io.*;

/*
 * modified version of the given Producer-Consumer
 * program that accommodates a GUI for ease of use
 * and user control over producers and consumers
 */
public class ProducerConsumer
{
	// class-relevant variables, fields and objects
	static int resource, averageProduced, averageConsumed;
	static Semaphore s = new Semaphore(1);

   
	// sleep method to add a random element to the order of the program
	public static void mySleep()
	{
		// this function puts the thread "to sleep" for a while,
		// to simulate time spent processing 

		try
		{
			Thread.sleep((int)(Math.random()*1000));
		}
		catch(InterruptedException e)
		{
			// do nothing
		}
	} // close sleep method

   
	// main method
	public static void main(String [] args)
	{
		ProducerConsumerGUI pcg = new ProducerConsumerGUI();
	}

   
	// custom thread subclass for producer
	private static class Producer extends Thread
	{
		int i;
		public Producer(int i)
		{
			super();
			this.i = i;
		}

		public void run()
		{
			while(true)
			{
				mySleep();
				System.out.println("Producer " + i + ": attempting to acquire");
				try
				{
					s.acquire();
					System.out.println("Producer " + i + ": semaphore acquired!");
					mySleep();
					System.out.println("Producer " + i + ": the resource (pre)  is " + resource);
					resource += (int) (Math.random()*averageProduced);
					System.out.println("Producer " + i + ": the resource (post) is " + resource);
					System.out.println("Producer " + i + ": semaphore released");
					s.release();
				}
				catch(InterruptedException e){}
			}   
		}
	}

   
	// custom thread subclass for consumer
	private static class Consumer extends Thread
	{
		int i;
		public Consumer(int i)
		{
			super();
			this.i = i;
		}

		public void run()
		{
			while(true)
			{
				mySleep();
				System.out.println("Consumer " + i + ": attempting to acquire");
				try
				{
					s.acquire();
					System.out.println("Consumer " + i + ": semaphore acquired!");
					mySleep();
					System.out.println("Consumer " + i + ": the resource is " + resource);
					int need = (int) (1 + Math.random()*averageConsumed);
					System.out.println("Consumer " + i + ": my need is " + need);
					if (resource >= need)
					{ 
						resource -= need;
						System.out.println("Consumer " + i + ": got what I needed!");
						System.out.println("Consumer " + i + ": the resource is now " + resource);
					}
					else
					{
						System.out.println("Consumer " + i + ": semaphore unavailable");
					}
					System.out.println("Consumer " + i + ": semaphore released");
					s.release();
				}
				catch(InterruptedException e){}
			}
		}
   } 

   
   /*
    * entirety of the GUI class, appended to the Producer-Consumer class.
    * the GUI is divided between two phases, represented by two master frames
    * where the subpanels and controls are housed: the prep phase and action phase.
    * the prep phase is where the user can input the number of producers, consumers,
    * average amount consumed, and average amount produced before entering the action
    * phase, where the user can witness the actions of the producers and consumers
    * through the constantly-updated resource integer and a text pane detailing what
    * the producers and consumers are doing in the console output stream
    */
   static class ProducerConsumerGUI extends JFrame 
   implements ActionListener
   {
	   /*
	    * subclass-relevant objects, fields, and controls. i used a custom grid 
	    * layout for the start button panel so that i could have more 
	    * control over the size, shape, and alignment of the fields on the prep
		* panel. i also chose to create an empty JPanel for the purpose of giving
		* me more control over the exact positioning of controls in my grid layouts;
		* any time i need a negative space, i can just add the gridSpace JPanel to
		* the next grid cell
	    */
	   private JFrame errorFrame, actionFrame;
	   private JPanel prepPanel, actionPanel, prepProducersPanel,
	   prepConsumersPanel, prepAverageProducePanel, prepCenterPanel,
	   prepAverageConsumePanel, prepStartButtonPanel;
	   private JLabel prepProducersLabel, prepConsumersLabel, 
	   prepAverageConsumeLabel, prepAverageProduceLabel, prepTitle;
	   private JButton startButton;
	   private JTextField prepProducersField, prepConsumersField,
	   prepAverageConsumeField, prepAverageProduceField;
	   private JScrollPane actionScrollPane;
	   static JTextArea errorText, actionResourceText, actionConsoleText;
	   private Producer[] p;
	   private Consumer[] c;
	   private Font prepFontSmall = new Font("Arial", Font.PLAIN, 14);
	   private Font actionFont = new Font("Arial", Font.PLAIN, 10);
	   private Font resourceFont = new Font("Impact", Font.PLAIN, 30);
	   private Font prepFontLarge = new Font("Arial", Font.PLAIN, 26);
	   private GridLayout prepCenterGridLayout = new GridLayout(4, 1);
	   private JPanel gridSpace = new JPanel();
	   
	   
	   /*
	    * the constructor performs basic frame setup for the prep pane. once
	    * the user clicks the start button, the actionPhase method is called,
	    * setting up the action panel
	    */
	   public ProducerConsumerGUI() 
	   {
		   // initial frame setup
		   super("Producer-Consumer Interface");
		   setSize(250, 240);
		   this.setResizable(false);
		   this.setLayout(new BorderLayout());
		   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		   
		   /*
		    * prep panel setup
		    */
		   // *panel
		   prepPanel = new JPanel(new BorderLayout());
		   
		   // *title
		   prepTitle = new JLabel("Preparation Phase", SwingConstants.CENTER);
		   prepTitle.setFont(prepFontLarge);
		   prepPanel.add(prepTitle, BorderLayout.NORTH);
		   
		   // *producers field and its label
		   prepProducersLabel = new JLabel("Number of Producers:", SwingConstants.RIGHT);
		   prepProducersLabel.setFont(prepFontSmall);
		   prepProducersField = new JTextField(5);
		   
		   prepProducersPanel = new JPanel(new BorderLayout());
		   prepProducersPanel.add(prepProducersLabel, BorderLayout.CENTER);
		   prepProducersPanel.add(prepProducersField, BorderLayout.EAST);
		   
		   // *consumers field and its label
		   prepConsumersLabel = new JLabel("Number of Consumers:", SwingConstants.RIGHT);
		   prepConsumersLabel.setFont(prepFontSmall);
		   prepConsumersField = new JTextField(5);
		   
		   prepConsumersPanel = new JPanel(new BorderLayout());
		   prepConsumersPanel.add(prepConsumersLabel, BorderLayout.CENTER);
		   prepConsumersPanel.add(prepConsumersField, BorderLayout.EAST);
		   
		   // *average produced field and its label
		   prepAverageProduceLabel = new JLabel("Average Value Produced:", SwingConstants.RIGHT);
		   prepAverageProduceLabel.setFont(prepFontSmall);
		   prepAverageProduceField = new JTextField(5);
		   
		   prepAverageProducePanel = new JPanel(new BorderLayout());
		   prepAverageProducePanel.add(prepAverageProduceLabel, BorderLayout.CENTER);
		   prepAverageProducePanel.add(prepAverageProduceField, BorderLayout.EAST);
		   
		   // *average consumed field and its label
		   prepAverageConsumeLabel = new JLabel("Average Value Consumed:", SwingConstants.RIGHT);
		   prepAverageConsumeLabel.setFont(prepFontSmall);
		   prepAverageConsumeField = new JTextField(5);
		   
		   prepAverageConsumePanel = new JPanel(new BorderLayout());
		   prepAverageConsumePanel.add(prepAverageConsumeLabel, BorderLayout.CENTER);
		   prepAverageConsumePanel.add(prepAverageConsumeField, BorderLayout.EAST);
		   
		   // *start button
		   startButton = new JButton("Start");
		   startButton.setFont(prepFontSmall);
		   
		   prepStartButtonPanel = new JPanel(new GridLayout(1, 3));
		   
		   prepStartButtonPanel.add(gridSpace);
		   prepStartButtonPanel.add(gridSpace);
		   prepStartButtonPanel.add(startButton, BorderLayout.SOUTH);
		   
		   /*
		    * assemble all controls and panels into the prep panel and
		    * add prep panel to frame
		    */
		   prepCenterPanel = new JPanel(prepCenterGridLayout);
		   prepCenterPanel.setSize(250, 160);
		   prepCenterGridLayout.setVgap(10);
		   prepCenterPanel.add(prepProducersPanel);
		   prepCenterPanel.add(prepAverageProducePanel);
		   prepCenterPanel.add(prepConsumersPanel);
		   prepCenterPanel.add(prepAverageConsumePanel);
		   
		   prepPanel.add(prepCenterPanel, BorderLayout.CENTER);
		   prepPanel.add(prepStartButtonPanel, BorderLayout.SOUTH);
		   
		   this.add(prepPanel);
		   
		   // implement actionlistener for start button that begins action phase when clicked
		   startButton.addActionListener((event) -> 
		   actionPhaseCheck(prepProducersField.getText(), prepAverageProduceField.getText(),
				   prepConsumersField.getText(), prepAverageConsumeField.getText()));
		   
		   // set to visible, completing the constructor process
		   this.add(prepPanel);
		   setVisible(true);
	   }
	   
	   
	   /*
	    * actionPhaseCheck method handles the inputs for the four text fields
	    * in the prep phase panel and checks if they're valid. if they are, then
	    * they're sent directly to the actionPhase method for processing into the
	    * final portion of the GUI. otherwise, the user is prompted to retry their
	    * inputs until they become valid
	    */
	   public void actionPhaseCheck(String pro, String avgPro, String con, String avgCon) 
	   {
		   try 
		   {
			   int producers = Integer.parseInt(pro);
			   int averageProducers = Integer.parseInt(avgPro);
			   int consumers = Integer.parseInt(con);
			   int averageConsumers = Integer.parseInt(avgCon);
			   
			   actionPhase(producers, averageProducers, consumers,
					   averageConsumers);
		   }
		   catch(NumberFormatException e) 
		   {
			   // an error results in a catch that creates a new frame describing the error to the user
			   errorFrame = new JFrame("Error Encountered");
			   errorFrame.setLayout(new BorderLayout());
			   errorFrame.setSize(350, 60);
			   errorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			   errorText = new JTextArea(1, 1);
			   errorText.setEditable(false);
			   errorText.setFont(prepFontSmall);
			   errorText.setText("Oops! Please enter integers only into the text boxes.");
			   errorFrame.add(errorText, BorderLayout.CENTER);
			   errorFrame.show();
		   }
	   }
	   
	   
	   /*
	    * the actionPhase method uses the successfully passed integers from actionPhaseCheck
	    * to setup the static average variables used by the consumer and producer custom threads
	    * ('theBuffer' has become 'resource' and the random resource generated or consumed is now
	    * based off of these average values). the method then initializes the frame for the action
	    * phase, sets up the console's output to the JTextArea in the action panel, runs the frame,
	    * and then finally sets up the SwingWorker for the resource value so that it will constantly
	    * update for the user
	    */
	   public void actionPhase(int pro, int avgPro, 
			   int con, int avgCon) 
	   {
		   // initial setup of producers and consumers from passed integers
		   averageProduced = avgPro;
		   averageConsumed = avgCon;
		   
		   Consumer [] c = new Consumer[con];
		   Producer [] p = new Producer[pro];

		   for(int i = 0; i < p.length; i++)
		   {
			   p[i] = new Producer(i);
			   p[i].start();
		   }
			
		   for(int i = 0; i < c.length; i++)
		   {
			   c[i] = new Consumer(i);
			   c[i].start();
		   }
		   
		   /*
		    * to display the console in actionTextPane, a replacement class must be created
		    * for the output stream. this will allow the program to redirect all console 
		    * messages to actionTextPane
		    */
		   class ConsoleOutputStream extends OutputStream 
		   {
			   private JTextArea textArea;
			   
			   public ConsoleOutputStream(JTextArea textArea) 
			   {
				   this.textArea = textArea;
			   }
			   
			   @Override
			   public void write(int b) throws IOException 
			   {
				   textArea.setText(textArea.getText() + String.valueOf((char)b));
				   textArea.setCaretPosition(textArea.getDocument().getLength());
				   textArea.update(textArea.getGraphics());
			   }
		   }
		   
		   /*
		    * now that everything has been prepared, the action phase is ready
		    * to begin; the actionFrame and its components are instantiated and
		    * display the current resource value and a log of the console's
		    * output. a custom thread will need to be created in order to 
		    * provide this information to the user, and so the GUI class
		    * will also house an override of SwingWorker
		    */
		   actionFrame = new JFrame("Action Phase");
		   actionFrame.setLayout(new BorderLayout());
		   actionFrame.setSize(420, 310);
		   actionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		   
		   actionConsoleText = new JTextArea(35, 15);
		   actionConsoleText.setEditable(false);
		   actionScrollPane = new JScrollPane(actionConsoleText);
		   actionScrollPane.setSize(420, 220);
		   
		   actionResourceText = new JTextArea(1, 1);
		   actionResourceText.setFont(resourceFont);
		   actionResourceText.setEditable(false);
		   actionResourceText.setSize(120, 120);
		   
		   actionPanel = new JPanel(new BorderLayout());
		   actionPanel.add(actionResourceText, BorderLayout.NORTH);
		   actionPanel.add(actionScrollPane, BorderLayout.CENTER);
		   
		   PrintStream ps = new PrintStream(new ConsoleOutputStream(actionConsoleText));
		   System.setOut(ps);
		   System.setErr(ps);
		   
		   actionFrame.add(actionPanel);
		   actionFrame.show();
		   
		   /*
		    * finally, a swingworker object is instantiated to update the actionResourceText
		    * with the current resource value now that it has been initialized
		    */
		   SwingWorker updater = new SwingWorker()
		   {
			   @Override
			   protected Object doInBackground() throws Exception
			   {
				   while (true) 
				   {
					   actionResourceText.setText("Number of Resources: " + String.valueOf(resource));
				   }
			   }
		   };
		   updater.execute();
	   }
	   
	   
	   /*
	    * obligatory implemented ActionListener method. i prefer individual
	    * methods called by the parameters of the addActionListener calls
	    * for each control in the constructor, as it's generally cleaner and
	    * more reliable
	    */
	   @Override
	   public void actionPerformed(ActionEvent arg0) {}
	}
} 
