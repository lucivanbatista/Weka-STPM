package weka.gui.stpm;

import java.util.List;

public class StringUtil {

    public static boolean stringCompareIgnoreCase(String stringOne, String stringTwo) {
        return stringOne.trim().equalsIgnoreCase(stringTwo.trim());
    }

    public static String formatNameParameter(String str) {
        if (str == null)
            return "";
        if (stringCompareIgnoreCase(str, "MaxAvgSpeed")) {
            return "as";
        } else if (stringCompareIgnoreCase(str, "MinAvgSpeed")) {
            return "as";
        } else if (stringCompareIgnoreCase(str, "MinTime (seconds)")) {
            return "mt";
        } else if (stringCompareIgnoreCase(str, "MaxSpeed")) {
            return "ms";
        } else if (stringCompareIgnoreCase(str, "MinDirChange (degrees)")) {
            return "md";
        } else if (stringCompareIgnoreCase(str, "MaxTolerance (points)")) {
            return "mt";
        } else if (stringCompareIgnoreCase(str, "MinTimeVar (seconds)")) {
            return "mtv";
        } else if (stringCompareIgnoreCase(str, "MinTimeSpeed (seconds)")) {
            return "mts";
        }
        return str;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String cleanUpString(String str) {
        return str.toLowerCase().replace(".", "_")
                .replace(",", "_").replace(" ", "")
                .replace("(degrees)", "")
                .replace("(seconds)", "")
                .replace("(points)", "");
    }

    public static String createNameOfStopTable(String str, List<Parameter> parameters) {
        return "stops_".concat(str.concat(parametersClusterStr(parameters)));
    }

    private static String parametersClusterStr(List<Parameter> parameters) {
        return cleanUpString(parameters.stream().map(parameter -> {
            if (!isEmpty(parameter.name) && parameter.value != null)
                return "_" + formatNameParameter(parameter.name) + "_" + parameter.value.toString();
            return "";
        }).reduce(String::concat).orElse(""));
    }

    public static boolean isNotEquals(Object obj, String notEqualValue) {
        if (obj == null) return false;
        final String str = obj.toString();
        return !isEmpty(str) && !str.equalsIgnoreCase(notEqualValue);
    }

}
