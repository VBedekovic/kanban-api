package hr.hivetech.Kanban.API.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MergePatchUtil {
    public static JsonNode applyMergePatch(JsonNode target, JsonNode patch) {
        if (patch.isObject()) {
            ObjectNode targetObject = (ObjectNode) target;
            ObjectNode patchObject = (ObjectNode) patch;
            patchObject.fieldNames().forEachRemaining(fieldName -> {
                JsonNode value = patchObject.get(fieldName);
                if (value.isNull()) {
                    targetObject.remove(fieldName);
                } else {
                    JsonNode existingValue = targetObject.get(fieldName);
                    if (existingValue != null && existingValue.isObject()) {
                        targetObject.replace(fieldName, applyMergePatch(existingValue, value));
                    } else {
                        targetObject.replace(fieldName, value);
                    }
                }
            });
            return targetObject;
        }
        return patch;
    }
}
