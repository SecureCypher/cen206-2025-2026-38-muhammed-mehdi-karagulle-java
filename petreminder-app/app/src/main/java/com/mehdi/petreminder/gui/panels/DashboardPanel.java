/**
 * @file DashboardPanel.java
 * @brief Dashboard — özet istatistikler ve son hatırlatıcılar.
 */
package com.mehdi.petreminder.gui.panels;

import com.mehdi.petreminder.gui.MainFrame;
import com.mehdi.petreminder.gui.util.GuiConstants;
import com.mehdi.petreminder.model.Reminder;
import com.mehdi.petreminder.service.MedicalRecordService;
import com.mehdi.petreminder.service.PetService;
import com.mehdi.petreminder.service.ReminderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * @class DashboardPanel
 * @brief Ana kontrol paneli — özet kartlar ve bekleyen hatırlatıcılar.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class DashboardPanel extends JPanel {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Ana pencere. */
    private final MainFrame mainFrame;

    /** @brief Servisler. */
    private final PetService petService;
    /** @brief Reminder service. */
    private final ReminderService reminderService;
    /** @brief Medical service. */
    private final MedicalRecordService medicalService;

    /** @brief Özet etiketleri: pet, hatırlatıcı, gecikmiş, sağlık sayıları. */
    private JLabel lblPetCount, lblReminderCount, lblOverdueCount, lblMedicalCount;

    /** @brief Bekleyen liste modeli. */
    private DefaultListModel<String> pendingModel;

    /**
     * @brief DashboardPanel yapıcısı.
     * @param mainFrame Ana pencere
     */
    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        petService      = new PetService();
        reminderService = new ReminderService();
        medicalService  = new MedicalRecordService();
        setBackground(GuiConstants.BG_COLOR);
        setLayout(new BorderLayout(GuiConstants.GAP, GuiConstants.GAP));
        setBorder(new EmptyBorder(GuiConstants.GAP_LARGE, GuiConstants.GAP_LARGE,
            GuiConstants.GAP_LARGE, GuiConstants.GAP_LARGE));
        buildUI();
    }

    /**
     * @brief UI bileşenlerini oluşturur.
     */
    private void buildUI() {
        // Başlık
        JLabel title = new JLabel("Dashboard");
        title.setFont(GuiConstants.TITLE_FONT);
        title.setForeground(GuiConstants.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        // Orta — özet kartlar + liste
        JPanel center = new JPanel(new BorderLayout(GuiConstants.GAP, GuiConstants.GAP));
        center.setOpaque(false);

        center.add(buildSummaryCards(), BorderLayout.NORTH);
        center.add(buildPendingSection(), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    /**
     * @brief Özet kart satırını oluşturur.
     * @return Panel
     */
    private JPanel buildSummaryCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, GuiConstants.GAP, 0));
        row.setOpaque(false);

        lblPetCount      = new JLabel("0", SwingConstants.CENTER);
        lblReminderCount = new JLabel("0", SwingConstants.CENTER);
        lblOverdueCount  = new JLabel("0", SwingConstants.CENTER);
        lblMedicalCount  = new JLabel("0", SwingConstants.CENTER);

        row.add(summaryCard("🐾 Hayvanlar",       lblPetCount,      GuiConstants.PRIMARY));
        row.add(summaryCard("🔔 Hatırlatıcılar",  lblReminderCount, GuiConstants.SUCCESS));
        row.add(summaryCard("⚠️ Gecikmiş",         lblOverdueCount,  GuiConstants.DANGER));
        row.add(summaryCard("📋 Sağlık Kayıtları", lblMedicalCount,  GuiConstants.WARNING));
        return row;
    }

    /**
     * @brief Tek bir özet kartı oluşturur.
     * @param label  Kart etiketi
     * @param value  Değer etiketi
     * @param color  Kart rengi
     * @return Kart paneli
     */
    private JPanel summaryCard(String label, JLabel value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(GuiConstants.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GuiConstants.BORDER_COLOR),
            new EmptyBorder(20, 20, 20, 20)));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(GuiConstants.BODY_FONT);
        lbl.setForeground(GuiConstants.TEXT_SECONDARY);

        value.setFont(new Font("Segoe UI", Font.BOLD, 36));
        value.setForeground(color);

        card.add(lbl,   BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    /**
     * @brief Bekleyen hatırlatıcılar bölümünü oluşturur.
     * @return Panel
     */
    private JPanel buildPendingSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GuiConstants.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GuiConstants.BORDER_COLOR),
            new EmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Bekleyen Hatırlatıcılar");
        title.setFont(GuiConstants.SECTION_FONT);
        title.setForeground(GuiConstants.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        pendingModel = new DefaultListModel<>();
        JList<String> list = new JList<>(pendingModel);
        list.setFont(GuiConstants.BODY_FONT);
        list.setSelectionBackground(new Color(238, 242, 255));
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton btnGoReminders = new JButton("Tümünü Gör →");
        btnGoReminders.setBackground(GuiConstants.PRIMARY);
        btnGoReminders.setForeground(Color.WHITE);
        btnGoReminders.setFocusPainted(false);
        btnGoReminders.addActionListener(e -> mainFrame.navigateTo(MainFrame.CARD_REMINDERS));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setOpaque(false);
        south.add(btnGoReminders);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * @brief Verileri yeniler.
     */
    public void refresh() {
        try {
            lblPetCount.setText(String.valueOf(petService.getPetCount()));
            lblReminderCount.setText(String.valueOf(reminderService.getReminderCount()));
            List<Reminder> overdue = reminderService.getOverdueReminders();
            lblOverdueCount.setText(String.valueOf(overdue.size()));
            lblMedicalCount.setText(String.valueOf(medicalService.getRecordCount()));

            pendingModel.clear();
            List<Reminder> pending = reminderService.getPendingReminders();
            for (Reminder r : pending) {
                String item = String.format("[%s] %s — %s %s",
                    r.getPriority(),
                    r.getPetName(),
                    r.getReminderType(),
                    r.getFormattedTime());
                if (r.isOverdue()) item = "⚠️ " + item;
                pendingModel.addElement(item);
            }
        } catch (Exception e) {
            // Veri yoksa sessizce devam et
        }
    }
}
