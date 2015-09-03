package org.robovm.samples.robopods.parse.anypic.ios.util;

import org.robovm.apple.foundation.NSCalendar;
import org.robovm.apple.foundation.NSCalendarOptions;
import org.robovm.apple.foundation.NSCalendarUnit;
import org.robovm.apple.foundation.NSComparisonResult;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSDateComponents;
import org.robovm.apple.foundation.NSFormatter;
import org.robovm.apple.foundation.NSLocale;

/**
 * Instances of TTTTimeIntervalFormatter create localized string representations
 * of NSTimeInterval values.
 * 
 * For example, a time interval of -75 would be formatted as "1 minute ago" in
 * English.
 */
public class TTTTimeIntervalFormatter extends NSFormatter {
    private NSLocale locale;
    private NSCalendar calendar;

    private String pastDeicticExpression = "ago";
    private String presentDeicticExpression = "just now";
    private String futureDeicticExpression = "from now";
    private String deicticExpressionFormat = "%s %s";
    private String approximateQualifierFormat = "about %s";
    private String suffixExpressionFormat = "%d %s";
    private double presentTimeIntervalMargin = 1;
    private boolean usesIdiomaticDeicticExpressions;
    private boolean usesApproximateQualifier;
    private NSCalendarUnit significantUnits = NSCalendarUnit.with(NSCalendarUnit.Year, NSCalendarUnit.Month,
            NSCalendarUnit.WeekOfYear, NSCalendarUnit.Day, NSCalendarUnit.Hour, NSCalendarUnit.Minute,
            NSCalendarUnit.Second);
    private int numberOfSignificantUnits = 1;
    private NSCalendarUnit leastSignificantUnit = NSCalendarUnit.Second;
    private boolean usesAbbreviatedCalendarUnits;

    public TTTTimeIntervalFormatter() {
        this.locale = NSLocale.getCurrentLocale();
        this.calendar = NSCalendar.getCurrentCalendar();
    }

    /**
     * Returns a string representation of a time interval formatted using the
     * receiver’s current settings.
     * 
     * @param seconds The number of seconds to add to the receiver. Use a
     *            negative value for seconds to have the returned object specify
     *            a date before the receiver.
     */
    public String format(double seconds) {
        NSDate date = new NSDate();
        return format(date, NSDate.createWithTimeIntervalSinceDate(seconds, date));
    }

    /**
     * Returns a string representation of the time interval between two
     * specified dates formatted using the receiver’s current settings.
     * 
     * @param startingDate The starting date
     * @param endingDate The ending date
     */
    public String format(NSDate startingDate, NSDate endingDate) {
        double seconds = startingDate.getTimeIntervalSince(endingDate);
        if (Math.abs(seconds) < presentTimeIntervalMargin) {
            return presentDeicticExpression;
        }

        NSDateComponents components = calendar.getComponents(significantUnits, startingDate, endingDate,
                NSCalendarOptions.None);

        if (usesIdiomaticDeicticExpressions) {
            String idiomaticDeicticExpression = getLocalizedIdiomaticDeicticExpression(components);
            if (idiomaticDeicticExpression != null) {
                return idiomaticDeicticExpression;
            }
        }

        String string = null;
        boolean isApproximate = false;
        int numberOfUnits = 0;

        String[] unitNames = new String[] { "year", "month", "weekOfYear", "day", "hour", "minute", "second" };
        for (String unitName : unitNames) {
            NSCalendarUnit unit = getCalendarUnitFromString(unitName);
            if (significantUnits.contains(unit)
                    && compareCalendarUnitSignificance(leastSignificantUnit, unit) != NSComparisonResult.Descending) {
                long number = Math.abs(components.getValue(unit));
                if (number != 0) {
                    String suffix = String.format(suffixExpressionFormat, number, getLocalizedString(number, unit));
                    if (string == null) {
                        string = suffix;
                    } else if (numberOfSignificantUnits == 0 || numberOfUnits < numberOfSignificantUnits) {
                        string += String.format(" %s", suffix);
                    } else {
                        isApproximate = true;
                    }

                    numberOfUnits++;
                }
            }
        }

        if (string != null) {
            if (seconds > 0) {
                if (pastDeicticExpression != null && pastDeicticExpression.length() > 0) {
                    string = String.format(deicticExpressionFormat, string, pastDeicticExpression);
                }
            } else {
                if (futureDeicticExpression != null && futureDeicticExpression.length() > 0) {
                    string = String.format(deicticExpressionFormat, string, futureDeicticExpression);
                }
            }

            if (isApproximate && usesApproximateQualifier) {
                string = String.format(approximateQualifierFormat, string);
            }
        } else {
            string = presentDeicticExpression;
        }

        return string;
    }

