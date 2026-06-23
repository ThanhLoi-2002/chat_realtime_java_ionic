package com.zalo.common.util;

public final class PermissionConstant {
    public static final class APP {

    }

    public static final class OA {

    }

    public static final class ADMIN {
        public static final class Lang {
            public static final String READ = "lang:read";
            public static final String CREATE = "lang:create";
            public static final String UPDATE = "lang:update";
            public static final String DELETE = "lang:delete";
        }

        public static final class Structure {
            public static final String READ = "structure:read";
            public static final String CREATE_UPDATE = "structure:create_update";
        }

        public static final class User {
            public static final String READ = "user:read";
            public static final String CREATE = "user:create";
            public static final String UPDATE = "user:update";
            public static final String DELETE = "user:delete";
        }
    }
}
