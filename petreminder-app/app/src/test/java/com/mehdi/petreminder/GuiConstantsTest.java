/**
 * @file GuiConstantsTest.java
 * @brief GuiConstants icin JUnit 5 testleri (app modulunde).
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.gui.util.GuiConstants;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class GuiConstantsTest
 * @brief GUI sabit degerlerinin dogruluğunu test eder.
 * @author Muhammed Mehdi Karagulle, Ibrahim Demirci, Zumre Uykun
 */
class GuiConstantsTest {

    /** Tests that all Color constants are non-null. */
    @Test
    void testGuiConstantsColorsNotNull() {
        assertNotNull(GuiConstants.NAV_COLOR);
        assertNotNull(GuiConstants.NAV_HOVER);
        assertNotNull(GuiConstants.NAV_ACTIVE);
        assertNotNull(GuiConstants.BG_COLOR);
        assertNotNull(GuiConstants.CARD_BG);
        assertNotNull(GuiConstants.PRIMARY);
        assertNotNull(GuiConstants.DANGER);
        assertNotNull(GuiConstants.SUCCESS);
        assertNotNull(GuiConstants.WARNING);
        assertNotNull(GuiConstants.TEXT_PRIMARY);
        assertNotNull(GuiConstants.TEXT_SECONDARY);
        assertNotNull(GuiConstants.BORDER_COLOR);
    }

    /** Tests that all Font constants are non-null. */
    @Test
    void testGuiConstantsFontsNotNull() {
        assertNotNull(GuiConstants.TITLE_FONT);
        assertNotNull(GuiConstants.SECTION_FONT);
        assertNotNull(GuiConstants.BODY_FONT);
        assertNotNull(GuiConstants.SMALL_FONT);
        assertNotNull(GuiConstants.NAV_FONT);
    }

    /** Tests dimension constants are correct. */
    @Test
    void testGuiConstantsDimensions() {
        assertEquals(200, GuiConstants.NAV_WIDTH);
        assertEquals(12, GuiConstants.GAP);
        assertEquals(24, GuiConstants.GAP_LARGE);
    }
}
