/*
 * Copyright (c) 2010 MIT Strategic Engineering Research Group
 * 
 * This file is part of SpaceNet 2.5r2.
 * 
 * SpaceNet 2.5r2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SpaceNet 2.5r2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SpaceNet 2.5r2.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.mit.spacenet.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 * The splash screen is a window that is displayed while the rest of the
 * application is loading.
 * 
 * @author Paul Grogan, ptgrogan@mit.edu
 */
public class SplashScreen extends JWindow {
	private static final long serialVersionUID = -1859950222357089575L;

	/**
	 * Instantiates a new splash screen.
	 */
	public SplashScreen() {
		JPanel content = new JPanel(new GridBagLayout());
		content.setBackground(new Color(153,51,51));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		
		c.gridheight = 2;
		JLabel logo = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("icons/spacenet_splash.png")));
		logo.setPreferredSize(new Dimension(138,119));
		content.add(logo, c);
		
		c.gridx++;
		c.gridheight = 1;
		c.insets = new Insets(20, 20, 2, 20);
		c.anchor = GridBagConstraints.LAST_LINE_START;
		JLabel title = new JLabel ("SpaceNet 2.5");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Arial", Font.BOLD, 36));
		content.add(title, c);
		
		c.gridy++;
		c.insets = new Insets(2, 20, 10, 20);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		JLabel subtitle = new JLabel("Simulation and modeling software for space logistics");
		subtitle.setForeground(Color.WHITE);
		subtitle.setFont(new Font("Arial", Font.BOLD, 12));
		content.add(subtitle, c);
		
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 2;
		c.insets = new Insets(10, 20, 5, 20);
		c.anchor = GridBagConstraints.CENTER;
		JLabel loading = new JLabel("Loading...");
		loading.setFont(new Font ("Arial", Font.BOLD, 12));
		loading.setForeground(Color.WHITE);
		content.add(loading, c);
		
		c.gridy++;
		c.insets = new Insets(5, 20, 20, 20);
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setForeground(new Color(153, 51, 51));
		progressBar.setBackground(Color.WHITE);
		progressBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		content.add(progressBar, c);
		
		add(content);
	}
}
