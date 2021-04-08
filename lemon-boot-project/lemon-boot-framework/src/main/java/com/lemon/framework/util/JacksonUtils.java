package com.lemon.framework.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/5
 */
@Slf4j
@SuppressWarnings("unused")
public class JacksonUtils {

    /**
     * ObjectMapper是线程安全的，只要不更改其配置就可以了。
     */
    private final static ObjectMapper mapper = new ObjectMapper();

    public static String parseString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> T parseObject(String content, Class<T> clazz) {
        try {
            return mapper.readValue(content, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String parseString(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asText();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public static List<String> parseStringList(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if (leaf != null) {
                return mapper.convertValue(leaf, new TypeReference<List<String>>() {
                });
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Integer parseInteger(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asInt();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Long parseLong(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asLong();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static List<Integer> parseIntegerList(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if (leaf != null) {
                return mapper.convertValue(leaf, new TypeReference<List<Integer>>() {
                });
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static List<Long> parseLongList(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if (leaf != null) {
                return mapper.convertValue(leaf, new TypeReference<List<Long>>() {
                });
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public static Boolean parseBoolean(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asBoolean();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Short parseShort(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                int value = leaf.asInt();
                return (short) value;
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Byte parseByte(String body, String field) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                int value = leaf.asInt();
                return (byte) value;
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> T parseObject(String body, String field, Class<T> clazz) {
        JsonNode node;
        try {
            node = mapper.readTree(body);
            node = node.get(field);
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Object toNode(String json) {
        if (json == null) {
            return null;
        }
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Map<String, String> toMap(String data) {
        try {
            return mapper.readValue(data, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String toJson(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
