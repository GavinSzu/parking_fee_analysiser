package excel;

import java.util.ArrayList;
import java.util.List;

/**
 * excel解析结果
 * @param <T>
 */
public class ExcelParseVo<T> {

    /**
     * 解析异常信息
     */
    private List<String> messages = new ArrayList<>();

    /**
     * 解析结果
     */
    private List<T> parseRs;

    /**
     * 增加异常信息
     * @param msg
     */
    public void addMessages(String msg) {
        messages.add(msg);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<T> getParseRs() {
        return parseRs;
    }

    public void setParseRs(List<T> parseRs) {
        this.parseRs = parseRs;
    }


}
