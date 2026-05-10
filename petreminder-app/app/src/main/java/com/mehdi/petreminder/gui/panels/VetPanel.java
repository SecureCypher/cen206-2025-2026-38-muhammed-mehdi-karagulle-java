/**
 * @file VetPanel.java
 * @brief Veteriner randevuları paneli.
 */
package com.mehdi.petreminder.gui.panels;

import com.mehdi.petreminder.gui.MainFrame;
import com.mehdi.petreminder.gui.util.GuiConstants;
import com.mehdi.petreminder.model.*;
import com.mehdi.petreminder.service.ReminderService;
import com.mehdi.petreminder.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @class VetPanel
 * @brief Veteriner randevuları CRUD.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class VetPanel extends JPanel {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Ana pencere. */
    private final MainFrame mainFrame;

    /** @brief Reminder service. */
    private final ReminderService reminderService;

    /** @brief Tablo modeli. */
    private DefaultTableModel tableModel;

    /** @brief Tablo. */
    private JTable table;

    /**
     * @brief VetPanel yapıcısı.
     * @param mainFrame Ana pencere
     */
    public VetPanel(MainFrame mainFrame) {
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
        JLabel title = new JLabel("🏥 Veteriner Randevuları");
        title.setFont(GuiConstants.TITLE_FONT);
        add(title, BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    /**
     * Member documentation.
     */
    private JScrollPane buildTable() {
        String[] cols = {"ID", "Pet", "Veteriner", "Klinik", "Sebep", "Zaman", "Onay"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(GuiConstants.BODY_FONT);
        table.setGridColor(GuiConstants.BORDER_COLOR);
        return new JScrollPane(table);
    }

    /**
     * Member documentation.
     */
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.add(btn("➕ Yeni Randevu", GuiConstants.PRIMARY, e -> addAppointment()));
        bar.add(btn("✅ Onayla", GuiConstants.SUCCESS, e -> confirmSelected()));
        bar.add(btn("🗑️ Sil", GuiConstants.DANGER, e -> deleteSelected()));
        return bar;
    }

    /**
     * Member documentation.
     */
    private void addAppointment() {
        JTextField petId     = new JTextField("1");
        JTextField petName   = new JTextField();
        JTextField vetName   = new JTextField();
        JTextField clinic    = new JTextField();
        JTextField reason    = new JTextField();
        JTextField time      = new JTextField(LocalDateTime.now().plusDays(1)
                                              .toString().substring(0, 16));

        Object[] fields = {
            "Pet ID:", petId, "Pet Adı:", petName,
            "Veteriner:", vetName, "Klinik:", clinic,
            "Sebep:", reason, "Zaman (yyyy-MM-ddTHH:mm):", time
        };
        int res = JOptionPane.showConfirmDialog(this, fields, "Yeni Randevu",
            JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                VetAppointment va = new VetAppointment(0,
                    Integer.parseInt(petId.getText().trim()),
                    petName.getText().trim(),
                    LocalDateTime.parse(time.getText().trim()),
                    vetName.getText().trim(),
                    clinic.getText().trim(),
                    reason.getText().trim());
                reminderService.addReminder(va);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Member documentation.
     */
    private void confirmSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Reminder r = reminderService.getReminderById(id);
            if (r instanceof VetAppointment) {
                ((VetAppointment) r).setConfirmed(true);
                reminderService.updateReminder(r);
                refresh();
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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
            List<Reminder> all = reminderService.getAllReminders();
            List<VetAppointment> vets = all.stream()
                .filter(r -> r instanceof VetAppointment)
                .map(r -> (VetAppointment) r)
                .collect(Collectors.toList());
            for (VetAppointment va : vets) {
                tableModel.addRow(new Object[]{
                    va.getId(), va.getPetName(), va.getVeterinarianName(),
                    va.getClinicName(), va.getReason(), va.getFormattedTime(),
                    va.isConfirmed() ? "✅" : "⏳"
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
