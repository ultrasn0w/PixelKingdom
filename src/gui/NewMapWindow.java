package gui;

import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gfx.SpriteSheet;
import gui.menu.MapSelection;
import main.Game;
import map.Map;

public class NewMapWindow extends JFrame {
	private static final long serialVersionUID = 9218100846608549089L;
	private static final int WIDTH = 400, HEIGHT = 90;
	
	private Label nameLabel;
	private JTextField nameField;
	private JButton addButton, cancelButton;
	
	public NewMapWindow(Game game, MapSelection mapSelection) {
		this.setSize(WIDTH+10, HEIGHT+30);
		this.setResizable(false);
		this.setLayout(null);
		
		this.setTitle("Creating a new Map");
		this.setAlwaysOnTop(true);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent){
				dispose();
			}
		});
		
		try {this.setIconImage(ImageIO.read(SpriteSheet.class.getResourceAsStream("/Buttons/WindowIconNew.png")));} catch (IOException e) {e.printStackTrace();}
		
		nameLabel = new Label();
		nameLabel.setBounds(10, 10, 140, 30);
		nameLabel.setFont(new Font("Arial",0,20));
		nameLabel.setText("Map Name :");
		this.add(nameLabel);
		
		nameField = new JTextField();
		nameField.setBounds(150, 10, 240, 30);
		nameField.setFont(new Font("Arial",0,20));
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {checkTextFields();}
			public void insertUpdate(DocumentEvent arg0) {checkTextFields();}
			public void removeUpdate(DocumentEvent arg0) {checkTextFields();}
		});
		this.add(nameField);
		
		addButton = new JButton();
		addButton.setBounds(10, 50, WIDTH/2-20, 30);
		addButton.setFont(new Font("Arial",0,20));
		addButton.setText("Create Map");
		addButton.setEnabled(false);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		        Map.newMap(MapSelection.FILE_DIR,nameField.getText());
				mapSelection.refresh();
				dispose();
			}
		});
		this.add(addButton);
		
		cancelButton = new JButton();
		cancelButton.setBounds(WIDTH/2+10, 50, WIDTH/2-20, 30);
		cancelButton.setFont(new Font("Arial",0,20));
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		this.add(cancelButton);
		
		this.setVisible(true);
	}
	
	private void checkTextFields() {
		addButton.setEnabled(nameField.getText().length()>0);
	}
}
