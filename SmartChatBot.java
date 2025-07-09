import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.*;
public class SmartChatBot extends JFrame {
    private JTextPane chatPane = new JTextPane();
    private JTextField userInput = new JTextField();
    private JButton sendButton = new JButton("Send");
    private StyledDocument doc = chatPane.getStyledDocument();
    private Map<String, String> knowledgeBase = new HashMap<>();

    public SmartChatBot() {
        super(" SmartChatBot");
        setupDarkNimbus();
        initStyles();
        initKnowledge();
        setupUI();
        appendStyled("bot", "Hi! Ask me GK/GS, Java/OOP, or 'weather <city>'.");
    }

    private void setupDarkNimbus() {
        // Set global dark theme colors
        UIManager.put("control", new ColorUIResource(18, 18, 18));
        UIManager.put("info", new ColorUIResource(18, 18, 18));
        UIManager.put("nimbusBase", new ColorUIResource(18, 18, 18));
        UIManager.put("nimbusLightBackground", new ColorUIResource(18, 18, 18));
        UIManager.put("text", new ColorUIResource(230, 230, 230));
        UIManager.put("nimbusSelectionBackground", new ColorUIResource(104, 93, 156));

        // Fix JTextPane background painting under Nimbus
        UIManager.put("TextPane[Enabled].backgroundPainter", (Painter<JComponent>) (g, comp, w, h) -> {
            g.setColor(comp.getBackground());
            g.fillRect(0, 0, w, h);
        });

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void initStyles() {
        Style user = doc.addStyle("user", null);
        StyleConstants.setForeground(user, new Color(135, 206, 235));
        Style bot = doc.addStyle("bot", null);
        StyleConstants.setForeground(bot, new Color(144, 238, 144));
    }

    private void initKnowledge() {
        knowledgeBase.put("what is java", "Java is a high‑level OO language by Oracle.");
        knowledgeBase.put("encapsulation", "Encapsulation: hide fields with getters/setters.");
        knowledgeBase.put("capital of france", "Paris is the capital of France.");
        knowledgeBase.put("largest organ", "The skin is the largest organ in the human body.");
        knowledgeBase.put("speed of light", "The speed of light is ~299,792,458 m/s.");
    knowledgeBase.put("what planet is known as the red planet", "Mars is called the Red Planet.");
    knowledgeBase.put("who built the pyramids at giza", "The ancient Egyptians built the pyramids at Giza.");
    knowledgeBase.put("what was the antikythera mechanism", "An ancient Greek analog computer discovered in 1901.");
    knowledgeBase.put("what is java", "Java is a platform‑independent, object‑oriented programming language.");
    knowledgeBase.put("difference between jdk jre jvm", "JDK compiles & develops, JRE runs bytecode, JVM executes it.");
    knowledgeBase.put("is java pass by value or reference", "Java is strictly pass‑by‑value—even object references are passed by value.");
    knowledgeBase.put("difference between arraylist and vector", "ArrayList is unsynchronized, Vector is synchronized.");
    knowledgeBase.put("what is multithreading", "Multithreading allows concurrent execution by multiple threads.");
    knowledgeBase.put("how to create array in java", "int[] arr = new int[5]; or int[] arr2 = {1,2,3};");
}

    private void setupUI() {
        setSize(700, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        chatPane.setEditable(false);
        chatPane.setFont(new Font("Consolas", Font.PLAIN, 16));
        chatPane.setBackground(Color.BLACK);
        chatPane.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(chatPane);
        scroll.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        scroll.getViewport().setBackground(Color.BLACK);
        add(scroll, BorderLayout.CENTER);

        userInput.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userInput.setBorder(new RoundedBorder(10));
        userInput.setBackground(new Color(30,30,30));
        userInput.setForeground(Color.WHITE);

        sendButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        sendButton.setBackground(new Color(60,60,60));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(new RoundedBorder(10));

        JPanel bottom = new JPanel(new BorderLayout(10,0));
        bottom.setBackground(new Color(18,18,18));
        bottom.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        bottom.add(userInput, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        ActionListener listener = e -> processInput();
        userInput.addActionListener(listener);
        sendButton.addActionListener(listener);

        setVisible(true);
    }

    private void processInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) return;
        appendStyled("user", input);
        userInput.setText("");
        String reply;
        if (input.toLowerCase().startsWith("weather ")) {
            reply = getWeather(input.substring(8).trim());
        } else {
            reply = lookupAnswer(input.toLowerCase());
        }
        appendStyled("bot", reply);
    }

    private void appendStyled(String style, String text) {
        try {
            doc.insertString(doc.getLength(), (style.equals("user") ? "You: " : "Bot: ") + text + "\n\n", doc.getStyle(style));
            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String lookupAnswer(String q) {
        for (String key : knowledgeBase.keySet()) {
            if (q.contains(key)) return knowledgeBase.get(key);
        }
        return " Sorry, I don't know that yet.";
    }

    private String getWeather(String city) {
        try {
            String api = "fa4bef34eec965725f10cd3b7da9cdff";
            String json = new BufferedReader(new InputStreamReader(
                new URL(String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                    URLEncoder.encode(city,"UTF-8"), api)).openStream()))
                .lines().reduce("", String::concat);
            String temp = extract(json, "\"temp\"\\s*:\\s*([\\d\\.\\-]+)");
            String desc = extract(json, "\"description\"\\s*:\\s*\"([^\"]+)\"");
            return String.format(" Weather in %s: %s, %s°C", city, desc, temp);
        } catch (Exception ex) {
            return " Error: " + ex.getMessage();
        }
    }

    private String extract(String txt, String regex) {
        Matcher m = Pattern.compile(regex).matcher(txt);
        return m.find() ? m.group(1) : "?";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartChatBot::new);
    }

    static class RoundedBorder extends AbstractBorder {
        private int r;
        RoundedBorder(int r) { this.r = r; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(Color.DARK_GRAY);
            g.drawRoundRect(x, y, w-1, h-1, r, r);
        }
        public Insets getBorderInsets(Component c) { return new Insets(6,10,6,10); }
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(6,10,6,10); return insets;
        }
    }
}
