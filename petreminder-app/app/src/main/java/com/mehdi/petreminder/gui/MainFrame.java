/**
 * @file MainFrame.java
 * @brief Ana Swing penceresi — tüm panelleri barındırır.
 * @details FlatLaf light tema, sol navigasyon, sağ içerik paneli.
 */
/**
 * Member documentation.
 */
package com.mehdi.petreminder.gui;

import com.formdev.flatlaf.FlatLightLaf;
import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import com.mehdi.petreminder.gui.panels.*;
import com.mehdi.petreminder.gui.util.GuiConstants;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * @class MainFrame
 * @brief Uygulamanın ana Swing penceresi.
 * @details PDF zorunluluğu: Swing GUI — ayrı GUI katmanı.
 *          FlatLaf light tema kullanılır (modern görünüm).
 *          Sol NavPanel + sağ CardLayout içerik alanı.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class MainFrame extends JFrame {

    /** @brief Logger. */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(MainFrame.class);

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** @brief Uygulama başlığı. */
    private static final String TITLE = "Pet Care Reminder System v1.0";

    /** @brief Kart isimleri. */
    public static final String CARD_DASHBOARD   = "DASHBOARD";
    /** @brief Pets kart adı. */
    public static final String CARD_PETS        = "PETS";
    /** @brief Reminders kart adı. */
    public static final String CARD_REMINDERS   = "REMINDERS";
    /** @brief Vet kart adı. */
    public static final String CARD_VET         = "VET";
    /** @brief Medical kart adı. */
    public static final String CARD_MEDICAL     = "MEDICAL";
    /** @brief Settings kart adı. */
    public static final String CARD_SETTINGS    = "SETTINGS";

    /** @brief İçerik paneli (CardLayout). */
    private JPanel contentPanel;

    /** @brief CardLayout yöneticisi. */
    private CardLayout cardLayout;

    /** @brief Sol navigasyon paneli. */
    private NavPanel navPanel;

    /** @brief Dashboard paneli. */
    private DashboardPanel dashboardPanel;

    /** @brief Pets paneli. */
    private PetsPanel petsPanel;

    /** @brief Reminders paneli. */
    private RemindersPanel remindersPanel;

    /** @brief Vet paneli. */
    private VetPanel vetPanel;

    /** @brief Medical records paneli. */
    private MedicalPanel medicalPanel;

    /** @brief Settings paneli. */
    private SettingsPanel settingsPanel;

    /**
     * @brief MainFrame yapıcısı — GUI bileşenlerini başlatır.
     */
    public MainFrame() {
        super(TITLE);
        initLookAndFeel();
        initComponents();
        setupLayout();
        setupFrame();
        navigateTo(CARD_DASHBOARD);
        logger.info("MainFrame başlatıldı.");
    }

    /**
     * @brief FlatLaf light temasını yükler.
     */
    private void initLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 6);
            UIManager.put("TextComponent.arc", 6);
            UIManager.put("TabbedPane.tabHeight", 36);
        } catch (Exception e) {
            logger.warn("FlatLaf yüklenemedi, varsayılan tema kullanılıyor.");
        }
    }

    /**
     * @brief GUI bileşenlerini oluşturur.
     */
    private void initComponents() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(GuiConstants.BG_COLOR);

        dashboardPanel  = new DashboardPanel(this);
        petsPanel       = new PetsPanel(this);
        remindersPanel  = new RemindersPanel(this);
        vetPanel        = new VetPanel(this);
        medicalPanel    = new MedicalPanel(this);
        settingsPanel   = new SettingsPanel(this);

        contentPanel.add(dashboardPanel,  CARD_DASHBOARD);
        contentPanel.add(petsPanel,       CARD_PETS);
        contentPanel.add(remindersPanel,  CARD_REMINDERS);
        contentPanel.add(vetPanel,        CARD_VET);
        contentPanel.add(medicalPanel,    CARD_MEDICAL);
        contentPanel.add(settingsPanel,   CARD_SETTINGS);

        navPanel = new NavPanel(this);
    }

    /**
     * @brief Ana layout'u kurar (BorderLayout).
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(navPanel,     BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    /**
     * @brief JFrame ayarlarını yapar.
     */
    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setIconImage(getAppIcon());
    }

    /**
     * @brief Uygulama ikonunu döndürür.
     * @return Image veya null
     */
    private Image getAppIcon() {
        // Kaynak yoksa null döner — sorun çıkarmaz
        return null;
    }

    /**
     * @brief Alt durum çubuğunu oluşturur.
     * @return Status bar paneli
     */
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(GuiConstants.NAV_COLOR);
        bar.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        bar.setPreferredSize(new Dimension(0, 28));

        JLabel storage = new JLabel("Storage: "
            + StorageConfig.getActiveBackend().getDisplayName());
        storage.setForeground(Color.WHITE);
        storage.setFont(GuiConstants.SMALL_FONT);

        JLabel version = new JLabel("CEN206 — Spring 2026  |  RTEU");
        version.setForeground(new Color(180, 180, 200));
        version.setFont(GuiConstants.SMALL_FONT);

        bar.add(storage, BorderLayout.WEST);
        bar.add(version, BorderLayout.EAST);
        return bar;
    }

    /**
     * @brief Belirtilen kartı gösterir.
     * @param cardName Kart adı (CARD_* sabitlerinden biri)
     */
    public void navigateTo(String cardName) {
        cardLayout.show(contentPanel, cardName);
        navPanel.setActiveButton(cardName);
        refreshCurrentPanel(cardName);
        logger.debug("Navigasyon: {}", cardName);
    }

    /**
     * @brief Aktif paneli yeniler.
     * @param cardName Kart adı
     */
    private void refreshCurrentPanel(String cardName) {
        switch (cardName) {
            case CARD_DASHBOARD:  dashboardPanel.refresh();  break;
            case CARD_PETS:       petsPanel.refresh();       break;
            case CARD_REMINDERS:  remindersPanel.refresh();  break;
            case CARD_VET:        vetPanel.refresh();        break;
            case CARD_MEDICAL:    medicalPanel.refresh();    break;
            case CARD_SETTINGS:   settingsPanel.refresh();   break;
            default: break;
        }
    }

    /**
     * @brief Storage backend değişince tüm panelleri günceller.
     * @param type Yeni StorageType
     */
    public void onStorageChanged(StorageType type) {
        StorageConfig.setActiveBackend(type);
        // Status bar'ı güncelle
        Component[] comps = ((JPanel) getContentPane().getComponent(2)).getComponents();
        for (Component c : comps) {
            if (c instanceof JLabel && ((JLabel) c).getText().startsWith("Storage:")) {
                ((JLabel) c).setText("Storage: " + type.getDisplayName());
            }
        }
        JOptionPane.showMessageDialog(this,
            "Storage backend değiştirildi:\n" + type.getDisplayName(),
            "Ayarlar", JOptionPane.INFORMATION_MESSAGE);
        logger.info("Storage değişti: {}", type);
    }

    /**
     * @brief Uygulamayı başlatır (EDT'de çalışır).
     */
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
