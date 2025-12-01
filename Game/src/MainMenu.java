import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JFormattedTextField;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable loadTable;
	JPanel mainMenuPanel = new JPanel();
	JPanel characterPanel = new JPanel();
	JPanel playPanel = new JPanel();
	JPanel loadGamePanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu frame = new MainMenu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	private void setPanel(JPanel panel) {
		mainMenuPanel.setVisible(false);
		characterPanel.setVisible(false);
		playPanel.setVisible(false);
		loadGamePanel.setVisible(false);
		panel.setVisible(true);
	}
	private void setPanel() {
		mainMenuPanel.setVisible(false);
		characterPanel.setVisible(false);
		playPanel.setVisible(false);
		loadGamePanel.setVisible(false);
	}
	
	public MainMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		playPanel.setBounds(0, 0, 436, 262);
		contentPane.add(playPanel);
		playPanel.setLayout(null);
		
		JButton btnNewGame = new JButton("New Game");
		btnNewGame.setBounds(165, 47, 88, 22);
		playPanel.add(btnNewGame);
		
		JButton btnLoadGame = new JButton("Load Game");
		btnLoadGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(loadGamePanel);
			}
		});
		btnLoadGame.setBounds(165, 97, 88, 22);
		playPanel.add(btnLoadGame);
		
		JButton btnPlayBack = new JButton("<--");
		btnPlayBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(mainMenuPanel);
			}
		});
		btnPlayBack.setBounds(10, 229, 55, 22);
		playPanel.add(btnPlayBack);
		
		characterPanel.setBounds(0, 0, 436, 263);
		contentPane.add(characterPanel);
		characterPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(182, 103, 48, 14);
		characterPanel.add(lblNewLabel);
		
		loadGamePanel.setLayout(null);
		loadGamePanel.setBounds(0, 0, 436, 262);
		contentPane.add(loadGamePanel);
		
		JButton btnLoad = new JButton("Load Game");
		btnLoad.setBounds(171, 214, 88, 22);
		loadGamePanel.add(btnLoad);
		
		JScrollPane loadScrollPane = new JScrollPane();
		loadScrollPane.setBounds(10, 11, 416, 191);
		loadGamePanel.add(loadScrollPane);
		
		loadTable = new JTable();
		loadScrollPane.setViewportView(loadTable);
		
		JButton btnLoadBack = new JButton("<--");
		btnLoadBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(playPanel);
			}
		});
		btnLoadBack.setBounds(10, 229, 49, 22);
		loadGamePanel.add(btnLoadBack);
		
		mainMenuPanel.setBounds(0, 0, 436, 263);
		contentPane.add(mainMenuPanel);
		mainMenuPanel.setLayout(null);
		setPanel(mainMenuPanel);
		
		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(playPanel);
			}
		});
		btnPlay.setBounds(175, 76, 81, 23);
		mainMenuPanel.add(btnPlay);
		
		JButton btnCharacter = new JButton("Character");
		btnCharacter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(characterPanel);
			}
		});
		btnCharacter.setBounds(175, 110, 81, 23);
		mainMenuPanel.add(btnCharacter);
		
		JButton btnKeybinds = new JButton("Keybinds");
		btnKeybinds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel();
			}
		});
		btnKeybinds.setBounds(175, 144, 81, 23);
		mainMenuPanel.add(btnKeybinds);
		
		JLabel lblGameName = new JLabel("Jone's Junction");
		lblGameName.setBounds(136, 11, 161, 29);
		lblGameName.setFont(new Font("Tahoma", Font.PLAIN, 24));
		mainMenuPanel.add(lblGameName);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); 
			}
		});
		btnExit.setBounds(175, 178, 81, 23);
		mainMenuPanel.add(btnExit);

	}
}
