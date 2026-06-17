package com.zalo.common.util;

public final class PermissionConstant {
    public static final class App {
        public static final class User {
            public static final String READ = "user:read";
            public static final String WRITE = "user:write";
            public static final String DELETE = "user:delete";
        }
    }

    public static final class OA {

    }

    public static final class Admin {
        public static final class Structure {
            public static final String VIEW_TREE = "structure:view_tree";
            public static final String CREATE_NODE = "structure:create_node";
            public static final String UPDATE_NODE = "structure:update_node";
            public static final String DELETE_NODE = "structure:delete_node";
            public static final String RESTORE_NODE = "structure:restore_node";
        }
    }
}
