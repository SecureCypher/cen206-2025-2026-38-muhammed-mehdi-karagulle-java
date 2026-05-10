/**
 * @file SettingsPanel.java
 * @brief Ayarlar paneli — runtime storage backend değiştirme.
 */
package com.mehdi.petreminder.gui.panels;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import com.mehdi.petreminder.gui.MainFrame;
import com.mehdi.petreminder.gui.util.GuiConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @class SettingsPanel
 * @brief Storage backend ve uygulama ayarları.
 * @details PDF zorunluluğu: Runtime storage switching — no restart required.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class SettingsPanel extends JPanel {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Ana pencere. */
    private final MainFrame mainFrame;

    /** @brief Storage seçici. */
    private JComboBox<StorageType> cmbStorage;

    /** @brief Mevcut backend etiketi. */
    private JLabel lblCurrent;

    /**
     * @brief SettingsPanel yapıcısı.
     * @param mainFrame Ana pencere
     */
    public SettingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(GuiConstants.BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(GuiConstants.GAP_LARGE, GuiConstants.GAP_LARGE,
            GuiConstants.GAP_LARGE, GuiConstants.GAP_LARGE));
        buildUI();
    }

    /**
     * Member documentation.
     */
    private void buildUI() {
        JLabel title = new JLabel("⚙️ Ayarlar");
        title.setFont(GuiConstants.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(Box.createVerticalStrut(32));
        center.add(buildStorageSection());
        center.add(Box.createVerticalStrut(24));
        center.add(buildInfoSection());
        add(center, BorderLayout.CENTER);
    }

    /**
     * Member documentation.
     */
    private JPanel buildStorageSection() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(GuiConstants.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GuiConstants.BORDER_COLOR),
            new EmptyBorder(24, 24, 24, 24)));
        card.setMaximumSize(new Dimension(500, 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel sectionTitle = new JLabel("Storage Backend");
        sectionTitle.setFont(GuiConstants.SECTION_FONT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(sectionTitle, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Mevcut:"), gbc);
        lblCurrent = new JLabel(StorageConfig.getActiveBackend().getDisplayName());
        lblCurrent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCurrent.setForeground(GuiConstants.PRIMARY);
        gbc.gridx = 1;
        card.add(lblCurrent, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Değiştir:"), gbc);
        cmbStorage = new JComboBox<>(StorageType.values());
        cmbStorage.setSelectedItem(StorageConfig.getActiveBackend());
        cmbStorage.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        card.add(cmbStorage, gbc);

        JButton btnApply = new JButton("Uygula");
        btnApply.setBackground(GuiConstants.PRIMARY);
        btnApply.setForeground(Color.WHITE);
        btnApply.setFocusPainted(false);
        btnApply.addActionListener(e -> applyStorage());
        gbc.gridx = 1; gbc.gridy = 3;
        card.add(btnApply, gbc);

        return card;
    }

    /**
     * Member documentation.
     */
    private JPanel buildInfoSection() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(GuiConstants.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GuiConstants.BORDER_COLOR),
            new EmptyBorder(24, 24, 24, 24)));
        card.setMaximumSize(new Dimension(500, 150));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel infoTitle = new JLabel("Uygulama Bilgileri");
        infoTitle.setFont(GuiConstants.SECTION_FONT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(infoTitle, gbc);
        gbc.gridwidth = 1;

        addInfoRow(card, gbc, 1, "Proje:", "Pet Care Reminder System v1.0");
        addInfoRow(card, gbc, 2, "Ders:", "CEN206 — OOP Term Project");
        addInfoRow(card, gbc, 3, "Takım:", "Mehdi / Ibrahim / Zümre");
        addInfoRow(card, gbc, 4, "Üniversite:", "RTEU — Spring 2026");
        return card;
    }

    /**
     * Member documentation.
     */
    private void addInfoRow(JPanel p, GridBagConstraints gbc, int row,
                             String key, String value) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.BOLD, 12));
        k.setForeground(GuiConstants.TEXT_SECONDARY);
        p.add(k, gbc);
        gbc.gridx = 1;
        JLabel v = new JLabel(value);
        v.setFont(GuiConstants.BODY_FONT);
        p.add(v, gbc);
    }

    /**
     * Member documentation.
     */
    private void applyStorage() {
        StorageType selected = (StorageType) cmbStorage.getSelectedItem();
        if (selected != null) {
            mainFrame.onStorageChanged(selected);
            lblCurrent.setText(selected.getDisplayName());
        }
    }

    /**
     * @brief Paneli yeniler.
     */
    public void refresh() {
        if (lblCurrent != null) {
            lblCurrent.setText(StorageConfig.getActiveBackend().getDisplayName());
        }
        if (cmbStorage != null) {
            cmbStorage.setSelectedItem(StorageConfig.getActiveBackend());
        }
    }
}
