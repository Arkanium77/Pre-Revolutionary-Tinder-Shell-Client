package team.isaz.prerevolutionarytinder.client.shell.services.helper;

import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class StringConvertationUtils {
    static Logger logger = LoggerFactory.getLogger(StringConvertationUtils.class);

    public static String sexFromRepresentToBoolean(String sex) {
        sex = StringUtils.trim(sex);

        if (StringUtils.containsIgnoreCase(sex, "сударь")) {
            sex = Boolean.TRUE.toString();
        } else if (StringUtils.containsIgnoreCase(sex, "сударыня")) {
            sex = Boolean.FALSE.toString();
        } else {
            throw new RuntimeException("Будьте внимательнее! Вводъ строго по шаблону сударь/сударыня.");
        }
        return sex;
    }

    public static boolean isThatUUID(String value) {
        boolean response = true;
        try {
            var uuid = UUID.fromString(value);
        } catch (Exception e) {
            response = false;
        }
        return response;
    }

    public static String sexFromBooleanToRepresent(String sex) {
        Boolean mappedSex = null;
        try {
            mappedSex = Boolean.parseBoolean(sex);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

        if (mappedSex == null) {
            sex = "Чудо неизвестного полу";
        } else if (mappedSex) {
            sex = "Сударь";
        } else {
            sex = "Сударыня";
        }
        return sex;
    }
}
