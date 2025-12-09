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
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable loadTable;
	JPanel mainMenuPanel = new JPanel();
	JPanel characterPanel = new JPanel();
	JPanel playPanel = new JPanel();
	JPanel loadGamePanel = new JPanel();
	JPanel keybindsPanel = new JPanel();
	private JTextField txtfldNewKeybind;
	private JTable keybindsTable;
	private DefaultTableModel keybindsModel;
 

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
	private void setPanel() {
		mainMenuPanel.setVisible(false);
		characterPanel.setVisible(false);
		playPanel.setVisible(false);
		loadGamePanel.setVisible(false);
		keybindsPanel.setVisible(false);
	}
	
	private void setPanel(JPanel panel) {
		setPanel();
		panel.setVisible(true);
	}
	
	public MainMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		loadGamePanel.setLayout(null);
		loadGamePanel.setBounds(0, 0, 436, 262);
		contentPane.add(loadGamePanel);
		
		JButton btnLoad = new JButton("Load Game");
		btnLoad.setBounds(171, 214, 102, 22);
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
		
		characterPanel.setBounds(0, 0, 436, 263);
		contentPane.add(characterPanel);
		characterPanel.setLayout(null);
		
		JLabel skinImgPanel = new JLabel("Insert Skin 1");
		skinImgPanel.setBackground(new Color(0, 128, 255));
		skinImgPanel.setBounds(138, 62, 158, 121);
		characterPanel.add(skinImgPanel);
		
		JButton btnNextSkin = new JButton("-->");
		btnNextSkin.setBounds(306, 73, 52, 95);
		characterPanel.add(btnNextSkin);
		
		JButton btnPrevSkin = new JButton("<--");
		btnPrevSkin.setBounds(76, 73, 52, 95);
		characterPanel.add(btnPrevSkin);
		
		JButton btnCharacterBack = new JButton("<--");
		btnCharacterBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(mainMenuPanel);
			}
		});
		btnCharacterBack.setBounds(10, 240, 52, 23);
		characterPanel.add(btnCharacterBack);
		
		JLabel skinLockStatus = new JLabel("Locked");
		skinLockStatus.setFont(new Font("Tahoma", Font.PLAIN, 16));
		skinLockStatus.setBounds(180, 39, 80, 23);
		characterPanel.add(skinLockStatus);
		
		playPanel.setBounds(0, 0, 436, 262);
		contentPane.add(playPanel);
		playPanel.setLayout(null);
		
		JButton btnNewGame = new JButton("New Game");
		btnNewGame.setBounds(165, 47, 105, 22);
		playPanel.add(btnNewGame);
		
		JButton btnLoadGame = new JButton("Load Game");
		btnLoadGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(loadGamePanel);
			}
		});
		btnLoadGame.setBounds(165, 97, 105, 22);
		playPanel.add(btnLoadGame);
		
		JButton btnPlayBack = new JButton("<--");
		btnPlayBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(mainMenuPanel);
			}
		});
		btnPlayBack.setBounds(10, 229, 55, 22);
		playPanel.add(btnPlayBack);
		
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
		btnPlay.setBounds(160, 76, 113, 23);
		mainMenuPanel.add(btnPlay);
		
		JButton btnCharacter = new JButton("Character");
		btnCharacter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(characterPanel);
			}
		});
		btnCharacter.setBounds(160, 110, 113, 23);
		mainMenuPanel.add(btnCharacter);
		
		JButton btnKeybinds = new JButton("Keybinds");
		btnKeybinds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(keybindsPanel);
			}
		});
		btnKeybinds.setBounds(160, 144, 113, 23);
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
		btnExit.setBounds(160, 178, 113, 23);
		mainMenuPanel.add(btnExit);
		
		keybindsPanel.setBounds(0, 0, 436, 263);
		contentPane.add(keybindsPanel);
		keybindsPanel.setLayout(null);
		
		JScrollPane keybindScrollPane = new JScrollPane();
		keybindScrollPane.setBounds(10, 11, 235, 225);
		keybindsPanel.add(keybindScrollPane);
		
		keybindsTable = new JTable();
		keybindScrollPane.setViewportView(keybindsTable);
		String[] columnNames = {"Action", "Key"};
		Object[][] data = {
		    {"Move Forward", "W"},
		    {"Move Backward", "S"},
		    {"Move Left", "A"},
		    {"Move Right", "D"},
		    {"Sprint", "Shift"},
		    {"Crouch", "Ctrl"},
		    {"Interact", "E"},
		    {"Throw Item", "Q"},
		    {"Drop Item", "R"}
		};

		keybindsModel = new DefaultTableModel(data, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false; // user can’t directly edit cells
		    }
		};

		keybindsTable.setModel(keybindsModel);
		
		JButton btnSwapKeybind = new JButton("Swap");
		btnSwapKeybind.setBounds(255, 55, 88, 22);
		keybindsPanel.add(btnSwapKeybind);
		
		btnSwapKeybind.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {

		        int row = keybindsTable.getSelectedRow();
		        if (row == -1) {
		            JOptionPane.showMessageDialog(MainMenu.this, "Please select an action in the table first.");
		            return;
		        }

		        JOptionPane.showMessageDialog(MainMenu.this, "Press the new key now.");

		        // Small invisible window to capture exactly one key press
		        JFrame keyCapture = new JFrame();
		        keyCapture.setUndecorated(true);
		        keyCapture.setSize(200, 100);
		        keyCapture.setLocationRelativeTo(null);

		        keyCapture.addKeyListener(new KeyAdapter() {
		            @Override
		            public void keyPressed(KeyEvent ke) {

		                String newKey = KeyEvent.getKeyText(ke.getKeyCode());

		                // show it in the textbox
		                txtfldNewKeybind.setText(newKey);

		                // update table
		                keybindsModel.setValueAt(newKey, row, 1);

		                keyCapture.dispose();
		            }
		        });

		        keyCapture.setVisible(true);
		        keyCapture.requestFocus();
		    }
		});
		
		txtfldNewKeybind = new JTextField();
		txtfldNewKeybind.setBounds(255, 24, 96, 20);
		keybindsPanel.add(txtfldNewKeybind);
		txtfldNewKeybind.setColumns(10);
		
		JLabel lblKeybind = new JLabel("Insert new keybind");
		lblKeybind.setBounds(255, 11, 130, 14);
		keybindsPanel.add(lblKeybind);
		
		JButton btnKeybindsBack = new JButton("<--");
		btnKeybindsBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(mainMenuPanel);
			}
		});
		btnKeybindsBack.setBounds(0, 240, 59, 22);
		keybindsPanel.add(btnKeybindsBack);

	}
}
