package team.isaz.prerevolutionarytinder.client.shell.utils;

import lombok.var;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Генератор отображений пользовательских профилей.
 */
public class ViewGenerator {
    private final int maxStringLength;

    public ViewGenerator() {
        maxStringLength = 50;
    }

    public ViewGenerator(int maxStringLength) {
        this.maxStringLength = maxStringLength;
    }

    public String createProfileView(String username, String sex, String profile_message) {
        sex = StringConvertationUtils.sexFromBooleanToRepresent(sex);
        var list = lineBreakers(profile_message);
        list.add(StringUtils.leftPad(sex, maxStringLength));
        list.add(StringUtils.leftPad(username, maxStringLength));
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.repeat('#', maxStringLength + 4)).append('\n');
        list.forEach(s -> builder.append("# ").append(s).append(" #").append('\n'));
        builder.append(StringUtils.repeat('#', maxStringLength + 4)).append('\n');
        return builder.toString();
    }

    private List<String> lineBreakers(String profile_message) {
        var base = profile_message.split(" ");
        var result = new ArrayList<String>();
        StringBuilder string = new StringBuilder();
        for (String s : base) {
            if ((string.length() + s.length()) > maxStringLength) {
                result.add(string.toString());
                string = new StringBuilder();
            }
            string.append(s).append(' ');
        }
        if (string.length() != 0) result.add(string.toString());

        return result.stream()
                .map(s -> StringUtils.rightPad(s, 50))
                .collect(Collectors.toList());

    }

    public String createMatchesList(Object[] array) {
        StringBuilder builder = new StringBuilder("Списокъ Любимцевъ:\n");
        if (array.length == 0) return builder.toString() + "Тутъ пока пусто! Смахните пару анкетъ!";
        for (int i = 0; i < array.length; i++) {
            builder
                    .append(i + 1)
                    .append(") ")
                    .append(array[i])
                    .append("\n");
        }
        return builder.toString();
    }
}
