/**
 * @file PetDialog.java
 * @brief Pet ekleme / düzenleme diyaloğu.
 */
package com.mehdi.petreminder.gui.dialogs;

import com.mehdi.petreminder.gui.util.GuiConstants;
import com.mehdi.petreminder.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * @class PetDialog
 * @brief Pet ekleme ve düzenleme modal dialog'u.
 * @details Yeni pet için null geçilir; düzenleme için mevcut Pet nesnesi geçilir.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class PetDialog extends JDialog {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Onaylandı mı. */
    private boolean confirmed = false;

    /** @brief Oluşturulan / düzenlenen pet. */
    private Pet resultPet;

    /** @brief Form alanları: ad, ırk, doğum tarihi, cinsiyet, ağırlık, notlar. */
    private JTextField txtName, txtBreed, txtBirthDate, txtGender, txtWeight, txtNotes;
    /** @brief Tür seçici. */
    private JComboBox<String> cmbSpecies;
    /** @brief Köpek eğitimli mi. */
    private JCheckBox chkTrained;
    /** @brief Kedi ev kedisi mi. */
    private JCheckBox chkIndoor;
    /** @brief Kuş konuşuyor mu. */
    private JCheckBox chkCanTalk;

    /** @brief Düzenleme modunda orijinal pet. */
    private final Pet editPet;

    /**
     * @brief PetDialog yapıcısı.
     * @param parent  Ana pencere
     * @param editPet Düzenlenecek pet (yeni için null)
     */
    public PetDialog(Frame parent, Pet editPet) {
        super(parent, editPet == null ? "Yeni Hayvan Ekle" : "Hayvanı Düzenle", true);
        this.editPet = editPet;
        buildUI();
        if (editPet != null) fillForm(editPet);
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(420, 500));
    }

    /**
     * @brief UI bileşenlerini oluşturur.
     */
    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        main.add(buildForm(), BorderLayout.CENTER);
        main.add(buildButtons(), BorderLayout.SOUTH);
        setContentPane(main);
    }

    /**
     * @brief Form panelini oluşturur.
     * @return Panel
     */
    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        txtName = addField(p, gbc, row++, "Ad *:", new JTextField(20));
        cmbSpecies = new JComboBox<>(new String[]{"Dog", "Cat", "Bird"});
        cmbSpecies.addActionListener(e -> updateSpecificFields());
        addLabel(p, gbc, row, "Tür *:");
        gbc.gridx = 1; gbc.gridy = row++;
        p.add(cmbSpecies, gbc);

        txtBreed      = addField(p, gbc, row++, "Irk / Tür:", new JTextField(20));
        txtBirthDate  = addField(p, gbc, row++, "Doğum Tarihi\n(yyyy-MM-dd):", new JTextField(20));
        txtGender     = addField(p, gbc, row++, "Cinsiyet:", new JTextField(20));
        txtWeight     = addField(p, gbc, row++, "Ağırlık (kg):", new JTextField(20));

        chkTrained = new JCheckBox("Eğitimli");
        chkIndoor  = new JCheckBox("Ev Kedisi");
        chkCanTalk = new JCheckBox("Konuşuyor");

        addLabel(p, gbc, row, "Özellik:");
        gbc.gridx = 1; gbc.gridy = row++;
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkPanel.setOpaque(false);
        checkPanel.add(chkTrained);
        checkPanel.add(chkIndoor);
        checkPanel.add(chkCanTalk);
        p.add(checkPanel, gbc);

        txtNotes = addField(p, gbc, row, "Notlar:", new JTextField(20));
        updateSpecificFields();
        return p;
    }

    /**
     * @brief Türe göre özel alanları aktif/pasif eder.
     */
    private void updateSpecificFields() {
        String species = (String) cmbSpecies.getSelectedItem();
        chkTrained.setEnabled("Dog".equals(species));
        chkIndoor.setEnabled("Cat".equals(species));
        chkCanTalk.setEnabled("Bird".equals(species));
    }

    /**
     * @brief Form'a etiket + alan ekler.
     * @param p    Panel
     * @param gbc  GridBagConstraints
     * @param row  Satır
     * @param label Etiket
     * @param field Alan
     * @return field
     */
    private JTextField addField(JPanel p, GridBagConstraints gbc,
                                 int row, String label, JTextField field) {
        addLabel(p, gbc, row, label);
        gbc.gridx = 1; gbc.gridy = row;
        p.add(field, gbc);
        return field;
    }

    /**
     * @brief Form'a etiket ekler (yardımcı metot).
     * @param p    Panel
     * @param gbc  GridBagConstraints
     * @param row  Satır
     * @param text Etiket metni
     */
    private void addLabel(JPanel p, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(text.replace("\n", " "));
        lbl.setFont(GuiConstants.BODY_FONT);
        p.add(lbl, gbc);
    }

    /**
     * @brief Buton paneli.
     * @return Panel
     */
    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Kaydet");
        btnOk.setBackground(GuiConstants.PRIMARY);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.addActionListener(e -> onSave());

        JButton btnCancel = new JButton("İptal");
        btnCancel.addActionListener(e -> dispose());

        p.add(btnCancel);
        p.add(btnOk);
        return p;
    }

    /**
     * @brief Formu doldurur (düzenleme modu).
     * @param pet Düzenlenecek pet
     */
    private void fillForm(Pet pet) {
        txtName.setText(pet.getName());
        cmbSpecies.setSelectedItem(pet.getSpecies());
        txtBreed.setText(getBreed(pet));
        if (pet.getBirthDate() != null) {
            txtBirthDate.setText(pet.getBirthDate().toString());
        }
        txtGender.setText(pet.getGender() != null ? pet.getGender() : "");
        txtWeight.setText(String.valueOf(pet.getWeight()));
        txtNotes.setText(pet.getNotes() != null ? pet.getNotes() : "");
        if (pet instanceof Dog) chkTrained.setSelected(((Dog) pet).isTrained());
        if (pet instanceof Cat) chkIndoor.setSelected(((Cat) pet).isIndoor());
        if (pet instanceof Bird) chkCanTalk.setSelected(((Bird) pet).isCanTalk());
        updateSpecificFields();
    }

    /**
     * @brief Pet'in ırk bilgisini döndürür.
     * @param pet Pet
     * @return Irk
     */
    private String getBreed(Pet pet) {
        if (pet instanceof Dog) return ((Dog) pet).getBreed() != null ? ((Dog) pet).getBreed() : "";
        if (pet instanceof Cat) return ((Cat) pet).getBreed() != null ? ((Cat) pet).getBreed() : "";
        if (pet instanceof Bird) return ((Bird) pet).getBirdType() != null ? ((Bird) pet).getBirdType() : "";
        return "";
    }

    /**
     * @brief Kaydet butonuna tıklandığında çalışır.
     */
    private void onSave() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ad boş olamaz!", "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate birthDate = null;
        String bdStr = txtBirthDate.getText().trim();
        if (!bdStr.isEmpty()) {
            try {
                birthDate = LocalDate.parse(bdStr);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Tarih formatı: yyyy-MM-dd", "Hata",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        double weight = 0;
        try {
            if (!txtWeight.getText().trim().isEmpty()) {
                weight = Double.parseDouble(txtWeight.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ağırlık sayısal olmalı", "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String species = (String) cmbSpecies.getSelectedItem();
        int ownerId = editPet != null ? editPet.getOwnerId() : 1;

        Pet pet;
        if ("Dog".equals(species)) {
            Dog dog = new Dog(editPet != null ? editPet.getId() : 0, name, birthDate, ownerId);
            dog.setBreed(txtBreed.getText().trim());
            dog.setTrained(chkTrained.isSelected());
            pet = dog;
        } else if ("Cat".equals(species)) {
            Cat cat = new Cat(editPet != null ? editPet.getId() : 0, name, birthDate, ownerId);
            cat.setBreed(txtBreed.getText().trim());
            cat.setIndoor(chkIndoor.isSelected());
            pet = cat;
        } else {
            Bird bird = new Bird(editPet != null ? editPet.getId() : 0, name, birthDate, ownerId);
            bird.setBirdType(txtBreed.getText().trim());
            bird.setCanTalk(chkCanTalk.isSelected());
            pet = bird;
        }
        pet.setGender(txtGender.getText().trim());
        pet.setWeight(weight);
        pet.setNotes(txtNotes.getText().trim());

        resultPet = pet;
        confirmed = true;
        dispose();
    }

    /**
     * @brief Onaylandı mı kontrolü.
     * @return true ise kullanıcı "Kaydet" tıkladı
     */
    public boolean isConfirmed() { return confirmed; }

    /**
     * @brief Oluşturulan / düzenlenen pet'i döndürür.
     * @return Pet nesnesi
     */
    public Pet getPet() { return resultPet; }
}
