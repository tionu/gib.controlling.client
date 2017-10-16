package gib.controlling.client;

import javax.swing.JTextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class GuiAppender extends AppenderSkeleton {
	private JTextArea textArea;

	public GuiAppender(JTextArea textArea) {
		this.textArea = textArea;
	}

	protected void append(LoggingEvent event) {
		if (event.getLevel().equals(Level.INFO) || event.getLevel().equals(Level.WARN)) {
			textArea.append(event.getMessage().toString() + "\n");
		}
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}
}