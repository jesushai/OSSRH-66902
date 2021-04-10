package com.lemon.schemaql.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.lemon.schemaql.config.PermissionsConfig;
import com.lemon.schemaql.enums.OperationTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/28
 */
public class JsonEnumTest {

    @Test
    public void test1() {
        PermissionsConfig permissionsConfig = new PermissionsConfig()
                .setPermissions(Sets.newHashSet("per1", "per2"))
                .setOperationTypes(Sets.newHashSet(OperationTypeEnum.get, OperationTypeEnum.gets))
                .setAction("action1")
                .setMenus(Sets.newHashSet("menu1", "menu2"));
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(permissionsConfig);
            System.out.println(json);

            PermissionsConfig newPermission = objectMapper.readValue(json, PermissionsConfig.class);
            Assert.isTrue(newPermission.getOperationTypes().contains(OperationTypeEnum.get), "xxxx");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
