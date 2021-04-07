package com.lemon.framework.auth.model;

import com.lemon.framework.core.annotation.PermissionDescription;
import com.lemon.framework.handler.MessageSourceHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/12
 */
@Data
@Accessors(chain = true)
public class PermissionTreeNode {

    /**
     * 多个id逗号分隔
     */
    private String id;

    private String label;

    private String api;

    private List<PermissionTreeNode> children;

    public static void addTreeNode(List<PermissionTreeNode> root, Permission permission) {

        String api = permission.getApi();
        PermissionDescription description = permission.getDescription();
        // id=module:function:action...
        String[] id = permission.getId();
        // menu={"level1","level2"...}
        String[] menus = description.menu();
        // action="actionCode"
        String action = description.action();

        PermissionTreeNode parent = null;
        for (int i = 0; i < menus.length; i++) {
            if (i == 0) {
                parent = addTreeNodeFirst(root, menus[i]);
            } else if (i == menus.length - 1) {
                addTreeNodeAction(parent, menus[i], action, id, api);
            } else {
                parent = addTreeNode(parent, menus[i]);
            }
        }
    }

    /**
     * 生成第一级菜单
     */
    private static PermissionTreeNode addTreeNodeFirst(List<PermissionTreeNode> root, String menu) {
        // 是否已经存在
        PermissionTreeNode node = root.stream()
                .filter(p -> p.getId().equals(menu)).findFirst().orElse(null);
        if (null != node) {
            return node;
        }
        // 不存在则建立一级菜单
        node = new PermissionTreeNode()
                .setId(menu)
                .setLabel(MessageSourceHandler.getMessage(menu))
                .setChildren(new ArrayList<>());
        root.add(node);
        return node;
    }

    /**
     * 生成非一级菜单
     */
    private static PermissionTreeNode addTreeNode(PermissionTreeNode parent, String menu) {
        // 是否已经存在，存在则直接返回即可
        PermissionTreeNode node = parent.getChildren().stream()
                .filter(p -> p.getId().equals(menu)).findFirst().orElse(null);
        if (node != null) {
            return node;
        }
        // 不存在则建立非一级菜单
        node = new PermissionTreeNode()
                .setId(menu)
                .setLabel(MessageSourceHandler.getMessage(menu))
                .setChildren(new ArrayList<>());
        parent.getChildren().add(node);
        return node;
    }

    /**
     * 添加最后的功能（同时会添加功能所属的末级菜单）
     *
     * @param parent   上级菜单
     * @param menu     末级菜单名
     * @param action   功能code（会转为国际化存储到Label中）
     * @param actionId 功能的ID（即注解上的功能ID）
     * @param api      api地址
     */
    private static void addTreeNodeAction(PermissionTreeNode parent, String menu, String action, String[] actionId, String api) {
        // 先把末级菜单加上
        PermissionTreeNode leaf = addTreeNode(parent, menu);
        // id
        String id = StringUtils.join(actionId, ',');
        // 是否已经存在
        if (leaf.getChildren().stream().anyMatch(p -> p.getId().equals(id))) {
            throw new RuntimeException(String.format("Permission [%s] already exists, cannot be defined repeatedly.", id));
        }
        // 添加功能
        PermissionTreeNode node = new PermissionTreeNode()
                .setId(id)
                .setLabel(MessageSourceHandler.getMessage(action))
                .setApi(api);
        leaf.getChildren().add(node);
    }

}
