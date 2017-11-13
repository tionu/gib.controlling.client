package gib.controlling.client;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class GuiAppender extends AppenderSkeleton {

	private static GuiAppender instance;

	private JFrame frame;
	private JTextArea textArea;

	private GuiAppender() {
		frame = new JFrame();
		textArea = new JTextArea(6, 30);
		drawGui();
	}

	public static GuiAppender getInstance() {
		if (instance == null) {
			instance = new GuiAppender();
		}
		return instance;
	}

	protected void append(LoggingEvent event) {
		if (!event.getLoggerName().toString().startsWith("gib.controlling")) {
			return;
		}
		if (event.getLevel().equals(Level.INFO) || event.getLevel().equals(Level.WARN)) {
			textArea.append(event.getMessage().toString() + "\n");
		}
	}

	public void hide() {
		frame.setVisible(false);
	}

	public void show() {
		frame.setVisible(true);
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}

	private void drawGui() {
		frame.setTitle("KlimaOnline");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		textArea.setSelectedTextColor(Color.BLUE);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		panel.add(scrollPane);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public JTextArea getTextArea() {
		return textArea;
	}

}