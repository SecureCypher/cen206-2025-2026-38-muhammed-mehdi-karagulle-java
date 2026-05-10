/**
 * @file ReminderDialog.java
 * @brief Hatırlatıcı ekleme / düzenleme diyaloğu.
 */
package com.mehdi.petreminder.gui.dialogs;

import com.mehdi.petreminder.gui.util.GuiConstants;
import com.mehdi.petreminder.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * @class ReminderDialog
 * @brief Reminder ekleme ve düzenleme modal dialog'u.
 * @details Tür seçimine göre dinamik form alanları gösterilir.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class ReminderDialog extends JDialog {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Onaylandı mı. */
    private boolean confirmed = false;

    /** @brief Oluşturulan / düzenlenen reminder. */
    private Reminder resultReminder;

    // Ortak alanlar
    /** @brief Pet ID. */
    private JTextField txtPetId;
    /** @brief Pet adı. */
    private JTextField txtPetName;
    /** @brief Zaman. */
    private JTextField txtTime;
    /** @brief Öncelik. */
    private JComboBox<String> cmbPriority;
    /** @brief Tür seçici. */
    private JComboBox<String> cmbType;
    /** @brief Açıklama. */
    private JTextField txtDesc;

    // Tür-özel alanlar
    /** @brief Yem türü ve porsiyon alanları. */
    private JTextField txtFoodType, txtPortionGrams;
    /** @brief İlaç adı, doz ve birim alanları. */
    private JTextField txtMedName, txtDosage, txtDosageUnit;
    /** @brief Egzersiz türü ve süre alanları. */
    private JTextField txtExerciseType, txtDuration;
    /** @brief Bakım türü. */
    private JTextField txtGroomingType;
    /** @brief Veteriner adı, klinik ve sebep alanları. */
    private JTextField txtVetName, txtClinic, txtReason;

    /** @brief Dinamik alan paneli. */
    private JPanel specificPanel;

    /** @brief Düzenleme modu. */
    private final Reminder editReminder;

    /**
     * @brief ReminderDialog yapıcısı.
     * @param parent       Ana pencere
     * @param editReminder Düzenlenecek reminder (yeni için null)
     */
    public ReminderDialog(Frame parent, Reminder editReminder) {
        super(parent, editReminder == null ? "Yeni Hatırlatıcı" : "Hatırlatıcı Düzenle", true);
        this.editReminder = editReminder;
        buildUI();
        if (editReminder != null) fillForm(editReminder);
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(440, 480));
    }

    /**
     * @brief UI bileşenlerini oluşturur.
     */
    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        main.add(buildCommonForm(), BorderLayout.NORTH);
        specificPanel = new JPanel(new GridBagLayout());
        main.add(new JScrollPane(specificPanel), BorderLayout.CENTER);
        main.add(buildButtons(), BorderLayout.SOUTH);
        setContentPane(main);
    }

    /**
     * @brief Ortak form panel oluşturur.
     * @return JPanel
     */
    private JPanel buildCommonForm() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        txtPetId   = addField(p, gbc, row++, "Pet ID:", new JTextField("1", 10));
        txtPetName = addField(p, gbc, row++, "Pet Adı:", new JTextField(10));
        txtTime    = addField(p, gbc, row++, "Zaman (yyyy-MM-ddTHH:mm):",
            new JTextField(LocalDateTime.now().plusHours(1).toString().substring(0, 16), 10));

        cmbPriority = new JComboBox<>(new String[]{"YUKSEK", "ORTA", "DUSUK"});
        cmbPriority.setSelectedItem("ORTA");
        addLabel(p, gbc, row, "Öncelik:");
        gbc.gridx = 1; gbc.gridy = row++;
        p.add(cmbPriority, gbc);

        cmbType = new JComboBox<>(new String[]{"Besleme", "Ilac", "Egzersiz", "Bakim", "Veteriner"});
        cmbType.addActionListener(e -> updateSpecificFields());
        addLabel(p, gbc, row, "Tür:");
        gbc.gridx = 1; gbc.gridy = row++;
        p.add(cmbType, gbc);

        txtDesc = addField(p, gbc, row, "Açıklama:", new JTextField(10));
        return p;
    }

    /**
     * @brief Türe göre özel alanları günceller.
     */
    private void updateSpecificFields() {
        specificPanel.removeAll();
        String type = (String) cmbType.getSelectedItem();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        switch (type != null ? type : "") {
            case "Besleme":
                txtFoodType    = addField(specificPanel, gbc, row++, "Besin Türü:", new JTextField(10));
                txtPortionGrams = addField(specificPanel, gbc, row, "Porsiyon (g):", new JTextField("100", 10));
                break;
            case "Ilac":
                txtMedName   = addField(specificPanel, gbc, row++, "İlaç Adı:", new JTextField(10));
                txtDosage    = addField(specificPanel, gbc, row++, "Doz:", new JTextField("1.0", 10));
                txtDosageUnit = addField(specificPanel, gbc, row, "Birim:", new JTextField("mg", 10));
                break;
            case "Egzersiz":
                txtExerciseType = addField(specificPanel, gbc, row++, "Egzersiz Türü:", new JTextField("Yürüyüş", 10));
                txtDuration     = addField(specificPanel, gbc, row, "Süre (dk):", new JTextField("30", 10));
                break;
            case "Bakim":
                txtGroomingType = addField(specificPanel, gbc, row, "Bakım Türü:", new JTextField("Tıraş", 10));
                break;
            case "Veteriner":
                txtVetName = addField(specificPanel, gbc, row++, "Veteriner:", new JTextField(10));
                txtClinic  = addField(specificPanel, gbc, row++, "Klinik:", new JTextField(10));
                txtReason  = addField(specificPanel, gbc, row, "Sebep:", new JTextField("Kontrol", 10));
                break;
            default:
                break;
        }
        specificPanel.revalidate();
        specificPanel.repaint();
        pack();
    }

    /**
     * @brief Formu mevcut reminder bilgileriyle doldurur.
     * @param r Düzenlenen reminder
     */
    private void fillForm(Reminder r) {
        txtPetId.setText(String.valueOf(r.getPetId()));
        txtPetName.setText(r.getPetName() != null ? r.getPetName() : "");
        if (r.getScheduledTime() != null) {
            txtTime.setText(r.getScheduledTime().toString().substring(0, 16));
        }
        cmbPriority.setSelectedItem(r.getPriority());
        cmbType.setSelectedItem(r.getReminderType());
        updateSpecificFields();
        txtDesc.setText(r.getDescription() != null ? r.getDescription() : "");
    }

    /**
     * @brief Kaydet butonuna tıklandığında çalışır — reminder oluşturur.
     */
    private void onSave() {
        try {
            int petId = Integer.parseInt(txtPetId.getText().trim());
            String petName = txtPetName.getText().trim();
            LocalDateTime time = LocalDateTime.parse(txtTime.getText().trim());
            String priority = (String) cmbPriority.getSelectedItem();
            String type = (String) cmbType.getSelectedItem();
            String desc = txtDesc.getText().trim();
            int editId = editReminder != null ? editReminder.getId() : 0;

            Reminder r;
            switch (type != null ? type : "") {
                case "Besleme":
                    String food = txtFoodType != null ? txtFoodType.getText().trim() : "Mama";
                    double portion = txtPortionGrams != null
                        ? Double.parseDouble(txtPortionGrams.getText().trim()) : 100;
                    r = new FeedingReminder(editId, petId, petName, time, desc,
                        priority, false, "", food, portion, false);
                    break;
                case "Ilac":
                    String med = txtMedName != null ? txtMedName.getText().trim() : "İlaç";
                    double dose = txtDosage != null
                        ? Double.parseDouble(txtDosage.getText().trim()) : 1.0;
                    String unit = txtDosageUnit != null ? txtDosageUnit.getText().trim() : "mg";
                    r = new MedicationReminder(editId, petId, petName, time, desc,
                        priority, false, "", med, dose, unit, false, null);
                    break;
                case "Egzersiz":
                    String exType = txtExerciseType != null ? txtExerciseType.getText().trim() : "Yürüyüş";
                    int dur = txtDuration != null
                        ? Integer.parseInt(txtDuration.getText().trim()) : 30;
                    r = new ExerciseReminder(editId, petId, petName, time, desc,
                        priority, false, "", exType, dur, 0);
                    break;
                case "Bakim":
                    String gType = txtGroomingType != null ? txtGroomingType.getText().trim() : "Tıraş";
                    r = new GroomingReminder(editId, petId, petName, time, desc,
                        priority, false, "", gType, false, "");
                    break;
                case "Veteriner":
                    String vet = txtVetName != null ? txtVetName.getText().trim() : "Veteriner";
                    String clinic = txtClinic != null ? txtClinic.getText().trim() : "Klinik";
                    String reason = txtReason != null ? txtReason.getText().trim() : "Kontrol";
                    r = new VetAppointment(editId, petId, petName, time, desc,
                        priority, false, "", vet, clinic, reason, 0, false, "");
                    break;
                default:
                    r = new FeedingReminder(editId, petId, petName, time, "Mama", 100);
            }
            resultReminder = r;
            confirmed = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Sayısal alanları kontrol edin.", "Hata",
                JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Zaman formatı: yyyy-MM-ddTHH:mm", "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Kaydet / İptal buton panelini oluşturur.
     * @return JPanel
     */
    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Kaydet");
        ok.setBackground(GuiConstants.PRIMARY);
        ok.setForeground(Color.WHITE);
        ok.setFocusPainted(false);
        ok.addActionListener(e -> onSave());
        JButton cancel = new JButton("İptal");
        cancel.addActionListener(e -> dispose());
        p.add(cancel);
        p.add(ok);
        return p;
    }

    /**
     * @brief Form'a etiket + alan ekler.
     * @param p     Panel
     * @param gbc   GridBagConstraints
     * @param row   Satır numarası
     * @param label Etiket metni
     * @param field Alan bileşeni
     * @return field
     */
    private JTextField addField(JPanel p, GridBagConstraints gbc, int row, String label, JTextField field) {
        addLabel(p, gbc, row, label);
        gbc.gridx = 1; gbc.gridy = row;
        p.add(field, gbc);
        return field;
    }

    /**
     * @brief Form'a etiket ekler (yardımcı).
     * @param p    Panel
     * @param gbc  GridBagConstraints
     * @param row  Satır
     * @param text Etiket metni
     */
    private void addLabel(JPanel p, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0; gbc.gridy = row;
        p.add(new JLabel(text), gbc);
    }

    /**
     * @brief Onaylandı mı.
     * @return true ise kaydet tıklandı
     */
    public boolean isConfirmed() { return confirmed; }

    /**
     * @brief Oluşturulan reminder'ı döndürür.
     * @return Reminder nesnesi
     */
    public Reminder getReminder() { return resultReminder; }
}
