/*
	Copyright 2013-2016 Mario Pascucci <mpascucci@gmail.com>
	This file is part of AppSettings.

	AppSettings is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	AppSettings is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with AppSettings.  If not, see <http://www.gnu.org/licenses/>.

*/


package bricksnspace.appsettings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;




public class OptionsDialog extends JDialog implements ActionListener {


	private static final long serialVersionUID = -2663213467371102L;
	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JButton cancelButton;
	private Vector<JComponent> controls = new Vector<JComponent>();
	private int userChoice = JOptionPane.CANCEL_OPTION;

	/**
	 * Create the dialog.
	 */
	public OptionsDialog(JFrame frame, String title, boolean modal) {
		
		super(frame,title,modal);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		buttonPane.add(cancelButton);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;

		gbc.gridy = 0;
		
		for (String k : new TreeSet<String>(AppSettings.getPrefsList())) {
			gbc.gridx = 0;
			switch (AppSettings.getType(k)) {
			case AppSettings.INTEGER:
				JLabel l = new JLabel(AppSettings.getDescr(k));
				l.setHorizontalAlignment(JLabel.RIGHT);
				contentPanel.add(l,gbc);
				JSpinner sp = new JSpinner();
				sp.setName(k);
				sp.setValue(AppSettings.getInt(k));
				gbc.gridx = 1;
				contentPanel.add(sp,gbc);
				controls.add(sp);
				break;
			case AppSettings.BOOLEAN:
				JCheckBox ck = new JCheckBox(AppSettings.getDescr(k));
				ck.setName(k);
				ck.setSelected(AppSettings.getBool(k));
				gbc.gridx = 1;
				contentPanel.add(ck,gbc);
				controls.add(ck);
				break;
			case AppSettings.STRING:
				JLabel lab = new JLabel(AppSettings.getDescr(k));
				lab.setHorizontalAlignment(JLabel.RIGHT);
				contentPanel.add(lab,gbc);
				JTextField s = new JTextField(AppSettings.get(k));
				s.setName(k);
				gbc.gridx = 1;
				contentPanel.add(s,gbc);
				controls.add(s);
				break;
			case AppSettings.FLOAT:
				JLabel lab1 = new JLabel(AppSettings.getDescr(k));
				lab1.setHorizontalAlignment(JLabel.RIGHT);
				contentPanel.add(lab1,gbc);
				JTextField n = new JTextField(String.format(Locale.US,"%s", AppSettings.getFloat(k)));
				n.setName(k);
				gbc.gridx = 1;
				contentPanel.add(n,gbc);
				controls.add(n);
				break;
			case AppSettings.FILE:
			case AppSettings.FOLDER:
				JLabel lab2 = new JLabel(AppSettings.getDescr(k));
				lab2.setHorizontalAlignment(JLabel.RIGHT);
				contentPanel.add(lab2,gbc);
				JButton s1 = new JButton(AppSettings.get(k));
				s1.setName(k);
				s1.setBorderPainted(false);
				s1.addActionListener(this);
				gbc.gridx = 1;
				contentPanel.add(s1,gbc);
				controls.add(s1);
				break;
			}
			gbc.gridy++;
	
		}
		
		pack();
	}

	
	
	public int getResponse() {
		
		return userChoice;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == cancelButton) {
			userChoice = JOptionPane.CANCEL_OPTION;
			setVisible(false);
		}
		else if (e.getSource() == okButton) {
			userChoice = JOptionPane.OK_OPTION;
			setVisible(false);
			for (JComponent c : controls) {
				if (c.getClass() == JCheckBox.class) {
					AppSettings.putBool(c.getName(),((JCheckBox)c).isSelected());
				}
				else if (c.getClass() == JSpinner.class) {
					AppSettings.putInt(c.getName(),(Integer)((JSpinner)c).getValue());
				}
				else if (c.getClass() == JTextField.class) {
					if (AppSettings.getType(c.getName()) == AppSettings.STRING) { 
						AppSettings.put(c.getName(),((JTextField)c).getText());
					}
					else if (AppSettings.getType(c.getName()) == AppSettings.FLOAT) {
						try {
							AppSettings.putFloat(c.getName(), Float
									.parseFloat(((JTextField) c).getText()));
						} catch (NumberFormatException e2) {
							AppSettings.putFloat(c.getName(), 0f);
						}
					}
				}
			}
		}
		else if (e.getSource() instanceof JButton) {
			String name = ((JButton)e.getSource()).getName();
			JFileChooser dlg = new JFileChooser(AppSettings.get(name));
			if (AppSettings.getType(name) == AppSettings.FILE) {
				dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
			else if (AppSettings.getType(name) == AppSettings.FOLDER) {
				dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			}
			dlg.setDialogTitle(AppSettings.getDescr(name));
			int res = dlg.showOpenDialog(this);
			if (res == JFileChooser.APPROVE_OPTION) {
				try {
					String file = dlg.getSelectedFile().getCanonicalPath();
					((JButton)e.getSource()).setText(file);
					AppSettings.put(name, file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}

}
