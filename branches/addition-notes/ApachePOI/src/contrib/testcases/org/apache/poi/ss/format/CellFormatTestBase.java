package org.apache.poi.ss.format;

import junit.framework.TestCase;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.format.CellFormatPart;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

import static java.awt.Color.*;

/**
 * This class is a base class for spreadsheet-based tests, such as are used for
 * cell formatting.  This reads tests from the spreadsheet, as well as reading
 * flags that can be used to paramterize these tests.
 * <p/>
 * Each test has four parts: The expected result (column A), the format string
 * (column B), the value to format (column C), and a comma-separated list of
 * categores that this test falls in. Normally all tests are run, but if the
 * flag "Categories" is not empty, only tests that have at least one category
 * listed in "Categories" are run.
 */
@SuppressWarnings(
        {"JUnitTestCaseWithNoTests", "JUnitTestClassNamingConvention"})
public class CellFormatTestBase extends TestCase {
    protected Workbook workbook;

    private String testFile;
    private Map<String, String> testFlags;
    private boolean tryAllColors;
    private JLabel label;

    private static final String[] COLOR_NAMES =
            {"Black", "Red", "Green", "Blue", "Yellow", "Cyan", "Magenta",
                    "White"};
    private static final Color[] COLORS =
            {BLACK, RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA, WHITE};

    public static final Color TEST_COLOR = ORANGE.darker();

    abstract static class CellValue {
        abstract Object getValue(Cell cell);

        @SuppressWarnings({"UnusedDeclaration"})
        Color getColor(Cell cell) {
            return TEST_COLOR;
        }

        void equivalent(String expected, String actual, CellFormatPart format) {
            assertEquals("format \"" + format + "\"", '"' + expected + '"',
                    '"' + actual + '"');
        }
    }

    protected void runFormatTests(String workbookName, CellValue valueGetter)
            throws IOException, InvalidFormatException {

        openWorkbook(workbookName);

        readFlags(workbook);

        Set<String> runCategories = new TreeSet<String>(
                String.CASE_INSENSITIVE_ORDER);
        String runCategoryList = flagString("Categories", "");
        if (runCategoryList != null) {
            runCategories.addAll(Arrays.asList(runCategoryList.split(
                    "\\s*,\\s*")));
            runCategories.remove(""); // this can be found and means nothing
        }

        Sheet sheet = workbook.getSheet("Tests");
        int end = sheet.getLastRowNum();
        // Skip the header row, therefore "+ 1"
        for (int r = sheet.getFirstRowNum() + 1; r <= end; r++) {
            Row row = sheet.getRow(r);
            if (row == null)
                continue;
            int cellnum = 0;
            String expectedText = row.getCell(cellnum).getStringCellValue();
            String format = row.getCell(1).getStringCellValue();
            String testCategoryList = row.getCell(3).getStringCellValue();
            boolean byCategory = runByCategory(runCategories, testCategoryList);
            if ((!expectedText.isEmpty() || !format.isEmpty()) && byCategory) {
                Cell cell = row.getCell(2);
                tryFormat(r, expectedText, format, valueGetter, cell);
            }
        }
    }

