/**
 * @file RemindersPanel.java
 * @brief Hatırlatıcılar yönetim paneli.
 */
package com.mehdi.petreminder.gui.panels;

import com.mehdi.petreminder.gui.MainFrame;
import com.mehdi.petreminder.gui.dialogs.ReminderDialog;
import com.mehdi.petreminder.gui.util.GuiConstants;
import com.mehdi.petreminder.model.Reminder;
import com.mehdi.petreminder.service.ReminderService;
import com.mehdi.petreminder.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * @class RemindersPanel
 * @brief Hatırlatıcılar CRUD paneli.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class RemindersPanel extends JPanel {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Ana pencere. */
    private final MainFrame mainFrame;

    /** @brief Reminder servisi. */
    private final ReminderService reminderService;

    /** @brief Tablo modeli. */
    private DefaultTableModel tableModel;

    /** @brief Tablo. */
    private JTable table;

    /** @brief Filtre — sadece bekleyenler. */
    private JCheckBox chkOnlyPending;

    /**
     * @brief RemindersPanel yapıcısı.
     * @param mainFrame Ana pencere
     */
    public RemindersPanel(MainFrame mainFrame) {
        this.mainFrame       = mainFrame;
        this.reminderService = new ReminderService();
        setBackground(GuiConstants.BG_COLOR);
        setLayout(new BorderLayout(GuiConstants.GAP, GuiConstants.GAP));
        setBorder(new EmptyBorder(GuiConstants.GAP_LARGE, GuiConstants.GAP_LARGE,
            GuiConstants.GAP_LARGE, GuiConstants.GAP_LARGE));
        buildUI();
    }

    /**
     * Member documentation.
     */
    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    /**
     * Member documentation.
     */
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel title = new JLabel("🔔 Hatırlatıcılar");
        title.setFont(GuiConstants.TITLE_FONT);
        p.add(title, BorderLayout.WEST);
        chkOnlyPending = new JCheckBox("Sadece bekleyenler");
        chkOnlyPending.addActionListener(e -> refresh());
        p.add(chkOnlyPending, BorderLayout.EAST);
        return p;
    }

    /**
     * Member documentation.
     */
    private JScrollPane buildTable() {
        String[] cols = {"ID", "Pet", "Tür", "Öncelik", "Zaman", "Durum"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(GuiConstants.BODY_FONT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setGridColor(GuiConstants.BORDER_COLOR);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });
        return new JScrollPane(table);
    }

    /**
     * Member documentation.
     */
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.add(btn("➕ Yeni", GuiConstants.PRIMARY, e -> addReminder()));
        bar.add(btn("✏️ Düzenle", null, e -> editSelected()));
        bar.add(btn("✅ Tamamla", GuiConstants.SUCCESS, e -> completeSelected()));
        bar.add(btn("🗑️ Sil", GuiConstants.DANGER, e -> deleteSelected()));
        return bar;
    }

    /**
     * Member documentation.
     */
    private void addReminder() {
        ReminderDialog dlg = new ReminderDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            try {
                reminderService.addReminder(dlg.getReminder());
                refresh();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Member documentation.
     */
    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Reminder r = reminderService.getReminderById(id);
            ReminderDialog dlg = new ReminderDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), r);
            dlg.setVisible(true);
            if (dlg.isConfirmed()) {
                reminderService.updateReminder(dlg.getReminder());
                refresh();
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Member documentation.
     */
    private void completeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Lütfen bir hatırlatıcı seçin.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String currentStatus = (String) tableModel.getValueAt(row, 5);
        if (currentStatus != null && currentStatus.contains("Tamamlandı")) {
            JOptionPane.showMessageDialog(this, "Bu hatırlatıcı zaten tamamlandı.");
            return;
        }
        try {
            reminderService.markCompleted(id);
            refresh();
            JOptionPane.showMessageDialog(this, "Hatırlatıcı tamamlandı olarak işaretlendi!",
                "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Member documentation.
     */
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Silinsin mi?", "Onayla",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            reminderService.deleteReminder((int) tableModel.getValueAt(row, 0));
            refresh();
        }
    }

    /**
     * @brief Tabloyu yeniler.
     */
    public void refresh() {
        tableModel.setRowCount(0);
        try {
            List<Reminder> list = chkOnlyPending != null && chkOnlyPending.isSelected()
                ? reminderService.getPendingReminders()
                : reminderService.getAllReminders();
            for (Reminder r : list) {
                String status = r.isCompleted() ? "✅ Tamamlandı"
                    : r.isOverdue() ? "⚠️ Gecikmiş" : "⏳ Bekliyor";
                tableModel.addRow(new Object[]{
                    r.getId(), r.getPetName(), r.getReminderType(),
                    r.getPriority(), r.getFormattedTime(), status
                });
            }
        } catch (Exception e) { /* sessiz */ }
    }

    /**
     * Member documentation.
     */
    private JButton btn(String text, Color bg, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        if (bg != null) { b.setBackground(bg); b.setForeground(Color.WHITE); }
        b.setFocusPainted(false);
        b.addActionListener(al);
        return b;
    }
}