import java.awt.EventQueue;
import javax.swing.ImageIcon;

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
	// character system variables
	private saveSystem saveData; // connects menu to save file data (skins + equipped skin)
	private boolean[] unlockedSkins; // tracks which skins are unlocked (true/false)
	private int equippedSkinIndex; // which skin is currently equipped
	private int currentSkinIndex; // which skin is currently being viewed in the menu
	private JLabel lblSkinName; // shows the current skin name (Rabbit, OldTimer, etc.)
	private JButton btnEquipSkin; // button to equip the selected skin (only if unlocked)

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
		skinImgPanel.setHorizontalAlignment(JLabel.CENTER); // centers the icon inside the label
		skinImgPanel.setVerticalAlignment(JLabel.CENTER); // centers vertically too

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
		lblSkinName = new JLabel("Rabbit"); // label that shows the skin name
		lblSkinName.setFont(new Font("Tahoma", Font.BOLD, 14)); // bold so it stands out
		lblSkinName.setBounds(165, 185, 120, 20); // position under the image
		characterPanel.add(lblSkinName); // add to panel

		btnEquipSkin = new JButton("Equip"); // equips current skin if unlocked
		btnEquipSkin.setBounds(170, 210, 100, 22); // position under name
		characterPanel.add(btnEquipSkin); // add to panel

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
		String[] columnNames = { "Action", "Key" };
		Object[][] data = { { "Move Forward", "W" }, { "Move Backward", "S" }, { "Move Left", "A" },
				{ "Move Right", "D" }, { "Sprint", "Shift" }, { "Crouch", "Ctrl" }, { "Interact", "E" },
				{ "Throw Item", "Q" }, { "Drop Item", "R" } };

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

				if (txtfldNewKeybind.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(MainMenu.this, "Please enter a new keybind.");
					return;
				} else if (txtfldNewKeybind.getText().trim().length() > 1) {
					JOptionPane.showMessageDialog(MainMenu.this, "Please enter a single character for the keybind.");
					return;
				} else if (!Character.isLetterOrDigit(txtfldNewKeybind.getText().trim().charAt(0))) {
					JOptionPane.showMessageDialog(MainMenu.this,
							"Please enter a valid alphanumeric character for the keybind.");
					return;
				} else if (keybindsModel.getDataVector().stream().anyMatch(
						rowData -> ((String) rowData.get(1)).equalsIgnoreCase(txtfldNewKeybind.getText().trim()))) {
					JOptionPane.showMessageDialog(MainMenu.this, "This keybind is already assigned to another action.");
					return;
				} else {
					// All validations passed, proceed to update the keybind
					keybindsModel.setValueAt(txtfldNewKeybind.getText().trim(), row, 1);
					JOptionPane.showMessageDialog(MainMenu.this, "Keybind updated successfully.");
				}
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

		// character system setup (Samir)

		saveData = new saveSystem(); // create save manager object
		saveData.loadFromFile(); // load save file so menu is accurate

		final String[] skinNames = { // skin names shown in GUI
				"Rabbit", // index 0
				"OldTimer", // index 1
				"Froseph", // index 2
				"Lifer", // index 3
				"BillyGoat", // index 4
				"Marv", // index 5
		}; // end names

		final String[] skinPaths = { // file paths for your imported sprites
				"src/assets/character1.png", // Rabbit
				"src/assets/character2.png", // OldTimer
				"src/assets/character3.png", // Froseph
				"src/assets/character4.png", // Lifer
				"src/assets/character5.png", // BillyGoat
				"src/assets/character6.png", // Marv
		}; // end paths

		unlockedSkins = saveData.getUnlockedSkinsCopy(); // get unlocked skins from save
		equippedSkinIndex = saveData.getEquippedSkinIndex(); // get equipped skin from save

		if (unlockedSkins.length != skinNames.length) { // if save file has wrong # of skins
			unlockedSkins = new boolean[skinNames.length]; // remake correct array size
			unlockedSkins[0] = true; // Rabbit always unlocked (default)
			equippedSkinIndex = 0; // default equipped is Rabbit
			saveData.setSkins(unlockedSkins, equippedSkinIndex); // store corrected skin data
			saveData.saveToFile(); // save corrected data to file
		}

		currentSkinIndex = equippedSkinIndex; // start viewing currently equipped skin

		Runnable refreshCharacterUI = new Runnable() { // updates image+ name + status
			public void run() { // redraw character menu UI

				lblSkinName.setText(skinNames[currentSkinIndex]); // show name of current skin

				if (unlockedSkins[currentSkinIndex]) { // if unlocked
					if (currentSkinIndex == equippedSkinIndex) { // if equipped
						skinLockStatus.setText("Equipped"); // show equipped
						btnEquipSkin.setEnabled(false); // disable equip button
					} else { // unlocked but not equipped
						skinLockStatus.setText("Unlocked"); // show unlocked
						btnEquipSkin.setEnabled(true); // allow equipping
					}
				} else { // locked
					skinLockStatus.setText("Locked"); // show locked
					btnEquipSkin.setEnabled(false); // cannot equip locked
				}

				ImageIcon icon = new ImageIcon(skinPaths[currentSkinIndex]); // load sprite image
				skinImgPanel.setIcon(icon); // set icon on label
				skinImgPanel.setText(""); // remove placeholder text
			}
		};

		refreshCharacterUI.run(); // update once at start

		btnNextSkin.addActionListener(new ActionListener() { // next skin button
			public void actionPerformed(ActionEvent e) { // when clicked
				currentSkinIndex++; // move forward
				if (currentSkinIndex >= skinNames.length) { // loop if past end
					currentSkinIndex = 0; // wrap back to 0
				}
				refreshCharacterUI.run(); // update UI
			}
		}); // end next

		btnPrevSkin.addActionListener(new ActionListener() { // previous skin button
			public void actionPerformed(ActionEvent e) { // when clicked
				currentSkinIndex--; // move back
				if (currentSkinIndex < 0) { // loop if before start
					currentSkinIndex = skinNames.length - 1; // wrap to last
				}
				refreshCharacterUI.run(); // update UI
			}
		}); // end prev

		btnEquipSkin.addActionListener(new ActionListener() { // equip button
			public void actionPerformed(ActionEvent e) { // when clicked
				if (!unlockedSkins[currentSkinIndex]) { // safety check if locked
					JOptionPane.showMessageDialog(MainMenu.this, "This skin is locked."); // popup
					return; // stop
				}
				equippedSkinIndex = currentSkinIndex; // set equipped skin
				saveData.setSkins(unlockedSkins, equippedSkinIndex); // store equipped skin in save
				saveData.saveToFile(); // save it to file
				refreshCharacterUI.run(); // update UI to show Equipped
			}
		});

		// end character system setup

	}
}
