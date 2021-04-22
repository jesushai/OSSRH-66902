package async.event;

import com.lemon.framework.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/20
 */
@Data
@AllArgsConstructor
public class AsyncEvent implements DomainEvent<String> {

    private String eventSource;

    @Override
    public boolean sameEventAs(String other) {
        return false;
    }

    @Override
    public String getEventState() {
        return null;
    }

    @Override
    public void setEventState(String eventState) {

    }
}
