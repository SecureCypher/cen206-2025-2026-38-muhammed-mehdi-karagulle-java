/**
 * @file PetsPanel.java
 * @brief Evcil hayvanlar yönetim paneli — tam CRUD.
 */
package com.mehdi.petreminder.gui.panels;

import com.mehdi.petreminder.gui.MainFrame;
import com.mehdi.petreminder.gui.dialogs.PetDialog;
import com.mehdi.petreminder.gui.util.GuiConstants;
import com.mehdi.petreminder.model.*;
import com.mehdi.petreminder.service.PetService;
import com.mehdi.petreminder.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * @class PetsPanel
 * @brief Evcil hayvanlar listesi ve CRUD işlemleri.
 * @details JTable + toolbar + dialog pattern.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class PetsPanel extends JPanel {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Ana pencere. */
    private final MainFrame mainFrame;

    /** @brief Pet servisi. */
    private final PetService petService;

    /** @brief Tablo modeli. */
    private DefaultTableModel tableModel;

    /** @brief Tablo. */
    private JTable table;

    /** @brief Arama alanı. */
    private JTextField searchField;

    /** @brief Tür filtresi. */
    private JComboBox<String> speciesFilter;

    /**
     * @brief PetsPanel yapıcısı.
     * @param mainFrame Ana pencere
     */
    public PetsPanel(MainFrame mainFrame) {
        this.mainFrame  = mainFrame;
        this.petService = new PetService();
        setBackground(GuiConstants.BG_COLOR);
        setLayout(new BorderLayout(GuiConstants.GAP, GuiConstants.GAP));
        setBorder(new EmptyBorder(GuiConstants.GAP_LARGE, GuiConstants.GAP_LARGE,
            GuiConstants.GAP_LARGE, GuiConstants.GAP_LARGE));
        buildUI();
    }

    /**
     * @brief UI'ı oluşturur.
     */
    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    /**
     * @brief Başlık + arama satırı.
     * @return Panel
     */
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(GuiConstants.GAP, 0));
        p.setOpaque(false);

        JLabel title = new JLabel("🐾 Evcil Hayvanlarım");
        title.setFont(GuiConstants.TITLE_FONT);
        title.setForeground(GuiConstants.TEXT_PRIMARY);
        p.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "Ad ile ara...");
        searchField.addActionListener(e -> refresh());

        speciesFilter = new JComboBox<>(new String[]{"Tümü", "Dog", "Cat", "Bird"});
        speciesFilter.addActionListener(e -> refresh());

        controls.add(new JLabel("Tür:"));
        controls.add(speciesFilter);
        controls.add(searchField);
        controls.add(buildIconButton("🔍", "Ara", e -> refresh()));

        p.add(controls, BorderLayout.EAST);
        return p;
    }

    /**
     * @brief Tablo alanını oluşturur.
     * @return ScrollPane
     */
    private JScrollPane buildTableArea() {
        String[] cols = {"ID", "Ad", "Tür", "Irk", "Doğum Tarihi", "Cinsiyet", "Ağırlık (kg)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(GuiConstants.BODY_FONT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(GuiConstants.BORDER_COLOR);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // Çift tıklama = düzenle
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        return new JScrollPane(table);
    }

    /**
     * @brief Alt araç çubuğu.
     * @return Panel
     */
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.add(buildPrimaryButton("➕ Yeni Hayvan", e -> addPet()));
        bar.add(buildButton("✏️ Düzenle", e -> editSelected()));
        bar.add(buildDangerButton("🗑️ Sil", e -> deleteSelected()));
        return bar;
    }

    /**
     * @brief Yeni pet ekleme dialogunu açar.
     */
    private void addPet() {
        PetDialog dlg = new PetDialog(mainFrame, null);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            try {
                petService.addPet(dlg.getPet());
                refresh();
                JOptionPane.showMessageDialog(this, "Hayvan eklendi!", "Başarılı",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @brief Seçili pet'i düzenler.
     */
    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Lütfen bir hayvan seçin.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Pet pet = petService.getPetById(id);
            PetDialog dlg = new PetDialog(mainFrame, pet);
            dlg.setVisible(true);
            if (dlg.isConfirmed()) {
                petService.updatePet(dlg.getPet());
                refresh();
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Seçili pet'i siler.
     */
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Lütfen bir hayvan seçin.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            name + " silinsin mi?", "Onayla", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            petService.deletePet(id);
            refresh();
        }
    }

    /**
     * @brief Tabloyu yeniler.
     */
    public void refresh() {
        tableModel.setRowCount(0);
        try {
            String search  = searchField != null ? searchField.getText().trim() : "";
            String species = speciesFilter != null
                ? (String) speciesFilter.getSelectedItem() : "Tümü";

            List<Pet> pets = search.isEmpty() ? petService.getAllPets()
                                              : petService.searchByName(search);
            if (!"Tümü".equals(species) && species != null) {
                pets = petService.filterBySpecies(species);
            }

            for (Pet p : pets) {
                tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getSpecies(),
                    getBreed(p),
                    p.getBirthDate() != null ? p.getBirthDate().toString() : "",
                    p.getGender() != null ? p.getGender() : "",
                    String.format("%.1f", p.getWeight())
                });
            }
        } catch (Exception e) {
            // Veri yoksa sessiz
        }
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

    // ── Yardımcı buton oluşturucular ─────────────────────────────────

    /**
     * @brief Birincil stil buton oluşturur.
     * @param text Buton metni
     * @param al   Action listener
     * @return JButton
     */
    private JButton buildPrimaryButton(String text, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.setBackground(GuiConstants.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(GuiConstants.BODY_FONT);
        b.addActionListener(al);
        return b;
    }

    /**
     * @brief Normal stil buton oluşturur.
     * @param text Buton metni
     * @param al   Action listener
     * @return JButton
     */
    private JButton buildButton(String text, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(GuiConstants.BODY_FONT);
        b.addActionListener(al);
        return b;
    }

    /**
     * @brief Tehlike stil buton oluşturur.
     * @param text Buton metni
     * @param al   Action listener
     * @return JButton
     */
    private JButton buildDangerButton(String text, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.setBackground(GuiConstants.DANGER);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(GuiConstants.BODY_FONT);
        b.addActionListener(al);
        return b;
    }

    /**
     * @brief Ikon buton oluşturur.
     * @param icon    İkon metni
     * @param tooltip Tooltip metni
     * @param al      Action listener
     * @return JButton
     */
    private JButton buildIconButton(String icon, String tooltip,
                                    java.awt.event.ActionListener al) {
        JButton b = new JButton(icon);
        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.addActionListener(al);
        return b;
    }
}
