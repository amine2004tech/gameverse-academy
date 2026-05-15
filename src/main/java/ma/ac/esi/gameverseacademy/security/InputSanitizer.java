package ma.ac.esi.gameverseacademy.security;

import java.util.regex.Pattern;

public class InputSanitizer {
    
    // Patterns for malicious script tags and events
    private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern SCRIPT_TAG_PATTERN_OPEN = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern SCRIPT_TAG_PATTERN_CLOSE = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVAL_PATTERN = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern VBSCRIPT_PATTERN = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern ON_EVENTS_PATTERN = Pattern.compile("on[a-z]+\\s*=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        // Normalize whitespace without destroying newlines (trim only to be safe)
        String sanitized = input.trim();
        
        // Strip dangerous scripts and eval injections
        sanitized = SCRIPT_TAG_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = SCRIPT_TAG_PATTERN_OPEN.matcher(sanitized).replaceAll("");
        sanitized = SCRIPT_TAG_PATTERN_CLOSE.matcher(sanitized).replaceAll("");
        sanitized = EVAL_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = EXPRESSION_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = JAVASCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = VBSCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = ON_EVENTS_PATTERN.matcher(sanitized).replaceAll("invalid=");
        
        // HTML Escaping to prevent basic XSS and SQL injection neutralization at boundary
        sanitized = sanitized.replace("&", "&amp;");
        sanitized = sanitized.replace("<", "&lt;").replace(">", "&gt;");
        sanitized = sanitized.replace("\"", "&quot;").replace("'", "&#x27;");
        
		return sanitized;
	}
}
