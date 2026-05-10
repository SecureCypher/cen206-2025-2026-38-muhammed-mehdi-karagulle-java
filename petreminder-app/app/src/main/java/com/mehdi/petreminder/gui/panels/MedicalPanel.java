/**
 * @file MedicalPanel.java
 * @brief Sağlık kayıtları yönetim paneli.
 */
package com.mehdi.petreminder.gui.panels;

import com.mehdi.petreminder.gui.MainFrame;
import com.mehdi.petreminder.gui.util.GuiConstants;
import com.mehdi.petreminder.model.MedicalRecord;
import com.mehdi.petreminder.service.MedicalRecordService;
import com.mehdi.petreminder.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * @class MedicalPanel
 * @brief Sağlık kayıtları CRUD paneli.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class MedicalPanel extends JPanel {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Ana pencere. */
    private final MainFrame mainFrame;

    /** @brief Servis. */
    private final MedicalRecordService medicalService;

    /** @brief Tablo modeli. */
    private DefaultTableModel tableModel;

    /** @brief Tablo. */
    private JTable table;

    /**
     * @brief MedicalPanel yapıcısı.
     * @param mainFrame Ana pencere
     */
    public MedicalPanel(MainFrame mainFrame) {
        this.mainFrame      = mainFrame;
        this.medicalService = new MedicalRecordService();
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
        JLabel title = new JLabel("📋 Sağlık Kayıtları");
        title.setFont(GuiConstants.TITLE_FONT);
        add(title, BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    /**
     * Member documentation.
     */
    private JScrollPane buildTable() {
        String[] cols = {"ID", "Pet", "Tür", "Teşhis", "Veteriner", "Tarih", "Maliyet (₺)"};
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
        bar.add(btn("➕ Yeni Kayıt", GuiConstants.PRIMARY, e -> addRecord()));
        bar.add(btn("🗑️ Sil", GuiConstants.DANGER, e -> deleteSelected()));
        return bar;
    }

    /**
     * Member documentation.
     */
    private void addRecord() {
        JTextField petId      = new JTextField("1");
        JTextField petName    = new JTextField();
        JComboBox<String> recType = new JComboBox<>(new String[]{
            "KONTROL", "ASII", "AMELIYAT", "HASTALIK", "DIGER"});
        JTextField diagnosis  = new JTextField();
        JTextField vet        = new JTextField();
        JTextField cost       = new JTextField("0");
        JTextField date       = new JTextField(LocalDate.now().toString());

        Object[] fields = {
            "Pet ID:", petId, "Pet Adı:", petName,
            "Kayıt Türü:", recType, "Teşhis:", diagnosis,
            "Veteriner:", vet, "Maliyet (₺):", cost,
            "Tarih (yyyy-MM-dd):", date
        };
        int res = JOptionPane.showConfirmDialog(this, fields, "Yeni Sağlık Kaydı",
            JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                MedicalRecord rec = new MedicalRecord(0,
                    Integer.parseInt(petId.getText().trim()),
                    petName.getText().trim(),
                    LocalDate.parse(date.getText().trim()),
                    (String) recType.getSelectedItem(),
                    diagnosis.getText().trim(),
                    vet.getText().trim());
                rec.setCost(Double.parseDouble(cost.getText().trim()));
                medicalService.addRecord(rec);
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
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Silinsin mi?", "Onayla",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                medicalService.deleteRecord((int) tableModel.getValueAt(row, 0));
                refresh();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    /**
     * @brief Tabloyu yeniler.
     */
    public void refresh() {
        tableModel.setRowCount(0);
        try {
            List<MedicalRecord> list = medicalService.getAllRecords();
            for (MedicalRecord r : list) {
                tableModel.addRow(new Object[]{
                    r.getId(), r.getPetName(), r.getRecordType(),
                    r.getDiagnosis(), r.getVeterinarianName(),
                    r.getFormattedDate(),
                    String.format("%.2f", r.getCost())
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