    /**
     * Open a given workbook.
     *
     * @param workbookName The workbook name.  This is presumed to live in the
     *                     "spreadsheets" directory under the directory named in
     *                     the Java property "POI.testdata.path".
     *
     * @throws IOException
     * @throws InvalidFormatException
     */
    protected void openWorkbook(String workbookName)
            throws IOException, InvalidFormatException {

        String testDir = System.getProperty("POI.testdata.path");
        InputStream inp = new BufferedInputStream(new FileInputStream(new File(
                new File(testDir, "spreadsheet"), workbookName)));
        workbook = WorkbookFactory.create(inp);
        workbook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);
        testFile = workbookName;
    }

    /**
     * Read the flags from the workbook.  Flags are on the sheet named "Flags",
     * and consist of names in column A and values in column B.  These are put
     * into a map that can be queried later.
     *
     * @param wb The workbook to look in.
     */
    private void readFlags(Workbook wb) {
        Sheet flagSheet = wb.getSheet("Flags");
        testFlags = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        if (flagSheet != null) {
            int end = flagSheet.getLastRowNum();
            // Skip the header row, therefore "+ 1"
            for (int r = flagSheet.getFirstRowNum() + 1; r <= end; r++) {
                Row row = flagSheet.getRow(r);
                if (row == null)
                    continue;
                String flagName = row.getCell(0).getStringCellValue();
                String flagValue = row.getCell(1).getStringCellValue();
                if (flagName.length() > 0) {
                    testFlags.put(flagName, flagValue);
                }
            }
        }

        tryAllColors = flagBoolean("AllColors", true);
    }

    /**
     * Returns <tt>true</tt> if any of the categories for this run are contained
     * in the test's listed categories.
     *
     * @param categories     The categories of tests to be run.  If this is
     *                       empty, then all tests will be run.
     * @param testCategories The categories that this test is in.  This is a
     *                       comma-separated list.  If <em>any</em> tests in
     *                       this list are in <tt>categories</tt>, the test will
     *                       be run.
     *
     * @return <tt>true</tt> if the test should be run.
     */
    private boolean runByCategory(Set<String> categories,
            String testCategories) {

        if (categories.isEmpty())
            return true;
        // If there are specified categories, find out if this has one of them
        for (String category : testCategories.split("\\s*,\\s*")) {
            if (categories.contains(category)) {
                return true;
            }
        }
        return false;
    }

    private void tryFormat(int row, String expectedText, String desc,
            CellValue getter, Cell cell) {

        Object value = getter.getValue(cell);
        Color testColor = getter.getColor(cell);
        if (testColor == null)
            testColor = TEST_COLOR;

        if (label == null)
            label = new JLabel();
        label.setForeground(testColor);
        label.setText("xyzzy");

        System.out.printf("Row %d: \"%s\" -> \"%s\": expected \"%s\"", row + 1,
                String.valueOf(value), desc, expectedText);
        System.out.flush();
        String actualText = tryColor(desc, null, getter, value, expectedText,
                testColor);
        System.out.printf(", actual \"%s\")%n", actualText);
        System.out.flush();

        if (tryAllColors && testColor != TEST_COLOR) {
            for (int i = 0; i < COLOR_NAMES.length; i++) {
                String cname = COLOR_NAMES[i];
                tryColor(desc, cname, getter, value, expectedText, COLORS[i]);
            }
        }
    }

    private String tryColor(String desc, String cname, CellValue getter,
            Object value, String expectedText, Color expectedColor) {

        if (cname != null)
            desc = "[" + cname + "]" + desc;
        Color origColor = label.getForeground();
        CellFormatPart format = new CellFormatPart(desc);
        if (!format.apply(label, value).applies) {
            // If this doesn't apply, no color change is expected
            expectedColor = origColor;
        }

        String actualText = label.getText();
        Color actualColor = label.getForeground();
        getter.equivalent(expectedText, actualText, format);
        assertEquals(cname == null ? "no color" : "color " + cname,
                expectedColor, actualColor);
        return actualText;
    }

    /**
     * Returns the value for the given flag.  The flag has the value of
     * <tt>true</tt> if the text value is <tt>"true"</tt>, <tt>"yes"</tt>, or
     * <tt>"on"</tt> (ignoring case).
     *
     * @param flagName The name of the flag to fetch.
     * @param expected The value for the flag that is expected when the tests
     *                 are run for a full test.  If the current value is not the
     *                 expected one, you will get a warning in the test output.
     *                 This is so that you do not accidentally leave a flag set
     *                 to a value that prevents running some tests, thereby
     *                 letting you accidentally release code that is not fully
     *                 tested.
     *
     * @return The value for the flag.
     */
    protected boolean flagBoolean(String flagName, boolean expected) {
        String value = testFlags.get(flagName);
        boolean isSet;
        if (value == null)
            isSet = false;
        else {
            isSet = value.equalsIgnoreCase("true") || value.equalsIgnoreCase(
                    "yes") || value.equalsIgnoreCase("on");
        }
        warnIfUnexpected(flagName, expected, isSet);
        return isSet;
    }

    /**
     * Returns the value for the given flag.
     *
     * @param flagName The name of the flag to fetch.
     * @param expected The value for the flag that is expected when the tests
     *                 are run for a full test.  If the current value is not the
     *                 expected one, you will get a warning in the test output.
     *                 This is so that you do not accidentally leave a flag set
     *                 to a value that prevents running some tests, thereby
     *                 letting you accidentally release code that is not fully
     *                 tested.
     *
     * @return The value for the flag.
     */
    protected String flagString(String flagName, String expected) {
        String value = testFlags.get(flagName);
        if (value == null)
            value = "";
        warnIfUnexpected(flagName, expected, value);
        return value;
    }

    private void warnIfUnexpected(String flagName, Object expected,
            Object actual) {
        if (!actual.equals(expected)) {
            System.err.println(
                    "WARNING: " + testFile + ": " + "Flag " + flagName +
                            " = \"" + actual + "\" [not \"" + expected + "\"]");
        }
    }
}