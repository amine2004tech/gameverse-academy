package ma.ac.esi.gameverseacademy.security;

/**
 * SEC-FIX: HTML output encoder for JSP pages.
 * Use this to encode user-controlled data before rendering in HTML context.
 * This is the primary defense against Stored/Reflected XSS — InputSanitizer
 * serves only as defense-in-depth.
 */
public class HtmlEncoder {

    /**
     * Encodes a string for safe HTML output.
     * Handles null gracefully by returning empty string.
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(input.length() + 16);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&':  sb.append("&amp;");  break;
                case '<':  sb.append("&lt;");   break;
                case '>':  sb.append("&gt;");   break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&#x27;"); break;
                default:   sb.append(c);        break;
            }
        }
        return sb.toString();
    }

    /**
     * Encodes for safe use inside a JavaScript string literal.
     */
    public static String encodeForJS(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("'", "\\'")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("<", "\\u003c")
                     .replace(">", "\\u003e");
    }
}