    private String getLocalizedString(long number, NSCalendarUnit unit) {
        boolean singular = number == 1;

        if (usesAbbreviatedCalendarUnits) {
            if (unit.contains(NSCalendarUnit.Year)) {
                return singular ? "yr" : "yrs";
            } else if (unit.contains(NSCalendarUnit.Month)) {
                return singular ? "mo" : "mos";
            } else if (unit.contains(NSCalendarUnit.WeekOfYear)) {
                return singular ? "wk" : "wks";
            } else if (unit.contains(NSCalendarUnit.Day)) {
                return singular ? "d" : "days";
            } else if (unit.contains(NSCalendarUnit.Hour)) {
                return singular ? "hr" : "hrs";
            } else if (unit.contains(NSCalendarUnit.Minute)) {
                return singular ? "min" : "mins";
            } else if (unit.contains(NSCalendarUnit.Second)) {
                return singular ? "s" : "s";
            } else {
                return null;
            }
        } else {
            if (unit.contains(NSCalendarUnit.Year)) {
                return singular ? "year" : "years";
            } else if (unit.contains(NSCalendarUnit.Month)) {
                return singular ? "month" : "months";
            } else if (unit.contains(NSCalendarUnit.WeekOfYear)) {
                return singular ? "week" : "weeks";
            } else if (unit.contains(NSCalendarUnit.Day)) {
                return singular ? "day" : "days";
            } else if (unit.contains(NSCalendarUnit.Hour)) {
                return singular ? "hour" : "hours";
            } else if (unit.contains(NSCalendarUnit.Minute)) {
                return singular ? "minute" : "minutes";
            } else if (unit.contains(NSCalendarUnit.Second)) {
                return singular ? "second" : "seconds";
            } else {
                return null;
            }
        }
    }

    private String getLocalizedIdiomaticDeicticExpression(NSDateComponents components) {
        String languageCode = locale.getLanguageCode();
        switch (languageCode) {
        case "ca":
            return caRelativeDateString(components);
        case "cs":
            return csRelativeDateString(components);
        case "de":
            return deRelativeDateString(components);
        case "es":
            return esRelativeDateString(components);
        case "en":
            return enRelativeDateString(components);
        case "fr":
            return frRelativeDateString(components);
        case "it":
            return itRelativeDateString(components);
        case "ja":
            return jaRelativeDateString(components);
        case "nl":
            return nlRelativeDateString(components);
        case "pl":
            return plRelativeDateString(components);
        default:
            return null;
        }
    }

