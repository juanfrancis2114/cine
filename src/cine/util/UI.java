package cine.util;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/** Utilidades de estilo para una interfaz Swing moderna y consistente. */
public class UI {
    public static final Color PRIMARY   = new Color(0x1F3864);
    public static final Color PRIMARY_2 = new Color(0x2E5496);
    public static final Color ACCENT    = new Color(0x0E9F9A);
    public static final Color BG        = new Color(0xF4F6FA);
    public static final Color CARD      = Color.WHITE;
    public static final Color TEXT      = new Color(0x21262D);
    public static final Color MUTED     = new Color(0x6B7280);
    public static final Color DANGER    = new Color(0xC0392B);
    public static final Color SUCCESS   = new Color(0x2E7D32);
    public static final Color BORDER    = new Color(0xD9DEE7);
    public static final Color STRIPE    = new Color(0xEEF2F8);

    public static final Font H1     = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font H2     = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BODY_B = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL  = new Font("Segoe UI", Font.PLAIN, 12);

    public static void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(i.getName())) { UIManager.setLookAndFeel(i.getClassName()); break; }
            }
            UIManager.put("control", BG);
            UIManager.put("nimbusBase", PRIMARY);
            UIManager.put("nimbusFocus", ACCENT);
            UIManager.put("nimbusSelectionBackground", PRIMARY_2);
        } catch (Exception e) { /* fallback */ }
    }

    public static JButton flatButton(String text, final Color bg, final Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = bg;
                if (!isEnabled())                 c = new Color(0xC2C8D0);
                else if (getModel().isPressed())  c = bg.darker();
                else if (getModel().isRollover()) c = brighter(bg);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(fg);
        b.setFont(BODY_B);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
    private static Color brighter(Color c){
        return new Color(Math.min(255,(int)(c.getRed()*1.12+10)),
                         Math.min(255,(int)(c.getGreen()*1.12+10)),
                         Math.min(255,(int)(c.getBlue()*1.12+10)));
    }
    public static JButton primary(String t){ return flatButton(t, PRIMARY_2, Color.WHITE); }
    public static JButton success(String t){ return flatButton(t, SUCCESS, Color.WHITE); }
    public static JButton danger(String t){ return flatButton(t, DANGER, Color.WHITE); }
    public static JButton accent(String t){ return flatButton(t, ACCENT, Color.WHITE); }
    public static JButton ghost(String t){ return flatButton(t, new Color(0xE6EBF3), PRIMARY); }

    public static JLabel title(String t){ JLabel l=new JLabel(t); l.setFont(H1); l.setForeground(Color.WHITE); return l; }
    public static JLabel subtitle(String t){ JLabel l=new JLabel(t); l.setFont(BODY); l.setForeground(new Color(0xCBD6E8)); return l; }
    public static JLabel h2(String t){ JLabel l=new JLabel(t); l.setFont(H2); l.setForeground(PRIMARY); return l; }
    public static JLabel field(String t){ JLabel l=new JLabel(t); l.setFont(BODY_B); l.setForeground(TEXT); return l; }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static JPanel header(String titleText, String subText) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(PRIMARY);
        h.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        JPanel texts = new JPanel(new GridLayout(2,1));
        texts.setOpaque(false);
        texts.add(title(titleText));
        texts.add(subtitle(subText));
        h.add(texts, BorderLayout.WEST);
        return h;
    }

    public static void styleTable(JTable t) {
        t.setRowHeight(30);
        t.setFont(BODY);
        t.setGridColor(BORDER);
        t.setShowVerticalLines(false);
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(new Color(0xCFE0F5));
        t.setSelectionForeground(TEXT);
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 34));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, s, f, r, c);
                l.setBackground(PRIMARY); l.setForeground(Color.WHITE);
                l.setFont(BODY_B); l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return l;
            }
        });
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tb, v, s, f, r, c);
                if (!s) comp.setBackground(r % 2 == 0 ? Color.WHITE : STRIPE);
                ((JLabel) comp).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return comp;
            }
        });
    }

    public static JScrollPane scroll(JComponent c){
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }
}
