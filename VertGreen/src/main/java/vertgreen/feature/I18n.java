package vertgreen.feature;

import vertgreen.db.DatabaseNotReadyException;
import vertgreen.db.EntityReader;
import vertgreen.db.EntityWriter;
import vertgreen.db.entity.GuildConfig;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n {

    private static final Logger log = LoggerFactory.getLogger(I18n.class);

    public static VertGreenLocale DEFAULT = new VertGreenLocale(new Locale("en","US"), "en_US", "English");
    public static final HashMap<String, VertGreenLocale> LANGS = new HashMap<>();

    public static void start() {
        LANGS.put("en_US", DEFAULT);
        LANGS.put("af_ZA", new VertGreenLocale(new Locale("af", "ZA"), "af_ZA", "Afrikaans"));
        LANGS.put("bg_BG", new VertGreenLocale(new Locale("bg", "BG"), "bg_BG", "български език"));
        LANGS.put("ca_ES", new VertGreenLocale(new Locale("ca", "ES"), "ca_ES", "Catalan"));
        LANGS.put("zh_CN", new VertGreenLocale(new Locale("zh", "CN"), "zh_CN", "简体中文"));
        LANGS.put("zh_TW", new VertGreenLocale(new Locale("zh", "TW"), "zh_TW", "繁體中文"));
        LANGS.put("cs_CZ", new VertGreenLocale(new Locale("cs", "CZ"), "cs_CZ", "Čeština"));
        LANGS.put("da_DK", new VertGreenLocale(new Locale("da", "DK"), "da_DK", "Dansk"));
        LANGS.put("nl_NL", new VertGreenLocale(new Locale("nl", "NL"), "nl_NL", "Nederlands"));
        LANGS.put("fr_FR", new VertGreenLocale(new Locale("fr", "FR"), "fr_FR", "Français"));
        LANGS.put("de_DE", new VertGreenLocale(new Locale("de", "DE"), "de_DE", "Deutsch"));
        LANGS.put("he_IL", new VertGreenLocale(new Locale("he", "IL"), "he_IL", "עברית"));
        LANGS.put("id_ID", new VertGreenLocale(new Locale("id", "ID"), "id_ID", "Bahasa Indonesia"));
        LANGS.put("it_IT", new VertGreenLocale(new Locale("it", "IT"), "it_IT", "Italiano"));
        LANGS.put("ko_KR", new VertGreenLocale(new Locale("ko", "KR"), "ko_KR", "한국어"));
        LANGS.put("pl_PL", new VertGreenLocale(new Locale("pl", "PL"), "pl_PL", "Polski"));
        LANGS.put("pt_BR", new VertGreenLocale(new Locale("pt", "BR"), "pt_BR", "Português (Brazil)"));
        LANGS.put("pt_PT", new VertGreenLocale(new Locale("pt", "PT"), "pt_PT", "Português"));
        LANGS.put("ro_RO", new VertGreenLocale(new Locale("ro", "RO"), "ro_RO", "Română"));
        LANGS.put("ru_RU", new VertGreenLocale(new Locale("ru", "RU"), "ru_RU", "Русский"));
        LANGS.put("es_ES", new VertGreenLocale(new Locale("es", "ES"), "es_ES", "Español"));
        LANGS.put("sv_SE", new VertGreenLocale(new Locale("sv", "SE"), "sv_SE", "Svenska"));
        LANGS.put("tr_TR", new VertGreenLocale(new Locale("tr", "TR"), "tr_TR", "Türkçe"));
        LANGS.put("vi_VN", new VertGreenLocale(new Locale("vi", "VN"), "vi_VN", "Tiếng Việt"));
        LANGS.put("cy_GB", new VertGreenLocale(new Locale("cy", "GB"), "cy_GB", "Cymraeg"));

        LANGS.put("en_PT", new VertGreenLocale(new Locale("en", "PT"), "en_PT", "Pirate English"));
        LANGS.put("en_TS", new VertGreenLocale(new Locale("en", "TS"), "en_TS", "Tsundere English"));

        log.info("Loaded " + LANGS.size() + " languages: " + LANGS);
    }

    public static ResourceBundle get(Guild guild) {
        if (guild == null) {
            return DEFAULT.getProps();
        }
        return getLocale(guild).getProps();
    }

    public static VertGreenLocale getLocale(Guild guild) {
        GuildConfig config;

        try {
            config = EntityReader.getGuildConfig(guild.getId());
        } catch (DatabaseNotReadyException e) {
            //don't log spam the full exceptions or logs
            return DEFAULT;
        } catch (Exception e) {
            log.error("Error when reading entity", e);
            return DEFAULT;
        }

        return LANGS.getOrDefault(config.getLang(), DEFAULT);
    }

    public static void set(Guild guild, String lang) throws LanguageNotSupportedException {
        if (!LANGS.containsKey(lang))
            throw new LanguageNotSupportedException("Language not found");

        GuildConfig config = EntityReader.getGuildConfig(guild.getId());
        config.setLang(lang);
        EntityWriter.mergeGuildConfig(config);
    }

    public static class VertGreenLocale {

        private final Locale locale;
        private final String code;
        private final ResourceBundle props;
        private final String nativeName;

        VertGreenLocale(Locale locale, String code, String nativeName) throws MissingResourceException {
            this.locale = locale;
            this.code = code;
            props = ResourceBundle.getBundle("lang." + code, locale);
            this.nativeName = nativeName;
        }

        public Locale getLocale() {
            return locale;
        }

        public String getCode() {
            return code;
        }

        public ResourceBundle getProps() {
            return props;
        }

        public String getNativeName() {
            return nativeName;
        }

        @Override
        public String toString() {
            return "[" + nativeName + ']';
        }
    }

    public static class LanguageNotSupportedException extends Exception {
        public LanguageNotSupportedException(String message) {
            super(message);
        }
    }

}