    private String caRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "any passat";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "mes passat";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "setmana passada";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "ahir";
        } else if (components.getDay() == -2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "abans d'ahir";
        }

        if (components.getYear() == 1) {
            return "pròxim any";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "pròxim mes";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "pròxima setmana";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "demà";
        } else if (components.getDay() == 2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "passat demà";
        }

        return null;
    }

    private String deRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "letztes Jahr";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "letzen Monat";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "letzte Woche";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "gestern";
        } else if (components.getDay() == -2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "vorgestern";
        }

        if (components.getYear() == 1) {
            return "nächstes Jahr";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "nächsten Monat";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "nächste Woche";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "morgen";
        } else if (components.getDay() == 2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "übermorgen";
        }

        return null;
    }

    private String enRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "last year";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "last month";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "last week";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "yesterday";
        }

        if (components.getYear() == 1) {
            return "next year";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "next month";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "next week";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "tomorrow";
        }

        return null;
    }

    private String esRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "año pasado";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "mes pasado";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "semana pasada";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "ayer";
        } else if (components.getDay() == -2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "antes de ayer";
        }

        if (components.getYear() == 1) {
            return "próximo año";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "próximo mes";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "próxima semana";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "mañana";
        } else if (components.getDay() == 2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "pasado mañana";
        }

        return null;
    }

    private String nlRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "vorig jaar";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "vorige maand";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "vorige week";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "gisteren";
        } else if (components.getDay() == -2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "eergisteren";
        }

        if (components.getYear() == 1) {
            return "volgend jaar";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "volgende maand";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "volgende week";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "morgen";
        } else if (components.getDay() == 2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "overmorgen";
        }

        return null;
    }

    private String plRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "zeszły rok";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "zeszły miesiąc";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "zeszły tydzień";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "wczoraj";
        } else if (components.getDay() == -2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "przedwczoraj";
        }

        if (components.getYear() == 1) {
            return "przyszły rok";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "przyszły miesiąc";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "przyszły tydzień";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "jutro";
        } else if (components.getDay() == 2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "pojutrze";
        }

        return null;
    }

    private String csRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "minulý rok";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "minulý měsíc";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "minulý týden";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "včera";
        } else if (components.getDay() == -2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "předevčírem";
        }

        if (components.getYear() == 1) {
            return "příští rok";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "příští měsíc";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "příští týden";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "zítra";
        } else if (components.getDay() == 2 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "pozítří";
        }

        return null;
    }

    private String jaRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "去年";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "先月";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "先週";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "昨日";
        }

        if (components.getYear() == 1) {
            return "来年";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "来月";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "来週";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "明日";
        }

        return null;
    }

    private String frRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "l'annnée dernière";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "le mois dernier";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "la semaine dernière";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "hier";
        }

        if (components.getYear() == 1) {
            return "l'année prochaine";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "le mois prochain";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "la semaine prochaine";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "demain";
        }

        return null;
    }

    private String itRelativeDateString(NSDateComponents components) {
        if (components.getYear() == -1) {
            return "un anno fa";
        } else if (components.getMonth() == -1 && components.getYear() == 0) {
            return "un mese fa";
        } else if (components.getWeekOfYear() == -1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "una settimana fa";
        } else if (components.getDay() == -1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "ieri";
        }

        if (components.getYear() == 1) {
            return "l'anno prossimo";
        } else if (components.getMonth() == 1 && components.getYear() == 0) {
            return "il mese prossimo";
        } else if (components.getWeekOfYear() == 1 && components.getYear() == 0 && components.getMonth() == 0) {
            return "la prossima settimana";
        } else if (components.getDay() == 1 && components.getYear() == 0 && components.getMonth() == 0
                && components.getWeekOfYear() == 0) {
            return "domani";
        }

        return null;
    }

    private static NSCalendarUnit getCalendarUnitFromString(String string) {
        switch (string) {
        case "year":
            return NSCalendarUnit.Year;
        case "month":
            return NSCalendarUnit.Month;
        case "weekOfYear":
            return NSCalendarUnit.WeekOfYear;
        case "day":
            return NSCalendarUnit.Day;
        case "hour":
            return NSCalendarUnit.Hour;
        case "minute":
            return NSCalendarUnit.Minute;
        case "second":
            return NSCalendarUnit.Second;
        default:
            return NSCalendarUnit.None;
        }
    }

    private static NSComparisonResult compareCalendarUnitSignificance(NSCalendarUnit unit1,
            NSCalendarUnit unit2) {
        long a = unit1.value();
        long b = unit2.value();
        if ((a == NSCalendarUnit.WeekOfYear.value()) ^ (b == NSCalendarUnit.WeekOfYear.value())) {
            if (a == NSCalendarUnit.WeekOfYear.value()) {
                if (a == NSCalendarUnit.Year.value() || a == NSCalendarUnit.Month.value()) {
                    return NSComparisonResult.Ascending;
                }
                return NSComparisonResult.Descending;
            } else {
                if (b == NSCalendarUnit.Year.value() || b == NSCalendarUnit.Month.value()) {
                    return NSComparisonResult.Descending;
                }
                return NSComparisonResult.Ascending;
            }
        } else {
            if (a > b) {
                return NSComparisonResult.Ascending;
            } else if (a < b) {
                return NSComparisonResult.Descending;
            } else {
                return NSComparisonResult.Same;
            }
        }
    }

    /**
     * Specifies the locale used to format strings. Defaults to the current
     * system locale.
     */
    public void setLocale(NSLocale locale) {
        this.locale = locale;
    }

    public NSLocale getLocale() {
        return locale;
    }

    /**
     * Specifies the calendar used in date calculation. Defaults to the current
     * system calendar.
     */
    public void setCalendar(NSCalendar calendar) {
        this.calendar = calendar;
    }

    public NSCalendar getCalendar() {
        return calendar;
    }

    /**
     * Specifies the localized string used to express the past deictic
     * expression. "ago" by default.
     */
    public void setPastDeicticExpression(String pastDeicticExpression) {
        this.pastDeicticExpression = pastDeicticExpression;
    }

    public String getPastDeicticExpression() {
        return pastDeicticExpression;
    }

    /**
     * Specifies the localized string used to express the present deictic
     * expression. "just now" by default.
     */
    public void setPresentDeicticExpression(String presentDeicticExpression) {
        this.presentDeicticExpression = presentDeicticExpression;
    }

    public String getPresentDeicticExpression() {
        return presentDeicticExpression;
    }

    /**
     * Specifies the localized string used to express the future deictic
     * expression. "from now" by default.
     */
    public void setFutureDeicticExpression(String futureDeicticExpression) {
        this.futureDeicticExpression = futureDeicticExpression;
    }

    public String getFutureDeicticExpression() {
        return futureDeicticExpression;
    }

    /**
     * Specifies the localized string used to format the time interval string
     * and deictic expression. Defaults to a format with the deictic expression
     * following the time interval
     */
    public void setDeicticExpressionFormat(String deicticExpressionFormat) {
        this.deicticExpressionFormat = deicticExpressionFormat;
    }

    public String getDeicticExpressionFormat() {
        return deicticExpressionFormat;
    }

    /**
     * Specifies the localized string used to format the time with its suffix.
     * "#{Time} #{Unit}" by default.
     */
    public void setSuffixExpressionFormat(String suffixExpressionFormat) {
        this.suffixExpressionFormat = suffixExpressionFormat;
    }

    public String getSuffixExpressionFormat() {
        return suffixExpressionFormat;
    }

    /**
     * Specifies the time interval before and after the present moment that is
     * described as still being in the present, rather than the past or future.
     * Defaults to 1 second.
     */
    public void setPresentTimeIntervalMargin(double presentTimeIntervalMargin) {
        this.presentTimeIntervalMargin = presentTimeIntervalMargin;
    }

    public double getPresentTimeIntervalMargin() {
        return presentTimeIntervalMargin;
    }

    /**
     * Specifies whether to use idiomatic deictic expressions when available,
     * such as "last week" instead of "1 week ago". Defaults to `NO`.
     * 
     * @discussion This implementation is entirely separate from the equivalent
     *             behavior used by `NSDateFormatter`.
     */
    public void setUsesIdiomaticDeicticExpressions(boolean usesIdiomaticDeicticExpressions) {
        this.usesIdiomaticDeicticExpressions = usesIdiomaticDeicticExpressions;
    }

    public boolean usesIdiomaticDeicticExpressions() {
        return usesIdiomaticDeicticExpressions;
    }

    /**
     * Specifies the localized string used to qualify a time interval as being
     * an approximate time. "about" by default.
     */
    public void setApproximateQualifierFormat(String approximateQualifierFormat) {
        this.approximateQualifierFormat = approximateQualifierFormat;
    }

    public String getApproximateQualifierFormat() {
        return approximateQualifierFormat;
    }

    /**
     * Specifies whether to use an approximate qualifier when the described
     * interval is not exact. `NO` by default.
     */
    public void setUsesApproximateQualifier(boolean usesApproximateQualifier) {
        this.usesApproximateQualifier = usesApproximateQualifier;
    }

    public boolean usesApproximateQualifier() {
        return usesApproximateQualifier;
    }

    /**
     * A bitmask specifying the significant units. Defaults to a bitmask of
     * year, month, week, day, hour, minute, and second.
     */
    public void setSignificantUnits(NSCalendarUnit significantUnits) {
        this.significantUnits = significantUnits;
    }

    public NSCalendarUnit getSignificantUnits() {
        return significantUnits;
    }

    /**
     * Specifies the number of units that should be displayed before
     * approximating. `0` to show all units. `1` by default.
     */
    public void setNumberOfSignificantUnits(int numberOfSignificantUnits) {
        this.numberOfSignificantUnits = numberOfSignificantUnits;
    }

    public int getNumberOfSignificantUnits() {
        return numberOfSignificantUnits;
    }

    /**
     * Specifies the least significant unit that should be displayed when not
     * approximating. Defaults to `NSCalendarUnitSeconds`.
     */
    public void setLeastSignificantUnit(NSCalendarUnit leastSignificantUnit) {
        this.leastSignificantUnit = leastSignificantUnit;
    }

    public NSCalendarUnit getLeastSignificantUnit() {
        return leastSignificantUnit;
    }

    /**
     * Specifies whether to use abbreviated calendar units to describe time
     * intervals, for instance "wks" instead of "weeks" in English. Defaults to
     * `NO`.
     */
    public void setUsesAbbreviatedCalendarUnits(boolean usesAbbreviatedCalendarUnits) {
        this.usesAbbreviatedCalendarUnits = usesAbbreviatedCalendarUnits;
    }

    public boolean usesAbbreviatedCalendarUnits() {
        return usesAbbreviatedCalendarUnits;
    }
}
