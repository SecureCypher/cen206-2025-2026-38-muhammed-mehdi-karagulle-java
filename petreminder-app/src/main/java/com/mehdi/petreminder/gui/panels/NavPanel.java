/**
 * @file NavPanel.java
 * @brief Sol navigasyon paneli.
 */
package com.mehdi.petreminder.gui.panels;

import com.mehdi.petreminder.gui.MainFrame;
import com.mehdi.petreminder.gui.util.GuiConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @class NavPanel
 * @brief Sol navigasyon çubuğu — menü butonları.
 * @details Her buton ilgili karta navigasyon yapar.
 *          Aktif buton vurgulanır.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class NavPanel extends JPanel {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Ana pencere referansı. */
    private final MainFrame mainFrame;

    /** @brief Nav butonları haritası (cardName → button). */
    private final Map<String, JButton> buttons = new LinkedHashMap<>();

    /** @brief Aktif buton. */
    private String activeCard = "";

    /**
     * @brief NavPanel yapıcısı.
     * @param mainFrame Ana pencere
     */
    public NavPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(GuiConstants.NAV_COLOR);
        setPreferredSize(new Dimension(GuiConstants.NAV_WIDTH, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        buildNav();
    }

    /**
     * @brief Nav içeriğini oluşturur.
     */
    private void buildNav() {
        add(Box.createVerticalStrut(20));
        addLogo();
        add(Box.createVerticalStrut(24));
        addNavButton("🏠  Dashboard",    MainFrame.CARD_DASHBOARD);
        addNavButton("🐾  Hayvanlarım",  MainFrame.CARD_PETS);
        addNavButton("🔔  Hatırlatıcılar", MainFrame.CARD_REMINDERS);
        addNavButton("🏥  Vet Randevuları", MainFrame.CARD_VET);
        addNavButton("📋  Sağlık Kayıtları", MainFrame.CARD_MEDICAL);
        add(Box.createVerticalGlue());
        addNavButton("⚙️  Ayarlar",       MainFrame.CARD_SETTINGS);
        add(Box.createVerticalStrut(16));
    }

    /**
     * @brief Logo / başlık alanını ekler.
     */
    private void addLogo() {
        JLabel logo = new JLabel("🐶 PetCare");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(logo);

        JLabel sub = new JLabel("Reminder System");
        sub.setFont(GuiConstants.SMALL_FONT);
        sub.setForeground(new Color(160, 170, 190));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(sub);
    }

    /**
     * @brief Navigasyon butonu ekler.
     * @param label    Buton metni
     * @param cardName Kart adı
     */
    private void addNavButton(String label, String cardName) {
        JButton btn = new JButton(label);
        btn.setMaximumSize(new Dimension(GuiConstants.NAV_WIDTH - 16, 44));
        btn.setPreferredSize(new Dimension(GuiConstants.NAV_WIDTH - 16, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBackground(GuiConstants.NAV_COLOR);
        btn.setForeground(new Color(200, 210, 230));
        btn.setFont(GuiConstants.NAV_FONT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 16, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!cardName.equals(activeCard)) {
                    btn.setBackground(GuiConstants.NAV_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!cardName.equals(activeCard)) {
                    btn.setBackground(GuiConstants.NAV_COLOR);
                }
            }
        });

        btn.addActionListener(e -> mainFrame.navigateTo(cardName));
        buttons.put(cardName, btn);
        add(Box.createVerticalStrut(4));
        add(btn);
    }

    /**
     * @brief Aktif butonu vurgular.
     * @param cardName Aktif kart adı
     */
    public void setActiveButton(String cardName) {
        if (activeCard != null && buttons.containsKey(activeCard)) {
            JButton prev = buttons.get(activeCard);
            prev.setBackground(GuiConstants.NAV_COLOR);
            prev.setForeground(new Color(200, 210, 230));
        }
        activeCard = cardName;
        if (buttons.containsKey(cardName)) {
            JButton active = buttons.get(cardName);
            active.setBackground(GuiConstants.NAV_ACTIVE);
            active.setForeground(Color.WHITE);
        }
    }
}
