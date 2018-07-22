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
package edu.mit.spacenet.gui.command;

import java.awt.Cursor;

import org.jdesktop.swingworker.SwingWorker;

import edu.mit.spacenet.gui.SpaceNetFrame;

/**
 * The command to save a scenario with the current filename (launches a save as
 * command if no file name exists).
 * 
 * @author Paul Grogan
 */
public class SaveScenarioCommand implements I_Command {
	private SpaceNetFrame spaceNetFrame;
	
	/**
	 * The constructor.
	 * 
	 * @param spaceNetFrame the SpaceNet frame
	 */
	public SaveScenarioCommand(SpaceNetFrame spaceNetFrame) {
		this.spaceNetFrame=spaceNetFrame;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.spacenet.gui.command.I_Command#execute()
	 */
	public void execute() {
		if(spaceNetFrame.getScenarioPanel().getScenario().getFilePath() == null) {
			SaveScenarioAsCommand command = new SaveScenarioAsCommand(spaceNetFrame);
			command.execute();
		} else {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				public Void doInBackground() {
					SpaceNetFrame.getInstance().getStatusBar().setStatusMessage("Saving Scenario " 
							+ spaceNetFrame.getScenarioPanel().getScenario().getFileName()
							+ "...");
					spaceNetFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					spaceNetFrame.saveScenario();
					return null;
				}
				public void done() {
					SpaceNetFrame.getInstance().getStatusBar().clearStatusMessage();
					spaceNetFrame.setCursor(Cursor.getDefaultCursor());
					SpaceNetFrame.getInstance().getStatusBar().setStatusMessage("Scenario " 
							+ spaceNetFrame.getScenarioPanel().getScenario().getFileName() 
							+ " Saved Successfully", false);
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
			            public void run() {
			            	try {
			            		Thread.sleep(3000);
			            	} catch(InterruptedException e) { }
			            	spaceNetFrame.getStatusBar().setStatusMessage("", false);
			            }
		            });
					
				}
			};
			worker.execute();
		}
	}
}
