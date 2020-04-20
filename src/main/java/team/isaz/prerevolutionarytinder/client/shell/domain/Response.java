package team.isaz.prerevolutionarytinder.client.shell.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * <b>Response</b><br>
 * Сущность, используемая для передачи данных между сервисами.<br>
 * Содержит статус операции и, опционально, дополнительные данные.
 * Это может быть строка-пояснение, данные необходимые для работы программы или какой-то мета-контейнер.
 */
@Getter
@EqualsAndHashCode
public class Response {
    private final boolean status;
    private final Object attach;

    public Response(Object attach) {
        this.attach = attach;
        status = false;
    }

    public Response(boolean status) {
        this.status = status;
        this.attach = null;
    }

    public Response(boolean status, Object attach) {
        this.status = status;
        this.attach = attach;
    }

}
