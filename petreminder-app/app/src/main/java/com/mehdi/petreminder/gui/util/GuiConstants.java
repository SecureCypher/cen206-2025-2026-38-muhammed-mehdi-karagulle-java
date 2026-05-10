/**
 * @file GuiConstants.java
 * @brief GUI için renk, font ve boyut sabitleri.
 */
/**
 * Member documentation.
 */
package com.mehdi.petreminder.gui.util;

import java.awt.*;

/**
 * @class GuiConstants
 * @brief GUI bileşenlerinde kullanılan sabitler.
 * @details Tüm renk, font ve boyut değerleri merkezi olarak burada tanımlanır.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public final class GuiConstants {

    /** @brief Gizli yapıcı. */
    private GuiConstants() { }

    // ── Renkler ──────────────────────────────────────────────────────

    /** @brief Sol nav paneli arka plan rengi. */
    public static final Color NAV_COLOR       = new Color(45, 55, 72);

    /** @brief Nav buton hover rengi. */
    public static final Color NAV_HOVER       = new Color(74, 85, 104);

    /** @brief Nav aktif buton rengi. */
    public static final Color NAV_ACTIVE      = new Color(99, 102, 241);

    /** @brief İçerik alanı arka plan. */
    public static final Color BG_COLOR        = new Color(248, 250, 252);

    /** @brief Kart arka plan rengi. */
    public static final Color CARD_BG         = Color.WHITE;

    /** @brief Birincil vurgu rengi (indigo). */
    public static final Color PRIMARY         = new Color(99, 102, 241);

    /** @brief Tehlike rengi (kırmızı). */
    public static final Color DANGER          = new Color(239, 68, 68);

    /** @brief Başarı rengi (yeşil). */
    public static final Color SUCCESS         = new Color(34, 197, 94);

    /** @brief Uyarı rengi (turuncu). */
    public static final Color WARNING         = new Color(245, 158, 11);

    /** @brief Başlık metni rengi. */
    public static final Color TEXT_PRIMARY    = new Color(17, 24, 39);

    /** @brief İkincil metin rengi. */
    public static final Color TEXT_SECONDARY  = new Color(107, 114, 128);

    /** @brief Kenarlık rengi. */
    public static final Color BORDER_COLOR    = new Color(229, 231, 235);

    // ── Fontlar ──────────────────────────────────────────────────────

    /** @brief Büyük başlık fontu. */
    public static final Font TITLE_FONT   = new Font("Segoe UI", Font.BOLD, 22);

    /** @brief Bölüm başlık fontu. */
    public static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 16);

    /** @brief Normal metin fontu. */
    public static final Font BODY_FONT    = new Font("Segoe UI", Font.PLAIN, 13);

    /** @brief Küçük metin fontu. */
    public static final Font SMALL_FONT   = new Font("Segoe UI", Font.PLAIN, 11);

    /** @brief Nav buton fontu. */
    public static final Font NAV_FONT     = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Boyutlar ─────────────────────────────────────────────────────

    /** @brief Sol nav genişliği. */
    public static final int NAV_WIDTH     = 200;

    /** @brief Standart boşluk. */
    public static final int GAP           = 12;

    /** @brief Büyük boşluk. */
    public static final int GAP_LARGE     = 24;
}
