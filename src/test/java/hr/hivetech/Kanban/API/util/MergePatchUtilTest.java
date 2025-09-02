package hr.hivetech.Kanban.API.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.hivetech.Kanban.API.utils.MergePatchUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MergePatchUtilTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void applyMergePatch_mergesFields() throws Exception {
        JsonNode target = mapper.readTree("{\"a\":1,\"b\":2}");
        JsonNode patch = mapper.readTree("{\"b\":3,\"c\":4}");

        JsonNode result = MergePatchUtil.applyMergePatch(target, patch);

        assertEquals(1, result.get("a").asInt());
        assertEquals(3, result.get("b").asInt());
        assertEquals(4, result.get("c").asInt());
    }

    @Test
    void applyMergePatch_removesFieldWithNull() throws Exception {
        JsonNode target = mapper.readTree("{\"a\":1,\"b\":2}");
        JsonNode patch = mapper.readTree("{\"b\":null}");

        JsonNode result = MergePatchUtil.applyMergePatch(target, patch);

        assertEquals(1, result.get("a").asInt());
        assertNull(result.get("b"));
    }

    @Test
    void applyMergePatch_mergesNestedObjects() throws Exception {
        JsonNode target = mapper.readTree("{\"a\":{\"x\":1,\"y\":2},\"b\":2}");
        JsonNode patch = mapper.readTree("{\"a\":{\"y\":3,\"z\":4}}");

        JsonNode result = MergePatchUtil.applyMergePatch(target, patch);

        assertEquals(1, result.get("a").get("x").asInt());
        assertEquals(3, result.get("a").get("y").asInt());
        assertEquals(4, result.get("a").get("z").asInt());
        assertEquals(2, result.get("b").asInt());
    }

    @Test
    void applyMergePatch_replacesNonObjectField() throws Exception {
        JsonNode target = mapper.readTree("\"oldValue\"");
        JsonNode patch = mapper.readTree("\"newValue\"");

        JsonNode result = MergePatchUtil.applyMergePatch(target, patch);

        assertEquals("newValue", result.asText());
    }
}